package learn.jpa.proxy.cache;

public class SecretText {
    private String plainText;

    public String getPlainText() {
        return plainText;
    }

    public SecretText(String plainText) {
        this.plainText = plainText;
    }
}
