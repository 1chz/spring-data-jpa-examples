package learn.jpa.experiment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Item> items = new HashSet<>();

    private CartItem(Long id, Set<Item> items) {
        this.id = id;
        this.items = items;
    }

    public static CartItem of(Long id, Set<Item> items) {
        return new CartItem(id, items);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean isTenBundles() {
        return items.size() == 10;
    }
}
