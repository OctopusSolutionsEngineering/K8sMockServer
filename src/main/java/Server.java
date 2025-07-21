import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {
    public Server() {

    }

    public void start() throws UnknownHostException {
        final KubernetesMockServer server = new KubernetesMockServer(false);
        addVersionEndpoint(server);
        addApiEndpoint(server);
        addApisEndpoint(server);
        final InetAddress address = InetAddress.getByName("localhost");
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

        server.expect()
                .get()
                .withPath("/api?timeout=1m0s")
                .andReturn(200, response.stripIndent())
                .always();

        server.expect()
                .get()
                .withPath("/api?timeout=32s")
                .andReturn(200, response.stripIndent())
                .always();
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

        server.expect()
                .get()
                .withPath("/apis?timeout=1m0s")
                .andReturn(200, response.stripIndent())
                .always();

        server.expect()
                .get()
                .withPath("/apis?timeout=32s")
                .andReturn(200, response.stripIndent())
                .always();
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

        server.expect()
                .get()
                .withPath("/version?timeout=32s")
                .andReturn(200, response.stripIndent())
                .always();

        server.expect()
                .get()
                .withPath("/version?timeout=1m0s")
                .andReturn(200, response.stripIndent())
                .always();
    }
}
