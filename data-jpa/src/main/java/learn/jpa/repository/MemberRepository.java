package learn.jpa.repository;

import learn.jpa.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data-JPA 가 지원하는 Return Type 참고 자료
 *
 * @see "Han-Changhun/src/test/resources/query-method-0.png"
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 수식어를 생략해도 findByNameIs, findByNameEquals 와 같이 동작함(==조건검색)
    Member findByName(String name);

    Member getByName(String name);

    Member readByName(String name);

    Member queryByName(String name);

    Member searchByName(String name);

    Member streamByName(String name);

    Member findFirst1ByName(String name);

    Member findTop1ByName(String name);

    List<Member> findTop2ByName(String name);

    Member findByNameAndAge(String name, int age);

    List<Member> findByNameOrAge(String name, int age);

    List<Member> findByIdAfter(Long id);

    List<Member> findByIdBefore(Long id);

    List<Member> findByIdIsLessThanEqual(Long id);

    List<Member> findByIdGreaterThanEqual(Long id);

    List<Member> findByAgeBetween(int age1, int age2);

    List<Member> findByIdIsNotNull();

    List<Member> findByAgeIn(List<Integer> ages);

    List<Member> findByNameStartingWith(String name);

    List<Member> findByNameEndingWith(String name);

    List<Member> findByNameContaining(String name);

    List<Member> findFirst2ByNameOrderByIdDesc(String name);

    Page<Member> findByName(String name, Pageable pageable);
}
