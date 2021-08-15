package learn.jpa.transaction.proxy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionActionProxy {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void action(final TransactionAction action) {
        action.run();
    }
}
