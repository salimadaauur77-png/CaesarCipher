package caesar;

public class StatisticalAnalyzer {

    private final Cipher cipher;

    public StatisticalAnalyzer(Cipher cipher) {
        this.cipher = cipher;
    }

    // Упрощенный анализ: ищем наиболее частый символ (вероятно, это пробел)
    public int guessKey(String encryptedText) {
        int[] frequency = new int[Cipher.ALPHABET_LENGTH];
        
        // Считаем частоту
        for (char c : encryptedText.toCharArray()) {
            int index = Cipher.ALPHABET.indexOf(c);
            if (index != -1) {
                frequency[index]++;
            }
        }

        // Находим самый частый символ
        int mostFrequentIndex = 0;
        for (int i = 1; i < frequency.length; i++) {
            if (frequency[i] > frequency[mostFrequentIndex]) {
                mostFrequentIndex = i;
            }
        }

        // Предполагаем, что самый частый символ — это пробел (' ')
        int spaceIndex = Cipher.ALPHABET.indexOf(' ');
        int guessedKey = (mostFrequentIndex - spaceIndex + Cipher.ALPHABET_LENGTH) % Cipher.ALPHABET_LENGTH;
        return guessedKey;
    }
}
