public class AdaptiveHuffmanTest {
    private AdaptiveHuffman huffman;
    public void setUp() {
        huffman = new AdaptiveHuffman();
    }
    public void testSimple() {
        System.out.println("====== TEST CASE 0: Very Simple ======");
        String original = "AA";
        System.out.println("Original: " + original);
        String encoded = huffman.encode(original);
        System.out.println("Encoded: " + encoded);
        String decoded = huffman.decode(encoded);
        System.out.println("Decoded: " + decoded);
        if (original.equals(decoded)) {
            System.out.println("✓ Success: Original matches decoded");
        } else {
            System.out.println("✗ Failure: Original does not match decoded");
        }
        System.out.println();
    }
    public void testCase1() {
        System.out.println("====== TEST CASE 1: Simple String ======");
        String original = "AABCBAACB";
        System.out.println("Original: " + original);
        String encoded = huffman.encode(original);
        System.out.println("Encoded: " + encoded);
        String decoded = huffman.decode(encoded);
        System.out.println("Decoded: " + decoded);
        if (original.equals(decoded)) {
            System.out.println("✓ Success: Original matches decoded");
        } else {
            System.out.println("✗ Failure: Original does not match decoded");
        }
        int originalBits = original.length() * 8;
        int encodedBits = encoded.length();
        float compressionRatio = (float) originalBits / encodedBits;
        System.out.println("Original length: " + originalBits + " bits");
        System.out.println("Encoded length: " + encodedBits + " bits");
        System.out.println("Compression ratio: " + compressionRatio);
        System.out.println("Compression percentage: " + (1 - (float) encodedBits / originalBits) * 100 + "%");
        System.out.println();
    }
    public void testCase2() {
        System.out.println("====== TEST CASE 2: Longer Text with Mixed Characters ======");
        String original = "This is a longer test case for Adaptive Huffman Coding. " + "It includes various characters, punctuation, and repeated patterns " + "to demonstrate the effectiveness of the algorithm.";
        System.out.println("Original: " + original);
        String encoded = huffman.encode(original);
        System.out.println("Encoded (first 100 bits): " + encoded.substring(0, Math.min(100, encoded.length())) + "...");
        String decoded = huffman.decode(encoded);
        System.out.println("Decoded (first 50 chars): " + decoded.substring(0, Math.min(50, decoded.length())) + "...");
        if (original.equals(decoded)) {
            System.out.println("✓ Success: Original matches decoded");
        } else {
            System.out.println("✗ Failure: Original does not match decoded");
        }
        int originalBits = original.length() * 8;
        int encodedBits = encoded.length();
        float compressionRatio = (float) originalBits / encodedBits;
        System.out.println("Original length: " + originalBits + " bits");
        System.out.println("Encoded length: " + encodedBits + " bits");
        System.out.println("Compression ratio: " + compressionRatio);
        System.out.println("Compression percentage: " + (1 - (float) encodedBits / originalBits) * 100 + "%");
        System.out.println();
    }
    public static void main(String[] args) {
        AdaptiveHuffmanTest test = new AdaptiveHuffmanTest();
        test.setUp();
        test.testSimple();
        test.setUp();
        test.testCase1();
        test.setUp();
        test.testCase2();
    }
}
