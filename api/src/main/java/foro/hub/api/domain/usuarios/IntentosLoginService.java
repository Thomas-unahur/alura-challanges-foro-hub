package foro.hub.api.domain.usuarios;

import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class IntentosLoginService {
    private final int MAXIMOS_INTENTOS = 5;
    private LoadingCache<String,Integer> intentosEnCache;

    public IntentosLoginService(){
        this(Ticker.systemTicker());
    }

    public IntentosLoginService(Ticker ticker){
        super();
        intentosEnCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .ticker(ticker)
                .build(new CacheLoader<String,Integer>(){
                    public Integer load(String key){
                        return 0;
                    }
        });
    }

    public void inicioDeSesionExitoso(String key){
        intentosEnCache.invalidate(key);
    }

    public void inicioDeSesionFallido(String key) {
        int intentos = 0;
        try{
            intentos = intentosEnCache.get(key);
        } catch (ExecutionException e) {
            intentos = 0;
        }
        intentos++;
        intentosEnCache.put(key,intentos);
    }

    public boolean estaBloqueado(String key){
        try {
            return intentosEnCache.get(key) >= MAXIMOS_INTENTOS;
        }catch (ExecutionException e){
            return false;
        }
    }
}


