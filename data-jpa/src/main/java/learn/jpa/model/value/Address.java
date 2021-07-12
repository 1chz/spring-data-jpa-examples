package learn.jpa.model.value;

import lombok.*;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Getter
@ToString
@Embeddable
@EqualsAndHashCode
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Address {
    @NonNull
    private String city;

    @NonNull
    private String street;

    @NonNull
    private String zipcode;

    @Builder
    public static Address of(@NonNull String city, @NonNull String street, @NonNull String zipcode) {
        return new Address(city, street, zipcode);
    }

    public String getAddress() {
        return city + " " + street + " " + zipcode;
    }

    public Address newInstance() {
        return Address.builder()
                      .city(this.city)
                      .street(this.street)
                      .zipcode(this.zipcode)
                      .build();
    }

    public void changeAddress(String address) {
        String[] s = address.split(" ");
        if(s.length != 3) {
            throw new IllegalArgumentException("입력 주소가 올바르지 않습니다.");
        }
        this.city = s[0];
        this.street = s[1];
        this.zipcode = s[2];
    }
}
