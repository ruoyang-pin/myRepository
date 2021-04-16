package threadPool;

import java.util.concurrent.RecursiveTask;

public class QuickSortTask extends RecursiveTask<int[]> {
    private static final long serialVersionUID = -7992189261360754408L;

    int start;
    int end;
    int[] nums;

    public QuickSortTask(int start, int end, int[] nums) {
        this.start = start;
        this.end = end;
        this.nums = nums;
    }

    @Override
    protected int[] compute() {
        int i = start, j = end;
        boolean flag = false;
        QuickSortTask left = null, right = null;
        while (i < j) {
            while (i < j && nums[i] <= nums[j]) {
                if (!flag)
                    j--;
                else
                    i++;
            }
            if (i != j) {
                int temp = nums[i];
                nums[i] = nums[j];
                nums[j] = temp;
                flag = !flag;
            }
        }
        if(start<i){
            left = new QuickSortTask(start, i, nums);
            left.fork();
        }
        if(i+1<end){
            right = new QuickSortTask(i + 1, end, nums);
            right.fork();
        }
        if(left!=null)
            left.join();
        if(right!=null)
            right.join();
        return nums;
    }
}
