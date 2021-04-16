package threadPool;

import java.util.concurrent.RecursiveTask;

public class myCountTask extends RecursiveTask<Integer> {

    private static final long serialVersionUID = 2854458741482087558L;
    int  start;
    int  end;
    int  Threshold;

    public myCountTask(int start, int end, int threshold) {
        this.start = start;
        this.end = end;
        Threshold = threshold;
    }

    @Override
    protected Integer compute() {
        if(end-start<=Threshold){
            int sum=0;
            for (int i = start; i <end ; i++) {
                sum+=i;
            }
            return sum;
        }else {
            int mid=(start+end)>>>1;
            myCountTask left = new myCountTask(start, mid, Threshold);
            myCountTask right = new myCountTask(mid+1, end, Threshold);
            left.fork();
            right.fork();
            return left.join()+right.join();
        }
    }
}
