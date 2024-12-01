package com.prove.domain.memorycache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prove.domain.AwsS3.AwsS3Service;
import com.prove.domain.Like.LikeRepository;
import com.prove.domain.Prove.Dto.ProveDtoV2;
import com.prove.domain.Prove.Prove;
import com.prove.domain.Prove.ProveRepository;
import com.prove.domain.Prove.ProveService;
import com.prove.domain.User.Dto.UserDto;
import com.prove.domain.User.UserEntity;
import com.prove.domain.comment.Comment;
import com.prove.domain.comment.CommentDto;
import com.prove.domain.comment.CommentRepository;
import com.prove.domain.comment_like.CommentLikeRepository;
import com.prove.domain.image.ImageRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CacheInitializer {

    private final ProveRepository proveRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final CacheManager cacheManager;
    @PostConstruct // Bean이 초기화될 때 호출됨
    public void init() {
        List<Prove> proves = proveRepository.findTop100ByStudyTagOrderByLikeCountDesc(); // DB에서 100개의 Prove 가져오기
        System.out.println(proves);
        System.out.println("hello");

        // Prove 객체를 DTO로 변환
        List<ProveDtoV2> proveDtos = makeProveDtos(proves);

        // 캐시에 저장하기 전에 DTO 리스트를 JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Cache cache = cacheManager.getCache("STUDY");

        if (cache != null) {
            try {
                // DTO 리스트를 JSON 문자열로 변환
                String jsonString = objectMapper.writeValueAsString(proveDtos);

                // 캐시에 JSON 문자열 저장
                cache.put("top100Proves", jsonString);
                System.out.println("Prove 데이터가 STUDY 캐시에 JSON 형태로 저장되었습니다.");

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                System.out.println("캐시에 저장 중 오류가 발생했습니다.");
            }
        }
    }


    public List<ProveDtoV2> makeProveDtos(List<Prove> proves) {
        System.out.println("dot 메서드");
        // 각 Prove 객체를 ProveDtoWithId로 변환한 후 리스트로 반환
        return proves.stream()
                .map(prove -> {
                    UserEntity userEntity = prove.getUser();
                    Long likeCount = likeRepository.countLikesByProveId(prove.getId());
                    // UserDto 생성
                    UserDto userDto = UserDto.builder()
                            .username(userEntity.getUsername())
                            .role(userEntity.getRole())
                            .build();

                    //proves를 통해서 comment 가져오기
                    List<Comment> comments = commentRepository.findByProveId(prove.getId());
                    System.out.println(comments);
                    // 댓글을 DTO로 변환
                    List<CommentDto> commentDtos = comments.stream()
                            .map(comment -> CommentDto.builder()
                                    .comment_id(comment.getId())
                                    .prove_id(prove.getId())
                                    .comment(comment.getComment())
                                    .like_count(commentLikeRepository.countLikesByCommentId(comment.getId()))
                                    .user_main_Img(comment.getUser().getMainImage())
                                    .username(comment.getUser().getUsername())
                                    .build())
                            .collect(Collectors.toList());


                    // ProveDtoV2 생성
                    ProveDtoV2 proveDtoV2 = ProveDtoV2.builder()
                            .proveId(prove.getId())
                            .importance(prove.getImportance())
                            .commentDtoList(commentDtos)
                            .tags(prove.getTags())
                            .startTime(prove.getStartTime())
                            .endTime(prove.getEndTime())
                            .success(prove.getSuccess())
                            .shortWord(prove.getShortWord())
                            .openOrNot(prove.getOpenOrNot())
                            .user(userDto)
                            .like(likeCount)
                            .imgList(imageRepository.findAllByProve(prove))
                            .color(prove.getColor())
                            .build();

                    return proveDtoV2;
                })
                .collect(Collectors.toList());
    }


}