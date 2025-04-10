public class HelloWorldTest{
    public static void main(String[]args){
    AdaptiveHuffman huffman=new AdaptiveHuffman();
    huffman.enableDebugMode();
    String original="Hello World";
    System.out.println("Original: "+original);
    String encoded=huffman.encode(original);
    System.out.println("Encoded: "+encoded);
    String decoded=huffman.decode(encoded);
    System.out.println("Decoded: "+decoded);
    System.out.println("Decoded matches original: "+original.equals(decoded));
    int originalBits=original.length()*8;
    int encodedBits=encoded.length();
    double ratio=(double)originalBits/encodedBits;
    double percentage=(1-(double)encodedBits/originalBits)*100;
    System.out.println("Original length: "+originalBits+" bits");
    System.out.println("Encoded length: "+encodedBits+" bits");
    System.out.println("Compression ratio: "+ratio);
    System.out.println("Compression percentage: "+percentage+"%");
    }
    }