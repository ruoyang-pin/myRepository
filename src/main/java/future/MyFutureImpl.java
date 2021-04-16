package future;


import io.netty.util.internal.ConcurrentSet;
import sun.misc.Unsafe;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

public class MyFutureImpl<V> implements MyFuture<V> {

    private  volatile   List<FutureListener>  listeners=new CopyOnWriteArrayList<>();
    private  volatile   Set<Thread>  waits=new ConcurrentSet<>();
    private  V result;

    private MyFutureImpl(){}

    @Override
    public void setResult(V result) {
        this.result = result;
        listeners.forEach(listener->listener.operationComplete(this));
        synchronized (this){
             this.notifyAll();
        }
    }

    public  static MyFuture  getInstance(){
        return   new MyFutureImpl();
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return result!=null;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
       synchronized (this){
           if(result==null)
               this.wait();
       }
        return result;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }



    @Override
    public void addListener(FutureListener listener) {
        listeners.add(listener);
    }
}
