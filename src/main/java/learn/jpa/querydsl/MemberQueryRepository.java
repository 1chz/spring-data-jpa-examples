package learn.jpa.querydsl;

import learn.jpa.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberQueryRepository extends JpaRepository<Member, Long> {
}
