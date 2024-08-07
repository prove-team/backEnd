package com.prove.domain.image;

import com.prove.domain.AwsS3.AwsS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private AwsS3Service awsS3Service;

    @InjectMocks
    private ImageService imageService;

    @Test
    public void testDeleteImage() {
        Image image = new Image();
        image.setId(1L);
        image.setImgName("test-image.jpg");
        List<Long> imgIds = new ArrayList<>();
        imgIds.add(1L);

        // Mocking
        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

        // 메서드 호출
        imageService.deleteImage(imgIds);

        // 검증
        verify(awsS3Service, times(1)).deleteFile("test-image.jpg");
        verify(imageRepository, times(1)).deleteById(1L);
    }
}