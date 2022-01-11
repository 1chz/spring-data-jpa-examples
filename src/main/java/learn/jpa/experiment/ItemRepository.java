package learn.jpa.experiment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // 슬라이스는 페이저블로 제공한 limit보다 1만큼의 레코드를 더 조회한 후 레코드가 있으면 다음 페이지가 있다고 판단한다
    Slice<Item> readAllByNameContaining(String name, Pageable pageable);

    // 페이저블로 제공한 limit 카운트보다 실제 반환되는 레코드가 적으면 카운트쿼리가 발생하지 않음
    // 페이지가 슬라이스를 상속받으며, getTotalPages, getTotalElements를 추가로 선언함
    Page<Item> findAllByNameContaining(String name, Pageable pageable);

    // DTO 프로젝션
    // 원하는 필드가 생성자에 추가돼있어야만 한다
    List<ItemDto> findByNameContaining(String name);
}
