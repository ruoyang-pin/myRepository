package pojo;

public class objectDemo {
  private  static  objectDemo  finalizeDemo=null;

  static {
      System.out.println("我在那里?");
  }
  objectDemo(){
      System.out.println("我出生了");
  }





  protected void finalize() throws Throwable {
      super.finalize();
      finalizeDemo=this;
  }


    public static void main(String[] args) throws InterruptedException {
//        finalizeDemo=new objectDemo();
//        finalizeDemo=null;
//        System.gc();
//        Thread.sleep(500);
//        if(finalizeDemo==null){
//            System.out.println("我已经死了");
//        }else {
//            System.out.println("我还没有死");
//        }




    }




}
