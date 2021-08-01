package learn.jpa.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class MemberDto {
    private Long id;
    private String name;
    private Integer age;

    public MemberDto() {}

    @QueryProjection
    public MemberDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @QueryProjection
    public MemberDto(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @QueryProjection
    public MemberDto(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public static MemberDto createMemberDto(Long id, String name, Integer age) {
        return new MemberDto(id, name, age);
    }

    public static MemberDto createMemberDto(Long id, String name) {
        return new MemberDto(id, name);
    }

    public static MemberDto createMemberDto(String name, Integer age) {
        return new MemberDto(name, age);
    }
}
