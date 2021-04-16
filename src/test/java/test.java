import pojo.MyForkJoinTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.locks.LockSupport;

public class test {

    /*
         [6,4,2,122,34,1243]

     */
    public static int indexForOrder(int[] nums1, int[] nums2, int order) {



        int l1 = nums1.length, l2 = nums2.length, lo1 = 0,lo2 = 0,ho1=0,ho2=0,temp = 0;
        if (order > l1 * l2 || l1 <= 0 || l2 <= 0) {
            throw new IllegalArgumentException();
        }
        if (order == 1)
            return nums1[0] * nums2[0];
        for (int i = 1; i < order; i++) {

        }
        return temp;

    }





    public static void main(String[] args) throws ExecutionException, InterruptedException {



        System.out.println(indexForOrder(new int[]{1, 2, 3, 4}, new int[]{1, 2, 3, 4}, 11));
    }


}
