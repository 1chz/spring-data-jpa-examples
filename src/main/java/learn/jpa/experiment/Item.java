package learn.jpa.experiment;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @CreatedDate
    private LocalDateTime createdAt;

    private Item(String name, String description, LocalDateTime createdAt) {
        this(null, name, description, createdAt);
    }

    @Builder
    private Item(Long id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Item of(Long id, String name, String description, LocalDateTime createdAt) {
        return new Item(id, name, description, createdAt);
    }
}
