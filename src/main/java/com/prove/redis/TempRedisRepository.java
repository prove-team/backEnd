package com.prove.redis;

import org.springframework.data.repository.CrudRepository;

public interface TempRedisRepository extends CrudRepository<TempResponseDto.TempShowDto,Long> {
}
