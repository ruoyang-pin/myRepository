package algorithm;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

public class GeneticAlgorithm {

    @Test
    public void init() {
        int[] nums = new int[4];
        for (int i = 0; i < 4; i++) {
            int left = ThreadLocalRandom.current().nextInt(8);
            int right = ThreadLocalRandom.current().nextInt(8);
            nums[i] = (left << 3) + right;
        }
        genetic(nums, 0);
        for (int num : nums) {
            System.out.println(calculateValue(num));
        }


    }

    public void genetic(int[] nums, int iterations) {
        if (iterations == 120)
            return;
        int min = 0x7fffffff, max = 0x80000000, minIndex = 0, maxIndex = 0, count = 0;
        for (int num : nums) {
            if (calculateValue(num) < min) {
                min = calculateValue(num);
                minIndex = count;
            }
            if (calculateValue(num) > max) {
                max = calculateValue(num);
                maxIndex = count;
            }
            count++;
        }
        nums[minIndex] = nums[maxIndex];
        cross(nums);
        variation(nums);
        genetic(nums, iterations + 1);
    }

    public void cross(int[] nums) {
        for (int i = 0; i < 4; i += 2) {
            int random = ThreadLocalRandom.current().nextInt(0, 6);
            int temp1 = nums[i] & (1 << random);
            int temp2 = nums[i + 1] & (1 << random);
            nums[i] = nums[i] - temp1 + temp2;
            nums[i + 1] = nums[i + 1] - temp2 + temp1;
        }
    }

    public void variation(int[] nums) {
        int count = 0;
        for (int num : nums) {
            int random = ThreadLocalRandom.current().nextInt(0, 6);
            int random2 = ThreadLocalRandom.current().nextInt(0, 6);
            int temp1 = num & (1 << random);
            int temp2 = num & (1 << random2);
            if (random != random2&&((temp1+temp2)!=0)&&(temp1*temp2)==0) {
                int temp3, temp4;
                if (random > random2) {
                    temp3 = temp1 >>> (random - random2);
                    temp4 = temp2 << (random - random2);
                } else {
                    temp3 = temp2 >>> (random2 - random);
                    temp4 = temp1 << (random2 - random);
                }
                nums[count] -= temp1;
                nums[count] -= temp2;
                nums[count] += temp3;
                nums[count] += temp4;
            }
            count++;
        }

    }


    public int calculateValue(int number) {
        return (number >> 3) * (number >> 3) + (number & 7) ;
    }


}
