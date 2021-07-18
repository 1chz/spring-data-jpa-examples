package learn.jpa.model;

import learn.jpa.model.value.Address;
import learn.jpa.model.value.Name;
import lombok.*;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity {
    @NotNull
    private Name name;

    @NotNull
    private String phoneNumber;

    @NotNull
    private Address address;

    private Customer(Name name, String phoneNumber, Address address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    @Builder
    public static Customer of(@NotNull Name name, @NotNull String phoneNumber, @NotNull Address address) {
        return new Customer(name, phoneNumber, address);
    }

    public void changeAddress(String address) {
        this.address.changeAddress(address);
    }
}
