package future;

import java.util.EventListener;
import java.util.concurrent.Future;

public interface FutureListener<F extends Future> extends EventListener {

    void  operationComplete(F f);

}
