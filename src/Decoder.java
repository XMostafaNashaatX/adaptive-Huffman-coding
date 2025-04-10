import java.util.ArrayList;
import java.util.List;

public class Decoder {
    private HuffmanTree tree;
    private List<DecodingListener> listeners;
    private boolean debugMode = true;

    public Decoder() {
        this.tree = new HuffmanTree();
        this.listeners = new ArrayList<>();
    }

    public void addListener(DecodingListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(char symbol, String bits) {
        for (DecodingListener listener : listeners) {
            listener.onSymbolDecoded(symbol, bits);
        }
    }

    public String decode(String encodedText) {
        StringBuilder decoded = new StringBuilder();
        if (encodedText == null || encodedText.isEmpty()) {
            System.out.println("DEBUG [Decoder] Error: Empty or null input");
            return "";
        }
        if (!encodedText.matches("[01]+")) {
            System.out.println("DEBUG [Decoder] Error: Input is not valid binary - " + encodedText);
            return "Error: Input must contain only 0s and 1s";
        }
        System.out.println("DEBUG [Decoder] Starting decoding of: " + encodedText);
        int currentIndex = 0;
        while (currentIndex < encodedText.length()) {
            Node currentNode = tree.getRoot();
            if (debugMode) {
                System.out.println("DEBUG [Decoder] Starting at node: " + currentNode);
            }
            if (currentNode.isNYT() && tree.getSymbolCount() == 0) {
                if (currentIndex + 8 <= encodedText.length()) {
                    String asciiCode = encodedText.substring(currentIndex, currentIndex + 8);
                    try {
                        int asciiValue = Integer.parseInt(asciiCode, 2);
                        char symbol = (char) asciiValue;
                        if (debugMode) {
                            System.out.println("DEBUG [Decoder] First symbol via ASCII code: '" + symbol + "' (" + asciiValue + ") from " + asciiCode);
                        }
                        decoded.append(symbol);
                        if (listeners != null) {
                            for (DecodingListener listener : listeners) {
                                if (listener != null) {
                                    listener.onSymbolDecoded(symbol, asciiCode);
                                }
                            }
                        }
                        tree.processSymbol(symbol);
                        currentIndex += 8;
                    } catch (NumberFormatException e) {
                        System.out.println("DEBUG [Decoder] Error: Invalid binary for ASCII - " + asciiCode);
                        return "Error: Invalid binary data for ASCII conversion";
                    }
                } else {
                    System.out.println("DEBUG [Decoder] Error: Not enough bits for initial ASCII code");
                    break;
                }
            } else {
                StringBuilder pathBits = new StringBuilder();
                while (!currentNode.isLeaf() && !currentNode.isNYT() && currentIndex < encodedText.length()) {
                    char bit = encodedText.charAt(currentIndex);
                    pathBits.append(bit);
                    if (debugMode) {
                        System.out.println("DEBUG [Decoder] Reading bit " + bit + " at node " + currentNode);
                    }
                    if (bit == '0') {
                        if (!currentNode.hasLeftChild()) {
                            System.out.println("DEBUG [Decoder] Error: No left child at node " + currentNode);
                            return decoded.toString();
                        }
                        currentNode = currentNode.getLeftChild();
                    } else if (bit == '1') {
                        if (!currentNode.hasRightChild()) {
                            System.out.println("DEBUG [Decoder] Error: No right child at node " + currentNode);
                            return decoded.toString();
                        }
                        currentNode = currentNode.getRightChild();
                    }
                    currentIndex++;
                }
                if (currentNode.isLeaf()) {
                    char symbol = currentNode.getSymbol();
                    if (debugMode) {
                        System.out.println("DEBUG [Decoder] Found symbol at leaf: '" + symbol + "' via path " + pathBits);
                    }
                    decoded.append(symbol);
                    if (listeners != null) {
                        for (DecodingListener listener : listeners) {
                            if (listener != null) {
                                listener.onSymbolDecoded(symbol, pathBits.toString());
                            }
                        }
                    }
                    tree.processSymbol(symbol);
                } else if (currentNode.isNYT()) {
                    if (currentIndex + 8 <= encodedText.length()) {
                        String asciiCode = encodedText.substring(currentIndex, currentIndex + 8);
                        try {
                            int asciiValue = Integer.parseInt(asciiCode, 2);
                            char symbol = (char) asciiValue;
                            if (debugMode) {
                                System.out.println("DEBUG [Decoder] Found new symbol via NYT path " + pathBits + " + ASCII code: '" + symbol + "' (" + asciiValue + ") from " + asciiCode);
                            }
                            decoded.append(symbol);
                            if (listeners != null) {
                                for (DecodingListener listener : listeners) {
                                    if (listener != null) {
                                        String fullPath = pathBits.toString() + asciiCode;
                                        listener.onSymbolDecoded(symbol, fullPath);
                                    }
                                }
                            }
                            tree.processSymbol(symbol);
                            currentIndex += 8;
                        } catch (NumberFormatException e) {
                            System.out.println("DEBUG [Decoder] Error: Invalid binary for ASCII - " + asciiCode);
                            return "Error: Invalid binary data for ASCII conversion";
                        }
                    } else {
                        System.out.println("DEBUG [Decoder] Error: Not enough bits for ASCII code after NYT");
                        break;
                    }
                } else {
                    System.out.println("DEBUG [Decoder] Error: Ran out of bits while traversing");
                    break;
                }
            }
        }
        return decoded.toString();
    }

    private boolean isLeaf(Node node) {
        char symbol = node.getSymbol();
        if (symbol != '\0' && tree.getNode(symbol) == node) {
            return true;
        }
        return node.isLeaf();
    }

    public HuffmanTree getTree() {
        return tree;
    }

    public interface DecodingListener {
        void onSymbolDecoded(char symbol, String bits);
    }
}
