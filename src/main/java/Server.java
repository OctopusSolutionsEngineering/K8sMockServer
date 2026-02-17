import io.fabric8.kubernetes.client.server.mock.KubernetesMixedDispatcher;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.mockwebserver.Context;
import io.fabric8.mockwebserver.MockWebServer;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;
import io.fabric8.mockwebserver.http.Dispatcher;
import io.fabric8.mockwebserver.http.Headers;
import io.fabric8.mockwebserver.http.RecordedRequest;
import io.fabric8.mockwebserver.utils.ResponseProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * A mock server, based on the responses provided by Kind 1.33, that implements just enough of the Kubernetes API
 * to allow Octopus to complete a simple deployment.
 */
public class Server {
    private static final String[] TIMEOUTS = {
            "1m0s",
            "32s"
    };

    public Server() {

    }

    public void start() throws UnknownHostException {

        //final KubernetesMockServer server = new KubernetesMockServer(false);

        final Map<ServerRequest, Queue<ServerResponse>> responses = new HashMap<>();
        final Dispatcher dispatcher = new KubernetesMixedDispatcher(responses);
        final KubernetesMockServer server = new KubernetesMockServer(new Context(Serialization.jsonMapper()),
                new MockWebServer(), responses, dispatcher, false);

        addHealthCheck(server);
        addVersionEndpoint(server);
        addApiEndpoint(server);
        addApisEndpoint(server);
        addApiRegistrationEndpoint(server);
        addApiBatchEndpoint(server);
        addApiAutoscalingEndpointV2(server);
        addApiAutoscalingEndpointV1(server);
        addApiNetworkingEndpoint(server);
        addApiNodeEndpoint(server);
        addApiCertificatesEndpoint(server);
        addApiStorageEndpoint(server);
        addApiAuthorizationEndpoint(server);
        addApiDiscoveryEndpoint(server);
        addApiFlowControlEndpointBeta3(server);
        addApiExtensionsEndpoint(server);
        addApiAdmissionRegistrationEndpoint(server);
        addApiRackAuthorizationEndpoint(server);
        addApiEventsEndpoint(server);
        addApiSchedulingEndpoint(server);
        addApiAppsEndpoint(server);
        addApiPolicyEndpoint(server);
        addApiV1Endpoint(server);
        addApiCoordinationEndpoint(server);
        addApiNamespacesEndpoint(server);
        addApiApiRegistrationEndpoint(server);
        addApiAuthenticationEndpoint(server);
        addApiFlowControlEndpointV1(server);
        addApiDefaultNamespaceEndpoint(server);
        addSelfSubjectAccessReviewsEndpoint(server);
        addOpenApiV3Endpoint(server);
        addOpenApiV3HashEndpoint(server);
        final InetAddress address = InetAddress.getByName("0.0.0.0");
        server.init(address, 48080);
    }

    /**
     * This method adds a health check endpoint that checks for the existence of a file at
     * /tmp/online and returns "ok" if the file exists, or "offline" if it does not.
     * The purpose of this endpoint is to allow the server to switch between an "online" and "offline" state
     * by creating or deleting the file at a shared location. This is used to simulate a problem with the server
     * and to allow the server to be restarted in a "healthy" state.
     */
    private void addHealthCheck(final KubernetesMockServer server) {
            final String marker = "/tmp/online";

            server.expect()
                    .get()
                    .withPath("/health")
                    .andReply(new ResponseProvider<Object>() {
                        @Override
                        public Object getBody(final RecordedRequest request) {
                            if (Files.exists(Paths.get(marker))) {
                                return "ok";
                            } else {
                                return "offline";
                            }
                        }

                        @Override
                        public int getStatusCode(final RecordedRequest request) {
                            if (Files.exists(Paths.get(marker))) {
                                return 200;
                            } else {
                                return 500;
                            }
                        }

                        @Override
                        public Headers getHeaders() {
                            return Headers.builder().build();
                        }

                        @Override
                        public void setHeaders(Headers headers) {

                        }
                    })
                    .always();



    }

    private void addApiEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIVersions",
                    "versions": [
                        "v1"
                    ],
                    "serverAddressByClientCIDRs": [
                        {
                            "clientCIDR": "0.0.0.0/0",
                            "serverAddress": "localhost:48080"
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/api?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApisEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                     "kind": "APIGroupList",
                     "apiVersion": "v1",
                     "groups": [
                         {
                             "name": "apiregistration.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "apiregistration.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "apiregistration.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "apps",
                             "versions": [
                                 {
                                     "groupVersion": "apps/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "apps/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "events.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "events.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "events.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "authentication.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "authentication.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "authentication.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "authorization.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "authorization.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "authorization.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "autoscaling",
                             "versions": [
                                 {
                                     "groupVersion": "autoscaling/v2",
                                     "version": "v2"
                                 },
                                 {
                                     "groupVersion": "autoscaling/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "autoscaling/v2",
                                 "version": "v2"
                             }
                         },
                         {
                             "name": "batch",
                             "versions": [
                                 {
                                     "groupVersion": "batch/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "batch/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "certificates.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "certificates.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "certificates.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "networking.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "networking.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "networking.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "policy",
                             "versions": [
                                 {
                                     "groupVersion": "policy/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "policy/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "rbac.authorization.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "rbac.authorization.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "rbac.authorization.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "storage.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "storage.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "storage.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "admissionregistration.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "admissionregistration.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "admissionregistration.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "apiextensions.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "apiextensions.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "apiextensions.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "scheduling.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "scheduling.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "scheduling.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "coordination.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "coordination.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "coordination.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "node.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "node.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "node.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "discovery.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "discovery.k8s.io/v1",
                                     "version": "v1"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "discovery.k8s.io/v1",
                                 "version": "v1"
                             }
                         },
                         {
                             "name": "flowcontrol.apiserver.k8s.io",
                             "versions": [
                                 {
                                     "groupVersion": "flowcontrol.apiserver.k8s.io/v1",
                                     "version": "v1"
                                 },
                                 {
                                     "groupVersion": "flowcontrol.apiserver.k8s.io/v1beta3",
                                     "version": "v1beta3"
                                 }
                             ],
                             "preferredVersion": {
                                 "groupVersion": "flowcontrol.apiserver.k8s.io/v1",
                                 "version": "v1"
                             }
                         }
                     ]
                 }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addVersionEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "major": "1",
                    "minor": "33",
                    "gitVersion": "v1.33.0",
                    "gitCommit": "9edcffcde5595e8a5b1a35f88c421764e575afce",
                    "gitTreeState": "clean",
                    "buildDate": "2024-08-13T22:44:37Z",
                    "goVersion": "go1.22.5",
                    "compiler": "gc",
                    "platform": "linux/amd64"
                }
                """;

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/version?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiRegistrationEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                        "kind": "APIResourceList",
                        "apiVersion": "v1",
                        "groupVersion": "apiregistration.k8s.io/v1",
                        "resources": [
                            {
                                "name": "apiservices",
                                "singularName": "apiservice",
                                "namespaced": false,
                                "kind": "APIService",
                                "verbs": [
                                    "create",
                                    "delete",
                                    "deletecollection",
                                    "get",
                                    "list",
                                    "patch",
                                    "update",
                                    "watch"
                                ],
                                "categories": [
                                    "api-extensions"
                                ],
                                "storageVersionHash": "InPBPD7+PqM="
                            },
                            {
                                "name": "apiservices/status",
                                "singularName": "",
                                "namespaced": false,
                                "kind": "APIService",
                                "verbs": [
                                    "get",
                                    "patch",
                                    "update"
                                ]
                            }
                        ]
                    }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("apis/apiregistration.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiBatchEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                         "kind": "APIResourceList",
                         "apiVersion": "v1",
                         "groupVersion": "batch/v1",
                         "resources": [
                             {
                                 "name": "cronjobs",
                                 "singularName": "cronjob",
                                 "namespaced": true,
                                 "kind": "CronJob",
                                 "verbs": [
                                     "create",
                                     "delete",
                                     "deletecollection",
                                     "get",
                                     "list",
                                     "patch",
                                     "update",
                                     "watch"
                                 ],
                                 "shortNames": [
                                     "cj"
                                 ],
                                 "categories": [
                                     "all"
                                 ],
                                 "storageVersionHash": "sd5LIXh4Fjs="
                             },
                             {
                                 "name": "cronjobs/status",
                                 "singularName": "",
                                 "namespaced": true,
                                 "kind": "CronJob",
                                 "verbs": [
                                     "get",
                                     "patch",
                                     "update"
                                 ]
                             },
                             {
                                 "name": "jobs",
                                 "singularName": "job",
                                 "namespaced": true,
                                 "kind": "Job",
                                 "verbs": [
                                     "create",
                                     "delete",
                                     "deletecollection",
                                     "get",
                                     "list",
                                     "patch",
                                     "update",
                                     "watch"
                                 ],
                                 "categories": [
                                     "all"
                                 ],
                                 "storageVersionHash": "mudhfqk/qZY="
                             },
                             {
                                 "name": "jobs/status",
                                 "singularName": "",
                                 "namespaced": true,
                                 "kind": "Job",
                                 "verbs": [
                                     "get",
                                     "patch",
                                     "update"
                                 ]
                             }
                         ]
                     }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/batch/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiAutoscalingEndpointV2(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "autoscaling/v2",
                    "resources": [
                        {
                            "name": "horizontalpodautoscalers",
                            "singularName": "horizontalpodautoscaler",
                            "namespaced": true,
                            "kind": "HorizontalPodAutoscaler",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "hpa"
                            ],
                            "categories": [
                                "all"
                            ],
                            "storageVersionHash": "qwQve8ut294="
                        },
                        {
                            "name": "horizontalpodautoscalers/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "HorizontalPodAutoscaler",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/autoscaling/v2?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiAutoscalingEndpointV1(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "autoscaling/v1",
                    "resources": [
                        {
                            "name": "horizontalpodautoscalers",
                            "singularName": "horizontalpodautoscaler",
                            "namespaced": true,
                            "kind": "HorizontalPodAutoscaler",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "hpa"
                            ],
                            "categories": [
                                "all"
                            ],
                            "storageVersionHash": "qwQve8ut294="
                        },
                        {
                            "name": "horizontalpodautoscalers/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "HorizontalPodAutoscaler",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/autoscaling/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiNetworkingEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "networking.k8s.io/v1",
                    "resources": [
                        {
                            "name": "ingressclasses",
                            "singularName": "ingressclass",
                            "namespaced": false,
                            "kind": "IngressClass",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "l/iqIbDgFyQ="
                        },
                        {
                            "name": "ingresses",
                            "singularName": "ingress",
                            "namespaced": true,
                            "kind": "Ingress",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "ing"
                            ],
                            "storageVersionHash": "39NQlfNR+bo="
                        },
                        {
                            "name": "ingresses/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "Ingress",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "networkpolicies",
                            "singularName": "networkpolicy",
                            "namespaced": true,
                            "kind": "NetworkPolicy",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "netpol"
                            ],
                            "storageVersionHash": "YpfwF18m1G8="
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/networking.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiNodeEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "node.k8s.io/v1",
                    "resources": [
                        {
                            "name": "runtimeclasses",
                            "singularName": "runtimeclass",
                            "namespaced": false,
                            "kind": "RuntimeClass",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "WQTu1GL3T2Q="
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/node.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiCertificatesEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "certificates.k8s.io/v1",
                    "resources": [
                        {
                            "name": "certificatesigningrequests",
                            "singularName": "certificatesigningrequest",
                            "namespaced": false,
                            "kind": "CertificateSigningRequest",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "csr"
                            ],
                            "storageVersionHash": "95fRKMXA+00="
                        },
                        {
                            "name": "certificatesigningrequests/approval",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "CertificateSigningRequest",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "certificatesigningrequests/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "CertificateSigningRequest",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/certificates.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiStorageEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "storage.k8s.io/v1",
                    "resources": [
                        {
                            "name": "csidrivers",
                            "singularName": "csidriver",
                            "namespaced": false,
                            "kind": "CSIDriver",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "hL6j/rwBV5w="
                        },
                        {
                            "name": "csinodes",
                            "singularName": "csinode",
                            "namespaced": false,
                            "kind": "CSINode",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "Pe62DkZtjuo="
                        },
                        {
                            "name": "csistoragecapacities",
                            "singularName": "csistoragecapacity",
                            "namespaced": true,
                            "kind": "CSIStorageCapacity",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "xeVl+2Ly1kE="
                        },
                        {
                            "name": "storageclasses",
                            "singularName": "storageclass",
                            "namespaced": false,
                            "kind": "StorageClass",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "sc"
                            ],
                            "storageVersionHash": "K+m6uJwbjGY="
                        },
                        {
                            "name": "volumeattachments",
                            "singularName": "volumeattachment",
                            "namespaced": false,
                            "kind": "VolumeAttachment",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "tJx/ezt6UDU="
                        },
                        {
                            "name": "volumeattachments/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "VolumeAttachment",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/storage.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiAuthorizationEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "authorization.k8s.io/v1",
                    "resources": [
                        {
                            "name": "localsubjectaccessreviews",
                            "singularName": "localsubjectaccessreview",
                            "namespaced": true,
                            "kind": "LocalSubjectAccessReview",
                            "verbs": [
                                "create"
                            ]
                        },
                        {
                            "name": "selfsubjectaccessreviews",
                            "singularName": "selfsubjectaccessreview",
                            "namespaced": false,
                            "kind": "SelfSubjectAccessReview",
                            "verbs": [
                                "create"
                            ]
                        },
                        {
                            "name": "selfsubjectrulesreviews",
                            "singularName": "selfsubjectrulesreview",
                            "namespaced": false,
                            "kind": "SelfSubjectRulesReview",
                            "verbs": [
                                "create"
                            ]
                        },
                        {
                            "name": "subjectaccessreviews",
                            "singularName": "subjectaccessreview",
                            "namespaced": false,
                            "kind": "SubjectAccessReview",
                            "verbs": [
                                "create"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/authorization.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiDiscoveryEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "discovery.k8s.io/v1",
                    "resources": [
                        {
                            "name": "endpointslices",
                            "singularName": "endpointslice",
                            "namespaced": true,
                            "kind": "EndpointSlice",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "qgS0xkrxYAI="
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/discovery.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiFlowControlEndpointBeta3(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "flowcontrol.apiserver.k8s.io/v1beta3",
                    "resources": [
                        {
                            "name": "flowschemas",
                            "singularName": "flowschema",
                            "namespaced": false,
                            "kind": "FlowSchema",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "GJVAJZSZBIw="
                        },
                        {
                            "name": "flowschemas/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "FlowSchema",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "prioritylevelconfigurations",
                            "singularName": "prioritylevelconfiguration",
                            "namespaced": false,
                            "kind": "PriorityLevelConfiguration",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "Kir5PVfvNeI="
                        },
                        {
                            "name": "prioritylevelconfigurations/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "PriorityLevelConfiguration",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/flowcontrol.apiserver.k8s.io/v1beta3?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiFlowControlEndpointV1(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "flowcontrol.apiserver.k8s.io/v1",
                    "resources": [
                        {
                            "name": "flowschemas",
                            "singularName": "flowschema",
                            "namespaced": false,
                            "kind": "FlowSchema",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "GJVAJZSZBIw="
                        },
                        {
                            "name": "flowschemas/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "FlowSchema",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "prioritylevelconfigurations",
                            "singularName": "prioritylevelconfiguration",
                            "namespaced": false,
                            "kind": "PriorityLevelConfiguration",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "Kir5PVfvNeI="
                        },
                        {
                            "name": "prioritylevelconfigurations/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "PriorityLevelConfiguration",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/flowcontrol.apiserver.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiExtensionsEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "apiextensions.k8s.io/v1",
                    "resources": [
                        {
                            "name": "customresourcedefinitions",
                            "singularName": "customresourcedefinition",
                            "namespaced": false,
                            "kind": "CustomResourceDefinition",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "crd",
                                "crds"
                            ],
                            "categories": [
                                "api-extensions"
                            ],
                            "storageVersionHash": "jfWCUB31mvA="
                        },
                        {
                            "name": "customresourcedefinitions/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "CustomResourceDefinition",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/apiextensions.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiAdmissionRegistrationEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "admissionregistration.k8s.io/v1",
                    "resources": [
                        {
                            "name": "mutatingwebhookconfigurations",
                            "singularName": "mutatingwebhookconfiguration",
                            "namespaced": false,
                            "kind": "MutatingWebhookConfiguration",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "categories": [
                                "api-extensions"
                            ],
                            "storageVersionHash": "Sqi0GUgDaX0="
                        },
                        {
                            "name": "validatingadmissionpolicies",
                            "singularName": "validatingadmissionpolicy",
                            "namespaced": false,
                            "kind": "ValidatingAdmissionPolicy",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "categories": [
                                "api-extensions"
                            ],
                            "storageVersionHash": "6OxvlMmQ6is="
                        },
                        {
                            "name": "validatingadmissionpolicies/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "ValidatingAdmissionPolicy",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "validatingadmissionpolicybindings",
                            "singularName": "validatingadmissionpolicybinding",
                            "namespaced": false,
                            "kind": "ValidatingAdmissionPolicyBinding",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "categories": [
                                "api-extensions"
                            ],
                            "storageVersionHash": "v9715VZqakg="
                        },
                        {
                            "name": "validatingwebhookconfigurations",
                            "singularName": "validatingwebhookconfiguration",
                            "namespaced": false,
                            "kind": "ValidatingWebhookConfiguration",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "categories": [
                                "api-extensions"
                            ],
                            "storageVersionHash": "B0wHjQmsGNk="
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/admissionregistration.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiRackAuthorizationEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "rbac.authorization.k8s.io/v1",
                    "resources": [
                        {
                            "name": "clusterrolebindings",
                            "singularName": "clusterrolebinding",
                            "namespaced": false,
                            "kind": "ClusterRoleBinding",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "48tpQ8gZHFc="
                        },
                        {
                            "name": "clusterroles",
                            "singularName": "clusterrole",
                            "namespaced": false,
                            "kind": "ClusterRole",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "bYE5ZWDrJ44="
                        },
                        {
                            "name": "rolebindings",
                            "singularName": "rolebinding",
                            "namespaced": true,
                            "kind": "RoleBinding",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "eGsCzGH6b1g="
                        },
                        {
                            "name": "roles",
                            "singularName": "role",
                            "namespaced": true,
                            "kind": "Role",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "7FuwZcIIItM="
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/rbac.authorization.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiEventsEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "events.k8s.io/v1",
                    "resources": [
                        {
                            "name": "events",
                            "singularName": "event",
                            "namespaced": true,
                            "kind": "Event",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "ev"
                            ],
                            "storageVersionHash": "r2yiGXH7wu8="
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/events.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiSchedulingEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "scheduling.k8s.io/v1",
                    "resources": [
                        {
                            "name": "priorityclasses",
                            "singularName": "priorityclass",
                            "namespaced": false,
                            "kind": "PriorityClass",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "pc"
                            ],
                            "storageVersionHash": "1QwjyaZjj3Y="
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/scheduling.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiAppsEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "apps/v1",
                    "resources": [
                        {
                            "name": "controllerrevisions",
                            "singularName": "controllerrevision",
                            "namespaced": true,
                            "kind": "ControllerRevision",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "85nkx63pcBU="
                        },
                        {
                            "name": "daemonsets",
                            "singularName": "daemonset",
                            "namespaced": true,
                            "kind": "DaemonSet",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "ds"
                            ],
                            "categories": [
                                "all"
                            ],
                            "storageVersionHash": "dd7pWHUlMKQ="
                        },
                        {
                            "name": "daemonsets/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "DaemonSet",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "deployments",
                            "singularName": "deployment",
                            "namespaced": true,
                            "kind": "Deployment",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "deploy"
                            ],
                            "categories": [
                                "all"
                            ],
                            "storageVersionHash": "8aSe+NMegvE="
                        },
                        {
                            "name": "deployments/scale",
                            "singularName": "",
                            "namespaced": true,
                            "group": "autoscaling",
                            "version": "v1",
                            "kind": "Scale",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "deployments/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "Deployment",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "replicasets",
                            "singularName": "replicaset",
                            "namespaced": true,
                            "kind": "ReplicaSet",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "rs"
                            ],
                            "categories": [
                                "all"
                            ],
                            "storageVersionHash": "P1RzHs8/mWQ="
                        },
                        {
                            "name": "replicasets/scale",
                            "singularName": "",
                            "namespaced": true,
                            "group": "autoscaling",
                            "version": "v1",
                            "kind": "Scale",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "replicasets/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "ReplicaSet",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "statefulsets",
                            "singularName": "statefulset",
                            "namespaced": true,
                            "kind": "StatefulSet",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "sts"
                            ],
                            "categories": [
                                "all"
                            ],
                            "storageVersionHash": "H+vl74LkKdo="
                        },
                        {
                            "name": "statefulsets/scale",
                            "singularName": "",
                            "namespaced": true,
                            "group": "autoscaling",
                            "version": "v1",
                            "kind": "Scale",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "statefulsets/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "StatefulSet",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/apps/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiPolicyEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "policy/v1",
                    "resources": [
                        {
                            "name": "poddisruptionbudgets",
                            "singularName": "poddisruptionbudget",
                            "namespaced": true,
                            "kind": "PodDisruptionBudget",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "pdb"
                            ],
                            "storageVersionHash": "EVWiDmWqyJw="
                        },
                        {
                            "name": "poddisruptionbudgets/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "PodDisruptionBudget",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/policy/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiV1Endpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "groupVersion": "v1",
                    "resources": [
                        {
                            "name": "bindings",
                            "singularName": "binding",
                            "namespaced": true,
                            "kind": "Binding",
                            "verbs": [
                                "create"
                            ]
                        },
                        {
                            "name": "componentstatuses",
                            "singularName": "componentstatus",
                            "namespaced": false,
                            "kind": "ComponentStatus",
                            "verbs": [
                                "get",
                                "list"
                            ],
                            "shortNames": [
                                "cs"
                            ]
                        },
                        {
                            "name": "configmaps",
                            "singularName": "configmap",
                            "namespaced": true,
                            "kind": "ConfigMap",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "cm"
                            ],
                            "storageVersionHash": "qFsyl6wFWjQ="
                        },
                        {
                            "name": "endpoints",
                            "singularName": "endpoints",
                            "namespaced": true,
                            "kind": "Endpoints",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "ep"
                            ],
                            "storageVersionHash": "fWeeMqaN/OA="
                        },
                        {
                            "name": "events",
                            "singularName": "event",
                            "namespaced": true,
                            "kind": "Event",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "ev"
                            ],
                            "storageVersionHash": "r2yiGXH7wu8="
                        },
                        {
                            "name": "limitranges",
                            "singularName": "limitrange",
                            "namespaced": true,
                            "kind": "LimitRange",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "limits"
                            ],
                            "storageVersionHash": "EBKMFVe6cwo="
                        },
                        {
                            "name": "namespaces",
                            "singularName": "namespace",
                            "namespaced": false,
                            "kind": "Namespace",
                            "verbs": [
                                "create",
                                "delete",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "ns"
                            ],
                            "storageVersionHash": "Q3oi5N2YM8M="
                        },
                        {
                            "name": "namespaces/finalize",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "Namespace",
                            "verbs": [
                                "update"
                            ]
                        },
                        {
                            "name": "namespaces/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "Namespace",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "nodes",
                            "singularName": "node",
                            "namespaced": false,
                            "kind": "Node",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "no"
                            ],
                            "storageVersionHash": "XwShjMxG9Fs="
                        },
                        {
                            "name": "nodes/proxy",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "NodeProxyOptions",
                            "verbs": [
                                "create",
                                "delete",
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "nodes/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "Node",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "persistentvolumeclaims",
                            "singularName": "persistentvolumeclaim",
                            "namespaced": true,
                            "kind": "PersistentVolumeClaim",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "pvc"
                            ],
                            "storageVersionHash": "QWTyNDq0dC4="
                        },
                        {
                            "name": "persistentvolumeclaims/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "PersistentVolumeClaim",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "persistentvolumes",
                            "singularName": "persistentvolume",
                            "namespaced": false,
                            "kind": "PersistentVolume",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "pv"
                            ],
                            "storageVersionHash": "HN/zwEC+JgM="
                        },
                        {
                            "name": "persistentvolumes/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "PersistentVolume",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "pods",
                            "singularName": "pod",
                            "namespaced": true,
                            "kind": "Pod",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "po"
                            ],
                            "categories": [
                                "all"
                            ],
                            "storageVersionHash": "xPOwRZ+Yhw8="
                        },
                        {
                            "name": "pods/attach",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "PodAttachOptions",
                            "verbs": [
                                "create",
                                "get"
                            ]
                        },
                        {
                            "name": "pods/binding",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "Binding",
                            "verbs": [
                                "create"
                            ]
                        },
                        {
                            "name": "pods/ephemeralcontainers",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "Pod",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "pods/eviction",
                            "singularName": "",
                            "namespaced": true,
                            "group": "policy",
                            "version": "v1",
                            "kind": "Eviction",
                            "verbs": [
                                "create"
                            ]
                        },
                        {
                            "name": "pods/exec",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "PodExecOptions",
                            "verbs": [
                                "create",
                                "get"
                            ]
                        },
                        {
                            "name": "pods/log",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "Pod",
                            "verbs": [
                                "get"
                            ]
                        },
                        {
                            "name": "pods/portforward",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "PodPortForwardOptions",
                            "verbs": [
                                "create",
                                "get"
                            ]
                        },
                        {
                            "name": "pods/proxy",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "PodProxyOptions",
                            "verbs": [
                                "create",
                                "delete",
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "pods/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "Pod",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "podtemplates",
                            "singularName": "podtemplate",
                            "namespaced": true,
                            "kind": "PodTemplate",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "LIXB2x4IFpk="
                        },
                        {
                            "name": "replicationcontrollers",
                            "singularName": "replicationcontroller",
                            "namespaced": true,
                            "kind": "ReplicationController",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "rc"
                            ],
                            "categories": [
                                "all"
                            ],
                            "storageVersionHash": "Jond2If31h0="
                        },
                        {
                            "name": "replicationcontrollers/scale",
                            "singularName": "",
                            "namespaced": true,
                            "group": "autoscaling",
                            "version": "v1",
                            "kind": "Scale",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "replicationcontrollers/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "ReplicationController",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "resourcequotas",
                            "singularName": "resourcequota",
                            "namespaced": true,
                            "kind": "ResourceQuota",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "quota"
                            ],
                            "storageVersionHash": "8uhSgffRX6w="
                        },
                        {
                            "name": "resourcequotas/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "ResourceQuota",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "secrets",
                            "singularName": "secret",
                            "namespaced": true,
                            "kind": "Secret",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "S6u1pOWzb84="
                        },
                        {
                            "name": "serviceaccounts",
                            "singularName": "serviceaccount",
                            "namespaced": true,
                            "kind": "ServiceAccount",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "sa"
                            ],
                            "storageVersionHash": "pbx9ZvyFpBE="
                        },
                        {
                            "name": "serviceaccounts/token",
                            "singularName": "",
                            "namespaced": true,
                            "group": "authentication.k8s.io",
                            "version": "v1",
                            "kind": "TokenRequest",
                            "verbs": [
                                "create"
                            ]
                        },
                        {
                            "name": "services",
                            "singularName": "service",
                            "namespaced": true,
                            "kind": "Service",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "shortNames": [
                                "svc"
                            ],
                            "categories": [
                                "all"
                            ],
                            "storageVersionHash": "0/CO1lhkEBI="
                        },
                        {
                            "name": "services/proxy",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "ServiceProxyOptions",
                            "verbs": [
                                "create",
                                "delete",
                                "get",
                                "patch",
                                "update"
                            ]
                        },
                        {
                            "name": "services/status",
                            "singularName": "",
                            "namespaced": true,
                            "kind": "Service",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/api/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiCoordinationEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "coordination.k8s.io/v1",
                    "resources": [
                        {
                            "name": "leases",
                            "singularName": "lease",
                            "namespaced": true,
                            "kind": "Lease",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "storageVersionHash": "gqkMMb/YqFM="
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/coordination.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiNamespacesEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "NamespaceList",
                    "apiVersion": "v1",
                    "metadata": {
                        "resourceVersion": "14725"
                    },
                    "items": [
                        {
                            "metadata": {
                                "name": "default",
                                "uid": "a3be68c7-bb0f-4d14-9e03-76c1ddf93005",
                                "resourceVersion": "67",
                                "creationTimestamp": "2025-07-20T21:56:19Z",
                                "labels": {
                                    "kubernetes.io/metadata.name": "default"
                                },
                                "managedFields": [
                                    {
                                        "manager": "kube-apiserver",
                                        "operation": "Update",
                                        "apiVersion": "v1",
                                        "time": "2025-07-20T21:56:19Z",
                                        "fieldsType": "FieldsV1",
                                        "fieldsV1": {
                                            "f:metadata": {
                                                "f:labels": {
                                                    ".": {},
                                                    "f:kubernetes.io/metadata.name": {}
                                                }
                                            }
                                        }
                                    }
                                ]
                            },
                            "spec": {
                                "finalizers": [
                                    "kubernetes"
                                ]
                            },
                            "status": {
                                "phase": "Active"
                            }
                        },
                        {
                            "metadata": {
                                "name": "kube-node-lease",
                                "uid": "92b6bbc4-c323-4eb0-95ed-62e98e826c38",
                                "resourceVersion": "87",
                                "creationTimestamp": "2025-07-20T21:56:19Z",
                                "labels": {
                                    "kubernetes.io/metadata.name": "kube-node-lease"
                                },
                                "managedFields": [
                                    {
                                        "manager": "kube-apiserver",
                                        "operation": "Update",
                                        "apiVersion": "v1",
                                        "time": "2025-07-20T21:56:19Z",
                                        "fieldsType": "FieldsV1",
                                        "fieldsV1": {
                                            "f:metadata": {
                                                "f:labels": {
                                                    ".": {},
                                                    "f:kubernetes.io/metadata.name": {}
                                                }
                                            }
                                        }
                                    }
                                ]
                            },
                            "spec": {
                                "finalizers": [
                                    "kubernetes"
                                ]
                            },
                            "status": {
                                "phase": "Active"
                            }
                        },
                        {
                            "metadata": {
                                "name": "kube-public",
                                "uid": "107c0a32-ecf1-4625-9f46-70c3f5e6a087",
                                "resourceVersion": "65",
                                "creationTimestamp": "2025-07-20T21:56:19Z",
                                "labels": {
                                    "kubernetes.io/metadata.name": "kube-public"
                                },
                                "managedFields": [
                                    {
                                        "manager": "kube-apiserver",
                                        "operation": "Update",
                                        "apiVersion": "v1",
                                        "time": "2025-07-20T21:56:19Z",
                                        "fieldsType": "FieldsV1",
                                        "fieldsV1": {
                                            "f:metadata": {
                                                "f:labels": {
                                                    ".": {},
                                                    "f:kubernetes.io/metadata.name": {}
                                                }
                                            }
                                        }
                                    }
                                ]
                            },
                            "spec": {
                                "finalizers": [
                                    "kubernetes"
                                ]
                            },
                            "status": {
                                "phase": "Active"
                            }
                        },
                        {
                            "metadata": {
                                "name": "kube-system",
                                "uid": "51d5a21b-9329-47b5-8798-c1a8c968993a",
                                "resourceVersion": "10",
                                "creationTimestamp": "2025-07-20T21:56:19Z",
                                "labels": {
                                    "kubernetes.io/metadata.name": "kube-system"
                                },
                                "managedFields": [
                                    {
                                        "manager": "kube-apiserver",
                                        "operation": "Update",
                                        "apiVersion": "v1",
                                        "time": "2025-07-20T21:56:19Z",
                                        "fieldsType": "FieldsV1",
                                        "fieldsV1": {
                                            "f:metadata": {
                                                "f:labels": {
                                                    ".": {},
                                                    "f:kubernetes.io/metadata.name": {}
                                                }
                                            }
                                        }
                                    }
                                ]
                            },
                            "spec": {
                                "finalizers": [
                                    "kubernetes"
                                ]
                            },
                            "status": {
                                "phase": "Active"
                            }
                        },
                        {
                            "metadata": {
                                "name": "local-path-storage",
                                "uid": "b06ca092-00c4-46af-b832-74a973a721e1",
                                "resourceVersion": "306",
                                "creationTimestamp": "2025-07-20T21:56:23Z",
                                "labels": {
                                    "kubernetes.io/metadata.name": "local-path-storage"
                                },
                                "annotations": {
                                    "kubectl.kubernetes.io/last-applied-configuration": "{\\"apiVersion\\":\\"v1\\",\\"kind\\":\\"Namespace\\",\\"metadata\\":{\\"annotations\\":{},\\"name\\":\\"local-path-storage\\"}}\\n"
                                },
                                "managedFields": [
                                    {
                                        "manager": "kubectl-client-side-apply",
                                        "operation": "Update",
                                        "apiVersion": "v1",
                                        "time": "2025-07-20T21:56:23Z",
                                        "fieldsType": "FieldsV1",
                                        "fieldsV1": {
                                            "f:metadata": {
                                                "f:annotations": {
                                                    ".": {},
                                                    "f:kubectl.kubernetes.io/last-applied-configuration": {}
                                                },
                                                "f:labels": {
                                                    ".": {},
                                                    "f:kubernetes.io/metadata.name": {}
                                                }
                                            }
                                        }
                                    }
                                ]
                            },
                            "spec": {
                                "finalizers": [
                                    "kubernetes"
                                ]
                            },
                            "status": {
                                "phase": "Active"
                            }
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/api/v1/namespaces?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiApiRegistrationEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "apiregistration.k8s.io/v1",
                    "resources": [
                        {
                            "name": "apiservices",
                            "singularName": "apiservice",
                            "namespaced": false,
                            "kind": "APIService",
                            "verbs": [
                                "create",
                                "delete",
                                "deletecollection",
                                "get",
                                "list",
                                "patch",
                                "update",
                                "watch"
                            ],
                            "categories": [
                                "api-extensions"
                            ],
                            "storageVersionHash": "InPBPD7+PqM="
                        },
                        {
                            "name": "apiservices/status",
                            "singularName": "",
                            "namespaced": false,
                            "kind": "APIService",
                            "verbs": [
                                "get",
                                "patch",
                                "update"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/apiregistration.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiAuthenticationEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "APIResourceList",
                    "apiVersion": "v1",
                    "groupVersion": "authentication.k8s.io/v1",
                    "resources": [
                        {
                            "name": "selfsubjectreviews",
                            "singularName": "selfsubjectreview",
                            "namespaced": false,
                            "kind": "SelfSubjectReview",
                            "verbs": [
                                "create"
                            ]
                        },
                        {
                            "name": "tokenreviews",
                            "singularName": "tokenreview",
                            "namespaced": false,
                            "kind": "TokenReview",
                            "verbs": [
                                "create"
                            ]
                        }
                    ]
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/apis/authentication.k8s.io/v1?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addApiDefaultNamespaceEndpoint(final KubernetesMockServer server) {
        final String response = """
                {
                    "kind": "Namespace",
                    "apiVersion": "v1",
                    "metadata": {
                        "name": "default",
                        "uid": "a3be68c7-bb0f-4d14-9e03-76c1ddf93005",
                        "resourceVersion": "67",
                        "creationTimestamp": "2025-07-20T21:56:19Z",
                        "labels": {
                            "kubernetes.io/metadata.name": "default"
                        },
                        "managedFields": [
                            {
                                "manager": "kube-apiserver",
                                "operation": "Update",
                                "apiVersion": "v1",
                                "time": "2025-07-20T21:56:19Z",
                                "fieldsType": "FieldsV1",
                                "fieldsV1": {
                                    "f:metadata": {
                                        "f:labels": {
                                            ".": {},
                                            "f:kubernetes.io/metadata.name": {}
                                        }
                                    }
                                }
                            }
                        ]
                    },
                    "spec": {
                        "finalizers": [
                            "kubernetes"
                        ]
                    },
                    "status": {
                        "phase": "Active"
                    }
                }""";

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/api/v1/namespaces/default?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addSelfSubjectAccessReviewsEndpoint(final KubernetesMockServer server) {
            server.expect()
                    .post()
                    .withPath("/apis/authorization.k8s.io/v1/selfsubjectaccessreviews")
                    .andReply(new ResponseProvider() {

                        @Override
                        public Object getBody(final RecordedRequest request) {
                            // The body is a protobuf binary blob, so we just print it as a string for debugging.
                            System.out.println(request.getBody().readString(StandardCharsets.UTF_8));
                            return null;
                        }

                        @Override
                        public int getStatusCode(final RecordedRequest request) {
                            return 404;
                        }

                        @Override
                        public Headers getHeaders() {
                            return Headers.builder().build();
                        }

                        @Override
                        public void setHeaders(Headers headers) {

                        }
                    })
                    .always();

    }

    private void addOpenApiV3Endpoint(final KubernetesMockServer server) {
        final String response = """
              {"paths":{".well-known/openid-configuration":{"serverRelativeURL":"/openapi/v3/.well-known/openid-configuration?hash=05A16556209A840BB72C2E4FEBD2F7A39FDFB81F27CFAA358B188BC6D22272D5F21E7C57989EA0F9B7CA0839C535F8410B3DBA8B5E5BD84951BC58E36070B630"},"api":{"serverRelativeURL":"/openapi/v3/api?hash=8EF5A2724EC4196D9BF9D2B01069C64B6100F8A335C0FABF7EF21BE2909E5521C9F10F0FCD65B2839E42917867059276ADABE111729DB3E267798748A2612A39"},"api/v1":{"serverRelativeURL":"/openapi/v3/api/v1?hash=13A652D4D75C2ABA714E4602AAC9D534F6AD8738AB2B3E6EA6A756D14FF29D7AA87E75E31497CFA79CB252770E266339863D4D1BF2D1BBAAD6D7840929EDDBF7"},"apis":{"serverRelativeURL":"/openapi/v3/apis?hash=9CD98EF5EA3A801DF08A84F2B3CCB50C2D356FA605467B3123CDC5122393EBE7A95FBF211F4A82607383620971611F008A41884C3BDE51FE4053E30DD6070079"},"apis/admissionregistration.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/admissionregistration.k8s.io?hash=C05EFE72F75FBA41459FE0871588A30544F2EF3A8D35647F8855E01408C784047D5D7EDC67C3CE3C0A661B9D2F68222E1F518A45E44D54D43AA576FAEEB55F17"},"apis/admissionregistration.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/admissionregistration.k8s.io/v1?hash=E125472FE914631D572995F835F0256A8323757AF3660501370CE93228E566AEBA2614576781BEC1E9F7BAD3AC8EE714D894C397AC8F5A0E1F78EAA3785536FC"},"apis/apiextensions.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/apiextensions.k8s.io?hash=FBB7CD44979E8E85760AC1B6A9211E12BB21206E4126AE6DA952AB4715AE822E563ADAA70D3A27A1328036926498E597BFBF86A1BBA4A6FC02127EC6B729085F"},"apis/apiextensions.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/apiextensions.k8s.io/v1?hash=11F374E76CFD2C431F9C5319F51F7F936535FF9D14BCC05C5DE5CDDCAC0A5E9A3AD389DA221BEF5068976631D29F3C9718C2D7E534BEC07E28734954461A74A3"},"apis/apiregistration.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/apiregistration.k8s.io?hash=1BDAD7ABE5A001863D0F1FFD51003784844D856F6C9A0F22526BCF92560944057142C2114B19DE0F8BF1B1B52B4DA486E6B827743BE74B20828B0115009D0794"},"apis/apiregistration.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/apiregistration.k8s.io/v1?hash=2325C26CE1BD5A8C214DC3137036247EDCB325C479B48569FEE90A279725140E55CE018ABF9D438C0BB4C0E4C784ACA643D002C066F502EF6AC0C8FCEB49FB3D"},"apis/apps":{"serverRelativeURL":"/openapi/v3/apis/apps?hash=4BD08389895F725478A408C8B38EE8DFC80CD047D8AD575D13C2951BFB4C3D74E268B9E81723027296F47B0304A61CD8E96D16E350655B388F3ED2CE1846538A"},"apis/apps/v1":{"serverRelativeURL":"/openapi/v3/apis/apps/v1?hash=0258D87672EC79003DA06605195D9D2D70342F500793A566484BF222B209BB3226067464C2433E2F9EAAFDCA22DB0BC49C7F7B39154CA67E29A40FA7CE1CEE65"},"apis/authentication.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/authentication.k8s.io?hash=4A958119AA6731FB8851F0AF08E6AE4FFCA6C3B5D3E0778D3BEC92AF04FDA7FA19560B83965A1E14DE26D501282661F823E210E063C23D91CADB0CA1215A3F28"},"apis/authentication.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/authentication.k8s.io/v1?hash=8E3B6E7164557C9ABB0702B5676A75110FFD91AFD21ACDBB597624F828629E7C926E0359E269BB5C459A06A9F7AC8F573D4B87862EF5C831A24482008097B3D0"},"apis/authorization.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/authorization.k8s.io?hash=335DC03432ADC77BDF2F820DC9ECE8D33107DA75AE10EDE7C4CF0559A9B1D67D38A7F7E51363D76380D631BD78F200267392E117A9C4B947F402494F500E5C5B"},"apis/authorization.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/authorization.k8s.io/v1?hash=48ED02BD159D89D0693295D5959383FD756F2D751C6CEECB0346FAD233F85E50849DD00F225ACB897D0560D335E9B43A5D47FC63F35A10269F31126E0B8F2D36"},"apis/autoscaling":{"serverRelativeURL":"/openapi/v3/apis/autoscaling?hash=C856D59378822B010F380E31BB2E73C215165EB550953B50E7E598B15D98591A4CA159BBE453F71A368EC49B7AD171BD8265B694D6233FCEB56CFC716A847319"},"apis/autoscaling/v1":{"serverRelativeURL":"/openapi/v3/apis/autoscaling/v1?hash=D4BB1F92C288A2A1B7353FF76408D004204AFAA5B399216CBB72E2363E8ABE8BBC26E66128C42BFE2F999AC4A23A97E5D9E2A63227F33F23566B7F68EE629D12"},"apis/autoscaling/v2":{"serverRelativeURL":"/openapi/v3/apis/autoscaling/v2?hash=B4FB2785F03F99712F9B4A89CA1575BE5023A8AA0C92C395AF17261090841E617431C32396CA61CFB0C6DE6323A2801D3D36EB36C56C90461F5D388CE062EFD6"},"apis/batch":{"serverRelativeURL":"/openapi/v3/apis/batch?hash=A1A823E17AD3592A2D38728C3D6682D43FB427F620D30E729612803EB1C0E061B603FC7F243953A736B825F5EE06F736FBC71582234420CFD1F0688F082889C8"},"apis/batch/v1":{"serverRelativeURL":"/openapi/v3/apis/batch/v1?hash=19EA9C2602C94BCB7C79B04B45AA95B61E2147F8613CB04A15B72CF24C112AA6D2F27E41786A78293B2EF39B9E5B1602E0EB437575CE4B05BE96D64681FE9B9F"},"apis/certificates.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/certificates.k8s.io?hash=C68B419687421AD65B634097FA16839E97E53741388BB71C4D8710E2F363856664493670566F16F840B4F374AC43813BC53B29620DA08D56E4E9B0118398CA57"},"apis/certificates.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/certificates.k8s.io/v1?hash=4A3D8D752D92357DA88A27D96047E78AE4CAFCB6DA7D8070361EEC3DCE123A50A26B5C4CD999C534C2330F5CE97006684DCDFA3F90419EEA1FABF01A8619E024"},"apis/coordination.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/coordination.k8s.io?hash=C4505C9CC7F29494FD5515550C67CA1BEE62451851B6FD1EC049E61113B6010E5D7E366DB3FABE5EEEFAF8D972766C102D96B51B577D23A74D90B58E6C37BFA0"},"apis/coordination.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/coordination.k8s.io/v1?hash=CB233425F0292426ED9395CC5222442D352BCECDA8C2639C4FF75A1DA9D740C94146CB6425C5FD3C16D595B358ADC4D25FFEFA174EF1E5BE802D9F4964ACFD32"},"apis/discovery.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/discovery.k8s.io?hash=AE137140EDA84314ECACD7F2DF2A7D95AD6958B8B20696D3CA20C3626548097296BF21EC717759D9F9D861E06CF04C0F38B7F20F3730DB349898BD5576C64903"},"apis/discovery.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/discovery.k8s.io/v1?hash=CDA3BCA78D293E0E87DAF8324EB6034F56DD3CAE011559358EC7456994752D14FBAC328ED32A1F542F278D2D200000BEB2B7DEDCFB8ACAE7678B7241764CF500"},"apis/events.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/events.k8s.io?hash=E01AD2F347E074450C5AF7F552A4B442E396F1B864F5B5BA5F00A442102AE0F65117AF1F9A25E9C2CE2BD05B8E11DA2162DDC6C9E34F976D4D02DB545D1F52E3"},"apis/events.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/events.k8s.io/v1?hash=5785E322A3D97D11B689C15FF749927EB2808DCD3C8BE492271824A95EDCC3B4657E9C2016EA11E32C721F105D5AFBDEC2ECA9B268B1ECDE28690737E6BE826F"},"apis/flowcontrol.apiserver.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/flowcontrol.apiserver.k8s.io?hash=6BBC4DB5B77989D935EAC9BCB1E22E5E84DB182B4FFB532A3FD0A8AA6A7CF5F86F06E15CA5078681A01D18A601A3A8E4F796AE027C209E6CB5869083C5709C44"},"apis/flowcontrol.apiserver.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/flowcontrol.apiserver.k8s.io/v1?hash=ED74AAA5A839CDC47C36A8DB2989D74EC6416ECD319F994FFC1FB6F9992D0926716F2E9A430C1E441AEDAD40C1422757BBCE6731C7EAA3BADBDE1D9509C7B74B"},"apis/flowcontrol.apiserver.k8s.io/v1beta3":{"serverRelativeURL":"/openapi/v3/apis/flowcontrol.apiserver.k8s.io/v1beta3?hash=C069F0FF8FDEBE6FA9896F8D363C3D574E4BC485AC52CE0F2570F7487EC600963D768B4737E924B06FDC7BBBA555A565E69721BF667198B23EBF8455889854E9"},"apis/networking.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/networking.k8s.io?hash=2B6C678E72AD7847A8DDAF51C78D626DA060318C826FBEE992A136116DDDD4A0FBE5BE5B18FC5E2A1A8EE3E39AF414A1A07C09EB1E08FC3740662149E7762FBF"},"apis/networking.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/networking.k8s.io/v1?hash=B8285F2FC6B7C7B6B1EB7FEF00C5D3DF185BCF453599102D22CCAB706965D3B5AF7521BD76699909032C01857CF0C6D642F6A745A31553660C38254FA2D61227"},"apis/node.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/node.k8s.io?hash=ECF9560AC5D4C6BF2A5240577D3F52B11AC7DC742C5FCDB1C7514C79E831632798036AA422F2CA502E73DB93D517821778C3ACFCCD59E505B56175DBF5856B86"},"apis/node.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/node.k8s.io/v1?hash=D54FA97C0E7008D72F5D9A4BB123B3E3B50339341F1F2353AEC1E5F79D8E7341E571BCABCBB6FF40413AAE217BC25F2105643C51B2688709C3E90A92A3605FBF"},"apis/policy":{"serverRelativeURL":"/openapi/v3/apis/policy?hash=94DA596BD07C557D3BD8F8306E860B999F13B3FA007F139FC79030F07278FB0E34FC5DC7843E8E9EA57A9940255FA14DFF69FBD8B0E42A29ECCC0E4AE059703B"},"apis/policy/v1":{"serverRelativeURL":"/openapi/v3/apis/policy/v1?hash=AB463D581E71F1D8DFD19FB5A3FE9B8BCC06C2AECD83D63FCF0EE20DD893DBEE5A57E11383A1F862186307BE41A25FC06E97DE0FA0617B182AD1C731B14C0EDD"},"apis/rbac.authorization.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/rbac.authorization.k8s.io?hash=37C8159694FB98C95776DEC508BB6B5107792E7D6A6FDE68D8C65FB48B671DA7B6CEBF028D30E2C3AE1D8B9DF250C19AFCBA4D7D35C6A6280DAC4C0AEC029698"},"apis/rbac.authorization.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/rbac.authorization.k8s.io/v1?hash=F10EAD0C3A9ABB4ADA3B082B29CA00DD3B32F2D6B6A948F0AA8D1D432D584369848A92844BC750BC008766E04EAC57ACD193FC4A1DC427BC95EA2D071D3FFD5C"},"apis/scheduling.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/scheduling.k8s.io?hash=5589B21432C9CA8AF5128FCE35742FC63065EDEADD2A742F45EF1DCA4DE80D8ED860A7F441FA8B5804E451C5F216AF7C23D2C697869CEAC852BE1D5C91302AF9"},"apis/scheduling.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/scheduling.k8s.io/v1?hash=F2E4201665C196EE119FA7A0C137ACC7ACD830687B892F142DFC06A290EB4004EC707FF64FBEAB28EC2736568881027E922BFC993F934C3BDF8B38DAE2B5394D"},"apis/storage.k8s.io":{"serverRelativeURL":"/openapi/v3/apis/storage.k8s.io?hash=551432BE89CFE855A8C573BE49D98E69C48917E7BBC65E21228F29FD5472E40D2DFAC3C4B953F160526ABB267E2813647E8AF5A8192214FCB87218272568EA7B"},"apis/storage.k8s.io/v1":{"serverRelativeURL":"/openapi/v3/apis/storage.k8s.io/v1?hash=15D0963A4F3192C60770CF20DD738AAA56DBAB051DCE1ECBBE84E2F98AEF3406516E768235D24890F2A14D49EA8D86EC276A1A97085EA8F91E8ED8A98D75F2E6"},"openid/v1/jwks":{"serverRelativeURL":"/openapi/v3/openid/v1/jwks?hash=7B056FFC14F2C5086DCC9D525DACB698EA04B337AADAD1856C44A6B0506C3910EDF4E9E0CCCDF215E52155463378A28A655F9560411E30FDE7D1173AA9751813"},"version":{"serverRelativeURL":"/openapi/v3/version?hash=111657D6E117962A3A92C1BE7BA726C9190440B9CD412101FE20F4BF338AE5E3E78EEF37408521A015B4844C683945774EC7AF18F5B9C9DB29C7A655DAD3AB42"}}}
              """;

        for (String timeout : TIMEOUTS) {
            server.expect()
                    .get()
                    .withPath("/openapi/v3?timeout=" + timeout)
                    .andReturn(200, response.stripIndent())
                    .always();
        }
    }

    private void addOpenApiV3HashEndpoint(final KubernetesMockServer server) {
        try (InputStream stream = Server.class.getResourceAsStream("/openapiv3.json")) {
            final InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            final BufferedReader reader = new BufferedReader(streamReader);
            final String json = reader.lines().collect(Collectors.joining("\n"));

            for (String timeout : TIMEOUTS) {
                server.expect()
                        .get()
                        .withPath("/openapi/v3/apis/apps/v1?hash=0258D87672EC79003DA06605195D9D2D70342F500793A566484BF222B209BB3226067464C2433E2F9EAAFDCA22DB0BC49C7F7B39154CA67E29A40FA7CE1CEE65&timeout=" + timeout)
                        .andReturn(200, json)
                        .always();
            }
        } catch (IOException e) {
            for (String timeout : TIMEOUTS) {
                server.expect()
                        .get()
                        .withPath("/openapi/v3/apis/apps/v1?hash=0258D87672EC79003DA06605195D9D2D70342F500793A566484BF222B209BB3226067464C2433E2F9EAAFDCA22DB0BC49C7F7B39154CA67E29A40FA7CE1CEE65&timeout=" + timeout)
                        .andReturn(500, "")
                        .always();
            }
        }
    }
}
