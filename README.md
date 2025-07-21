This project implements a mock Kubernetes API using the Fabric8 Mock Kubernetes Server. This allows an Octopus project to complete a real Kubernetes deployment without requiring a real Kubernetes cluster.

The mock server is embedded in an Octopus Container Image. The server is started when `kubectl` is called, and is destroyed once `kubectl` exits.

Because of this, the Kubernetes server appears to be blank with each step run by Octopus.

# Target Configuration

* URL: `http://localhost:48080`
* Health Check Container Image: `octopussolutionsengineering/k8s-mockserver` from a docker feed pointing to `ghcr.io`.

# Step Configuration

* Container Image: `octopussolutionsengineering/k8s-mockserver` from a docker feed pointing to `ghcr.io`.
* `Kubernetes Object Status Check`: `Don't do any verification checks`
* `Server-Side Apply`: `Use client-side apply`

# Local Testing

Create a Kubernetes configuration file with the contents:

```yaml
apiVersion: v1
kind: Config
clusters:
- name: local
  cluster:
    server: http://localhost:48080
    insecure-skip-tls-verify: true
contexts:
- name: local-context
  context:
    cluster: local
    user: local-user
current-context: local-context
users:
- name: local-user
  user:
    username: admin
    password: admin123
```

Build the mock server with:

```bash
mvn clean package -DskipTests
```

Run the mock server with:

```bash
java -jar target/mockk8s.jar
```

Then run `kubectl` commands against the mock server:

```bash
kubectl run nginx --image=nginx
```

Get the list of pods:

```bash
kubectl get pods
```