package com.prove.domain.Prove;

import com.prove.domain.PagedDTO;
import com.prove.domain.Prove.Dto.*;
import com.prove.domain.image.ImageService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

//TODO 친구username을 가지고, getAllMyProve에서 친구 username으로 가져오기 proveDto 가져오고, 친구 숫자만 따로 추가해서 가져오기-완료

@RequiredArgsConstructor
@RestController
public class ProveController {
    private final ProveService proveService;
    private final ProveRepository proveRepository;
    private final ImageService imageService;

    //계획 만들기-완료
    @PostMapping("/api/prove")
    public ResponseEntity<?> createProve(@RequestPart("proveDto") ProveDto proveDto){
        proveService.create(proveDto);
        return new ResponseEntity<>("prove계획을 만들었습니다.",HttpStatus.OK);
    }

    //계획 완료시-완료
    @PutMapping("/api/completeProve/{proveId}")
    public ResponseEntity<?> completeProve(@RequestPart("proveDto") ProveDto proveDto,
                            @RequestPart("uploadImgs") @Nullable List<MultipartFile> uploadImages,@PathVariable Long proveId){
        proveService.completeProve(proveDto,uploadImages,proveId);
        return new ResponseEntity<>("Prove 성공을 축하합니다.",HttpStatus.OK);
    }

    //계획표 가져오는거-완료
    @GetMapping("/api/TodayProve/{date}")
    public List<ProveDtoV2> getTodayProve(@PathVariable("date") String dateStr){
        return proveService.getTodayProve(dateStr);
    }

    //proveID로 가져오기 - 완료
    @GetMapping("/api/{proveId}")
    public List<ProveDtoV2> prove(@PathVariable Long proveId){
        return proveService.getProveWithId(proveId);
    }

    //계획 수정 모달에 필요한 수정- 완료
    @PutMapping("/api/{proveId}")
    public ResponseEntity<?> editProve(@RequestPart("proveDto") ProveDto proveDto,
                                @RequestPart("uploadImgs") @Nullable List<MultipartFile> uploadImages,@PathVariable Long proveId){
        if (uploadImages==null){
            System.out.println("이미지 안보냈음");
        }
        proveService.editProve(proveDto,uploadImages,proveId);
        return new ResponseEntity<>("Prove 수정을 완료했습니다.",HttpStatus.OK);
    }

    //prove 완료 상태에서 이미지 수정하고 싶을때, 삭제 버튼 올려두고, 삭제 누르면, 삭제됨, 실수로 눌러도 다시 추가시 editProve
    //메서드에서 자동적으로 삽입됨
    // 리스트 보내면 이미지 한번에 지우기
    @DeleteMapping("/api/image")
    public ResponseEntity<?>  deleteImage(@RequestBody List<Long> imageIds){
        imageService.deleteImage(imageIds);
        return new ResponseEntity<>("image 삭제에 성공했습니다.",HttpStatus.OK);
    }

    //prove 자체를 삭제하는 컨트롤러
    @DeleteMapping("/api/prove/{proveId}")
    public ResponseEntity<?> deleteProve(@PathVariable Long proveId){
        Optional<Prove> prove  = proveRepository.findById(proveId);
        proveService.deleteProve(prove);
        return new ResponseEntity<>("prove 삭제에 성공했습니다.",HttpStatus.OK);
    }

    //로그인 하지 않은 상태에서 접속시 Tags를 쿠키에 넣어서 하는 방법 밖에 없긴함
    //로그인을 하지 않은상태 all page접속시 1. tags를 띄움 2. 프론트에서 exercise,study 쿠키에 저장, 3. 요청시
    //백단에서 1. 로그인이 되어있는지 확인 2. 쿠키를 확인 3. 쿠키 없으면 전부다.
    @GetMapping("/api/allProves")
    public List<ProveDtoV2> getAllProves(HttpServletRequest request){
        return proveService.getAllProve(request);
    }


    //Pageable안쓴거
    @GetMapping("/api/allProves/v2/{tags}")
    public List<ProveDtoV2> getAllProvesv2(@PathVariable String tags){
        return proveService.getAllProveWithString(tags);
    }

    //Pageable쓴거
    @GetMapping("/api/allProves/v3/{tags}")
    public PagedDTO<List<ProveDtoV2>> getAllProvesv2withPageable(@PageableDefault(page = 0, size = 5) Pageable pageable , @PathVariable String tags){
        return proveService.getAllProveWithStringWithPagable(pageable,tags);
    }

    //pageable? 쓰자
    //TODO Pageing--완료
    @GetMapping("/api/myProves")
    public PagedDTO<List<ProveDtoV2>> getAllMyProve(@PageableDefault(page = 0, size = 5) Pageable pageable){
        return proveService.getAllMyProve(pageable);
    }

    @PostMapping("/api/like/{proveId}")
    public ResponseEntity<?> addLike(@PathVariable Long proveId){
        proveService.likePost(proveId);
        return new ResponseEntity<>("좋아요 추가!",HttpStatus.OK);
    }

    @GetMapping("/{proveId}/liked")
    public ResponseEntity<?> hasUserLikedPost(@PathVariable Long proveId) {
        boolean liked = proveService.hasUserLikedPost(proveId);
        return new ResponseEntity<>(liked, HttpStatus.OK);
    }

    //알고리즘적인 측면 고려 -> 현재 날짜와 가까운 prove들만 가져옴
    //TODO 준수한테 이거 쓰는지 물어보기
    @GetMapping("/api/friend/prove")
    public List<ProveDtoV2> getFriendProve(){
        return proveService.getFriendProve();
    }


    @GetMapping("/api/prove/{username}")
    public ProveWithF_CNTDTO getPCNDTO(@PathVariable String username){
        return proveService.getProveWithF_CNTDTO(username);
    }

}
