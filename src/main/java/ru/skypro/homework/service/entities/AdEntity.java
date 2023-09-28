package ru.skypro.homework.service.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "ad")
public class AdEntity {

    @Column(name = "pk", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int pk;

    @ManyToOne
    @JoinColumn(name = "id")
    UserEntity user; //user

    @Column(name = "price", nullable = false)
    int price;

    @Column(name = "title", nullable = false)
    int title;

    @Column(name = "image", nullable = false)
    String image;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "phone", nullable = false)
    String phone;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "adEntity", cascade = CascadeType.ALL)
    List<CommentEntity> commentEntityList = new ArrayList<>();
}
