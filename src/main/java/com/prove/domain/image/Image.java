package com.prove.domain.image;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.prove.domain.Prove.Prove;
import jakarta.persistence.*;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imgName;

    @Column(nullable = false)
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn
    private Prove prove;

    //이거 없어도 되나?
    public Image(String imgName, String imgUrl, Prove prove) {
            this.imgName = imgName;
            this.imgUrl = imgUrl;
            this.prove = prove;
    }
}

