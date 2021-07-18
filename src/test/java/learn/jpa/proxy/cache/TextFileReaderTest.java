package learn.jpa.proxy.cache;

import org.junit.jupiter.api.Test;

class TextFileReaderTest {
    @Test
    void noCache() {
        TextFileReader reader = new RealTextFileReader("text");
        reader.read();
        reader.read();
        reader.read();
        reader.read();
        reader.read();
    }

    @Test
    void useCache() {
        TextFileReader reader = new ProxyTextFileReader("text");
        reader.read();
        reader.read();
        reader.read();
        reader.read();
        reader.read();
    }

    @Test
    void lazy() {
        TextFileReader reader = new LazyTextFileReader("text");
        reader.read();
        reader.read();
        reader.read();
        reader.read();
        reader.read();
    }
}
