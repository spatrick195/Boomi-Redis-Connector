package nz.co.fmg.redis.Utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Iterator;
import java.util.List;

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
        if (_scanResult == null) {
            _scanResult = _jedis.scan("0", _scanParams);
        }
        _scanResult = _jedis.scan(_scanResult.getCursor(), _scanParams);

        return _scanResult.getResult();
    }
}
