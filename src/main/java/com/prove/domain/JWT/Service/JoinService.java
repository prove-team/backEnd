package com.prove.domain.JWT.Service;

import com.prove.domain.JWT.dto.JoinDto;
import com.prove.domain.User.UserEntity;
import com.prove.domain.User.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;

@Service
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<?> joinProcess(JoinDto joinDto){
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();
        Boolean isExist = userRepository.existsByUsername(username);

        if(isExist){
            //이미 회원있는경우
            return new ResponseEntity<>("이미 회원가입이 되어있습니다.", HttpStatus.BAD_REQUEST);
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        //일단 전부 관리자로 세팅
        userEntity.setRole("User");
        userEntity.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(userEntity);
        return new ResponseEntity<>("회원가입 완료",HttpStatus.OK);
    }
}
