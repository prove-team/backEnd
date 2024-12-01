package com.prove.domain.JWT.Service;

import com.prove.domain.JWT.dto.JoinDto;
import com.prove.domain.Tags;
import com.prove.domain.User.UserEntity;
import com.prove.domain.User.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<?> joinProcess(JoinDto joinDto, String tags){
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();
        System.out.println(username);
        System.out.println(password);
        Boolean isExist = userRepository.existsByUsername(username);

        String[] tagArray = tags.split("\\."); // '.'으로 파싱
        List<Tags> tagsList = new ArrayList<>();
        for (String t : tagArray) {
            Tags tagEnum = Tags.valueOf(t); // 문자열을 Tags로 변환
            tagsList.add(tagEnum); // 리스트에 추가
        }

        if(isExist){
            //이미 회원있는경우
            return new ResponseEntity<>("이미 회원가입이 되어있습니다.", HttpStatus.BAD_REQUEST);
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setRole("User");
        userEntity.setPassword(bCryptPasswordEncoder.encode(password));
        userEntity.setLank(0);
        userEntity.setLevel(0);
        for (Tags tag : tagsList) {
            System.out.println(tag);
            userEntity.getTags().add(tag);
        }
        userRepository.save(userEntity);
        return new ResponseEntity<>("회원가입 완료",HttpStatus.OK);
    }
}
