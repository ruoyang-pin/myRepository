package pojo;

import lock.MyselfReadWriteLock;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class newThread implements Runnable{

    private  Integer[] target;
    private CountDownLatch latch;
    private ReentrantLock reentrantLock;
    private AtomicInteger atomicInteger;
    private Lock mutex;
    private MyselfReadWriteLock myselfReadWriteLock;
    private Lock writeLock;
    private Lock readLock;




    public newThread(Integer[] target, CountDownLatch latch, AtomicInteger atomicInteger, Lock mutex,MyselfReadWriteLock myselfReadWriteLock ){
        this.target=target;
        this.latch=latch;
        this.atomicInteger=atomicInteger;
        this.mutex=mutex;
        this.myselfReadWriteLock=myselfReadWriteLock;
        this.writeLock=myselfReadWriteLock.writeLock();
        this.readLock=myselfReadWriteLock.readLock();
    }


    @Override
    public void run() {
        try {
            writeLock.lock();
            target[0]++;
            atomicInteger.addAndGet(1);
            writeLock.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
              latch.countDown();
        }



    }
}
