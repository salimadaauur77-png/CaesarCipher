package caesar;

public class Cipher {

    // Русский алфавит + пробел и знаки препинания
    private static final String ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя .,\"':-!?";
    private static final int ALPHABET_LENGTH = ALPHABET.length();

    public String encrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            int index = ALPHABET.indexOf(c);
            if (index == -1) {
                result.append(c); // Символ не в алфавите — пропускаем
                continue;
            }
            int newIndex = (index + shift) % ALPHABET_LENGTH;
            result.append(ALPHABET.charAt(newIndex));
        }
        return result.toString();
    }

    public String decrypt(String encryptedText, int shift) {
        return encrypt(encryptedText, -shift); // Сдвиг в обратную сторону
    }
}
