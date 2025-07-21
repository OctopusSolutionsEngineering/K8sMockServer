This project implements a mock Kubernetes API using the Fabric8 Mock Kubernetes Server.

Just enough of the API is implemented to allow a deployment from Octopus to succeed. However, the following setting must be enabled:

* `Kubernetes Object Status Check`: `Don't do any verification checks`
* `Server-Side Apply`: `Use client-side apply`