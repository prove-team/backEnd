package com.prove.domain.Prove;

import com.prove.domain.AwsS3.AwsS3Service;
import com.prove.domain.Like.Like;
import com.prove.domain.Like.LikeRepository;
import com.prove.domain.PageMetaData;
import com.prove.domain.PagedDTO;
import com.prove.domain.Prove.Dto.ProveDto;
import com.prove.domain.Prove.Dto.ProveDtoV2;
import com.prove.domain.Prove.Dto.ProveWithF_CNTDTO;
import com.prove.domain.Tags;
import com.prove.domain.User.Dto.UserDto;
import com.prove.domain.User.UserEntity;
import com.prove.domain.User.UserRepository;
import com.prove.domain.comment.Comment;
import com.prove.domain.comment.CommentDto;
import com.prove.domain.comment.CommentRepository;
import com.prove.domain.image.Image;
import com.prove.domain.image.ImageDto;
import com.prove.domain.image.ImageRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProveService {
    private final ProveRepository proveRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final AwsS3Service awsS3Service;

    private void makeImageList(List<Image> imageList, List<ImageDto> imageDtoList) {
        //여기 Image에서는 현재 Prove 매핑이 되어있지 않음
        //prove Builder에서 매핑해줘야함
        for(ImageDto imageDto:imageDtoList){
            Image image = Image.builder()
                    .imgName(imageDto.getImgName())
                    .imgUrl(imageDto.getImgUrl())
                    .build();
            imageList.add(image);
        }
    }

    public void completeProve(ProveDto proveDto, List<MultipartFile> uploadImages, Long proveId) {
        Prove prove = proveRepository.findById(proveId).orElseThrow(() -> new IllegalArgumentException());
        List<Image> imageList = new ArrayList<>();
        List<ImageDto> imageDtoList = new ArrayList<>();

        if(uploadImages != null){
            for(MultipartFile img : uploadImages){
                ImageDto imageDto = awsS3Service.uploadFile(img);
                imageDtoList.add(imageDto);
            }
        }

        makeImageList(imageList,imageDtoList);

        prove.completeProve(imageList,proveDto);

        String username = getUsername();
        UserEntity user = userRepository.findByUsername(username);

        user.complit();

        proveRepository.save(prove);
    }

    public void create(ProveDto proveDto) {
        String username = getUsername();
        System.out.println(proveDto.getStartTime());
        System.out.println(proveDto.getEndTime());
        UserEntity user = userRepository.findByUsername(username);
        Prove prove = Prove.builder()
                .openOrNot(proveDto.getOpenOrNot())
                .importance(proveDto.getImportance())
                .tags(proveDto.getTags())
                .startTime(proveDto.getStartTime())
                .endTime(proveDto.getEndTime())
                .user(user)
                .color(proveDto.getColor())
                .shortWord(proveDto.getShortWord())
                .build();
        proveRepository.save(prove);
    }

    private static String getUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        return username;
    }

    public void editProve(ProveDto proveDto, List<MultipartFile> uploadImages, Long proveId) {
        Prove prove = proveRepository.findById(proveId).orElseThrow(() -> new IllegalArgumentException());

        List<Image> imageList = new ArrayList<>();
        List<ImageDto> imageDtoList = new ArrayList<>();

        if(uploadImages != null){
            for(MultipartFile img : uploadImages){
                ImageDto imageDto = awsS3Service.uploadFile(img);
                imageDtoList.add(imageDto);
            }
        }else {
            System.out.println("이미지가 없음");
        }

        makeImageList(imageList,imageDtoList);

        prove.editProve(proveDto,imageList);

        proveRepository.save(prove);
    }

    public void deleteProve(Optional<Prove> optionalProve) {
        String username = getUsername();
        UserEntity userEntity = userRepository.findByUsername(username);

        if (optionalProve.isPresent()) {
            Prove prove = optionalProve.get();

            // UserEntity가 Prove를 소유하고 있는지 확인
            if (prove.getUser().equals(userEntity)) {
                // Image 삭제
                List<Image> imgList = imageRepository.findAllByProve(prove);
                for (Image img : imgList) {
                    awsS3Service.deleteFile(img.getImgName());
                    imageRepository.delete(img);
                }

                // Comment 삭제
                List<Comment> commentList = prove.getCommentList();
                for (Comment comment : commentList) {
                    commentRepository.delete(comment);
                }

                // Prove 삭제
                proveRepository.deleteById(prove.getId());
            } else {
                throw new SecurityException("이 사용자는 해당 증명을 삭제할 권한이 없습니다.");
            }
        } else {
            throw new IllegalArgumentException("잘못된 Prove 값입니다.");
        }
    }
    public List<ProveDtoV2> getFriendProve() {
        String username = getUsername();
        // 친구들의 Prove를 저장할 리스트
        List<Prove> friendProves = new ArrayList<>();
        UserEntity user = userRepository.findByUsername(username);
        List<UserEntity> friends = user.getFriends();
        if (user != null && friends != null) {
            for (UserEntity friend : friends) {
                friendProves.addAll(friend.getProves());
            }
        }
        sortProvesByEndTime(friendProves);
        return makeProveDtos(friendProves);
    }

    public PagedDTO<List<ProveDtoV2>> getAllMyProve(Pageable pageable) {
        String username = getUsername();
        Page<Prove> provesPage = proveRepository.findByUserName(username, pageable);
        PageMetaData pageMetaData = new PageMetaData(provesPage.getSize(),
                provesPage.getTotalElements(), provesPage.getTotalPages(), provesPage.getNumber());
        List<Prove> content = provesPage.getContent();
        List<ProveDtoV2> proveDtoV2s = makeProveDtos(content);
        return PagedDTO.<List<ProveDtoV2>>builder()
                .content(proveDtoV2s)
                .pageMetaData(pageMetaData)
                .build();
    }

    public List<ProveDtoV2> makeProveDtos(List<Prove> proves) {
        System.out.println("dot 메서드");
        // 각 Prove 객체를 ProveDtoWithId로 변환한 후 리스트로 반환
        return proves.stream()
                .map(prove -> {
                    UserEntity userEntity = prove.getUser();
                    Long likeCount = prove.getLikeCount();

                    // UserDto 생성
                    UserDto userDto = UserDto.builder()
                            .username(userEntity.getUsername())
                            .role(userEntity.getRole())
                            .build();

                    List<CommentDto> commentDtos = prove.getCommentList().stream()
                            .map(comment -> CommentDto.builder()
                                    .comment_id(comment.getId())
                                    .prove_id(prove.getId())
                                    .comment(comment.getComment())
                                    .like_count((long) comment.getCommentLikes().size())
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
                            .imgList(prove.getImgList())
                            .color(prove.getColor())
                            .build();

                    return proveDtoV2;
                })
                .collect(Collectors.toList());
    }


    public List<ProveDtoV2> getTodayProve(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        ZoneId zoneId = ZoneId.systemDefault(); // 시스템 기본 시간대 사용

        LocalDateTime startDateTime = date.atStartOfDay(zoneId).toLocalDateTime();
        LocalDateTime endDateTime = date.plusDays(1).atStartOfDay(zoneId).toLocalDateTime().minusNanos(1);
        List<Prove> proves = proveRepository.findAllByDateWithImagesAndUser(startDateTime, endDateTime);
        return makeProveDtos(proves);
    }

    public List<ProveDtoV2> getProveWithId(Long proveId) {
        Prove prove = proveRepository.findById(proveId).orElseThrow(() -> new IllegalArgumentException());
        List<Prove> proves = new ArrayList<>();
        proves.add(prove);
        return makeProveDtos(proves);
    }

    // Prove 리스트를 현재 날짜와 가장 가까운 endTime 순서로 정렬하는 메서드
    public void sortProvesByEndTime(List<Prove> proves) {
        LocalDateTime now = LocalDateTime.now();
        proves.sort((p1, p2) -> {
            long diff1 = Duration.between(now, p1.getEndTime()).abs().toMillis();
            long diff2 = Duration.between(now, p2.getEndTime()).abs().toMillis();
            return Long.compare(diff1, diff2);
        });
    }


    public Map<YearMonth, Map<String, Integer>> getMonthlySuccessFailureStats(int year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59);
        System.out.println(startOfYear);
        System.out.println(endOfYear);
        List<Prove> proves = proveRepository.findAllByEndTimeBetweenAndUserId(startOfYear, endOfYear, getUsername());

        for (Prove prove : proves) {
            System.out.println(prove);
        }

        Map<YearMonth, Map<String, Integer>> stats = new HashMap<>();
        for (Prove prove : proves) {
            YearMonth month = YearMonth.from(prove.getEndTime());
            stats.putIfAbsent(month, new HashMap<>());
            Map<String, Integer> monthStats = stats.get(month);
            monthStats.put("success", monthStats.getOrDefault("success", 0) + ("NotYet".equals(prove.getSuccess()) ? 0 : 1));
            monthStats.put("unsuccess", monthStats.getOrDefault("unsuccess", 0) + ("NotYet".equals(prove.getSuccess()) ? 1 : 0));
        }
        return stats;
    }


    public void likePost(Long postId) {
        Prove prove = proveRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        String username = getUsername();
        UserEntity userEntity = userRepository.findByUsername(username);

        if (likeRepository.existsByProveAndUser(prove, userEntity)) {
            throw new RuntimeException("User has already liked this post");
        }

        Like like = new Like();
        like.setProve(prove);
        like.setUser(userEntity);
        likeRepository.save(like);
    }

    public boolean hasUserLikedPost(Long proveId) {
        Prove prove = proveRepository.findById(proveId).orElseThrow(() -> new RuntimeException("Post not found"));
        String username = getUsername();
        UserEntity userEntity = userRepository.findByUsername(username);

        return likeRepository.existsByProveAndUser(prove, userEntity);
    }


    public List<ProveDtoV2> getAllProve(HttpServletRequest request) {
        //1.로그인 유무 확인
        String username = getUsername();
        if(!username.equals("anonymousUser")){
            System.out.println("hi");
            //로그인이 되어있는 상태
            UserEntity userEntity = userRepository.findByUsername(username);
            List<Tags> userTags = userEntity.getTags();
            System.out.println(userTags);
            return getProveDtowithTags(userTags);
        }else {
            Optional<List<Tags>> tags = getTags(request);
            if (!tags.isEmpty()) {
                System.out.println("hiiii");
                // 'tags' 쿠키가 존재할 때
                List<Tags> tagList = tags.get();
                for (Tags tags1 : tagList) {
                    System.out.println(tags1);
                }
                return getProveDtowithTags(tagList);
            }
        }
        // 'tags' 쿠키가 존재하지 않을 때 그냥 모든 prove 최신순으로 가져오기
        System.out.println("helloo");
        List<Prove> proves = proveRepository.findAll();
        sortProvesByEndTime(proves);
        return makeProveDtos(proves);
    }

    public List<ProveDtoV2> getAllProveWithString(String tag) {
        //1.로그인 유무 확인
        String username = getUsername();
        if(!username.equals("anonymousUser")){
            System.out.println("hi");
            //로그인이 되어있는 상태
            UserEntity userEntity = userRepository.findByUsername(username);
            List<Tags> userTags = userEntity.getTags();
            System.out.println(userTags);
            return getProveDtowithTags(userTags);
        }else if(!tag.isBlank()){
            List<Tags> tags = new ArrayList<>();
            String[] tagArray = tag.split("\\."); // '.'으로 파싱
            for (String t : tagArray) {
                Tags tagEnum = Tags.valueOf(t); // 문자열을 Tags로 변환
                tags.add(tagEnum); // 리스트에 추가
            }
            return getProveDtowithTags(tags);
        }
        // 'tags' 쿠키가 존재하지 않을 때 그냥 모든 prove 최신순으로 가져오기
        System.out.println("helloo");
        List<Prove> proves = proveRepository.findAll();
        sortProvesByEndTime(proves);
        return makeProveDtos(proves);
    }

    private static Optional<List<Tags>> getTags(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            System.out.println("not null");
            for (Cookie cookie : cookies) {
                if ("tags".equals(cookie.getName())) {
                    System.out.println("split");
                    System.out.println(cookie.getValue());
                    // 'tags' 쿠키가 존재하면 값을 가져와서 Tags 열거형 리스트로 변환합니다.
                    List<Tags> tags = Arrays.stream(cookie.getValue().split("\\."))
                            .map(Tags::valueOf)
                            .collect(Collectors.toList());
                    System.out.println(tags);  // 디버깅을 위해 리스트 출력
                    return Optional.of(tags);
                }
            }
        }
        return Optional.empty();
    }



    private List<ProveDtoV2> getProveDtowithTags(List<Tags> userTags) {
        for (Tags userTag : userTags) {
            System.out.println(userTag);
        }
        System.out.println("여기까지 왔음");
        List<Prove> proves = proveRepository.findByTagsIn(userTags);
        System.out.println("이거돼나?");
        // 중복 제거
        List<Prove> uniqueProves = proves.stream()
                .distinct()
                .collect(Collectors.toList());

        // 날짜로 정렬
        sortProvesByEndTime(uniqueProves);

        // DTO로 변환
        return makeProveDtos(proves);
    }

    public PagedDTO<List<ProveDtoV2>> getAllProveWithStringWithPagable(Pageable pageable, String tag) {
        List<Tags> tags = new ArrayList<>();
        String[] tagArray = tag.split("\\."); // '.'으로 파싱

        for (String t : tagArray) {
            Tags tagEnum = Tags.valueOf(t); // 문자열을 Tags로 변환
            System.out.println(tagEnum);
            tags.add(tagEnum); // 리스트에 추가
        }

        System.out.println("Hello");
        //prove가져옴
        Page<Prove> proves = proveRepository.findAByTagAndPage(tags,pageable);

        PageMetaData pageMetaData = new PageMetaData(proves.getSize(),
                proves.getTotalElements(), proves.getTotalPages(), proves.getNumber());

        List<Prove> proveList = proves.getContent();
        System.out.println(proveList);
        //DTO변환
        List<ProveDtoV2> proveDtoV2s = makeProveDtos(proveList);
        sortProveDtoByEndTime(proveDtoV2s);

        return PagedDTO.<List<ProveDtoV2>>builder()
                .content(proveDtoV2s)
                .pageMetaData(pageMetaData)
                .build();
    }

    private void sortProveDtoByEndTime(List<ProveDtoV2> proves) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        proves.sort((p1, p2) -> {
            long diff1 = Duration.between(now, p1.getEndTime()).abs().toMillis();
            long diff2 = Duration.between(now, p2.getEndTime()).abs().toMillis();
            return Long.compare(diff1, diff2);
        });
    }

    public ProveWithF_CNTDTO getProveWithF_CNTDTO(String username) {
        ProveWithF_CNTDTO proveWithFCntdto = new ProveWithF_CNTDTO();

        UserEntity userEntity = userRepository.findByUsername(username);
        List <Prove> proves = userEntity.getProves();
        List<ProveDtoV2> proveDtoV2s = makeProveDtos(proves);

        proveWithFCntdto.setProveDtoV2(proveDtoV2s);
        proveWithFCntdto.setFriendCnt(userEntity.getFriends().size());
        return proveWithFCntdto;
    }
}
