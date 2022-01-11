package learn.jpa.experiment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.HashSet;

@DataJpaTest
class CartItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @BeforeEach
    void setUp() {
        CartItem cartItem = null;
        for (int i = 1; i <= 100; i++) {
            if (cartItem == null) {
                cartItem = CartItem.of(null, new HashSet<>());
            }
            if (cartItem.isTenBundles()) {
                cartItemRepository.save(cartItem);
                cartItem = null;
            }
            else {
                cartItem.addItem(createItem(i));
            }
        }
    }

    @Test
    void findByIdAfter_interface() throws Exception {
        cartItemRepository.findByIdAfter(5L).forEach(System.out::println);
    }

    @Test
    void findByIdAfter_dynamic() throws Exception {
        cartItemRepository.findByIdAfter(5L, CartItemProjection.class)
                .forEach(System.out::println);
    }

    private Item createItem(int itemName) {
        return Item.builder()
                .name("item" + itemName)
                .description("item description")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
