package learn.jpa.experiment;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@AllArgsConstructor
public class ItemDto {
    private String name;
    private LocalDateTime createdAt;
}
