import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {
    public Server() {

    }

    public void start() throws UnknownHostException {
        final KubernetesMockServer server = new KubernetesMockServer(false);
        addVersionEndpoint(server);
        final InetAddress address = InetAddress.getByName("localhost");
        server.init(address, 48080);
    }

    private void addVersionEndpoint(final KubernetesMockServer server) {
        final String response = """
        {
            "major": "1",
            "minor": "31",
            "gitVersion": "v1.31.0",
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
    }
}
