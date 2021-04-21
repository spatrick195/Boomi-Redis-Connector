package nz.co.fmg.redis.Utils;

import java.util.Iterator;
import java.util.List;

public class HashScan implements Iterator<List<String>> {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public List<String> next() {
        return null;
    }
}
