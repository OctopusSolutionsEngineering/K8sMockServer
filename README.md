This project implements a mock Kubernetes API using the [Fabric8 Mock Kubernetes Server](https://github.com/fabric8io/kubernetes-client?tab=readme-ov-file#mocking-kubernetes). This allows an Octopus project to complete a real Kubernetes deployment without requiring a real Kubernetes cluster.

The mock server is embedded in an Octopus Container Image. The server is started when `kubectl` is called, and is destroyed once `kubectl` exits.

Because of this, the Kubernetes server appears to be blank with each step run by Octopus.

# Target Configuration

* URL: `http://localhost:48080`
* Health Check Container Image: `octopussolutionsengineering/k8s-mockserver` from a docker feed pointing to `ghcr.io`.

# Project Configuration

* Add the `Octopus.Action.Container.Options` variable and set the value to `-v /tmp:/tmp`. This mounts the temporary directory of the worker into the container. 
* The mock server will return 200 OK for the endpoint `http://localhost:48080/health` if the file `/tmp/online` exists. If the file does not exist, it will return 500.
* Steps can be added that check the return code of the endpoint `http://localhost:48080/health` to simulate a smoke test. These steps will succeed for fail based on the existence of the `/tmp/online` file.

This is an example of a smoke test step that checks the health of the mock server:

```bash
CODE=$(curl --silent --output /dev/null --write-out "%{http_code}" "http://localhost:48080/health")
if [[ $CODE != "200" ]]
then
  exit 1
fi
```

This is an example of a step that creates the online marker file:

```bash
touch /tmp/online
```

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
