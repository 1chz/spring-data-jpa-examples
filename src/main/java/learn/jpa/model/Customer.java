package learn.jpa.model;

import learn.jpa.model.value.Address;
import learn.jpa.model.value.Name;
import lombok.*;

import javax.persistence.Entity;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Customer extends BaseEntity {
    @NonNull
    private Name name;

    @NonNull
    private String phoneNumber;

    @NonNull
    private Address address;

    @Builder
    public static Customer of(@NonNull Name name, @NonNull String phoneNumber, @NonNull Address address) {
        return new Customer(name, phoneNumber, address);
    }

    public void changeAddress(String address) {
        this.address.changeAddress(address);
    }
}
