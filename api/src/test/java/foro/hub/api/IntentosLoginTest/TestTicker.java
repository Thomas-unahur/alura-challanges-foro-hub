package foro.hub.api.IntentosLoginTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Ticker;

public class TestTicker extends Ticker{
    private final AtomicLong nanos = new AtomicLong();

    @Override
    public long read(){
        return nanos.get();
    }

    public void advance(long duration, TimeUnit unit){
        nanos.addAndGet(unit.toNanos(duration));
    }
}
