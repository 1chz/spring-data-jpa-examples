package learn.jpa.proxy.cache;

public class RealTextFileReader implements TextFileReader {
    private String plainText;

    public RealTextFileReader(String plainText) {
        this.plainText = SecretUtil.decode(plainText);
    }

    @Override
    public SecretText read() {
        System.out.println("RealTextFileReader reading text from : " + plainText);
        return new SecretText(plainText);
    }
}
