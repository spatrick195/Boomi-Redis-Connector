package nz.co.fmg.redis.Utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Iterator;
import java.util.List;

import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

public class ScanIterator implements Iterator<List<String>> {
    private final Jedis _jedis;
    private final ScanParams _scanParams;
    private ScanResult<String> _scanResult;

    public ScanIterator(Jedis jedis, ScanParams params) {
        _jedis = jedis;
        _scanParams = params;
    }

    @Override
    public boolean hasNext() {
        if (_scanResult == null) {
            return true;
        }
        return !_scanResult.getCursor().equals("0");
    }

    @Override
    public List<String> next() {
        _scanResult = _jedis.scan(SCAN_POINTER_START, _scanParams);
        return _scanResult.getResult();
    }
}
