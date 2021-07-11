package learn.jpa.proxy.example;

public class Proxy implements Interface {
    Interface real = new Real();

    @Override public void operation() {
        System.out.println("Proxy Operation");
        real.operation();
    }
}
