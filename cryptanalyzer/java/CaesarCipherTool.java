
import .io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class CaesarCipherTool {

    // Класс, содержащий алфавит программы
    public static class Alphabet {
        public static final String CHARS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                                           "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                                           ".,\"':-!? ";
        public static final int SIZE = CHARS.length();

        public static int indexOf(char c) {
            return CHARS.indexOf(c);
        }

        public static char getChar(int index) {
            return CHARS.charAt(index);
        }

        public static boolean contains(char c) {
            return CHARS.indexOf(c) != -1;
        }
    }

    // Валидатор входных параметров
    public static class Validator {
        public static void validateInputFile(String pathStr) throws FileNotFoundException {
            if (pathStr == null || pathStr.isBlank()) {
                throw new IllegalArgumentException("Путь к файлу не может быть пустым.");
            }
            Path path = Path.of(pathStr);
            if (!Files.exists(path)) {
                throw new FileNotFoundException("Файл не найден по пути: " + path.toAbsolutePath());
            }
            if (Files.isDirectory(path)) {
                throw new IllegalArgumentException("Указанный путь является директорией, а не файлом: " + path.toAbsolutePath());
            }
        }

        public static void validateOutputFile(String pathStr) {
            if (pathStr == null || pathStr.isBlank()) {
                throw new IllegalArgumentException("Путь сохранения не может быть пустым.");
            }
        }

        public static int validateAndNormalizeKey(int key) {
            // Ключ может быть любого размера (даже больше размера алфавита или отрицательным)
            int normalizedKey = key % Alphabet.SIZE;
            if (normalizedKey < 0) {
                normalizedKey += Alphabet.SIZE;
            }
            return normalizedKey;
        }
    }

    // Класс для посимвольного шифрования
    public static class Cipher {
        public static char shiftChar(char c, int shift) {
            int index = Alphabet.indexOf(c);
            if (index == -1) {
                return c; // Символы, не входящие в алфавит, оставляем без изменений
            }
            int newIndex = (index + shift) % Alphabet.SIZE;
            if (newIndex < 0) {
                newIndex += Alphabet.SIZE;
            }
            return Alphabet.getChar(newIndex);
        }
    }

    // Класс для потоковой работы с файлами (подходит для ОЧЕНЬ больших файлов)
    public static class FileManager {
        public static void processFile(Path input, Path output, int shift) throws IOException {
            try (BufferedReader reader = Files.newBufferedReader(input);
                 BufferedWriter writer = Files.newBufferedWriter(output)) {
                char[] buffer = new char[8192]; // Буфер на 8 КБ
                int charsRead;
                while ((charsRead = reader.read(buffer)) != -1) {
                    for (int i = 0; i < charsRead; i++) {
                        buffer[i] = Cipher.shiftChar(buffer[i], shift);
                    }
                    writer.write(buffer, 0, charsRead);
                }
            }
        }
    }

    // Класс для дешифровки методом Brute Force (Перебор ключей)
    public static class BruteForce {
        // Набор частых русских слов с пробелами для анализа осмысленности текста
        private static final String[] COMMON_WORDS = {
                " и ", " в ", " не ", " на ", " я ", " с ", " что ", " как ", " то ", " это "
        };

        public static int findKey(Path encryptedPath) throws IOException {
            // Читаем первые 4096 символов для быстрого анализа
            String sample = readSample(encryptedPath, 4096);
            int bestKey = 0;
            int maxScore = -1;

            for (int key = 0; key < Alphabet.SIZE; key++) {
                String decryptedSample = decryptString(sample, key);
                int score = calculateReadableScore(decryptedSample);
                if (score > maxScore) {
                    maxScore = score;
                    bestKey = key;
                }
            }
            return bestKey;
        }

        private static String decryptString(String text, int key) {
            StringBuilder sb = new StringBuilder();
            for (char c : text.toCharArray()) {
                sb.append(Cipher.shiftChar(c, -key));
            }
            return sb.toString();
        }

        private static int calculateReadableScore(String text) {
            int score = 0;
            // Проверяем наличие частых слов
            for (String word : COMMON_WORDS) {
                int index = 0;
                while ((index = text.indexOf(word, index)) != -1) {
                    score += 10; // Большой вес за полноценное слово
                    index += word.length();
                }
            }
            // Проверяем правила пунктуации (после запятой или точки должен идти пробел)
            for (int i = 0; i < text.length() - 1; i++) {
                char current = text.charAt(i);
                char next = text.charAt(i + 1);
                if ((current == ',' || current == '.') && next == ' ') {
                    score += 2;
                }
            }
            return score;
        }

        private static String readSample(Path path, int limit) throws IOException {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                char[] buffer = new char[limit];
                int read = reader.read(buffer);
                return read == -1 ? "" : new String(buffer, 0, read);
            }
        }
    }

    // Класс для автоматической дешифровки через Статистический Анализ
    public static class StatisticalAnalyzer {
        public static int findKey(Path encryptedPath, Path representativePath) throws IOException {
            double[] representativeFreqs = calculateFrequencies(representativePath);
            double[] encryptedFreqs = calculateFrequencies(encryptedPath);

            int bestKey = 0;
            double minDifference = Double.MAX_VALUE;

            // Находим сдвиг, при котором разница частот распределения символов минимальна (MSE)
            for (int key = 0; key < Alphabet.SIZE; key++) {
                double difference = 0;
                for (int i = 0; i < Alphabet.SIZE; i++) {
                    int shiftedIndex = (i + key) % Alphabet.SIZE;
                    double diff = representativeFreqs[i] - encryptedFreqs[shiftedIndex];
                    difference += diff * diff; // Квадрат разности
                }
                if (difference < minDifference) {
                    minDifference = difference;
                    bestKey = key;
                }
            }
            return bestKey;
        }

        private static double[] calculateFrequencies(Path path) throws IOException {
            double[] counts = new double[Alphabet.SIZE];
            long totalCount = 0;

            try (BufferedReader reader = Files.newBufferedReader(path)) {
                int c;
                while ((c = reader.read()) != -1) {
                    char character = (char) c;
                    int index = Alphabet.indexOf(character);
                    if (index != -1) {
                        counts[index]++;
                        totalCount++;
                    }
                }
            }

            if (totalCount > 0) {
                for (int i = 0; i < Alphabet.SIZE; i++) {
                    counts[i] /= totalCount; // Превращаем в относительную частоту
                }
            }
            return counts;
        }
    }

    // Консольный интерфейс программы
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Инструмент Шифр Цезаря ===");

        while (true) {
            System.out.println("\nВыберите режим работы:");
            System.out.println("1. Шифрование текста с помощью ключа");
            System.out.println("2. Расшифровка текста с известным ключом");
            System.out.println("3. Расшифровка методом brute force (подбор ключа)");
            System.out.println("4. Расшифровка методом стат. анализа (требуется эталонный файл)");
            System.out.println("0. Выход");
            System.out.print("Ваш выбор: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("Программа завершена. До свидания!");
                break;
            }

            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Введите путь к оригинальному файлу: ");
                        String src = scanner.nextLine().trim();
                        Validator.validateInputFile(src);

                        System.out.print("Введите путь для сохранения зашифрованного файла: ");
                        String dest = scanner.nextLine().trim();
                        Validator.validateOutputFile(dest);

                        System.out.print("Введите ключ (число-сдвиг): ");
                        int key = Integer.parseInt(scanner.nextLine().trim());
                        int normalizedKey = Validator.validateAndNormalizeKey(key);

                        FileManager.processFile(Path.of(src), Path.of(dest), normalizedKey);
                        System.out.println("Файл успешно зашифрован!");
                    }
                    case "2" -> {
                        System.out.print("Введите путь к зашифрованному файлу: ");
                        String src = scanner.nextLine().trim();
                        Validator.validateInputFile(src);

                        System.out.print("Введите путь для сохранения расшифрованного файла: ");
                        String dest = scanner.nextLine().trim();
                        Validator.validateOutputFile(dest);

                        System.out.print("Введите ключ (число-сдвиг): ");
                        int key = Integer.parseInt(scanner.nextLine().trim());
                        int normalizedKey = Validator.validateAndNormalizeKey(key);

                        // Расшифровка — это шифрование с отрицательным сдвигом
                        FileManager.processFile(Path.of(src), Path.of(dest), -normalizedKey);
                        System.out.println("Файл успешно расшифрован!");
                    }
                    case "3" -> {
                        System.out.print("Введите путь к зашифрованному файлу: ");
                        String src = scanner.nextLine().trim();
                        Validator.validateInputFile(src);

                        System.out.print("Введите путь для сохранения расшифрованного файла: ");
                        String dest = scanner.nextLine().trim();
                        Validator.validateOutputFile(dest);

                        System.out.println("Выполняется подбор ключа методом Brute Force...");
                        int guessedKey = BruteForce.findKey(Path.of(src));
                        System.out.println("Найден наиболее вероятный ключ: " + guessedKey);

                        FileManager.processFile(Path.of(src), Path.of(dest), -guessedKey);
                        System.out.println("Файл расшифрован и сохранен!");
                    }
                    case "4" -> {
                        System.out.print("Введите путь к зашифрованному файлу: ");
                        String src = scanner.nextLine().trim();
                        Validator.validateInputFile(src);

                        System.out.print("Введите путь к эталонному файлу (текст того же автора/стилистики): ");
                        String rep = scanner.nextLine().trim();
                        Validator.validateInputFile(rep);

                        System.out.print("Введите путь для сохранения расшифрованного файла: ");
                        String dest = scanner.nextLine().trim();
                        Validator.validateOutputFile(dest);

                        System.out.println("Выполняется статистический анализ...");
                        int guessedKey = StatisticalAnalyzer.findKey(Path.of(src), Path.of(rep));
                        System.out.println("Статистический анализ определил ключ: " + guessedKey);

                        FileManager.processFile(Path.of(src), Path.of(dest), -guessedKey);
                        System.out.println("Файл успешно расшифрован и сохранен!");
                    }
                    default -> System.out.println("Неверный выбор. Пожалуйста, попробуйте еще раз.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Ошибка: Ключ должен быть целым числом!");
            } catch (FileNotFoundException e) {
                System.err.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Произошла ошибка при выполнении операции: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();
    }
}
