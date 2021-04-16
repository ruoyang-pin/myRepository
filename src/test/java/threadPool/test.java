package threadPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class test {

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 100, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(2), new ThreadPoolExecutor.CallerRunsPolicy());

        for (int i = 0; i <100 ; i++) {
            final int s=i;
            threadPoolExecutor.execute(()-> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        threadPoolExecutor.shutdown();
        System.out.println(342);


    }

















}
