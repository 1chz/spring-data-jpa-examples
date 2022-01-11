package learn.jpa.experiment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItemProjection> findByIdAfter(Long id);

    <T> List<T> findByIdAfter(Long id, Class<T> classType);
}
