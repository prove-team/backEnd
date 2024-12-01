package com.prove.domain.JWT.controller;


import com.prove.domain.JWT.Service.JoinService;
import com.prove.domain.JWT.dto.JoinDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JoinController {
    private  final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join/{tags}")
    public ResponseEntity<?> joinProcess(@RequestBody JoinDto joinDto, @PathVariable String tags){
        return joinService.joinProcess(joinDto,tags);
    }
}