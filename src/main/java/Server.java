import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {
    private static final String[] TIMEOUTS = {
            "1m0s",
            "32s"
    };

    public Server() {

    }

    public void start() throws UnknownHostException {
        final KubernetesMockServer server = new KubernetesMockServer(false);
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
        final InetAddress address = InetAddress.getByName("0.0.0.0");
        server.init(address, 48080);
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
        // I'm not too sure about the response structure - this was what chatgpt provided
        final String response = """
                {
                    "kind": "SelfSubjectAccessReview",
                    "apiVersion": "authorization.k8s.io/v1",
                    "spec": {
                        "resourceAttributes": {
                            "namespace": "default",
                            "verb": "get",
                            "resource": "pods"
                        }
                    },
                    "status": {
                        "allowed": true,
                        "reason": "User has permission to get pods in the default namespace."
                    }
                }""";

            server.expect()
                    .post()
                    .withPath("/apis/authorization.k8s.io/v1/selfsubjectaccessreviews")
                    .andReturn(200, response.stripIndent())
                    .always();

    }
}
