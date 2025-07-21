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
