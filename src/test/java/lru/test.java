package lru;

import org.junit.Test;

public class test {


    @Test
    public void test(){
        LruList<Integer> newList = LruList.getNewList(3);
        newList.add(2);
        newList.add(23);
        newList.get(0);
        newList.add(123);
        newList.add(1234);

        System.out.println(newList.toString());
        System.out.println(test.class.getClassLoader());
    }



}

