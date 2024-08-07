package com.prove.domain.User;

import com.prove.domain.Prove.ProveService;
import com.prove.domain.Tags;
import com.prove.domain.User.Dto.ProveStatic;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;




@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final UserService userService;
    private final ProveService proveService;
    //1. 전체 완료 미완료 비율
    @GetMapping("/api/get/prove/static")
    public ProveStatic getProveStatic(){
        return userService.getProveStatic();
    }

    //3. 가장 많이 사용한 태그
    @GetMapping("/api/get/tags/static")
    public ConcurrentHashMap<Tags,Integer> getTagsStatic(){
        return userService.getTagsStatic();
    }

    //월간단위 완료 미완료 비율
    /*prove1이 success가 "Success" endtime 2024-07-05
    prove2이 success가 "i did it" endtime 2024-06-05
    prove3이 success가 "NotYet" endtime 2024-07-01
    이면
    6월은 success1개 unsuccess 0개
    7월은 success1개 unsuccess1개
    올해는 2024년이니까 조회하는 년도를 기준으로 이전년도는 수치에 포함시키지 않게 만들어줘

*/
    @GetMapping("/api/monthly-stats")
    public Map<YearMonth, Map<String, Integer>> getMonthlyStats(@RequestParam int year) {
        return proveService.getMonthlySuccessFailureStats(year);
    }

}
