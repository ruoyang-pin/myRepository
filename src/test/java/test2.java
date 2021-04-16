import org.junit.Test;
import sortingAlgorithm.HeapSort;
import sortingAlgorithm.Isort;
import sortingAlgorithm.QuickSort;
import threadPool.QuickSortTask;
import threadPool.myCountTask;

import java.io.*;
import java.net.Socket;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.locks.StampedLock;

public class test2 {


    @Test
    public void test() {
        myCountTask myCountTask = new myCountTask(0, 1000, 10);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.submit(myCountTask);
        try {
            System.out.println(myCountTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        test23();
        System.out.println(2);
    }
    public  void test23() throws ExecutionException, InterruptedException {
        int[] nums={23,4,21,564,456,2,3423,1,2,8,321123123};
        QuickSortTask quickSortTask = new QuickSortTask(0,nums.length-1,nums);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.submit(quickSortTask);
        int[] ints = quickSortTask.get();
        for (int anInt : ints) {
            System.out.println(anInt);
        }
        forkJoinPool.shutdown();

    }
    
    @Test
    public  void  test2(){
        int[] nums={23,4,21,564,456,2,3423,1,2,8};
        QuickSort quickSort = new QuickSort();
        quickSort.sort(nums);
         for (int anInt : nums) {
            System.out.println(anInt);
        }
    }




}
