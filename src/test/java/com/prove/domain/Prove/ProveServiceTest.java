package com.prove.domain.Prove;

import com.prove.domain.AwsS3.AwsS3Service;
import com.prove.domain.Prove.Dto.ProveDto;
import com.prove.domain.Prove.Prove;
import com.prove.domain.Prove.ProveRepository;
import com.prove.domain.Prove.ProveService;
import com.prove.domain.User.UserEntity;
import com.prove.domain.User.UserRepository;
import com.prove.domain.comment.CommentRepository;
import com.prove.domain.image.ImageDto;
import com.prove.domain.image.ImageRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProveServiceTest {

    @Mock
    private ProveRepository proveRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private AwsS3Service awsS3Service;

    @InjectMocks
    private ProveService proveService;

    private ProveDto proveDto;
    private List<MultipartFile> uploadImages;
    private Long proveId;
    private Prove prove;
    private UserEntity user;

    SecurityContext securityContext;
    Authentication authentication;
    @BeforeEach
    public void setUp() {
        proveDto = new ProveDto(
                "open",
                1L,
                "short description",
                "success",
                "tag1,tag2",
                10L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );// 필요한 필드 설정
        uploadImages = new ArrayList<>();
        uploadImages.add(new MockMultipartFile("image1", "image1.jpg", "image/jpeg", new byte[0]));

        prove = new Prove(); // 필요한 필드 설정
        prove.setShortWord("test_Shortword");
        user = new UserEntity(); // 필요한 필드 설정
        prove.setId(1L);
        prove.setUser(user);
        // SecurityContext와 Authentication 객체를 모킹
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);

        // SecurityContextHolder에 모킹된 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    public void testCompleteProve() {
        // Mocking
        when(proveRepository.findById(proveId)).thenReturn(Optional.of(prove));
        when(awsS3Service.uploadFile(any(MultipartFile.class))).thenReturn(new ImageDto("image1.jpg", "http://example.com/image1.jpg"));
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        // 모킹된 Authentication 객체를 SecurityContext에 설정
        when(securityContext.getAuthentication()).thenReturn(authentication);
        // 인증된 사용자의 이름을 "testuser"로 설정
        when(authentication.getName()).thenReturn("testuser");

        // Method call
        proveService.completeProve(proveDto, uploadImages, proveId);

        // Verify interactions
        assertThat(prove.getShortWord()).isEqualTo("short description");
        boolean containsImage1 = prove.getImgList().stream()
                .anyMatch(image -> "image1.jpg".equals(image.getImgName()));
        System.out.println(prove.getImgList().get(0).getImgName());
        assertThat(containsImage1).isTrue();
    }

    @Test
    void testCreate() {
        // Given
        ProveDto proveDto = new ProveDto();
        proveDto.setOpenOrNot("true");
        proveDto.setImportance(1L);
        proveDto.setShortWord("shortWord");
        proveDto.setTags("tag1,tag2");
        proveDto.setStartTime(LocalDateTime.of(2024, 6, 21, 10, 0));
        proveDto.setEndTime(LocalDateTime.of(2024, 6, 21, 12, 0));

        UserEntity user = new UserEntity();
        user.setUsername("testuser");

        // Mocking userRepository.findByUsername
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        // 모킹된 Authentication 객체를 SecurityContext에 설정
        when(securityContext.getAuthentication()).thenReturn(authentication);
        // 인증된 사용자의 이름을 "testuser"로 설정
        when(authentication.getName()).thenReturn("testuser");
        // When
        proveService.create(proveDto);

        // Then
        // Prove 객체가 올바르게 생성되었는지 확인
        //ArgumentCaptor 객체를 생성, Prove 클래스를 인수로 전달, Prove 타입의 인수를 캡처할 수 있도록 합니다.
        ArgumentCaptor<Prove> proveCaptor = ArgumentCaptor.forClass(Prove.class);
        //.save -> proveRepository.save(prove)메서드 호출시, 넘겨지는 인자 prove를 캡쳐해둠
        verify(proveRepository, times(1)).save(proveCaptor.capture());

        //캡쳐해둔 prove를 가져옴
        Prove savedProve = proveCaptor.getValue();

        // ProveDto의 값과 저장된 Prove 객체의 값이 동일한지 검증
        assertEquals(proveDto.getOpenOrNot(), savedProve.getOpenOrNot());
        assertEquals(proveDto.getImportance(), savedProve.getImportance());
        assertEquals(proveDto.getTags(), savedProve.getTags());
        assertEquals(proveDto.getStartTime(), savedProve.getStartTime());
        assertEquals(proveDto.getEndTime(), savedProve.getEndTime());
        assertEquals(user, savedProve.getUser());
    }

    @Test
    void testEditProve() {
        when(proveRepository.findById(proveId)).thenReturn(Optional.of(prove));
        when(awsS3Service.uploadFile(any(MultipartFile.class))).thenReturn(new ImageDto("url", "key"));

        proveService.editProve(proveDto, uploadImages, proveId);

        ArgumentCaptor<Prove> proveCaptor = ArgumentCaptor.forClass(Prove.class);
        verify(proveRepository).save(proveCaptor.capture());
        Prove savedProve = proveCaptor.getValue();

        assertNotNull(savedProve);
        //현재 prove에는 shortword가 "test_shortword"로 구성
        assertEquals(proveDto.getShortWord(), savedProve.getShortWord());
        assertEquals(proveDto.getOpenOrNot(), savedProve.getOpenOrNot());
        assertEquals(proveDto.getImportance(), savedProve.getImportance());
        assertEquals(proveDto.getTags(), savedProve.getTags());
        assertEquals(proveDto.getStartTime(), savedProve.getStartTime());
        assertEquals(proveDto.getEndTime(), savedProve.getEndTime());
        assertEquals(1, savedProve.getImgList().size());
    }

    @Test
    void testDeleteProve_Success() {
        Optional<Prove> optionalProve = Optional.of(prove);
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(imageRepository.findAllByProve(prove)).thenReturn(new ArrayList<>());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");

        proveService.deleteProve(optionalProve);
        verify(proveRepository).deleteById(prove.getId());
    }

    @Test
    void testDeleteProve_NotOwner() {
        UserEntity anotherUser = new UserEntity();
        anotherUser.setUsername("anotheruser");
        prove.setUser(anotherUser);
        Optional<Prove> optionalProve = Optional.of(prove);
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");

        SecurityException thrown = assertThrows(
                SecurityException.class,
                () -> proveService.deleteProve(optionalProve)
        );

        assertEquals("이 사용자는 해당 증명을 삭제할 권한이 없습니다.", thrown.getMessage());
        verify(proveRepository, never()).deleteById(anyLong());
    }
}