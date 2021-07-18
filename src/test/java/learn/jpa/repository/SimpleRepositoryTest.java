package learn.jpa.repository;

import learn.jpa.model.Simple;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import static org.springframework.data.domain.ExampleMatcher.matching;
import static org.springframework.data.domain.Sort.Order;
import static org.springframework.data.domain.Sort.by;

@DataJpaTest
@RequiredArgsConstructor
class SimpleRepositoryTest {
    private final SimpleRepository simpleRepository;
    private final TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        List<Simple> Simples = List.of(Simple.createSimple("siro", 29),
                                       Simple.createSimple("sophia", 32),
                                       Simple.createSimple("dennis", 25),
                                       Simple.createSimple("james", 41),
                                       Simple.createSimple("michael", 33));

        simpleRepository.saveAllAndFlush(Simples);
    }

    @AfterEach
    void tearDown() {
        entityManager.getEntityManager()
                     .createNativeQuery("ALTER TABLE `SIMPLE` ALTER COLUMN `ID` RESTART WITH 1")
                     .executeUpdate();
    }

    private Sort orderByIdDesc() {
        return by(Order.desc("id"));
    }

    @Test
    @DisplayName("Simple_1번을_조회")
    void findById() {
        Simple Simple = simpleRepository.findById(1L)
                                        .orElseThrow(NoSuchElementException::new);
        assertThat(Simple.getName()).isEqualTo("siro");
        assertThat(Simple.getAge()).isEqualTo(29);
    }

    @Test
    @DisplayName("Simple_1번_3번을_조회")
    void findAllById() {
        List<Simple> Simples = simpleRepository.findAllById(Lists.newArrayList(1L, 3L));
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("dennis", 25))
                           .size().isEqualTo(2);
    }

    @Test
    @DisplayName("Simple_초기_데이터는_5명")
    void findAll() {
        List<Simple> Simples = simpleRepository.findAll();
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("sophia", 32),
                                     tuple("dennis", 25),
                                     tuple("james", 41),
                                     tuple("michael", 33))
                           .size().isEqualTo(5);
    }

    @Test
    @DisplayName("Simple_1번을_제거")
    void deleteById() {
        simpleRepository.deleteById(1L);
        List<Simple> Simples = simpleRepository.findAll();
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("sophia", 32),
                                     tuple("dennis", 25),
                                     tuple("james", 41),
                                     tuple("michael", 33))
                           .size().isEqualTo(4);
    }

    @Test
    @DisplayName("Simple_1번_3번을_제거")
    void deleteAllById() {
        simpleRepository.deleteAllById(Lists.newArrayList(1L, 3L));
        List<Simple> Simples = simpleRepository.findAll();
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("sophia", 32),
                                     tuple("james", 41),
                                     tuple("michael", 33))
                           .size().isEqualTo(3);
    }

    @Test
    @DisplayName("Simple_전체제거")
    void deleteAll() {
        simpleRepository.deleteAll();
        List<Simple> Simples = simpleRepository.findAll();

        assertThat(Simples).isEmpty();
    }

    @Test
    @DisplayName("Simple_Batch_1번_3번을_제거")
    void deleteAllByIdInBatch() {
        simpleRepository.deleteAllByIdInBatch(Lists.newArrayList(1L, 3L));
        List<Simple> Simples = simpleRepository.findAll();
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("sophia", 32),
                                     tuple("james", 41),
                                     tuple("michael", 33))
                           .size().isEqualTo(3);
    }

    @Test
    @DisplayName("Simple_Batch_전체제거")
    void deleteAllInBatch() {
        simpleRepository.deleteAllInBatch();
        List<Simple> Simples = simpleRepository.findAll();
        assertThat(Simples).isEmpty();
    }

    @Test
    @DisplayName("Simple_1번이_존재하는지_확인")
    void existsById() {
        boolean exists = simpleRepository.existsById(1L);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Simple_전체수를_조회")
    void count() {
        long count = simpleRepository.count();
        assertThat(count).isEqualTo(5);
    }

    /**
     * JPA Page는 0부터 시작한다 <br/>
     * <br/>
     * Creates a new unsorted {@link PageRequest}. <br/>
     * page zero-based page index, must not be negative. <br/>
     * the size of the page to be returned, must be greater than 0. <br/>
     * <br/>
     * 참고자료 경로 <br/>
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-0.png"
     */
    @Test
    @DisplayName("Page_API")
    void pageV1() {
        Page<Simple> Simples = simpleRepository.findAll(PageRequest.of(1, 3));
        Pageable pageable = Simples.getPageable();

        Sort sort = Simples.getSort();
        int pageNumber = pageable.getPageNumber();
        int totalPages = Simples.getTotalPages();
        long totalElements = Simples.getTotalElements();
        int numberOfElements = Simples.getNumberOfElements();
        int size = Simples.getSize();

        assertThat(sort.isUnsorted()).isTrue();
        assertThat(pageNumber).isEqualTo(1);
        assertThat(totalPages).isEqualTo(2);
        assertThat(totalElements).isEqualTo(5);
        assertThat(numberOfElements).isEqualTo(2);
        assertThat(size).isEqualTo(3);
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("james", 41),
                                     tuple("michael", 33))
                           .size().isEqualTo(2);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-0.png"
     */
    @Test
    @DisplayName("Query_Methods_Pageable_조회")
    void pageV2() {
        List<Simple> createSimples = new ArrayList<>();
        createSimples.add(Simple.createSimple("siro", 11));
        createSimples.add(Simple.createSimple("siro", 22));
        createSimples.add(Simple.createSimple("siro", 33));
        createSimples.add(Simple.createSimple("siro", 44));
        simpleRepository.saveAllAndFlush(createSimples);

        Page<Simple> Simples = simpleRepository.findByName("siro", PageRequest.of(0, 3, orderByIdDesc()));
        Pageable pageable = Simples.getPageable();

        Sort sort = Simples.getSort();
        int pageNumber = pageable.getPageNumber();
        int totalPages = Simples.getTotalPages();
        long totalElements = Simples.getTotalElements();
        int numberOfElements = Simples.getNumberOfElements();
        int size = Simples.getSize();

        assertThat(sort.isSorted()).isTrue();
        assertThat(pageNumber).isEqualTo(0);
        assertThat(totalPages).isEqualTo(2);
        assertThat(totalElements).isEqualTo(5);
        assertThat(numberOfElements).isEqualTo(3);
        assertThat(size).isEqualTo(3);
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 44),
                                     tuple("siro", 33),
                                     tuple("siro", 22))
                           .size().isEqualTo(3);

        Simples = simpleRepository.findByName("siro", PageRequest.of(1, 3, orderByIdDesc()));
        pageable = Simples.getPageable();

        sort = Simples.getSort();
        pageNumber = pageable.getPageNumber();
        totalPages = Simples.getTotalPages();
        totalElements = Simples.getTotalElements();
        numberOfElements = Simples.getNumberOfElements();
        size = Simples.getSize();

        assertThat(sort.isSorted()).isTrue();
        assertThat(pageNumber).isEqualTo(1);
        assertThat(totalPages).isEqualTo(2);
        assertThat(totalElements).isEqualTo(5);
        assertThat(numberOfElements).isEqualTo(2);
        assertThat(size).isEqualTo(3);
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 11),
                                     tuple("siro", 29))
                           .size().isEqualTo(2);
    }

    @Test
    @DisplayName("Example_API")
    void exampleFindAll() {
        ExampleMatcher matcher = matching()
                .withIgnorePaths("age") // age 는 무시하고 검색한다
                .withMatcher("name", GenericPropertyMatchers.contains()); // name 을 검색조건에 포함시킨다 - like 검색

        /*-----------------------------------------
         조건 검색을 위한 Simple proxy 를 생성한다
         name 에 i가 들어가는 멤버를 조회한다
         age 는 무시되므로 값이 몇이든 의미없다
         -----------------------------------------*/
        Example<Simple> example = Example.of(Simple.createSimple("i", 0), matcher);

        List<Simple> Simples = simpleRepository.findAll(example);
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("sophia", 32),
                                     tuple("dennis", 25),
                                     tuple("michael", 33))
                           .size().isEqualTo(4);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-1.png"
     */
    @Test
    @DisplayName("Query_Methods_조회_접두사")
    void queryMethodsV1() {
        Simple simple = Simple.createSimple("tester", 77);
        Simple tester = simpleRepository.save(simple);

        assertThat(tester).usingRecursiveComparison().isEqualTo(simpleRepository.findByName("tester"));
        assertThat(tester).usingRecursiveComparison().isEqualTo(simpleRepository.getByName("tester"));
        assertThat(tester).usingRecursiveComparison().isEqualTo(simpleRepository.readByName("tester"));
        assertThat(tester).usingRecursiveComparison().isEqualTo(simpleRepository.queryByName("tester"));
        assertThat(tester).usingRecursiveComparison().isEqualTo(simpleRepository.searchByName("tester"));
        assertThat(tester).usingRecursiveComparison().isEqualTo(simpleRepository.streamByName("tester"));
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-1.png"
     */
    @Test
    @DisplayName("Query_Methods_Top_조회")
    void queryMethodsV2() {
        /*-----------------------------------------
         id=1 siro 와 id=6 siro 가 존재하는 상황에서
        limit query 를 사용하여 id 우선순위가 더 높은 데이터를 조회한다
         -----------------------------------------*/
        Simple simple = Simple.createSimple("siro", 77);
        simpleRepository.saveAndFlush(simple); // id=6 siro save

        Simple siro = simpleRepository.findById(1L).get(); // id=1 siro select
        assertThat(siro).usingRecursiveComparison().isEqualTo(simpleRepository.findTop1ByName("siro"));
        assertThat(siro).usingRecursiveComparison().isEqualTo(simpleRepository.findFirst1ByName("siro"));

        List<Simple> Simples = simpleRepository.findTop2ByName("siro"); //  limit = 2 select
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("siro", 77))
                           .size().isEqualTo(2);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_And_조회")
    void queryMethodsV3() {
        Simple simple = Simple.createSimple("siro", 77);
        simpleRepository.saveAndFlush(simple); // id=6 siro save

        Simple siro = simpleRepository.findByNameAndAge("siro", 77);
        assertThat(siro).extracting("name", "age")
                        .containsExactly("siro", 77);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_Or_조회")
    void queryMethodsV4() {
        Simple simple = Simple.createSimple("siro", 25);
        simpleRepository.saveAndFlush(simple); // id=6 siro save

        List<Simple> Simples = simpleRepository.findByNameOrAge("siro", 25);
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("dennis", 25),
                                     tuple("siro", 25))
                           .size().isEqualTo(3);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_After_조회(초과)")
    void queryMethodsV5() {
        List<Simple> Simples = simpleRepository.findByIdAfter(1L);
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("sophia", 32),
                                     tuple("dennis", 25),
                                     tuple("james", 41),
                                     tuple("michael", 33))
                           .size().isEqualTo(4);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_After_조회(이상)")
    void queryMethodsV6() {
        List<Simple> Simples = simpleRepository.findByIdGreaterThanEqual(1L);
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("sophia", 32),
                                     tuple("dennis", 25),
                                     tuple("james", 41),
                                     tuple("michael", 33))
                           .size().isEqualTo(5);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_Before_조회(미만)")
    void queryMethodsV7() {
        List<Simple> Simples = simpleRepository.findByIdBefore(5L);
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("sophia", 32),
                                     tuple("dennis", 25),
                                     tuple("james", 41))
                           .size().isEqualTo(4);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_Before_조회(이하)")
    void queryMethodsV8() {
        List<Simple> Simples = simpleRepository.findByIdIsLessThanEqual(5L);
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("sophia", 32),
                                     tuple("dennis", 25),
                                     tuple("james", 41),
                                     tuple("michael", 33))
                           .size().isEqualTo(5);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_Between_조회")
    void queryMethodsV9() {
        List<Simple> Simples = simpleRepository.findByAgeBetween(20, 30);
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("dennis", 25))
                           .size().isEqualTo(2);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_NotNull_조회")
    void queryMethodsV10() {
        List<Simple> Simples = simpleRepository.findByIdIsNotNull();
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("sophia", 32),
                                     tuple("dennis", 25),
                                     tuple("james", 41),
                                     tuple("michael", 33))
                           .size().isEqualTo(5);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_In_조회(Batch)")
    void queryMethodsV11() {
        List<Simple> Simples = simpleRepository.findByAgeIn(Lists.newArrayList(29, 32, 25));
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 29),
                                     tuple("sophia", 32),
                                     tuple("dennis", 25))
                           .size().isEqualTo(3);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_Starting_조회")
    void queryMethodsV12() {
        Simple siro = simpleRepository.findByNameStartingWith("si").get(0);
        assertThat(siro).extracting("name", "age")
                        .containsExactly("siro", 29);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_Ending_조회")
    void queryMethodsV13() {
        Simple siro = simpleRepository.findByNameEndingWith("ro").get(0);
        assertThat(siro).extracting("name", "age")
                        .containsExactly("siro", 29);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-2.png"
     */
    @Test
    @DisplayName("Query_Methods_Containing_조회")
    void queryMethodsV14() {
        Simple siro = simpleRepository.findByNameContaining("ir").get(0);
        assertThat(siro).extracting("name", "age")
                        .containsExactly("siro", 29);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-3.png"
     */
    @Test
    @DisplayName("Query_Methods_First_OrderBy_조회")
    void queryMethodsV15() {
        Simple simple = Simple.createSimple("siro", 77);
        simpleRepository.saveAndFlush(simple);

        List<Simple> Simples = simpleRepository.findFirst2ByNameOrderByIdDesc("siro");
        assertThat(Simples).extracting("name", "age")
                           .contains(tuple("siro", 77),
                                     tuple("siro", 29))
                           .size().isEqualTo(2);
    }

    /**
     * 참고자료 경로
     *
     * @see "Han-Changhun/src/test/resources/images/query-method-3.png"
     */
    @Test
    @DisplayName("Query_Methods_Sort_조회")
    void queryMethodsV16() {
        List<Simple> simple = simpleRepository.findAll(orderByIdDesc());
        assertThat(simple).extracting("name", "age")
                          .contains(tuple("michael", 33),
                                    tuple("james", 41),
                                    tuple("dennis", 25),
                                    tuple("sophia", 32),
                                    tuple("siro", 29))
                          .size().isEqualTo(5);
    }
}
