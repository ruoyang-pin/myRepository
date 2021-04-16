package pojo;

import java.util.concurrent.RecursiveAction;

public class MyForkJoinTask extends RecursiveAction {
    private static final long serialVersionUID = 4217960673887555540L;

    @Override
    protected void compute() {

        System.out.println("Ö´ÐÐÁ¦");
    }
}
