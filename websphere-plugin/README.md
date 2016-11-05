To perform manual testing using the Websphere instance started during the integration tests:

*   Run `mvn verify -Ddocker.follow`.

*   Use `docker ps` to determine the local port on which the SOAP connector is exposed.

*   Connect using the following credentials:

    *   User: `wsadmin`
    *   Password: `abcd1234`

*   Use Ctrl-C to stop the WebSphere instance. No cleanup required.
