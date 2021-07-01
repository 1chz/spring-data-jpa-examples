package learn.jpa.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.*;

@Entity
@Getter @Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public void joinMember(Member member){
        this.members.add(member);
        member.changeTeam(this);
    }
}
