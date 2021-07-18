package learn.jpa.model;

import learn.jpa.model.value.Address;
import learn.jpa.model.value.Name;
import learn.jpa.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

@DataJpaTest
class CustomerTest {
    @Autowired
    CustomerRepository repository;

    @Test
    void customer() {
        Name name = Name.of("firstName", "lastName");
        Address address = Address.of("city", "street", "zipcode");

        Customer customer1 = Customer.of(name, "01000000001", address);
        //        Customer customer2 = Customer.of(name, "01000000002", address.newInstance()); // 깊은복사
        Customer customer2 = Customer.of(name, "01000000002", address); // 깊은복사

        Customer saveCustomer1 = repository.save(customer1); // insert 쿼리 작성(쓰기지연)
        Customer saveCustomer2 = repository.save(customer2); // insert 쿼리 작성(쓰기지연)
        repository.flush(); // insert 쿼리 두번 발생

        System.out.println("saveCustomer1 = " + saveCustomer1);
        System.out.println("saveCustomer2 = " + saveCustomer2);

        customer1.changeAddress("changeCity changeStreet changeZipcode"); // update 쿼리 한번 작성(쓰기지연)
        repository.flush(); // update 쿼리 한번 발생

        System.out.println("changeCustomer1 = " + saveCustomer1);
        System.out.println("changeCustomer2 = " + saveCustomer2);
    }
}
