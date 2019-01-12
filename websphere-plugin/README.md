To perform manual testing using the Websphere instance started during the integration tests:

*   Run `WEBSPHERE_VERSION=a.b.c.d mvn verify -Ddocker.follow`.

*   Use `docker ps` to determine the local port on which the SOAP connector is exposed (mapped to port 8880 in the container).

*   Connect using the following credentials:

    *   User: `administrator`
    *   Password: `changeme`

*   Use Ctrl-C to stop the WebSphere instance. No cleanup required.
