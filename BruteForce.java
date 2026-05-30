package caesar;

public class BruteForce {

    private final Cipher cipher;

    public BruteForce(Cipher cipher) {
        this.cipher = cipher;
    }

    public void bruteForce(String encryptedText, String outputFile) throws IOException {
        FileManager.writeFile(outputFile, "Результаты перебора ключей:\n\n");
        for (int key = 0; key < Cipher.ALPHABET_LENGTH; key++) {
            String decrypted = cipher.decrypt(encryptedText, key);
            FileManager.writeFile(outputFile, FileManager.readFile(outputFile) + "Ключ " + key + ":\n" + decrypted + "\n\n");
        }
    }
}
