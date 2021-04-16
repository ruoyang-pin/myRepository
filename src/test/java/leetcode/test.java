package leetcode;

import org.junit.Test;

import java.util.Arrays;

@sun.misc.Contended
public class test {


    @Test
    public void execute() {

        System.out.println(minRefuelStops(1000000000,1000000000,new int[][]{{5,1000000000},{1000,1000000000},{100000,1000000000}}));

    }

    /**
     * dp[i][j] 表示i到j的开区间戳破气球最大的硬币数
     * 动态转移方程为 dp[i][j]=max(dp[i,k]+dp[k,j]+nums[k]*nums[i]*nums[j])
     * 当j-i<=1 dp[i][j]=0
     */

    public int maxCoins(int[] nums) {
        int[][] dp = new int[nums.length + 2][nums.length + 2];
        int[] newNums = new int[nums.length + 2];
        for (int i = 0; i < newNums.length; i++) {
            if (i == 0 || i == newNums.length - 1)
                newNums[i] = 1;
            else
                newNums[i] = nums[i - 1];
        }
        for (int i = dp.length - 1; i >= 0; i--) {
            for (int j = i + 1; j < dp.length; j++) {
                int max = 0;
                for (int k = i + 1; k < j; k++) {
                    max = Math.max(max, dp[i][k] + dp[k][j] + newNums[k] * newNums[i] * newNums[j]);
                }
                dp[i][j] = max;
            }
        }
        return dp[0][dp.length - 1];
    }

   /*
       dp[i]表示加i次油能行驶最远的距离  dp[0]=startFuel
    */

    public int minRefuelStops(int target, int startFuel, int[][] stations) {
        int len = stations.length;

        long[] dp = new long[len + 1];

        dp[0] = startFuel;

        if(startFuel>=target)

            return 0;

        for (int i = 0; i < len; i++) {

            for (int j = i; j >=0; j--) {

                if(dp[j]>=stations[i][0])

                    dp[j+1]=Math.max(dp[j+1],dp[j]+stations[i][1]);
            }

        }
        for (int i = 0; i < dp.length; i++) {
            if(dp[i]>target)
                return i;
        }
        return  -1;

    }


}
