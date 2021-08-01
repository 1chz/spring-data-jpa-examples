package learn.jpa.repository;

import learn.jpa.model.Simple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * Data-JPA 가 지원하는 Return Type 참고 자료
 *
 * @see "Han-Changhun/src/test/resources/query-method-0.png"
 */
public interface SimpleRepository extends JpaRepository<Simple, Long> {
    // 수식어를 생략해도 findByNameIs, findByNameEquals 와 같이 동작함(==조건검색)
    Simple findByName(String name);

    Simple getByName(String name);

    Simple readByName(String name);

    Simple queryByName(String name);

    Simple searchByName(String name);

    Simple streamByName(String name);

    Simple findFirst1ByName(String name);

    Simple findTop1ByName(String name);

    List<Simple> findTop2ByName(String name);

    Simple findByNameAndAge(String name, int age);

    List<Simple> findByNameOrAge(String name, int age);

    List<Simple> findByIdAfter(Long id);

    @Modifying
    List<Simple> findByIdBefore(Long id);

    List<Simple> findByIdIsLessThanEqual(Long id);

    List<Simple> findByIdGreaterThanEqual(Long id);

    List<Simple> findByAgeBetween(int age1, int age2);

    List<Simple> findByIdIsNotNull();

    List<Simple> findByAgeIn(List<Integer> ages);

    List<Simple> findByNameStartingWith(String name);

    List<Simple> findByNameEndingWith(String name);

    List<Simple> findByNameContaining(String name);

    List<Simple> findFirst2ByNameOrderByIdDesc(String name);

    Page<Simple> findByName(String name, Pageable pageable);
}
