package com.prove.domain.User;

import com.prove.domain.PagedDTO;
import com.prove.domain.Tags;
import com.prove.domain.User.Dto.UserDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    //다른User엔티티의 id를 보냄
    @PostMapping("/api/make/friend/{friendId}")
    public ResponseEntity<?> makeFriend(@PathVariable int friendId){
        String username = getUsername();
        return userService.addFriend(username, friendId);
    }

    @GetMapping("/api/get/friendId/{username}")
    public Long getFriendId(@PathVariable String username){
        return userService.getFriendId(username);
    }

    @DeleteMapping("/api/friend/{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable int friendId){
        String username = getUsername();
        return userService.deleteFriend(username,friendId);
    }

    @PostMapping("/api/tags/{user_tags}")
    public ResponseEntity<?> postTags(@PathVariable String user_tags){
        userService.addTags(user_tags);
        return new ResponseEntity<>("tags 등록 완료",HttpStatus.OK);
    }

    @GetMapping("/api/myTags")
    public List<Tags> getMyTags(){
        return userService.getMyTags();
    }


    @GetMapping("/api/get/myfriends")
    public List<UserDto> getAllFriends(){
        return userService.getMyFriends();
    }

    @GetMapping("/api/friends/{username}")
    public List<UserDto> getAllFriendsWithUsername(@PathVariable String username){
        return userService.getMyFriendsWithUsername(username);
    }

    @GetMapping("/api/level/lank")
    public List<Integer> getLevelLank(){
        return userService.getLevelLank();
    }

    @GetMapping("/api/mainImage")
    public String getMainImage(){
        return userService.getMainImage();
    }

    @PostMapping("/api/mainImage")
    public ResponseEntity<?> postMainImage( @RequestPart("uploadImgs") @Nullable MultipartFile uploadImages){
        return userService.postMainImage(uploadImages);
    }

    @PutMapping("/api/mainImage")
    public ResponseEntity<?> changeMainImage(@RequestPart("uploadImgs") @Nullable MultipartFile uploadImages){
        return userService.changeMainImage(uploadImages);
    }

    @DeleteMapping("/api/mainImage")
    public ResponseEntity<?> deleteMainImage(){
        return userService.deleteMainImage();
    }

    //TODO 내가 -> junsu를 친구추가(jun까지 보냄), 내 친구가 A,B,C 3명이 있다. A에서 jun친구가 jungo B,C가 junsu??
    @GetMapping("/api/search/{username}")
    public PagedDTO<List<UserDto>> searchWithUserName(@PathVariable String username, @PageableDefault(page = 0, size = 5) Pageable pageable){
        return userService.searchWithUserName(username,pageable);
    }

    @GetMapping("/api/username")
    public String getUsernameWithToken(){
        return userService.getUseranmeWithToken();
    }

    private static String getUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return username;
    }
}
