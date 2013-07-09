import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;

public class Main {
    public static void main(String[] args) throws Exception {
        Map<Method,OperationHandler> operationHandlers = new HashMap<>();
        for (Method method : AdminService.class.getMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            int paramCount = parameterTypes.length;
            ParamHandler[] paramHandlers = new ParamHandler[paramCount];
            for (int i=0; i<paramCount; i++) {
                Class<?> type = parameterTypes[i];
                Annotation[] annotations = parameterAnnotations[i];
                Param paramAnnotation = null;
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Param) {
                        paramAnnotation = (Param)annotation;
                        break;
                    }
                }
                String name = paramAnnotation.name();
                paramHandlers[i] = new ParamHandler(name, getValueHandler(type));
            }
            String methodName = method.getName();
            operationHandlers.put(method, new OperationHandler(methodName, methodName + "Response", paramHandlers, getValueHandler(method.getReturnType())));
        }
        AdminService adminService = (AdminService)Proxy.newProxyInstance(Main.class.getClassLoader(), new Class<?>[] { AdminService.class }, new AdminServiceInvocationHandler(operationHandlers));
        ObjectName serverMBean = adminService.getServerMBean();
        System.out.println(adminService.invoke(serverMBean, "getPid", new Object[] { }, new String[] { }));
    }
    
    private static ValueHandler getValueHandler(Class<?> javaType) {
        if (javaType == String.class) {
            return StringValueHandler.INSTANCE;
        } else {
            return new ObjectValueHandler(javaType);
        }
    }
}
