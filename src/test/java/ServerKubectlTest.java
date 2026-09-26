import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end test that drives the mock server with the real kubectl binary: it starts the server on a free
 * port, then shells out to kubectl to create a pod and read it back. If kubectl is not on the PATH (or
 * KUBECTL_BIN), the test is skipped rather than failed, since this sandbox does not vendor kubectl.
 */
public class ServerKubectlTest {
    private static final String KUBECTL_BIN = System.getenv().getOrDefault("KUBECTL_BIN", "kubectl");
    private static final String POD_NAME = "test-pod";
    private static final String ROLLOUT_NAME = "rollouts-demo";
    private static final String NAMESPACE = "default";

    private final Server server = new Server();

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void kubectlCanCreateAndGetAPod() throws Exception {
        assumeKubectlIsAvailable();

        final int port = findFreePort();
        server.start(port);

        final Path kubeconfig = writeKubeconfig(port);
        final Path podManifest = writePodManifest();

        // "create" (rather than "apply") avoids kubectl fetching the OpenAPI v3 schema for validation, which
        // the mock server cannot serve for arbitrary hashed URLs.
        final ProcessResult create = runKubectl(kubeconfig,
                "create", "-f", podManifest.toString(), "--validate=false");
        assertEquals(0, create.exitCode, "kubectl create failed: " + create.stderr);

        final ProcessResult get = runKubectl(kubeconfig,
                "get", "pod", POD_NAME, "-n", NAMESPACE, "-o", "json");
        assertEquals(0, get.exitCode, "kubectl get failed: " + get.stderr);

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode pod = mapper.readTree(get.stdout);
        assertEquals("Pod", pod.get("kind").asText());
        assertEquals(POD_NAME, pod.get("metadata").get("name").asText());
    }

    /**
     * Verifies the argoproj.io/v1alpha1 discovery support added for Argo Rollouts: without it, kubectl fails
     * client-side with "no matches for kind \"Rollout\" in version \"argoproj.io/v1alpha1\"" before ever
     * reaching the mock server.
     */
    @Test
    public void kubectlCanCreateAndGetARollout() throws Exception {
        assumeKubectlIsAvailable();

        final int port = findFreePort();
        server.start(port);

        final Path kubeconfig = writeKubeconfig(port);
        final Path rolloutManifest = writeRolloutManifest();

        final ProcessResult create = runKubectl(kubeconfig,
                "create", "-f", rolloutManifest.toString(), "--validate=false");
        assertEquals(0, create.exitCode, "kubectl create failed: " + create.stderr);

        final ProcessResult get = runKubectl(kubeconfig,
                "get", "rollout", ROLLOUT_NAME, "-n", NAMESPACE, "-o", "json");
        assertEquals(0, get.exitCode, "kubectl get failed: " + get.stderr);

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode rollout = mapper.readTree(get.stdout);
        assertEquals("argoproj.io/v1alpha1", rollout.get("apiVersion").asText());
        assertEquals("Rollout", rollout.get("kind").asText());
        assertEquals(ROLLOUT_NAME, rollout.get("metadata").get("name").asText());
    }

    private void assumeKubectlIsAvailable() {
        try {
            final Process process = new ProcessBuilder(KUBECTL_BIN, "version", "--client")
                    .redirectErrorStream(true)
                    .start();
            process.waitFor(10, TimeUnit.SECONDS);
        } catch (final IOException e) {
            Assumptions.abort("kubectl (" + KUBECTL_BIN + ") is not available; skipping test");
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            Assumptions.abort("Interrupted while checking for kubectl");
        }
    }

    private int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private Path writeKubeconfig(final int port) throws IOException {
        final String config = """
                apiVersion: v1
                kind: Config
                clusters:
                - name: mock
                  cluster:
                    server: http://127.0.0.1:%d
                contexts:
                - name: mock
                  context:
                    cluster: mock
                    namespace: %s
                current-context: mock
                """.formatted(port, NAMESPACE);

        final Path path = Files.createTempFile("kubeconfig", ".yaml");
        path.toFile().deleteOnExit();
        Files.writeString(path, config, StandardCharsets.UTF_8);
        return path;
    }

    private Path writePodManifest() throws IOException {
        final String manifest = """
                apiVersion: v1
                kind: Pod
                metadata:
                  name: %s
                  namespace: %s
                spec:
                  containers:
                  - name: test-container
                    image: nginx:latest
                """.formatted(POD_NAME, NAMESPACE);

        final Path path = Files.createTempFile("pod", ".yaml");
        path.toFile().deleteOnExit();
        Files.writeString(path, manifest, StandardCharsets.UTF_8);
        return path;
    }

    private Path writeRolloutManifest() throws IOException {
        final String manifest = """
                apiVersion: argoproj.io/v1alpha1
                kind: Rollout
                metadata:
                  name: %s
                  namespace: %s
                spec:
                  replicas: 1
                  selector:
                    matchLabels:
                      app: rollouts-demo
                  template:
                    metadata:
                      labels:
                        app: rollouts-demo
                    spec:
                      containers:
                      - name: rollouts-demo
                        image: argoproj/rollouts-demo:blue
                  strategy:
                    canary:
                      steps:
                      - setWeight: 20
                      - pause: {}
                """.formatted(ROLLOUT_NAME, NAMESPACE);

        final Path path = Files.createTempFile("rollout", ".yaml");
        path.toFile().deleteOnExit();
        Files.writeString(path, manifest, StandardCharsets.UTF_8);
        return path;
    }

    private ProcessResult runKubectl(final Path kubeconfig, final String... args) throws IOException, InterruptedException {
        final String[] command = new String[args.length + 2];
        command[0] = KUBECTL_BIN;
        command[1] = "--kubeconfig=" + kubeconfig;
        System.arraycopy(args, 0, command, 2, args.length);

        final Process process = new ProcessBuilder(command).start();
        final String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        final String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        final boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        assertTrue(finished, "kubectl did not exit within 30 seconds: " + String.join(" ", command));

        return new ProcessResult(process.exitValue(), stdout, stderr);
    }

    private static final class ProcessResult {
        private final int exitCode;
        private final String stdout;
        private final String stderr;

        private ProcessResult(final int exitCode, final String stdout, final String stderr) {
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
        }
    }
}
