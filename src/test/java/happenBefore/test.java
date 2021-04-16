package happenBefore;

import org.junit.Test;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class test {
    boolean flag=false;


    @Test
    public void  test(){
        CopyOnWriteArrayList<Object> objects = new CopyOnWriteArrayList<>();

        new Thread(()->{
             flag=true;
        }).start();
         new Thread(()->{
           if(flag)
               System.out.println("正确");
           else
               System.out.println("错误");
        }).start();



    }






}
