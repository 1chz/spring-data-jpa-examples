package learn.jpa.model.value;

import lombok.*;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Getter
@ToString
@Embeddable
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    @NotNull
    private String city;

    @NotNull
    private String street;

    @NotNull
    private String zipcode;

    private Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    @Builder
    public static Address of(@NotNull String city, @NotNull String street, @NotNull String zipcode) {
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
