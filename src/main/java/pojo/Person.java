package pojo;

import lombok.SneakyThrows;

public class Person implements Action{

    @Override
    public int eat() {
        buyFood();
        return 0;
    }

   @SneakyThrows
   public  void buyFood(){
        int s=3/0;
       System.out.println("买苹果");
   }


}
