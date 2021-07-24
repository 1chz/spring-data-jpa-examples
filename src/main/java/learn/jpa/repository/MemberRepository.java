package learn.jpa.repository;

import learn.jpa.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data-JPA 가 지원하는 Return Type 참고 자료
 *
 * @see "Han-Changhun/src/test/resources/query-method-0.png"
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    @EntityGraph(attributePaths = "team")
    Member findByName(String name);
}
