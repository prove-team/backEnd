package com.prove.domain.JWT.Service;

import com.prove.domain.JWT.dto.CustomUserDetails;
import com.prove.domain.User.UserEntity;
import com.prove.domain.User.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("KKKKKKKKKKKKKKKKKKKKK");
        System.out.println(username);
        //DB에서 특정 user조회해서 return
        //DB연결필요
        UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity != null) {
            return new CustomUserDetails(userEntity);
        }else if(userEntity == null) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + username);
        }
        return null;
    }
}