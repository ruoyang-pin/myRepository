package guava;

import com.google.common.base.*;
import com.google.common.collect.*;
import org.junit.Test;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class test {


    @Test
    public void test() {

        List<String>  strings= Lists.newArrayList("dsad","dscaa","ada");
        List<String>  strings2= Lists.newArrayList("dsad","dscaa","ada","ada");
        ImmutableMap<Integer, String> stringsByIndex = Maps.uniqueIndex(strings,
                string -> string.length());
        ImmutableListMultimap<Integer, String> index = Multimaps.index(strings2, s -> s.length());
        ImmutableList<String> strings1 = index.get(3);

    }
    @Test
    public void test2(){
        String[] s={"sda","dsa",null};
        Joiner joiner = Joiner.on(":").skipNulls();
        String join = joiner.join(s);
        System.out.println(join);

        String join1 = String.join(":",s);
        StringJoiner stringJoiner = new StringJoiner(":", "", ";");
        for (String s1 : s) {
            stringJoiner.add(s1);
        }
        System.out.println(stringJoiner.toString());

        Iterable<String> split1 = Splitter.on(",").omitEmptyStrings().split(",a,,b,");
        ArrayList<String> strings = Lists.newArrayList(split1);
        System.out.println(strings);




    }





}
