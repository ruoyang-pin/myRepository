package hash;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCacheHash extends LinkedHashMap {
    private static final long serialVersionUID = 7006209334050111798L;
    private  int cacheSize;

    public  LruCacheHash(int cacheSize){
        super(cacheSize,0.75f,true);
        this.cacheSize=cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return  size()>cacheSize;
    }


}
