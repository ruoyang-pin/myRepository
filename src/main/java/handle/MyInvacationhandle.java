package handle;

import interfaces.Hinterface;
import interfaces.Rinterface;
import pojo.Hello;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvacationhandle implements InvocationHandler {
    private Hello hello;


    public MyInvacationhandle(Hello h) {
        hello = h;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("代理对象执行了");
        method.invoke(hello, args);
        return null;
    }
}
