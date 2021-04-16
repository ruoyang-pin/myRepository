package lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class MyselfReadWriteLock implements ReadWriteLock {
    final Sync sync;
    private MyselfReadWriteLock.ReadLock  readLock;
    private MyselfReadWriteLock.WriteLock  writeLock;

    public MyselfReadWriteLock(){
        sync=new Sync();
        readLock=new ReadLock(sync);
        writeLock=new WriteLock(sync);
    }

    @Override
    public  Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }

    private static class Sync extends AbstractQueuedSynchronizer {
        static final long serialVersionUID = -3970548067209334309L;
        final static int SHARED_SHIFT = 16;
        final static int SHARED_UNIT = (1 << SHARED_SHIFT);
        final static int MAX_COUNT = (1 << SHARED_SHIFT) - 1;
        final static int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

        private transient MyselfReadWriteLock.Sync.ThreadLocalHoldCounter readHolds;
        private transient MyselfReadWriteLock.Sync.HoldCounter cachedHoldCounter;
        private transient Thread firstReader = null;
        private transient int firstReaderHoldCount;
        private static  Unsafe unsafe;
        private static  long TID_OFFSET;

        static {
            try {
                unsafe = getUnsafe();
                Class<?> tk = Thread.class;
                TID_OFFSET = unsafe.objectFieldOffset
                        (tk.getDeclaredField("tid"));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }


        Sync()  {
            readHolds = new MyselfReadWriteLock.Sync.ThreadLocalHoldCounter();
            setState(getState()); // ensures visibility of readHolds
        }


        static int sharedCount(int c) {
            return c >>> SHARED_SHIFT;
        }

        static int exclusiveCount(int c) {
            String string = Integer.toBinaryString(c);
            String string1 = Integer.toBinaryString(EXCLUSIVE_MASK);
            String string2 = Integer.toBinaryString(c & EXCLUSIVE_MASK);
            return c & EXCLUSIVE_MASK;
        }

        static final class HoldCounter {
            int count = 0;
            // Use id, not reference, to avoid garbage retention
            final long tid = getThreadId(Thread.currentThread());
        }

        static final class ThreadLocalHoldCounter extends ThreadLocal<MyselfReadWriteLock.Sync.HoldCounter> {
            public MyselfReadWriteLock.Sync.HoldCounter initialValue() {
                return new MyselfReadWriteLock.Sync.HoldCounter();
            }
        }


        @Override//写锁的获取
        protected boolean tryAcquire(int arg) {
            Thread currentThread = Thread.currentThread();
            int state = getState();
            int e = exclusiveCount(state);//写锁被占用数
            //此时读锁或者写锁被占用
            if (state != 0) {
                if (e == 0 || !isHeldExclusively())
                    return false;
                if (e + exclusiveCount(arg) > MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                setState(e + arg);
                return true;
            }
            if (!compareAndSetState(e, e + arg))
                return false;
            setExclusiveOwnerThread(currentThread);
            return true;
        }

        @Override//写锁的释放
        protected boolean tryRelease(int arg) {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            int c = getState() - exclusiveCount(arg);
            boolean fee = c == 0;
            if (fee)
                setExclusiveOwnerThread(null);
            setState(c);
            return fee;
        }

        @Override//读锁的获取
        protected int tryAcquireShared(int arg) {
            int c = getState();
            int e = exclusiveCount(c);
            Thread currentThread = Thread.currentThread();
            if (e != 0 && !isHeldExclusively())
                return -1;
            int r = sharedCount(c);
            if (r < MAX_COUNT && compareAndSetState(c, c + SHARED_UNIT)) {
                if (r == 0) {
                   firstReader=currentThread;
                   firstReaderHoldCount=1;
                }else if(firstReader==currentThread){
                   firstReaderHoldCount++;
                }else {
                   HoldCounter rh=cachedHoldCounter;
                   if(rh==null||rh.tid!=getThreadId(currentThread)){
                       cachedHoldCounter=rh=readHolds.get();
                   }else if(rh.count==0){
                       readHolds.set(rh);
                   }
                   rh.count++;
                }
                return 1;
            }
            return fullTryAcquireShared(currentThread);
        }

        @Override//读锁的释放
        protected boolean tryReleaseShared(int arg) {
             Thread current = Thread.currentThread();
             if(firstReader==current){
                 if(firstReaderHoldCount==1){
                     firstReader=null;
                 }else
                    firstReaderHoldCount--;

             }else {
                 HoldCounter rh = cachedHoldCounter;
                 if (rh == null || rh.tid != getThreadId(current))
                     rh = readHolds.get();
                 int count = rh.count;
                 if (count <= 1) {
                     readHolds.remove();
                     if (count <= 0)
                         throw unmatchedUnlockException();
                 }
                 --rh.count;
             }
             for(;;){
                 int state = getState();
                 int nextc=state-SHARED_UNIT;
                 if(compareAndSetState(state,nextc)){
                    return   nextc==0;
                 }
             }
        }

        @Override
        protected boolean isHeldExclusively() {
            return Thread.currentThread() == getExclusiveOwnerThread();
        }


        static final long getThreadId(Thread thread) {
            return unsafe.getLongVolatile(thread, TID_OFFSET);
        }


        static final Unsafe getUnsafe() throws Throwable {
            Class<?> unsafeClass = Unsafe.class;
            for (Field f : unsafeClass.getDeclaredFields()) {
                if ("theUnsafe".equals(f.getName())) {
                    f.setAccessible(true);
                    return ( Unsafe ) f.get(null);
                }
            }
            throw new IllegalAccessException("no declared field: theUnsafe");
        }

         final int fullTryAcquireShared(Thread current) {
            /*
             * This code is in part redundant with that in
             * tryAcquireShared but is simpler overall by not
             * complicating tryAcquireShared with interactions between
             * retries and lazily reading hold counts.
             */
            MyselfReadWriteLock.Sync.HoldCounter rh = null;
            for (;;) {
                int c = getState();
                if (exclusiveCount(c) != 0) {
                    if (getExclusiveOwnerThread() != current)
                        return -1;
                    // else we hold the exclusive lock; blocking here
                    // would cause deadlock.
                } else  {
                    // Make sure we're not acquiring read lock reentrantly
                    if (firstReader == current) {
                        // assert firstReaderHoldCount > 0;
                    } else {
                        if (rh == null) {
                            rh = cachedHoldCounter;
                            if (rh == null || rh.tid != getThreadId(current)) {
                                rh = readHolds.get();
                                if (rh.count == 0)
                                    readHolds.remove();
                            }
                        }
                        if (rh.count == 0)
                            return -1;
                    }
                }
                if (sharedCount(c) == MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                if (compareAndSetState(c, c + SHARED_UNIT)) {
                    if (sharedCount(c) == 0) {
                        firstReader = current;
                        firstReaderHoldCount = 1;
                    } else if (firstReader == current) {
                        firstReaderHoldCount++;
                    } else {
                        if (rh == null)
                            rh = cachedHoldCounter;
                        if (rh == null || rh.tid != getThreadId(current))
                            rh = readHolds.get();
                        else if (rh.count == 0)
                            readHolds.set(rh);
                        rh.count++;
                        cachedHoldCounter = rh; // cache for release
                    }
                    return 1;
                }
            }
        }

         private IllegalMonitorStateException unmatchedUnlockException() {
            return new IllegalMonitorStateException(
                "attempt to unlock read lock, not locked by current thread");
        }

    }

    public  static   class  WriteLock implements Lock{
        private static final long serialVersionUID = 404461545017761332L;
        private final Sync sync;
        private WriteLock(Sync sync){
            this.sync=sync;
        }
        @Override
        public void lock() {
             sync.acquire(1);
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
            return sync.tryAcquireNanos(1,unit.toNanos(time));
        }

        @Override
        public void unlock() {
            sync.release(1);
        }

        @Override
        public Condition newCondition() {
            return null;
        }

    }


    public  static class  ReadLock implements Lock{
        private  final   Sync sync;
        private ReadLock(Sync sync) {
            this.sync=sync;
        }
        @Override
        public void lock() {
            sync.acquireShared(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
           sync.acquireSharedInterruptibly(1);
        }

        @Override
        public boolean tryLock() {
            return sync.tryAcquireShared(1)>0;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return sync.tryAcquireSharedNanos(1,unit.toNanos(time));
        }

        @Override
        public void unlock() {
             sync.releaseShared(1);
        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }


}
