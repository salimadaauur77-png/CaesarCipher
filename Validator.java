package caesar;

import java.io.File;

public class Validator {

    public static boolean isFileExists(String filePath) {
        return new File(filePath).exists();
    }

    public static boolean isValidKey(int key) {
        return key >= 0 && key < Cipher.ALPHABET_LENGTH;
    }
}
