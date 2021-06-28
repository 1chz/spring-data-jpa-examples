package learn.jpa.model;

import learn.jpa.repository.UserRepository;
import learn.jpa.type.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.NoSuchElementException;

@DataJpaTest
class UserTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void listenerTest() throws Exception {
        User user = User.createUser("siro", "1234", "test@test.com", Gender.MALE);

        // prePersist
        userRepository.saveAndFlush(user);
        // postPersist

        User findUser = userRepository.findById(1L).orElseThrow(NoSuchElementException::new);

        // preUpdate
        findUser.changeUserName("changeName");
        userRepository.saveAndFlush(findUser);
        // postUpdate

        // preRemove
        userRepository.deleteById(1L);
        userRepository.flush();
        // postRemove

        System.out.println("findUser = " + findUser);
    }
}
