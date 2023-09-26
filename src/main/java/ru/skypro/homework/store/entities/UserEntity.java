package ru.skypro.homework.store.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "phone", nullable = false)
    String phone;

    @Column(name = "image", nullable = false)
    String image;

    @Column(name = "username", nullable = false)
    String username;

    @Column(name = "password", nullable = false)
    String password;

    @NonNull
    @Enumerated(EnumType.STRING)
    Role role;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private List<AdEntity> ads = new ArrayList<>();

    @OneToOne(targetEntity = ImageIEntity.class, fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_image_id")
    private ImageIEntity imageIEntity;


    public static UserEntity makeDefaults(
            String lastName,
            String firstName,
            String email,
            String phone,
            String image,
            String username,
            String password,
            Role role) {
        return builder()
                .lastName(lastName)
                .firstName(firstName)
                .email(email)
                .phone(phone)
                .image(image)
                .username(username)
                .password(password)
                .role(role)
                .build();
    }
}
