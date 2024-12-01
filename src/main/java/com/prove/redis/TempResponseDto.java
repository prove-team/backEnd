package com.prove.redis;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

public record TempResponseDto() {

    @RedisHash(value = "temp",timeToLive = 20)
    @Builder
    public record TempShowDto(@Id Long redisKey, String name){}

    public static TempShowDto from(Temp temp){
        return TempShowDto
                .builder()
                .redisKey(temp.getId())
                .name(temp.getName())
                .build();
    }
}
