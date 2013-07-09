import javax.management.ObjectName;

public interface AdminService {
    ObjectName getServerMBean();
    
    ObjectName[] invoke(@Param(name="objectname") ObjectName objectName,
                        @Param(name="operationname") String operationName,
                        @Param(name="params") Object[] params,
                        @Param(name="signature") String[] signature);
}
