import java.util.concurrent.atomic.AtomicInteger;

public class test1 {

    public static void main(String[] args) throws InterruptedException {


        find(100);



    }

    public static void find(int n) throws InterruptedException {

         synchronized (test1.class){
             test1.class.wait();



         }
            test1.class.notify();


    }





}
