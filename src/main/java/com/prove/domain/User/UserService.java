package com.prove.domain.User;

import com.prove.domain.AwsS3.AwsS3Service;
import com.prove.domain.PageMetaData;
import com.prove.domain.PagedDTO;
import com.prove.domain.Prove.Dto.ProveDtoV2;
import com.prove.domain.Prove.Prove;
import com.prove.domain.Tags;
import com.prove.domain.User.Dto.ProveStatic;
import com.prove.domain.User.Dto.UserDto;
import com.prove.domain.image.ImageDto;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;
    public void addFriend(String userName, int friendId) {
        UserEntity user = userRepository.findByUsername(userName);
        UserEntity friend = userRepository.findById(friendId).orElseThrow(()->new IllegalArgumentException("잘못된 friend ID"));

        if (!user.getFriends().contains(friend)) {
            user.getFriends().add(friend);
        }

        if (!friend.getFriends().contains(user)) {
            friend.getFriends().add(user);
        }
    }

    public void deleteFriend(String username, int friendId) {
        UserEntity user = userRepository.findByUsername(username);
        UserEntity friend = userRepository.findById(friendId).orElseThrow(()->new IllegalArgumentException("잘못된 friend ID"));

        user.getFriends().removeIf(f -> f.equals(friend));
    }

    public List<UserDto> getMyFriends() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        List<UserEntity> friends = userEntity.getFriends();
        List<UserDto> userDtos = new ArrayList<>();
        for (UserEntity friend : friends) {
            userDtos.add(UserDto.builder()
                    .username(friend.getUsername())
                    .userImg(friend.getMainImage())
                    .build());
        }
        return userDtos;
    }

    public Long getFriendId(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        return (long) userEntity.getId();
    }

    public ProveStatic getProveStatic() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        ProveStatic proveStatic = new ProveStatic();
        List<Prove> proves = userEntity.getProves();
        int total = proves.size();
        int success = 0;
        int fail = 0;
        for (Prove prove : proves) {
            if(prove.getSuccess().equals("NotYet")){
                fail++;
            }else {
                success++;
            }
        }
        proveStatic.setTotal(total);
        proveStatic.setSuccess(success);
        proveStatic.setFail(fail);
        return proveStatic;
    }

    public ConcurrentHashMap<Tags,Integer> getTagsStatic() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        List<Prove> proves = userEntity.getProves();
        ConcurrentHashMap<Tags,Integer> Map = new ConcurrentHashMap<>();
        for (Prove prove : proves) {
            Tags tag = prove.getTags();
            if (Map.containsKey(tag)) {
                Map.put(tag, Map.get(tag) + 1);
            } else {
                Map.put(tag, 1);
            }
        }
        return Map;
    }

    public void addTags(String tags) {
        System.out.println(tags);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        List<Tags> userEntityTags = userEntity.getTags();
        List<Tags> userTags = Arrays.stream(tags.split("\\."))
                .map(Tags::valueOf)
                .collect(Collectors.toList());
        for (Tags tag : userTags) {
            if(!userEntityTags.contains(tag)) {
                userEntity.getTags().add(tag);
            }
        }
    }


    public List<Tags> getMyTags() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        System.out.println("zzz");
        System.out.println(userEntity.getTags());
        return userEntity.getTags();
    }

    public List<Integer> getLevelLank() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        List<Integer> data = new ArrayList<>();
        data.add(userEntity.getLank());
        data.add(userEntity.getLevel());
        return data;
    }

    public ResponseEntity<?> postMainImage(MultipartFile uploadImages) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        ImageDto imageDto = awsS3Service.uploadFile(uploadImages);
        userEntity.setMainImage(imageDto.getImgUrl());
        return new ResponseEntity<>("메인이미지 업로드완료",HttpStatus.OK);
    }

    public ResponseEntity<?> changeMainImage(MultipartFile uploadImages) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity.getMainImage().isBlank()){
            return new ResponseEntity<>("교체할 이미지가 없습니다.",HttpStatus.BAD_REQUEST);
        }
        String fileName = awsS3Service.getFileName(userEntity.getMainImage());
        awsS3Service.deleteFile(fileName);
        ImageDto imageDto = awsS3Service.uploadFile(uploadImages);
        userEntity.setMainImage(imageDto.getImgUrl());
        return new ResponseEntity<>("이미지 교체완료",HttpStatus.OK);
    }

    public ResponseEntity<?> deleteMainImage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity.getMainImage().isBlank()){
            return new ResponseEntity<>("삭제할 이미지가 없습니다.",HttpStatus.BAD_REQUEST);
        }
        String fileName = awsS3Service.getFileName(userEntity.getMainImage());
        awsS3Service.deleteFile(fileName);
        userEntity.setMainImage("");
        return new ResponseEntity<>("메인 이미지 삭제완료",HttpStatus.OK);
    }

    public String getMainImage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        if(!userEntity.getMainImage().isBlank()){
            return userEntity.getMainImage();
        }
        return "이미지가 존재하지않습니다.";
    }

    public List<UserDto> getMyFriendsWithUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        List<UserEntity> friends = userEntity.getFriends();
        List<UserDto> userDtos = new ArrayList<>();
        for (UserEntity friend : friends) {
            userDtos.add(UserDto.builder()
                    .username(friend.getUsername())
                    .userImg(friend.getMainImage())
                    .build());
        }
        return userDtos;
    }

    public PagedDTO<List<UserDto>> searchWithUserName(String username, Pageable pageable) {
        Page<UserEntity> userEntities = userRepository.findByUsernameWithPage(username, pageable);
        List<UserDto> userDtos = new ArrayList<>();
        List<UserEntity> content = userEntities.getContent();
        for (UserEntity userEntity : content) {
            userDtos.add(UserDto.builder()
                    .username(userEntity.getUsername())
                    .userImg(userEntity.getMainImage())
                    .build());
        }
        PageMetaData pageMetaData = new PageMetaData(userEntities.getSize(),
                userEntities.getTotalElements(), userEntities.getTotalPages(), userEntities.getNumber());

        return PagedDTO.<List<UserDto>>builder()
                .content(userDtos)
                .pageMetaData(pageMetaData)
                .build();

    }
}
