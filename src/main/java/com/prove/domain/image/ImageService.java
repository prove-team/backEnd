package com.prove.domain.image;

import com.prove.domain.AwsS3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final AwsS3Service awsS3Service;
    public void deleteImage(List<Long> imageIds) {
        for (Long imageId : imageIds) {
            Image image = imageRepository.findById(imageId).orElseThrow(() -> new IllegalArgumentException());
            awsS3Service.deleteFile(image.getImgName());
            imageRepository.deleteById(image.getId());
        }
    }
}
