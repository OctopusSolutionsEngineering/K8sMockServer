import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.fabric8.kubernetes.client.server.mock.KubernetesMixedDispatcher;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;
import io.fabric8.mockwebserver.http.MockResponse;
import io.fabric8.mockwebserver.http.RecordedRequest;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Queue;

/**
 * The mock k8s server works well with JSON requests, but commands like `kubectl create namespace` send protobuf requests.
 * This dispatcher converts the protobuf request into a JSON request that the mock server can match against.
 */
public class CustomDispatcher extends KubernetesMixedDispatcher {
    private static final byte[] K8S_MAGIC = new byte[]{'k', '8', 's', 0x00};
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public CustomDispatcher(Map<ServerRequest, Queue<ServerResponse>> responses) {
        super(responses);
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) {
        String contentType = request.getHeader("Content-Type");
        boolean isProtobufRequest = contentType != null && contentType.contains("application/vnd.kubernetes.protobuf");

        if (isProtobufRequest) {
            RecordedRequest jsonRequest = convertProtobufToJsonRequest(request);
            if (jsonRequest != null) {
                MockResponse jsonResponse = super.dispatch(jsonRequest);
                // Return JSON response as-is — kubectl accepts application/json as a fallback.
                // Setting Content-Type explicitly ensures the client doesn't try to decode as protobuf.
                jsonResponse.setHeader("Content-Type", "application/json");
                return jsonResponse;
            }
        }
        return super.dispatch(request);
    }

    /**
     * Converts a protobuf-encoded Kubernetes request into a JSON request that the mock server can match against.
     * The Kubernetes protobuf wire format is:
     *   - 4 bytes: magic "k8s\0"
     *   - Protobuf-encoded Unknown message containing TypeMeta and raw object bytes
     */
    private RecordedRequest convertProtobufToJsonRequest(RecordedRequest original) {
        try {
            byte[] body = original.getBody().getBytes();
            if (body == null || body.length < 4) {
                return null;
            }

            // Verify the k8s magic prefix
            if (body[0] != K8S_MAGIC[0] || body[1] != K8S_MAGIC[1]
                    || body[2] != K8S_MAGIC[2] || body[3] != K8S_MAGIC[3]) {
                return null;
            }

            // Parse the protobuf Unknown envelope after the magic bytes
            K8sProtobufEnvelope envelope = parseUnknownEnvelope(body, 4);
            if (envelope == null) {
                return null;
            }

            // Parse the raw protobuf object into a JSON representation
            ObjectNode jsonBody = OBJECT_MAPPER.createObjectNode();
            if (envelope.apiVersion != null) {
                jsonBody.put("apiVersion", envelope.apiVersion);
            }
            if (envelope.kind != null) {
                jsonBody.put("kind", envelope.kind);
            }

            // Parse the raw bytes as a Kubernetes resource protobuf
            if (envelope.raw != null && envelope.raw.length > 0) {
                parseResourceFields(envelope.raw, jsonBody);
            }

            String jsonString = OBJECT_MAPPER.writeValueAsString(jsonBody);
            System.out.println("Converted protobuf to JSON: " + jsonString);

            io.fabric8.mockwebserver.http.Buffer newBody =
                    new io.fabric8.mockwebserver.http.Buffer(jsonString.getBytes(StandardCharsets.UTF_8));
            io.fabric8.mockwebserver.http.Headers newHeaders = original.getHeaders().newBuilder()
                    .set("Content-Type", "application/json")
                    .build();

            return new RecordedRequest(
                    original.getHttpVersion(),
                    original.method(),
                    original.getPath(),
                    newHeaders,
                    newBody
            );
        } catch (Exception e) {
            System.err.println("Failed to convert protobuf request: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parses the Kubernetes protobuf Unknown envelope.
     * The Unknown message has:
     *   field 1 (TypeMeta): embedded message with apiVersion (field 1) and kind (field 2)
     *   field 2 (raw): bytes containing the serialized resource
     *   field 3 (contentEncoding): string
     *   field 4 (contentType): string
     */
    private K8sProtobufEnvelope parseUnknownEnvelope(byte[] data, int offset) {
        K8sProtobufEnvelope envelope = new K8sProtobufEnvelope();
        int pos = offset;

        while (pos < data.length) {
            if (pos >= data.length) break;
            long tagAndType = readVarint(data, pos);
            if (tagAndType < 0) break;
            pos += varintSize(data, pos);

            int fieldNumber = (int) (tagAndType >>> 3);
            int wireType = (int) (tagAndType & 0x07);

            switch (wireType) {
                case 0: // Varint
                    readVarint(data, pos);
                    pos += varintSize(data, pos);
                    break;
                case 2: // Length-delimited
                    long length = readVarint(data, pos);
                    pos += varintSize(data, pos);
                    if (length < 0 || pos + length > data.length) return envelope;
                    byte[] fieldData = new byte[(int) length];
                    System.arraycopy(data, pos, fieldData, 0, (int) length);

                    if (fieldNumber == 1) {
                        // TypeMeta
                        parseTypeMeta(fieldData, envelope);
                    } else if (fieldNumber == 2) {
                        // raw bytes
                        envelope.raw = fieldData;
                    } else if (fieldNumber == 3) {
                        envelope.contentEncoding = new String(fieldData, StandardCharsets.UTF_8);
                    } else if (fieldNumber == 4) {
                        envelope.contentType = new String(fieldData, StandardCharsets.UTF_8);
                    }
                    pos += (int) length;
                    break;
                case 5: // 32-bit
                    pos += 4;
                    break;
                case 1: // 64-bit
                    pos += 8;
                    break;
                default:
                    return envelope;
            }
        }
        return envelope;
    }

    private void parseTypeMeta(byte[] data, K8sProtobufEnvelope envelope) {
        int pos = 0;
        while (pos < data.length) {
            long tagAndType = readVarint(data, pos);
            if (tagAndType < 0) break;
            pos += varintSize(data, pos);

            int fieldNumber = (int) (tagAndType >>> 3);
            int wireType = (int) (tagAndType & 0x07);

            if (wireType == 2) {
                long length = readVarint(data, pos);
                pos += varintSize(data, pos);
                if (length < 0 || pos + length > data.length) return;
                String value = new String(data, pos, (int) length, StandardCharsets.UTF_8);
                if (fieldNumber == 1) {
                    envelope.apiVersion = value;
                } else if (fieldNumber == 2) {
                    envelope.kind = value;
                }
                pos += (int) length;
            } else if (wireType == 0) {
                readVarint(data, pos);
                pos += varintSize(data, pos);
            } else {
                break;
            }
        }
    }

    /**
     * Attempts to parse common Kubernetes resource fields from protobuf raw bytes.
     * Extracts metadata.name and metadata.namespace from the ObjectMeta (typically field 1 or 2).
     */
    private void parseResourceFields(byte[] data, ObjectNode jsonBody) {
        int pos = 0;
        while (pos < data.length) {
            long tagAndType = readVarint(data, pos);
            if (tagAndType < 0) break;
            pos += varintSize(data, pos);

            int fieldNumber = (int) (tagAndType >>> 3);
            int wireType = (int) (tagAndType & 0x07);

            switch (wireType) {
                case 0:
                    readVarint(data, pos);
                    pos += varintSize(data, pos);
                    break;
                case 2:
                    long length = readVarint(data, pos);
                    pos += varintSize(data, pos);
                    if (length < 0 || pos + length > data.length) return;
                    byte[] fieldData = new byte[(int) length];
                    System.arraycopy(data, pos, fieldData, 0, (int) length);

                    // Field 1 in most Kubernetes resources is ObjectMeta
                    if (fieldNumber == 1) {
                        ObjectNode metadata = parseObjectMeta(fieldData);
                        if (metadata.size() > 0) {
                            jsonBody.set("metadata", metadata);
                        }
                    }
                    pos += (int) length;
                    break;
                case 5:
                    pos += 4;
                    break;
                case 1:
                    pos += 8;
                    break;
                default:
                    return;
            }
        }
    }

    /**
     * Parses ObjectMeta protobuf fields.
     * ObjectMeta field numbers:
     *   1 = name, 2 = generateName, 3 = namespace, 5 = uid, ...
     */
    private ObjectNode parseObjectMeta(byte[] data) {
        ObjectNode metadata = OBJECT_MAPPER.createObjectNode();
        int pos = 0;
        while (pos < data.length) {
            long tagAndType = readVarint(data, pos);
            if (tagAndType < 0) break;
            pos += varintSize(data, pos);

            int fieldNumber = (int) (tagAndType >>> 3);
            int wireType = (int) (tagAndType & 0x07);

            switch (wireType) {
                case 0:
                    readVarint(data, pos);
                    pos += varintSize(data, pos);
                    break;
                case 2:
                    long length = readVarint(data, pos);
                    pos += varintSize(data, pos);
                    if (length < 0 || pos + length > data.length) return metadata;
                    String value = new String(data, pos, (int) length, StandardCharsets.UTF_8);
                    if (fieldNumber == 1) {
                        metadata.put("name", value);
                    } else if (fieldNumber == 2) {
                        metadata.put("generateName", value);
                    } else if (fieldNumber == 3) {
                        metadata.put("namespace", value);
                    }
                    pos += (int) length;
                    break;
                case 5:
                    pos += 4;
                    break;
                case 1:
                    pos += 8;
                    break;
                default:
                    return metadata;
            }
        }
        return metadata;
    }

    private long readVarint(byte[] data, int offset) {
        long result = 0;
        int shift = 0;
        int pos = offset;
        while (pos < data.length) {
            byte b = data[pos];
            result |= (long) (b & 0x7F) << shift;
            pos++;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
            if (shift >= 64) return -1;
        }
        return -1;
    }

    private int varintSize(byte[] data, int offset) {
        int pos = offset;
        while (pos < data.length) {
            if ((data[pos] & 0x80) == 0) {
                return pos - offset + 1;
            }
            pos++;
        }
        return pos - offset;
    }

    private static class K8sProtobufEnvelope {
        String apiVersion;
        String kind;
        byte[] raw;
        String contentEncoding;
        String contentType;
    }
}
