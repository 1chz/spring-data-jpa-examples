package learn.jpa.proxy.example;

import org.junit.jupiter.api.Test;

class ClientTest {
    @Test
    void proxy() {
        Client client = new Client(new Proxy());

        client.callOperation();
    }
}
