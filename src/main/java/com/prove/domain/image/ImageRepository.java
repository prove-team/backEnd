package com.prove.domain.image;

import com.prove.domain.Prove.Prove;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository <Image,Long> {
    List<Image> findAllByProve(Prove prove);
}
