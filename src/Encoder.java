import java.util.ArrayList;
import java.util.List;
public class Encoder {
    private HuffmanTree tree;
    private List<EncodingListener> listeners;
    private boolean debugMode = true;
    public Encoder() {
        this.tree = new HuffmanTree();
        this.listeners = new ArrayList<>();
    }
    public void addListener(EncodingListener listener) {
        listeners.add(listener);
    }
    private void notifyListeners(char symbol, String encoding) {
        for (EncodingListener listener : listeners) {
            listener.onSymbolEncoded(symbol, encoding);
        }
    }
    public String encode(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        tree.prepareForFirstSymbol();
        if (debugMode) {
            System.out.println("DEBUG [Encoder] Starting encoding of: " + input);
        }
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char symbol = input.charAt(i);
            String symbolEncoding = encodeSymbol(symbol);
            encoded.append(symbolEncoding);
        }
        return encoded.toString();
    }
    public String encodeSymbol(char symbol) {
        String encoding = tree.getSymbolEncoding(symbol);
        if (debugMode) {
            System.out.println("DEBUG [Encoder] Encoding symbol '" + symbol + "' as: " + encoding);
        }
        tree.processSymbol(symbol);
        notifyListeners(symbol, encoding);
        return encoding;
    }
    public HuffmanTree getTree() {
        return tree;
    }
    public interface EncodingListener {
        void onSymbolEncoded(char symbol, String encoding);
    }
}
