package sortingAlgorithm;

public class QuickSort implements Isort ,Cloneable{


    @Override
    public void sort(int[] nums) {
        quickSort(nums, 0, nums.length - 1);
    }



    public static void quickSort(int[] nums, int start, int end) {
        int i = start, j = end;
        boolean turnFlag = false;
        while (i != j) {
            while (j > i && nums[j] >= nums[i]) {
                if (!turnFlag)
                    j--;
                else
                    i++;
            }
            if (i != j) {
                int temp = nums[j];
                nums[j] = nums[i];
                nums[i] = temp;
                turnFlag = !turnFlag;
            }
        }
        if (start < i)
            quickSort(nums, start, i);
        if (i + 1 < end)
            quickSort(nums, i + 1, end);
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
