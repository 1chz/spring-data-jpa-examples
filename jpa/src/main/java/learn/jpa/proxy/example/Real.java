package learn.jpa.proxy.example;

public class Real implements Interface{
    @Override public void operation() {
        System.out.println("Real Operation.");
    }
}
