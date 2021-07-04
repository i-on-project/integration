This **error** will occur in Integration's Web API associated with the status code [400: Bad Request](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/400) and signals that either an expected argument is missing or incorrect.

The response body will be in the `application/json+problem` mediatype and its `detail` field will include more context-specific information about the error cause.