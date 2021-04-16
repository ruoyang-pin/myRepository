import org.junit.Test;
import sortingAlgorithm.HeapSort;
import sortingAlgorithm.Isort;
import sortingAlgorithm.QuickSort;
import threadPool.QuickSortTask;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadLocalRandom;

public class test {


    public static boolean judge(int[] nums1, int[] nums2) {
        if (nums1.length != nums2.length)
            return false;
        for (int i = 0; i < nums1.length; i++) {
            if (nums1[i] != nums2[i])
                return false;
        }
        return true;
    }

    @Test
    public void test() {
        int[] s = {23, 31, 1};
        for (int i : s) {
            i = 3;
        }
        System.out.println(s);
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Isort quickSort = new QuickSort();
        Isort heapSort = new HeapSort();
        int[] nums = new int[100000000];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = ThreadLocalRandom.current().nextInt(1000000);
        }
        int[] nums2 = nums.clone();
        int[] nums3 = nums.clone();
        int[] nums4 = nums.clone();
        QuickSortTask quickSortTask = new QuickSortTask(0,nums4.length-1,nums4);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        long l = System.currentTimeMillis();
        quickSort.sort(nums);
        long l2 = System.currentTimeMillis();
        System.out.println("快排耗时间" + (l2 - l));
        long l3 = System.currentTimeMillis();
        Arrays.parallelSort(nums2);
        long l4 = System.currentTimeMillis();
        System.out.println("双轴快排耗时间" + (l4 - l3));
        long l5 = System.currentTimeMillis();
        heapSort.sort(nums3);
        long l6 = System.currentTimeMillis();
        System.out.println("堆排序耗时间" + (l6 - l5));
        long l7 = System.currentTimeMillis();
        forkJoinPool.submit(quickSortTask);
        int[] result = quickSortTask.get();
        long l8 = System.currentTimeMillis();
        System.out.println("forkjoin快排耗时间" + (l8 - l7));
        System.out.println(judge(nums, nums2));
        System.out.println(judge(nums2, nums3));
        System.out.println(judge(nums3, result));
    }
}
