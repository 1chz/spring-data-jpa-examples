package learn.jpa.proxy.cache;

public class ProxyTextFileReader implements TextFileReader {
    private String plainText;
    private SecretText secretText;

    public ProxyTextFileReader(String plainText) {
        this.plainText = SecretUtil.decode(plainText);
    }

    @Override
    public SecretText read() {
        if(secretText == null || !secretText.getPlainText().equals(plainText)) {
            System.out.println("RealTextFileReader reading text from : " + plainText);
            this.secretText = new SecretText(plainText);
            return this.secretText;
        }

        System.out.println("RealTextFileReader use cache");
        return new SecretText(plainText);
    }
}
