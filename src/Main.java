import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        PortEncode portEncode = new PortEncode();
        Map<Character, List<Character>> map = portEncode.getPortTable();
        for (Map.Entry<Character, List<Character>> pair: map.entrySet()) {
            System.out.println(pair.getKey() + "  --  " + pair.getValue());
        }
        portEncode.encode("message.txt", "cryptogram.txt", "key.txt");
        portEncode.decode("cryptogram.txt", "result.txt", "key.txt");

    }
}
