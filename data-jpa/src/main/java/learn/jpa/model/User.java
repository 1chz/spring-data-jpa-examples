package learn.jpa.model;

import lombok.*;

import javax.persistence.*;

@Entity
@ToString @Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user",                                                              // 데이터베이스 테이블명
        indexes = {@Index(columnList = "username")},                                // 해당 컬럼으로 인덱스를 생성
        uniqueConstraints = @UniqueConstraint(columnNames = {"username", "email "}) // 해당 컬럼으로 복합 유니크 인덱스를 생성
)
public class User extends BaseEntity {
    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    @Column(unique = true) // 단일 컬럼 유니크 인덱스를 생성
    private String email;
}
