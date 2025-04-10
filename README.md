# Adaptive Huffman Coding

Adaptive Huffman Coding is a data compression algorithm that dynamically adjusts the coding tree based on the frequency of symbols in the input data. This repository contains a Java implementation of the Adaptive Huffman Coding algorithm, along with a visualizer to help understand the encoding and decoding processes.

## Table of Contents
- [Features](#features)
- [Dependencies](#dependencies)
- [Installation](#installation)
- [Usage](#usage)
- [Running Tests](#running-tests)
- [Using the Visualizer](#using-the-visualizer)
- [Contributing](#contributing)
- [License](#license)

## Features
- Encode and decode strings using Adaptive Huffman Coding.
- File encoding and decoding capabilities.
- Debug and visualization modes for better understanding of the algorithm.

## Dependencies
To run this project, you need:
- Java Development Kit (JDK) 8 or higher

## Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/adaptive-huffman-coding.git
   ```
2. **Navigate to the project directory:**
   ```bash
   cd adaptive-huffman-coding
   ```

## Usage
### Encoding and Decoding
To encode and decode strings, you can use the `AdaptiveHuffman` class. Here’s a simple example:

```java
AdaptiveHuffman huffman = new AdaptiveHuffman();
String original = "Hello, World!";
String encoded = huffman.encode(original);
System.out.println("Encoded: " + encoded);
String decoded = huffman.decode(encoded);
System.out.println("Decoded: " + decoded);
```

### File Encoding/Decoding
You can also encode and decode files using the following methods:

```java
huffman.encodeFile("input.txt", "encoded.txt");
huffman.decodeFile("encoded.txt", "decoded.txt");
```

### Running Tests
To run the tests, you can execute the `AdaptiveHuffmanTest` class. This will run a series of test cases to ensure the functionality of the encoding and decoding processes.

```bash
javac src/*.java
java src/AdaptiveHuffmanTest
```

## Using the Visualizer
The project includes a visualization tool to help you understand how the Adaptive Huffman Tree evolves during encoding and decoding. You can run the visualizer with:

```bash
java -cp visualization HuffmanTreeVisualizer
```

### Visualizer Instructions
1. **Input Text**: Enter the text you want to encode in the input field.
2. **Select Test Cases**: You can choose predefined test cases (e.g., "ABC ABC ABC", "AABBC AAB", "Hello World") to see how the algorithm works with different inputs.
3. **Encoding**:
   - Click the **Encode** button to start the encoding process.
   - Click the **Step** button to process each character one by one. The tree will update after each step, showing how the encoding progresses.
4. **Decoding**:
   - Enter the encoded binary string in the **Encoded Bits** area.
   - Click the **Decode** button to decode the input.
5. **Tree Navigation**:
   - Use the **+** and **-** buttons to zoom in and out of the tree.
   - Click and drag to pan the view.
   - Use the mouse wheel for zooming.
   - Click **Reset View** to restore the default view.

### Visualization Features
- The visualizer highlights nodes as they are processed during encoding and decoding.
- It displays the current state of the Huffman tree, allowing you to see how symbols are added and how the tree structure changes.

## Contributing
Contributions are welcome! Please feel free to submit a pull request or open an issue for any suggestions or improvements.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
