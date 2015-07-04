To deploy the JARs for a new VisualVM version, use the following procedure:

*   Clone the repository and check out the `mvn-repo` branch.

*   Download and uncompress the VisualVM distribution.

*   Execute the following command:

        mvn org.codehaus.mojo:nb-repository-plugin:1.3:populate -DdeployUrl=file:///path/to/cloned/repo/ \
          -DnetbeansInstallDirectory=/path/to/visualvm/install -DforcedVersion=VISUALVMxxx \
          -DnetbeansNbmDirectory=/path/to/empty/directory

*   Add and commit the new files.
