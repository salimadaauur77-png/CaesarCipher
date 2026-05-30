package caesar;

import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cipher cipher = new Cipher();
        BruteForce bruteForce = new BruteForce(cipher);
        StatisticalAnalyzer analyzer = new StatisticalAnalyzer(cipher);

        while (true) {
            System.out.println("\nШифр Цезаря");
            System.out.println("1. Зашифровать файл");
            System.out.println("2. Расшифровать файл (известный ключ)");
            System.out.println("3. Расшифровка методом Brute Force");
            System.out.println("4. Расшифровка методом статистического анализа");
            System.out.println("5. Выход");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Сброс буфера

            switch (choice) {
                case 1:
                    encryptFile(scanner, cipher);
                    break;
                case 2:
                    decryptFile(scanner, cipher);
                    break;
                case 3:
                    bruteForceFile(scanner, bruteForce);
                    break;
                case 4:
                    statisticalDecrypt(scanner, analyzer, cipher);
                    break;
                case 5:
                    System.out.println("До свидания!");
                    return;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    private static void encryptFile(Scanner scanner, Cipher cipher) {
        System.out.print("Введите путь к исходному файлу: ");
        String inputPath = scanner.nextLine();
        System.out.print("Введите путь к зашифрованному файлу: ");
        String outputPath = scanner.nextLine();
        System.out.print("Введите сдвиг (ключ): ");
        int key = scanner.nextInt();

        if (!Validator.isFileExists(inputPath)) {
            System.out.println("Файл не найден!");
            return;
        }
        if (!Validator.isValidKey(key)) {
            System.out.println("Неверный ключ!");
            return;
        }

        try {
            String text = FileManager.readFile(inputPath);
            String encrypted = cipher.encrypt(text, key);
            FileManager.writeFile(outputPath, encrypted);
            System.out.println("Файл зашифрован!");
        } catch (IOException e) {
            System.out.println("Ошибка при работе с файлами!");
        }
    }

    // Остальные методы (decryptFile, bruteForceFile, statisticalDecrypt)
    // аналогичны по структуре — реализуют ввод данных и вызов соответствующих методов классов.
    // Если нужно полное описание этих методов — напишите, я дополню ответ.
}
