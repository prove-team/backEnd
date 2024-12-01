package com.prove.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.prove.redis.TempRequestDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TempService {
    private final TempRedisRepository tempRedisRepository;
    private final TempRepository tempRepository;

    public Temp save(TempSignUpDto request){
        Temp newTemp = TempRequestDto.toEntity(request);
        Temp savedTemp = tempRepository.save(newTemp);

        //save in redis
        TempResponseDto.TempShowDto tempShowDto = TempResponseDto.from(savedTemp);
        tempRedisRepository.save(tempShowDto);

        return savedTemp;
    }

    public TempResponseDto.TempShowDto findTemp(Long tempId){
        //Cache Loginc
        Optional<TempResponseDto.TempShowDto> optionalTempDto = tempRedisRepository.findById(tempId);
        if(optionalTempDto.isPresent()){
            log.info("Temp Dto Cache Exist!");
            return optionalTempDto.get();
        }else{
            log.info("Temp Dto Cache not Exist");
        }

        log.info("DB조회");
        Temp temp = tempRepository.findById(tempId).get();
        TempResponseDto.TempShowDto tempShowDto = TempResponseDto.from(temp);

        log.info("다시 Redis에 캐싱");
        tempRedisRepository.save(tempShowDto);

        log.info("Temp Dto: " + tempShowDto);
        return tempShowDto;
    }

}
