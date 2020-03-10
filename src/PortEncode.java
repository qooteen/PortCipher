import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PortEncode {
    private Map<Character, List<Character>> portTable = new LinkedHashMap<>();
    private List<Character> firstPartOfAlphabet;
    private List<Character> secondPartOfAlphabet;

    public PortEncode () {
        this.firstPartOfAlphabet = Arrays.asList('а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п');
        this.secondPartOfAlphabet = Arrays.asList('р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я');
        generatePortTable();
    }

    private void generatePortTable() {
        for (Character i = 'А'; i <= 'Я'; i++) {
            if ((int) i % 2 == 0) {
                portTable.put(i, firstPartOfAlphabet);
            } else {
                portTable.put(i, new ArrayList<>(secondPartOfAlphabet));
                shiftCycleLeft(secondPartOfAlphabet);
            }
        }
    }

    private void shiftCycleLeft(List<Character> list) {
        Character first = list.get(0);
        for (int i = 0; i < list.size() - 1; i++) {
            swapElements(i, i + 1, list);
        }
        list.set(list.size() - 1, first);
    }

    private void swapElements(int i, int j, List<Character> list) {
        Character temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    private String readFromFile(String fileName) {
        try (Scanner textScanner = new Scanner(new FileReader(fileName))){
            String result = "";
            while (textScanner.hasNext()) {
                result += textScanner.nextLine();
            }
            return result;
        }
        catch (IOException e) {
            System.out.println("Ошибка чтения файла " + fileName);
        }
        return null;
    }

    private void writeInFile(String fileName, String message) {
        try (FileWriter fileWriter = new FileWriter(fileName)){
            fileWriter.write(message);
        }
        catch (IOException e) {
            System.out.println("Ошибка записи в файл " + fileName);
        }
    }

    public Map<Character, List<Character>> getPortTable() {
        return portTable;
    }

    public void encode(String messageFile, String cryptogramFile, String keyFile) {
        String message = readFromFile(messageFile);
        String key = readFromFile(keyFile);
        String upperKey = key.toUpperCase().replace('Ё', 'Е');
        String lowerMessage = message.toLowerCase().replace('ё', 'е');

        List<Character> messageLetters = lowerMessage.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        List<Character> keyLetters = upperKey.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        addKeysLetters(messageLetters, keyLetters);

        for (int i = 0; i < messageLetters.size(); i++) {
            Character character = messageLetters.get(i);
            if (firstPartOfAlphabet.contains(character) || secondPartOfAlphabet.contains(character)) {
                Character firstLetter;
                Character secondLetter;
                if ((int) keyLetters.get(i) % 2 == 0) {
                    firstLetter = keyLetters.get(i);
                    secondLetter = (char) ((int) keyLetters.get(i) + 1);
                } else {
                    secondLetter = keyLetters.get(i);
                    firstLetter = (char) ((int) keyLetters.get(i) - 1);
                }

                Character resultLetter;
                if (portTable.get(firstLetter).contains(messageLetters.get(i))) {
                    int index = portTable.get(firstLetter).indexOf(messageLetters.get(i));
                    resultLetter = portTable.get(secondLetter).get(index);
                } else {
                    int index = portTable.get(secondLetter).indexOf(messageLetters.get(i));
                    resultLetter = portTable.get(firstLetter).get(index);
                }

                messageLetters.set(i, resultLetter);
            }
        }
        String encodeMessage = "";
        for (Character c: messageLetters) {
            encodeMessage += c;
        }
        writeInFile(cryptogramFile, encodeMessage);
    }

    private void addKeysLetters(List<Character> messageLetters, List<Character> keyLetters) {
        if (messageLetters.size() > keyLetters.size()) {
            int length = messageLetters.size() - keyLetters.size();
            for (int i = 0; i < length; i++) {
                keyLetters.add(keyLetters.get(i % keyLetters.size()));
            }
        }
    }

    public void decode(String cryptogramFile, String messageFile, String keyFile) {
        String message = readFromFile(cryptogramFile);
        String key = readFromFile(keyFile);
        List<Character> messageLetters = message.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        List<Character> keyLetters = key.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        addKeysLetters(messageLetters, keyLetters);

        for (int i = 0; i < messageLetters.size(); i++) {
            Character character = messageLetters.get(i);
            if (firstPartOfAlphabet.contains(character) || secondPartOfAlphabet.contains(character)) {
                Character firstLetter;
                Character secondLetter;
                if ((int) keyLetters.get(i) % 2 == 0) {
                    firstLetter = keyLetters.get(i);
                    secondLetter = (char) ((int) keyLetters.get(i) + 1);
                } else {
                    secondLetter = keyLetters.get(i);
                    firstLetter = (char) ((int) keyLetters.get(i) - 1);
                }

                Character resultLetter;
                if (portTable.get(firstLetter).contains(messageLetters.get(i))) {
                    int index = portTable.get(firstLetter).indexOf(messageLetters.get(i));
                    resultLetter = portTable.get(secondLetter).get(index);
                } else {
                    int index = portTable.get(secondLetter).indexOf(messageLetters.get(i));
                    resultLetter = portTable.get(firstLetter).get(index);
                }

                messageLetters.set(i, resultLetter);
            }
        }
        String encodeMessage = "";
        for (Character c: messageLetters) {
            encodeMessage += c;
        }
        writeInFile(messageFile, encodeMessage);
    }
}
