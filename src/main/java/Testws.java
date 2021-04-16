import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class Testws implements Serializable {
    private static final long serialVersionUID = 2010307013874058143L;
    private Map<Object, Object> map;


    public Testws(Map<Object, Object> map) {
        this.map = map;
    }

    private void writeObject(java.io.ObjectOutputStream s)
            throws IOException {
        s.defaultWriteObject();
    }

     private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        map.put("ds",231 );
     }

}
