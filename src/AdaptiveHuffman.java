import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdaptiveHuffman {
    private Encoder encoder;
    private Decoder decoder;

    public AdaptiveHuffman() {
        this.encoder = new Encoder();
        this.decoder = new Decoder();
    }

    public String encode(String input) {
        return encoder.encode(input);
    }

    public String decode(String encodedInput) {
        return decoder.decode(encodedInput);
    }

    public void encodeFile(String inputFile, String outputFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            encoder = new Encoder();
            StringBuilder input = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                input.append(line).append("\n");
            }
            String encoded = encoder.encode(input.toString());
            writer.write(encoded);
        }
    }

    public void decodeFile(String inputFile, String outputFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            decoder = new Decoder();
            StringBuilder encodedInput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                encodedInput.append(line);
            }
            String decoded = decoder.decode(encodedInput.toString());
            writer.write(decoded);
        }
    }

    public Encoder getEncoder() {
        return encoder;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void addEncodingListener(Encoder.EncodingListener listener) {
        encoder.addListener(listener);
    }

    public void addDecodingListener(Decoder.DecodingListener listener) {
        decoder.addListener(listener);
    }

    public void addTreeUpdateListener(HuffmanTree.TreeUpdateListener listener) {
        encoder.getTree().addListener(listener);
        decoder.getTree().addListener(listener);
    }

    public void enableDebugMode() {
        encoder.getTree().setDebugMode(true);
        decoder.getTree().setDebugMode(true);
        System.out.println("Debug mode enabled for Adaptive Huffman encoding/decoding");
    }

    public void enableVisualizationMode() {
        encoder.getTree().setVisualizationMode(true);
        decoder.getTree().setVisualizationMode(true);
        System.out.println("Visualization mode enabled for Adaptive Huffman encoding/decoding");
    }

    public static void main(String[] args) {
        AdaptiveHuffman huffman = new AdaptiveHuffman();
        String original = "Hello, World! This is a test of Adaptive Huffman coding.";
        System.out.println("Original: " + original);
        String encoded = huffman.encode(original);
        System.out.println("Encoded: " + encoded);
        String decoded = huffman.decode(encoded);
        System.out.println("Decoded: " + decoded);
        if (original.equals(decoded)) {
            System.out.println("Encoding and decoding successful!");
            System.out.println("Original length: " + original.length() * 8 + " bits");
            System.out.println("Encoded length: " + encoded.length() + " bits");
            float compressionRatio = (float) (original.length() * 8) / encoded.length();
            System.out.println("Compression ratio: " + compressionRatio);
        } else {
            System.out.println("Error: Encoding and decoding failed!");
        }
    }
}
