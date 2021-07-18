package learn.jpa.proxy.example;

public class Client {
    Interface anInterface;

    public Client(Interface anInterface) {
        this.anInterface = anInterface;
    }

    public void callOperation(){
        anInterface.operation();
    }
}
