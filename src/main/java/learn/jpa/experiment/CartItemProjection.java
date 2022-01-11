package learn.jpa.experiment;

import lombok.ToString;

import java.util.List;

public interface CartItemProjection {
    Long getId();

    List<ItemDto> getItems();

    interface ItemDto {
        Long getId();

        String getName();

        String getDescription();
    }
}
