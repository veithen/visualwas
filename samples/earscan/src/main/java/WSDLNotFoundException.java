
public class WSDLNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -743446950998260614L;
    
    private final String name;
    
    public WSDLNotFoundException(String name) {
        super(name + " not found");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
