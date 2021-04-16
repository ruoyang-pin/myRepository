package future;



import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface  MyFuture<V> extends Future<V> {


      void  addListener(FutureListener  listener);
      void setResult(V result);




}
