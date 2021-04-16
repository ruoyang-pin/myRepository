package sortingAlgorithm;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

public class HeapSort implements Isort ,Cloneable{
    public  QuickSort qs=new QuickSort();
    @Override
    public void sort(int[] nums) {
        buildHeap(nums);
        for (int i = nums.length - 1; i > 0; i--) {
            int temp = nums[0];
            nums[0] = nums[i];
            nums[i] = temp;
            adjustHeap(nums,0,i);
        }

    }


    public static void buildHeap(int[] nums) {
        int parent, left, right, max, maxIndex;
        for (int i = nums.length - 1; i > 0; i -= 2) {
            if ((i & 1) == 1) {
                parent = i >> 1;
                left = i;
                right = (i + 1) == nums.length ? i : i + 1;
            } else {
                parent = (i >> 1) - 1;
                right = i;
                left = i - 1;
            }
            max = nums[left] > nums[right] ? nums[left] : nums[right];
            maxIndex = max == nums[left] ? left : right;
            if (max > nums[parent]) {
                int temp = nums[parent];
                nums[parent] = max;
                nums[maxIndex] = temp;
                adjustHeap(nums, maxIndex,nums.length);
            }
        }
    }



    public static void adjustHeap(int[] nums, int index,int length) {
        int left, right, h1 = 0, h2 = 0, temp, max;
        do {
            left = (h1 = ((index << 1) + 1)) >= length ? Integer.MIN_VALUE : nums[h1];
            right = (h2 = ((index + 1) << 1)) >= length ? Integer.MIN_VALUE : nums[h2];
            if (left > right) {
                temp = h1;
                max = left;
            } else {
                temp = h2;
                max = right;
            }
            if (max > nums[index]) {
                int swap = nums[index];
                nums[index] = max;
                nums[temp] = swap;
                index = temp;
            } else
                break;
        } while (((index << 1) + 1) < length);
    }


    @Override
    public Object clone()  {
        try {

            HeapSort clone =(HeapSort) super.clone();
            for (Field field : clone.getClass().getDeclaredFields()) {
                 ParameterizedType pt =  ( ParameterizedType )field.getGenericType();
                Type rawType = pt.getRawType();
                Object o =(Object) field.get(this);





            }



            return clone;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
