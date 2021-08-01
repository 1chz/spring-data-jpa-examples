package learn.jpa.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import learn.jpa.config.JpaConfig;
import learn.jpa.dto.MemberDto;
import learn.jpa.dto.QMemberDto;
import learn.jpa.fixture.Fixture;
import learn.jpa.model.Member;
import learn.jpa.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.PersistenceUnitUtil;
import java.util.List;
import java.util.Objects;

import static learn.jpa.model.QMember.member;
import static learn.jpa.model.QTeam.team;

@DataJpaTest
@Import(JpaConfig.class)
class MemberQueryRepositoryTest {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private final MemberRepository memberRepository;
    private final JPAQueryFactory queryFactory;
    private final TestEntityManager entityManager;

    MemberQueryRepositoryTest(MemberRepository memberRepository, JPAQueryFactory queryFactory, TestEntityManager entityManager) {
        this.memberRepository = memberRepository;
        this.queryFactory = queryFactory;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        Fixture fixture = Fixture.getInstance();
        List<Member> members = fixture.createMembers();
        members.forEach(member -> member.changeTeam(fixture.createTeam()));
        memberRepository.saveAllAndFlush(members);
        entityManager.clear();
    }

    @Test
    void search() throws Exception {
        queryFactory
                .selectFrom(member).fetch();
    }

    @Test
    void searchAndParam() throws Exception {
        queryFactory
                .selectFrom(member)
                .where(
                        member.name.eq("siro"),
                        member.age.eq(10)
                      )
                .fetchOne();
    }

    @Test
    void paging_1() throws Exception {
        queryFactory
                .selectFrom(member)
                .orderBy(member.name.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
    }

    @Test
    void paging_2() throws Exception {
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .orderBy(member.name.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        List<Member> content = results.getResults();
        long totalCount = results.getTotal();
    }

    @Test
    void aggregation() throws Exception {
        List<Tuple> tuples = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                       )
                .from(member)
                .fetch();
    }

    @Test
    void join() throws Exception {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .fetch();

        PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();

        for (Member member : fetch) {
            System.out.println("persistenceUnitUtil.isLoaded(member) = " + persistenceUnitUtil.isLoaded(member)); // true
            System.out.println("persistenceUnitUtil.isLoaded(member.getTeam()) = " + persistenceUnitUtil.isLoaded(member.getTeam())); // false
        }
    }

    @Test
    void outerJoin() throws Exception {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .leftJoin(member.team, team)
                .fetch();

        PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();

        for (Member member : fetch) {
            System.out.println("persistenceUnitUtil.isLoaded(member) = " + persistenceUnitUtil.isLoaded(member)); // true
            System.out.println("persistenceUnitUtil.isLoaded(member.getTeam()) = " + persistenceUnitUtil.isLoaded(member.getTeam())); // false
        }
    }

    @Test
    void fetchJoin() throws Exception {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .fetchJoin()
                .fetch();

        PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();

        for (Member member : fetch) {
            System.out.println("persistenceUnitUtil.isLoaded(member) = " + persistenceUnitUtil.isLoaded(member)); // true
            System.out.println("persistenceUnitUtil.isLoaded(member.getTeam()) = " + persistenceUnitUtil.isLoaded(member.getTeam())); // true
        }
    }

    @Test
    void thetaJoin() throws Exception {
        queryFactory
                .select(member)
                .from(member, team)
                .where(member.name.eq(team.name)) // 세타조인(크로스조인)시 join 조건절 역할을 하는 where이 없거나 잘못된 경우 카테시안 곱(카르테시안 곱)이 발생한다
                .fetch();
    }

    @Test
    @DisplayName("간단한 스위치")
    void simpleCase() throws Exception {
        List<String> members = queryFactory
                .select(member.age
                                .when(10).then("열살")
                                .when(20).then("스무살")
                                .otherwise("기타")
                       )
                .from(member)
                .fetch();
    }

    @Test
    @DisplayName("복잡한 스위치")
    void complexCase() throws Exception {
        List<String> members = queryFactory
                .select(new CaseBuilder()
                                .when(member.name.startsWith("김")).then("김씨")
                                .when(member.name.startsWith("이")).then("이씨")
                                .otherwise("기타"))
                .from(member)
                .fetch();
    }

    @Test
    @DisplayName("문자열, 상수 이어 붙이기")
    void concat() throws Exception {
        List<String> members = queryFactory
                .select(member.name.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.name.eq("siro"))
                .fetch();
    }

    /**
     * 프로젝션을 사용할 경우 필드명이 다른 경우 as를 사용해 필드명을 맞춰주면 된다.
     * 예를 들자면, Member 엔티티의 값을 꼭 MemberDto에 넣어야만 되는 것이 아니다. 다양한 DTO에 넣어줄 수 있다.
     * <pre>
     *
     *     queryFactory
     *          .select(Projections.fields(UserDto.class,
     *                  member.username.as("name"),
     *                  member.age.as("age")
     *                  ))
     *          .fetch();
     *
     * </pre>
     * <p>
     * 또한, 프로젝션 사용 시 DTO의 기본 생성자가 반드시 public 이어야만 하는 단점이 있다.
     */
    @Test
    @DisplayName("수정자 프로젝션")
    void projection_property() throws Exception {
        List<MemberDto> memberDtos = queryFactory
                .select(Projections.bean(MemberDto.class, member.name, member.age))
                .from(member)
                .fetch();
    }

    @Test
    @DisplayName("필드 프로젝션")
    void projection_field() throws Exception {
        List<MemberDto> memberDtos = queryFactory
                .select(Projections.fields(MemberDto.class, member.name, member.age))
                .from(member)
                .fetch();
    }

    /**
     * 생성자 프로젝션의 경우 생성자의 접근제한자가 public 이어야만 하는 단점이 있다.
     */
    @Test
    @DisplayName("생성자 프로젝션")
    void projection_constructor() throws Exception {
        List<MemberDto> memberDtos = queryFactory
                .select(Projections.constructor(MemberDto.class, member.name, member.age))
                .from(member)
                .fetch();
    }

    @Test
    @DisplayName("@QueryProjection 를 사용한 프로젝션")
    void projection_query_projection() throws Exception {
        List<MemberDto> memberDtos = queryFactory
                .select(new QMemberDto(member.name, member.age))
                .from(member)
                .fetch();
    }

    @Test
    void dynamicQuery_booleanBuilder() throws Exception {
        queryFactory
                .selectFrom(member)
                .where(createBooleanBuilder("siro", null))
                .fetch();
    }

    private BooleanBuilder createBooleanBuilder(String name, Integer age) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (Objects.nonNull(name)) {
            booleanBuilder.and(member.name.eq(name));
        }

        if (Objects.nonNull(age)) {
            booleanBuilder.and(member.age.eq(age));
        }

        return booleanBuilder;
    }

    @Test
    @DisplayName("where 절은 null 을 무시하기 때문에 이런 방식이 가능하다")
    void dynamicQuery_whereParam() throws Exception {
        List<Member> members = queryFactory
                .selectFrom(member)
                .where(allEq("siro", null))
                .fetch();
    }

    // 이런 방식의 경우 계속해서 재사용할 수 있다는 큰 장점이 있다.
    private BooleanExpression nameEq(String name) {
        return Objects.isNull(name) ? null : member.name.eq(name);
    }

    private BooleanExpression ageEq(Integer age) {
        return Objects.isNull(age) ? null : member.age.eq(age);
    }

    private BooleanExpression allEq(String name, Integer age) {
        return nameEq(name).and(ageEq(age));
    }

    @Test
    @DisplayName("벌크 업데이트")
    void bulkUpdate() throws Exception {
        queryFactory
                .update(member)
                .set(member.age, 100)
                .where(member.age.lt(35))
                .execute();

        queryFactory
                .selectFrom(member)
                .fetch();
    }

    @Test
    @DisplayName("벌크 삭제")
    void bulkDelete() throws Exception {
        queryFactory
                .delete(member)
                .where(member.age.eq(29))
                .execute();
    }

    /**
     * org.hibernate.dialect.Dialect 의 DB벤더 별 확장 클래스를 찾아서 (H2Dialect같은 것) 사용하고자 하는 함수가 등록돼있는지 확인하고,
     * 사용하려는 함수가 없는 경우(커스텀 함수) 해당 확장 클래스를 한번 더 확장하여 커스텀 함수를 등록한 후 확장한 클래스를 설정파일에 등록해야한다.
     */
    @Test
    @DisplayName("DB 함수")
    void function() throws Exception {
        queryFactory
                .select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})", member.name, "s", "S"))
                .from(member)
                .fetch();
    }

    /**
     * 모든 DB 벤더가 지원하는 ANSI 표준에 있는 기본적인 함수들은 이미 Hibernate가 지원하므로,
     * stringTemplate 을 직접 사용하지 않아도 된다.
     */
    @Test
    void basicAnsiFunction() throws Exception {
        queryFactory
                .select(member.name)
                .from(member)
                .where(member.name.eq(member.name.lower()))
                .fetch();
    }
}
