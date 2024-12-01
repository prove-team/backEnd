package com.prove.domain.memorycache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheStatistics {

    @Autowired
    private CacheManager cacheManager;

    public void printCacheStats() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                // 캐시의 이름
                System.out.println("Cache Name: " + cacheName);

                // 캐시의 크기 (항목 수)
                System.out.println("Cache Size: " + getCacheSize(cache));

                // 캐시의 데이터 출력 (필요한 경우)
                System.out.println("Cache Content: " + getCacheContent(cache));
            }
        }
    }

    private int getCacheSize(Cache cache) {
        // 캐시의 항목 수를 세는 방법
        // Redis 캐시와 같은 경우에는 이 방법이 적용되지 않을 수 있습니다.
        return ((ConcurrentHashMap) cache.getNativeCache()).size();
    }

    private String getCacheContent(Cache cache) {
        // 캐시의 내용을 출력할 수 있는 로직
        // 이는 캐시의 구현에 따라 다릅니다. 예: ConcurrentHashMap인 경우
        return cache.getNativeCache().toString();
    }
}
