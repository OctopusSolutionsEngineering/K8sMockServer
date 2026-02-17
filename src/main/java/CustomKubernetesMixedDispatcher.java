import io.fabric8.kubernetes.client.server.mock.KubernetesMixedDispatcher;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;
import io.fabric8.mockwebserver.http.Buffer;
import io.fabric8.mockwebserver.http.MockResponse;
import io.fabric8.mockwebserver.http.RecordedRequest;

import java.util.Map;
import java.util.Queue;

/**
 * This class exists to fix this error:
 * unacceptable code point '' (0x0) special characters are not allowed in "reader", position 3
 */
public class CustomKubernetesMixedDispatcher extends KubernetesMixedDispatcher {
    private static final char[] INVALID_CHARS = new char[] {'\0', '\u000F', '\u0002', '\u0012', '\u001B', '\u000B', '\u001A'};

    public CustomKubernetesMixedDispatcher(Map<ServerRequest, Queue<ServerResponse>> responses) {
        super(responses);
    }

    @Override
    public MockResponse dispatch(final RecordedRequest request) {
        // Read the original body and remove null characters and 0xF character
        final Buffer originalBody = request.getBody();
        final String originalContent = originalBody.readUtf8();

        String sanitizedContent = originalContent;
        for (char invalidChar : INVALID_CHARS) {
            sanitizedContent = sanitizedContent.replace(String.valueOf(invalidChar), "");
        }

        // Create a new buffer with the sanitized content
        final Buffer sanitizedBody = new Buffer();
        sanitizedBody.writeUtf8(sanitizedContent);

        final RecordedRequest sanitisedRequest = new RecordedRequest(request.getHttpVersion(), request.method(), request.getPath(), request.getHeaders(), sanitizedBody);
        return super.dispatch(sanitisedRequest);
    }
}
