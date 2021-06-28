package learn.jpa.model;

import learn.jpa.type.Gender;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user",                                                              // 데이터베이스 테이블명
        indexes = {@Index(columnList = "username")},                                // 해당 컬럼으로 인덱스를 생성
        uniqueConstraints = @UniqueConstraint(columnNames = {"username", "email"}) // 해당 컬럼으로 복합 유니크 인덱스를 생성
        )
public class User extends BaseEntity {
    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String email;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true) // 단일 컬럼 유니크 인덱스를 생성
    private String address;

    @Builder
    public static User createUser(@NonNull String username, @NonNull String password, @NonNull String email, @NonNull Gender gender) {
        return new User(username, password, email, gender);
    }

    public void changeUserName(String username) {
        this.username = username;
    }

    /*----------------------------------------------------
            JPA Listener: Event Driven Methods
     ----------------------------------------------------*/
    @PrePersist
    public void prePersist() {
        System.out.println(">>> prePersist <<<");
    }

    @PostPersist
    public void postPersist() {
        System.out.println(">>> postPersist <<<");
    }

    @PreUpdate
    public void preUpdate() {
        System.out.println(">>> preUpdate <<<");
    }

    @PostUpdate
    public void postUpdate() {
        System.out.println(">>> postUpdate <<<");
    }

    @PreRemove
    public void preRemove() {
        System.out.println(">>> preRemove <<<");
    }

    @PostRemove
    public void postRemove() {
        System.out.println(">>> postRemove <<<");
    }

    @PostLoad
    public void postLoad() {
        System.out.println(">>> postLoad <<<");
    }
}
