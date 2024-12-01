package com.prove.redis;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TempRepository extends JpaRepository<Temp,Long> {
}
