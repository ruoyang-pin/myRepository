package dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyDynamicProxyHandle implements InvocationHandler {

    private Object  target;

    public MyDynamicProxyHandle(Object  target){
        this.target=target;
    }




    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(target,args);
    }
}
