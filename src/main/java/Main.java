import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(final String[] args) throws UnknownHostException {
        final KubernetesMockServer server = new KubernetesMockServer();
        final InetAddress address = InetAddress.getByName("localhost");
        server.init(address, 48433);
    }
}
