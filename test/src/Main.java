import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.AdminServiceFactory;

public class Main {
    public static void main(String[] args) throws Exception {
        AdminService adminService = AdminServiceFactory.getInstance().createAdminService();
        ObjectName serverMBean = adminService.getServerMBean();
        System.out.println(adminService.invoke(serverMBean, "getPid", new Object[] { }, new String[] { }));
    }
}
