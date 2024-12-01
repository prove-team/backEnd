package com.prove.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/temps")
@Slf4j
public class TempController {
    private final TempService tempService;

    @GetMapping("/{tempId}")
    public ResponseEntity<TempResponseDto.TempShowDto> getTemp(@PathVariable Long tempId){
        long startTime = System.currentTimeMillis();
        TempResponseDto.TempShowDto tempShowDto = tempService.findTemp(tempId);
        long endTime = System.currentTimeMillis();

        log.info(" Get /tempId ResponseTime: "+(endTime-startTime));
        return ResponseEntity.ok(tempShowDto);
    }

    @PostMapping()
    public ResponseEntity<String> saveMember(@RequestBody TempRequestDto.TempSignUpDto request){
        System.out.println(request.toString());
        Temp temp = tempService.save(request);
        return ResponseEntity.ok("생성된 Temp Dto의 키 = "+temp.getId());
    }
}
