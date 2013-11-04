
public class WebServiceImplementation {
    private final String className;
    private final String endpointInterface;
    private final String wsdlLocation;
    private final String targetNamespace;
    private final String serviceName;
    private final String portName;
    
    public WebServiceImplementation(String className, String endpointInterface,
            String wsdlLocation, String targetNamespace, String serviceName,
            String portName) {
        this.className = className;
        this.endpointInterface = endpointInterface;
        this.wsdlLocation = wsdlLocation;
        this.targetNamespace = targetNamespace;
        this.serviceName = serviceName;
        this.portName = portName;
    }

    public String getClassName() {
        return className;
    }

    public String getEndpointInterface() {
        return endpointInterface;
    }

    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPortName() {
        return portName;
    }
}
