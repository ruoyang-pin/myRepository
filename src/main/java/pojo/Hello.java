package pojo;

import interfaces.Hinterface;
import interfaces.Rinterface;

public class Hello implements Hinterface, Rinterface {
    @Override
    public void sayHello() {
        System.out.println("���");
    }

    @Override
    public void sayGoodbye() {
         System.out.println("�ݰ�");
    }
}
