package learn.jpa.proxy.cache;

public class LazyTextFileReader implements TextFileReader{
    private String plainText;
    private TextFileReader reader;

    public LazyTextFileReader(String plainText) {
        this.plainText = plainText;
    }

    @Override
    public SecretText read() {
        if(reader == null){
            reader = new RealTextFileReader(plainText);
        }
        System.out.println("lazy initialisation");
        return reader.read();
    }
}
