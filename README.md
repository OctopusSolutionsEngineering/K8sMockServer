This project implements a mock Kubernetes API using the Fabric8 Mock Kubernetes Server. This allows an Octopus project to complete a real Kubernetes deployment without requiring a real Kubernetes cluster.

Just enough of the API is implemented to allow a deployment from Octopus to succeed. However, the following setting must be enabled:

* `Kubernetes Object Status Check`: `Don't do any verification checks`
* `Server-Side Apply`: `Use client-side apply`

The mock server is embedded in an Octopus Container Image. The server is started when `kubectl` is called, and is destroyed once `kubectl` exits.

Because of this, the Kubernetes server appears to be blank with each step run by Octopus.