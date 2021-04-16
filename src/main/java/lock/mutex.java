package lock;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class mutex implements Lock {

    private Sync sync;

    public mutex() {
        sync = new Sync();
    }

    @Override
    public void lock() {

        sync.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }


    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }


    final static class Sync extends AbstractQueuedSynchronizer {
        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        @Override
        protected final boolean tryAcquire(int arg) {

            final Thread current = Thread.currentThread();
            final int c = getState();
            if (c == 0 && compareAndSetState(0, arg)) {
                setExclusiveOwnerThread(current);
                return true;
            } else if (Thread.currentThread()==getExclusiveOwnerThread()) {
                int newState=c + arg;
                assert newState>0;
                setState(newState);
                return true;
            }
            return false;
        }

        @Override
        protected final boolean tryRelease(int arg) {
            boolean fee = false;
            if (Thread.currentThread()!=getExclusiveOwnerThread())
                 throw  new IllegalMonitorStateException();
            int newState = getState() - arg;
            if (newState == 0) {
                setExclusiveOwnerThread(null);
                fee = true;
            }else  if(newState<0)
                throw  new IllegalMonitorStateException();
            setState(newState);
            return fee;
        }

        @Override
        protected boolean isHeldExclusively() {
            return getState()==1;
        }


    }


}
