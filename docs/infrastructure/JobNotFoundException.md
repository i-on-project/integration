This **error** will occur in Integration's Web API associated with the status code [404: Not Found](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/404) and indicates that the request Job ID does not exist.

The response body will be in the `application/json+problem` mediatype and its `detail` field will include the given Job ID. The `instance` field will contain the requested path.