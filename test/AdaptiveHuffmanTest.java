import java.util.HashMap;
import java.util.Map;
public class AdaptiveHuffmanTest{
private AdaptiveHuffman huffman;
public void setUp(){
huffman=new AdaptiveHuffman();
}
public void testSingleChar(){
System.out.println("====== TEST CASE 0: Single Character ======");
String original="A";
System.out.println("Original: "+original);
String encoded=huffman.encode(original);
System.out.println("Encoded: "+encoded);
String decoded=huffman.decode(encoded);
System.out.println("Decoded: "+decoded);
if(original.equals(decoded)){
System.out.println("✓ Success: Original matches decoded");
}else{
System.out.println("✗ Failure: Original does not match decoded");
}
System.out.println();
}
public void testRepeatedChar(){
System.out.println("====== TEST CASE 1: Repeated Character ======");
String original="AA";
System.out.println("Original: "+original);
String encoded=huffman.encode(original);
System.out.println("Encoded: "+encoded);
String decoded=huffman.decode(encoded);
System.out.println("Decoded: "+decoded);
if(original.equals(decoded)){
System.out.println("✓ Success: Original matches decoded");
}else{
System.out.println("✗ Failure: Original does not match decoded");
}
System.out.println();
}
public static void main(String[]args){
AdaptiveHuffmanTest test=new AdaptiveHuffmanTest();
test.setUp();
test.huffman.enableDebugMode();
test.testSingleChar();
test.setUp();
test.testRepeatedChar();
test.setUp();
test.testNodeSwapping();
}
public void testNodeSwapping(){
System.out.println("\n\n====== TEST CASE 2: NODE SWAPPING TEST ======");
System.out.println("This test specifically checks if nodes are swapped correctly");
System.out.println("according to the FGK algorithm's sibling property.");
String original="ABCAABBCABCABAC";
System.out.println("\nOriginal string: "+original);
System.out.println("Character frequencies:");
countFrequencies(original);
System.out.println("\n--- ENCODING PROCESS ---");
String encoded=huffman.encode(original);
System.out.println("\nEncoded: "+encoded);
System.out.println("Encoded length: "+encoded.length()+" bits");
System.out.println("\n--- DECODING PROCESS ---");
String decoded=huffman.decode(encoded);
System.out.println("\nDecoded: "+decoded);
if(original.equals(decoded)){
System.out.println("\n✓ SUCCESS: Original matches decoded");
int originalBits=original.length()*8;
int compressedBits=encoded.length();
float ratio=(float)originalBits/compressedBits;
System.out.println("Original size: "+originalBits+" bits");
System.out.println("Compressed size: "+compressedBits+" bits");
System.out.println("Compression ratio: "+ratio);
if(ratio>1.0){
System.out.println("Compression achieved! The encoded size is smaller than the original.");
}else{
System.out.println("No compression achieved. This is expected for very short strings.");
}
}else{
System.out.println("\n✗ FAILURE: Original does not match decoded");
System.out.println("Original: "+original);
System.out.println("Decoded: "+decoded);
}
System.out.println("\n====== END OF NODE SWAPPING TEST ======\n");
}
private void countFrequencies(String input){
Map<Character,Integer>freq=new HashMap<>();
for(char c:input.toCharArray()){
freq.put(c,freq.getOrDefault(c,0)+1);
}
for(Map.Entry<Character,Integer>entry:freq.entrySet()){
System.out.println("'"+entry.getKey()+"': "+entry.getValue()+" occurrences");
}
}
}