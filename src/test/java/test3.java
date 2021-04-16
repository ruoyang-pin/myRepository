import com.google.common.collect.Maps;
import future.MyFuture;
import future.MyFutureImpl;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class test3 {




    @Test
    public void test() throws ExecutionException, InterruptedException {
          MyFuture<Integer>  myFuture= MyFutureImpl.getInstance();
          myFuture.addListener((future)->{
              if(future.isDone()) {
                  try {
                      System.out.println(future.get());
                  } catch (Exception e) {

              }
              }else
                  System.out.println("失败");
          });
          myFuture.addListener((future)->{
              if(future.isDone()) {
                  try {
                      System.out.println("真的完成了");
                  } catch (Exception e) {

              }
              }else
                  System.out.println("失败");
          });
          myFuture.setResult(2);
    }

    public static void main(String[] args) {
        Map<String,Integer> map= Maps.newHashMap();
        Optional<Integer> integer = map.values()
                .stream()
                .max(Integer::compare);



    }






}
