package dynamicProxy;

import org.junit.Test;
import pojo.Action;
import pojo.Person;


import java.lang.reflect.Proxy;

public class test {


    @Test
    public void test() {

        Action action = ( Action ) Proxy.newProxyInstance(Person.class.getClassLoader(), Person.class.getInterfaces(), new MyDynamicProxyHandle(new Person()));
        action.eat();
    }

    @Test
    public void test1() {
        Person person = new Person();
        person.eat();

    }


}
