package learn.jpa.transaction.proxy;

@FunctionalInterface
public interface TransactionAction {
    void run();
}
