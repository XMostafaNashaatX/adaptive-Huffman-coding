import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class HuffmanTreeVisualizer extends JFrame implements HuffmanTree.TreeUpdateListener,Encoder.EncodingListener,Decoder.DecodingListener{
private JPanel treePanel;
private JPanel statsPanel;
private JTextArea logArea;
private JTextField inputField;
private JButton encodeButton;
private JButton decodeButton;
private JButton stepButton;
private JTextArea encodedTextArea;
private JTextArea decodedTextArea;
private JLabel originalLengthLabel;
private JLabel encodedLengthLabel;
private JLabel compressionRatioLabel;
private JLabel compressionPercentageLabel;
private AdaptiveHuffman huffman;
private String inputText="";
private String encodedText="";
private int currentStep=0;
private boolean isEncoding=true;
private Map<Node,Point> nodePositions=new HashMap<>();
private Map<Node,Color> nodeColors=new HashMap<>();
private static final Color BACKGROUND_COLOR=new Color(245,245,250);
private static final Color NODE_COLOR=new Color(80,120,200);
private static final Color NYT_NODE_COLOR=new Color(255,140,0);
private static final Color LEAF_NODE_COLOR=new Color(50,170,100);
private static final Color HIGHLIGHT_COLOR=new Color(220,50,50);
private static final Color TEXT_COLOR=Color.BLACK;
private static final Color LINE_COLOR=new Color(60,60,60);
private static final int NODE_RADIUS=22;
private static final int VERTICAL_SPACING=75;
private double zoomFactor=1.0;
private int panX=0;
private int panY=0;
private Point lastMousePos;
private boolean isPanning=false;
public HuffmanTreeVisualizer(){
super("Adaptive Huffman Tree Visualizer");
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setSize(1200,800);
huffman=new AdaptiveHuffman();
huffman.addTreeUpdateListener(this);
huffman.addEncodingListener(this);
huffman.addDecodingListener(this);
setupUI();
setLocationRelativeTo(null);
}
private void setupUI(){
JPanel mainPanel=new JPanel(new BorderLayout(10,10));
mainPanel.setBackground(BACKGROUND_COLOR);
mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
treePanel=createTreePanel();
JPanel controlPanel=createControlPanel();
JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(treePanel),controlPanel);
splitPane.setResizeWeight(0.7);
splitPane.setDividerLocation(800);
mainPanel.add(splitPane,BorderLayout.CENTER);
setContentPane(mainPanel);
}
private JPanel createTreePanel(){
JPanel panel=new JPanel(){
@Override
protected void paintComponent(Graphics g){
super.paintComponent(g);
Graphics2D g2d=(Graphics2D)g;
g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
g2d.setColor(Color.WHITE);
g2d.fillRect(0,0,getWidth(),getHeight());
g2d.translate(panX,panY);
g2d.scale(zoomFactor,zoomFactor);
drawTree(g2d);
}
};
panel.setPreferredSize(new Dimension(800,600));
panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
panel.addMouseListener(new MouseAdapter(){
@Override
public void mousePressed(MouseEvent e){
isPanning=true;
lastMousePos=e.getPoint();
}
@Override
public void mouseReleased(MouseEvent e){
isPanning=false;
}
});
panel.addMouseMotionListener(new MouseMotionAdapter(){
@Override
public void mouseDragged(MouseEvent e){
if(isPanning){
int dx=e.getX()-lastMousePos.x;
int dy=e.getY()-lastMousePos.y;
panX+=dx;
panY+=dy;
lastMousePos=e.getPoint();
panel.repaint();
}
}
});
panel.addMouseWheelListener(e->{
if(e.getWheelRotation()<0){
zoomFactor*=1.1;
}else{
zoomFactor*=0.9;
}
zoomFactor=Math.max(0.1,Math.min(zoomFactor,5.0));
panel.repaint();
});
return panel;
}
private JPanel createControlPanel(){
JPanel controlPanel=new JPanel(new BorderLayout(10,10));
controlPanel.setBackground(BACKGROUND_COLOR);
JPanel topControlsPanel=new JPanel(new BorderLayout(5,5));
topControlsPanel.setBackground(BACKGROUND_COLOR);
JPanel zoomPanel=createZoomPanel();
topControlsPanel.add(zoomPanel,BorderLayout.NORTH);
JPanel inputPanel=new JPanel(new BorderLayout(5,5));
inputPanel.setBackground(BACKGROUND_COLOR);
JLabel inputLabel=new JLabel("Input Text:");
inputLabel.setFont(new Font("Arial",Font.BOLD,14));
inputField=new JTextField();
inputField.setFont(new Font("Monospaced",Font.BOLD,16));
inputField.setPreferredSize(new Dimension(300,30));
inputField.setBorder(BorderFactory.createCompoundBorder(
BorderFactory.createLineBorder(Color.BLUE,2),
BorderFactory.createEmptyBorder(5,5,5,5)
));
inputField.setText("Enter text to encode here");
inputPanel.add(inputLabel,BorderLayout.WEST);
inputPanel.add(inputField,BorderLayout.CENTER);
topControlsPanel.add(inputPanel,BorderLayout.CENTER);
JPanel testCasePanel=createTestCasePanel();
topControlsPanel.add(testCasePanel,BorderLayout.SOUTH);
JPanel buttonPanel=createButtonPanel();
JPanel dataPanel=createDataPanel();
statsPanel=createStatsPanel();
logArea=new JTextArea();
logArea.setEditable(false);
logArea.setFont(new Font("Monospaced",Font.PLAIN,12));
JScrollPane logScrollPane=new JScrollPane(logArea);
logScrollPane.setPreferredSize(new Dimension(350,200));
logScrollPane.setBorder(BorderFactory.createTitledBorder("Log"));
JPanel southPanel=new JPanel(new BorderLayout());
southPanel.setBackground(BACKGROUND_COLOR);
southPanel.add(statsPanel,BorderLayout.NORTH);
southPanel.add(logScrollPane,BorderLayout.CENTER);
JPanel topSection=new JPanel(new BorderLayout(5,5));
topSection.setBackground(BACKGROUND_COLOR);
topSection.add(topControlsPanel,BorderLayout.NORTH);
topSection.add(buttonPanel,BorderLayout.CENTER);
controlPanel.add(topSection,BorderLayout.NORTH);
controlPanel.add(dataPanel,BorderLayout.CENTER);
controlPanel.add(southPanel,BorderLayout.SOUTH);
return controlPanel;
}
private JPanel createZoomPanel(){
JPanel zoomPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
zoomPanel.setBackground(BACKGROUND_COLOR);
JButton zoomInButton=new JButton("+");
zoomInButton.setForeground(TEXT_COLOR);
JButton zoomOutButton=new JButton("-");
zoomOutButton.setForeground(TEXT_COLOR);
JButton resetButton=new JButton("Reset View");
resetButton.setForeground(TEXT_COLOR);
zoomInButton.addActionListener(e->{
zoomFactor*=1.2;
treePanel.repaint();
});
zoomOutButton.addActionListener(e->{
zoomFactor*=0.8;
treePanel.repaint();
});
resetButton.addActionListener(e->{
zoomFactor=1.0;
panX=0;
panY=0;
treePanel.repaint();
});
zoomPanel.add(new JLabel("Zoom:"));
zoomPanel.add(zoomInButton);
zoomPanel.add(zoomOutButton);
zoomPanel.add(resetButton);
return zoomPanel;
}
private JPanel createTestCasePanel(){
JPanel testCasePanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
testCasePanel.setBackground(BACKGROUND_COLOR);
JLabel testCaseLabel=new JLabel("Test Cases:");
testCaseLabel.setFont(new Font("Arial",Font.BOLD,12));
JButton testCase1=new JButton("ABC ABC ABC");
testCase1.setForeground(TEXT_COLOR);
testCase1.addActionListener(e->inputField.setText("ABCABCABC"));
JButton testCase2=new JButton("AABBC AAB");
testCase2.setForeground(TEXT_COLOR);
testCase2.addActionListener(e->inputField.setText("AABBCAAB"));
JButton testCase3=new JButton("Hello World");
testCase3.setForeground(TEXT_COLOR);
testCase3.addActionListener(e->inputField.setText("Hello World"));
JButton clearButton=new JButton("Clear");
clearButton.setForeground(TEXT_COLOR);
clearButton.addActionListener(e->inputField.setText(""));
testCasePanel.add(testCaseLabel);
testCasePanel.add(testCase1);
testCasePanel.add(testCase2);
testCasePanel.add(testCase3);
testCasePanel.add(clearButton);
return testCasePanel;
}
private JPanel createButtonPanel(){
JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,10,5));
buttonPanel.setBackground(BACKGROUND_COLOR);
encodeButton=new JButton("Encode");
decodeButton=new JButton("Decode");
stepButton=new JButton("Step");
encodeButton.setBackground(new Color(70,130,180));
encodeButton.setForeground(TEXT_COLOR);
encodeButton.setFont(new Font("Arial",Font.BOLD,14));
decodeButton.setBackground(new Color(70,130,180));
decodeButton.setForeground(TEXT_COLOR);
decodeButton.setFont(new Font("Arial",Font.BOLD,14));
stepButton.setBackground(new Color(70,130,180));
stepButton.setForeground(TEXT_COLOR);
stepButton.setFont(new Font("Arial",Font.BOLD,14));
encodeButton.addActionListener(e->startEncoding());
decodeButton.addActionListener(e->startDecoding());
stepButton.addActionListener(e->processStep());
stepButton.setEnabled(false);
buttonPanel.add(encodeButton);
buttonPanel.add(decodeButton);
buttonPanel.add(stepButton);
return buttonPanel;
}
private JPanel createDataPanel(){
JPanel dataPanel=new JPanel(new GridLayout(2,1,5,5));
dataPanel.setBackground(BACKGROUND_COLOR);
encodedTextArea=new JTextArea();
encodedTextArea.setEditable(true);
encodedTextArea.setFont(new Font("Monospaced",Font.PLAIN,12));
encodedTextArea.setLineWrap(true);
encodedTextArea.setWrapStyleWord(true);
JScrollPane encodedScrollPane=new JScrollPane(encodedTextArea);
encodedScrollPane.setBorder(BorderFactory.createTitledBorder("Encoded Bits"));
decodedTextArea=new JTextArea();
decodedTextArea.setEditable(false);
decodedTextArea.setFont(new Font("Monospaced",Font.PLAIN,12));
decodedTextArea.setLineWrap(true);
decodedTextArea.setWrapStyleWord(true);
JScrollPane decodedScrollPane=new JScrollPane(decodedTextArea);
decodedScrollPane.setBorder(BorderFactory.createTitledBorder("Decoded Text"));
dataPanel.add(encodedScrollPane);
dataPanel.add(decodedScrollPane);
return dataPanel;
}
private JPanel createStatsPanel(){
statsPanel=new JPanel(new GridLayout(2,2,10,5));
statsPanel.setBackground(BACKGROUND_COLOR);
statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
originalLengthLabel=new JLabel("Original Length: 0 bits");
encodedLengthLabel=new JLabel("Encoded Length: 0 bits");
compressionRatioLabel=new JLabel("Compression Ratio: 0.0");
compressionPercentageLabel=new JLabel("Compression: 0.0%");
statsPanel.add(originalLengthLabel);
statsPanel.add(encodedLengthLabel);
statsPanel.add(compressionRatioLabel);
statsPanel.add(compressionPercentageLabel);
return statsPanel;
}
private void drawTree(Graphics2D g2d){
HuffmanTree tree=huffman.getEncoder().getTree();
Node root=tree.getRoot();
if(root==null)return;
nodePositions.clear();
calculateNodePositions(root,getWidth()/2,50,getWidth()/4);
drawConnections(g2d,root);
drawNodes(g2d,root);
}
private void calculateNodePositions(Node node,int x,int y,int xOffset){
if(node==null)return;
nodePositions.put(node,new Point(x,y));
Node leftChild=node.getLeftChild();
Node rightChild=node.getRightChild();
if(leftChild!=null||rightChild!=null){
int nextLevelOffset=Math.max(xOffset/2,NODE_RADIUS*2);
if(leftChild!=null){
calculateNodePositions(leftChild,x-nextLevelOffset,y+VERTICAL_SPACING,nextLevelOffset);
}
if(rightChild!=null){
calculateNodePositions(rightChild,x+nextLevelOffset,y+VERTICAL_SPACING,nextLevelOffset);
}
}
}
private void drawConnections(Graphics2D g2d,Node node){
if(node==null)return;
Point nodePos=nodePositions.get(node);
if(nodePos==null)return;
Node leftChild=node.getLeftChild();
Node rightChild=node.getRightChild();
if(leftChild!=null){
Point leftPos=nodePositions.get(leftChild);
if(leftPos!=null){
g2d.setStroke(new BasicStroke(1.5f));
g2d.setColor(LINE_COLOR);
g2d.drawLine(nodePos.x,nodePos.y,leftPos.x,leftPos.y);
Point labelPos=new Point(
nodePos.x-(nodePos.x-leftPos.x)/4,
nodePos.y+(leftPos.y-nodePos.y)/4
);
g2d.setColor(TEXT_COLOR);
g2d.drawString("0",labelPos.x,labelPos.y);
drawConnections(g2d,leftChild);
}
}
if(rightChild!=null){
Point rightPos=nodePositions.get(rightChild);
if(rightPos!=null){
g2d.setStroke(new BasicStroke(1.5f));
g2d.setColor(LINE_COLOR);
g2d.drawLine(nodePos.x,nodePos.y,rightPos.x,rightPos.y);
Point labelPos=new Point(
nodePos.x+(rightPos.x-nodePos.x)/4,
nodePos.y+(rightPos.y-nodePos.y)/4
);
g2d.setColor(TEXT_COLOR);
g2d.drawString("1",labelPos.x,labelPos.y);
drawConnections(g2d,rightChild);
}
}
}
private void drawNodes(Graphics2D g2d,Node node){
if(node==null)return;
Point nodePos=nodePositions.get(node);
if(nodePos==null)return;
Color nodeColor=getNodeColor(node);
g2d.setColor(nodeColor);
g2d.fillOval(nodePos.x-NODE_RADIUS,nodePos.y-NODE_RADIUS,
NODE_RADIUS*2,NODE_RADIUS*2);
g2d.setColor(Color.BLACK);
g2d.setStroke(new BasicStroke(1.5f));
g2d.drawOval(nodePos.x-NODE_RADIUS,nodePos.y-NODE_RADIUS,
NODE_RADIUS*2,NODE_RADIUS*2);
g2d.setColor(TEXT_COLOR);
g2d.setFont(new Font("SansSerif",Font.BOLD,11));
String nodeText;
if(node.isNYT()){
nodeText="NYT";
}else if(node.isLeaf()){
char symbol=node.getSymbol();
nodeText=displayableChar(symbol)+" W:"+node.getWeight();
}else{
nodeText="W:"+node.getWeight();
}
String nodeNumber="#"+node.getNodeNumber();
FontMetrics fm=g2d.getFontMetrics();
int textWidth=fm.stringWidth(nodeText);
int numberWidth=fm.stringWidth(nodeNumber);
g2d.drawString(nodeText,nodePos.x-textWidth/2,nodePos.y+4);
g2d.setFont(new Font("SansSerif",Font.PLAIN,9));
g2d.drawString(nodeNumber,nodePos.x-numberWidth/2,nodePos.y+15);
if(node.getLeftChild()!=null){
drawNodes(g2d,node.getLeftChild());
}
if(node.getRightChild()!=null){
drawNodes(g2d,node.getRightChild());
}
}
private String displayableChar(char c){
if(c==' ')return"[space]";
if(c=='\n')return"\\n";
if(c=='\t')return"\\t";
if(c=='\r')return"\\r";
if(c<32||c>126)return"\\u"+String.format("%04x",(int)c);
return String.valueOf(c);
}
private Color getNodeColor(Node node){
Color customColor=nodeColors.get(node);
if(customColor!=null){
return customColor;
}
if(node.isNYT()){
return NYT_NODE_COLOR;
}else if(node.isLeaf()){
return LEAF_NODE_COLOR;
}else{
return NODE_COLOR;
}
}
private void highlightNode(Node node,Color color){
if(node!=null){
nodeColors.put(node,color);
javax.swing.Timer timer=new javax.swing.Timer(1500,e->{
nodeColors.remove(node);
treePanel.repaint();
});
timer.setRepeats(false);
timer.start();
}
}
private void updateStatistics(){
if(statsPanel==null)return;
int originalBits=0;
int encodedBits=0;
if(inputText!=null&&!inputText.isEmpty()){
originalBits=inputText.length()*8;
}
if(encodedText!=null&&!encodedText.isEmpty()){
encodedBits=encodedText.length();
}
originalLengthLabel.setText("Original Length: "+originalBits+" bits");
encodedLengthLabel.setText("Encoded Length: "+encodedBits+" bits");
if(originalBits>0&&encodedBits>0){
double ratio=(double)originalBits/encodedBits;
double percentage=(1.0-(double)encodedBits/originalBits)*100;
compressionRatioLabel.setText(String.format("Compression Ratio: %.2f",ratio));
compressionPercentageLabel.setText(String.format("Compression: %.2f%%",percentage));
}else{
compressionRatioLabel.setText("Compression Ratio: 0.0");
compressionPercentageLabel.setText("Compression: 0.0%");
}
statsPanel.revalidate();
statsPanel.repaint();
}
private void startEncoding(){
resetState();
inputText=inputField.getText();
if(inputText==null||inputText.isEmpty()){
logArea.append("Please enter text to encode.\n");
return;
}
isEncoding=true;
currentStep=0;
encodedTextArea.setText("");
decodedTextArea.setText("");
logArea.append("Starting encoding process for: \""+inputText+"\"\n");
stepButton.setEnabled(true);
encodeButton.setForeground(TEXT_COLOR);
decodeButton.setForeground(TEXT_COLOR);
stepButton.setForeground(TEXT_COLOR);
treePanel.repaint();
}
private void startDecoding(){
resetState();
encodedText=encodedTextArea.getText();
if(encodedText==null||encodedText.isEmpty()){
logArea.append("Please enter binary text to decode.\n");
return;
}
if(!encodedText.matches("[01]+")){
logArea.append("The encoded text must consist of only 0s and 1s.\n");
return;
}
isEncoding=false;
currentStep=0;
decodedTextArea.setText("");
logArea.append("Starting decoding process for binary input.\n");
stepButton.setEnabled(true);
encodeButton.setForeground(TEXT_COLOR);
decodeButton.setForeground(TEXT_COLOR);
stepButton.setForeground(TEXT_COLOR);
}
private void processStep(){
encodeButton.setForeground(TEXT_COLOR);
decodeButton.setForeground(TEXT_COLOR);
stepButton.setForeground(TEXT_COLOR);
if(isEncoding){
if(currentStep<inputText.length()){
try{
char symbol=inputText.charAt(currentStep);
logArea.append("Processing symbol: '"+displayableChar(symbol)+
"' (Step "+(currentStep+1)+" of "+inputText.length()+")\n");
String encoding=huffman.getEncoder().encodeSymbol(symbol);
if(encoding!=null){
encodedTextArea.append(encoding);
encodedText=encodedTextArea.getText();
logArea.append("Encoded '"+displayableChar(symbol)+"' as '"+encoding+"'\n");
}else{
logArea.append("Error encoding symbol: "+displayableChar(symbol)+"\n");
}
currentStep++;
updateStatistics();
}catch(Exception e){
logArea.append("Error during encoding: "+e.getMessage()+"\n");
e.printStackTrace(System.out);
}
}else{
logArea.append("Encoding complete.\n");
stepButton.setEnabled(false);
updateStatistics();
}
}else{
if(currentStep==0){
try{
encodedText=encodedTextArea.getText();
if(encodedText==null||encodedText.isEmpty()){
logArea.append("Please enter binary text to decode.\n");
return;
}
if(!encodedText.matches("[01]+")){
logArea.append("The encoded text must consist of only 0s and 1s.\n");
return;
}
logArea.append("Starting decoding of: "+encodedText+"\n");
String decodedText=huffman.decode(encodedText);
if(decodedText!=null){
decodedTextArea.setText(decodedText);
inputText=decodedText;
logArea.append("Decoding complete. Result: \""+decodedText+"\"\n");
}else{
logArea.append("Error during decoding. Check the encoded text format.\n");
}
currentStep++;
stepButton.setEnabled(false);
updateStatistics();
}catch(Exception e){
logArea.append("Error during decoding: "+e.getMessage()+"\n");
e.printStackTrace(System.out);
}
}
}
treePanel.repaint();
}
private void resetState(){
huffman=new AdaptiveHuffman();
huffman.getEncoder().getTree().setDebugMode(true);
huffman.addTreeUpdateListener(this);
huffman.addEncodingListener(this);
huffman.addDecodingListener(this);
logArea.append("\n--- Starting new operation ---\n");
encodeButton.setForeground(TEXT_COLOR);
decodeButton.setForeground(TEXT_COLOR);
stepButton.setForeground(TEXT_COLOR);
nodePositions.clear();
nodeColors.clear();
originalLengthLabel.setText("Original Length: 0 bits");
encodedLengthLabel.setText("Encoded Length: 0 bits");
compressionRatioLabel.setText("Compression Ratio: 0.0");
compressionPercentageLabel.setText("Compression: 0.0%");
treePanel.repaint();
updateStatistics();
}
@Override
public void onTreeUpdate(HuffmanTree tree,String updateType){
Node root=tree.getRoot();
treePanel.repaint();
if(updateType.equals("symbol")){
char symbol=updateType.charAt(0);
Node node=tree.getNode(symbol);
if(node!=null){
highlightNode(node,HIGHLIGHT_COLOR);
logArea.append("Tree updated with symbol: "+displayableChar(symbol)+"\n");
}
}else{
logArea.append("Tree updated: "+updateType+"\n");
}
}
@Override
public void onSymbolEncoded(char symbol,String encoding){
Node node=huffman.getEncoder().getTree().getNode(symbol);
if(node!=null){
highlightNode(node,HIGHLIGHT_COLOR);
logArea.append("Symbol encoded: '"+displayableChar(symbol)+"' → "+encoding+"\n");
}
}
@Override
public void onSymbolDecoded(char symbol,String bits){
Node node=huffman.getDecoder().getTree().getNode(symbol);
if(node!=null){
highlightNode(node,HIGHLIGHT_COLOR);
logArea.append("Symbol decoded: "+bits+" → '"+displayableChar(symbol)+"'\n");
}
}
public static void main(String[]args){
try{
UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
UIManager.put("Button.foreground",Color.BLACK);
}catch(Exception e){
e.printStackTrace();
}
SwingUtilities.invokeLater(()->{
HuffmanTreeVisualizer visualizer=new HuffmanTreeVisualizer();
visualizer.setVisible(true);
visualizer.logArea.setText("Welcome to the Adaptive Huffman Tree Visualizer!\n\n"+
"Instructions:\n"+
"1. Enter your own text in the input field or select one of the test cases.\n"+
"2. Click 'Encode' to start encoding.\n"+
"3. Click 'Step' to process each character one by one.\n"+
"4. The tree will be updated after each step.\n\n"+
"Tree Navigation:\n"+
"- Use the +/- buttons to zoom in/out\n"+
"- Click and drag to pan the view\n"+
"- Mouse wheel also works for zooming\n"+
"- Click 'Reset View' to restore the default view\n\n"+
"To observe node swapping, try encoding a string with repeated characters.\n"+
"Custom test cases will help you explore different tree formations.\n");
visualizer.inputField.setText("");
visualizer.inputText="";
visualizer.currentStep=0;
visualizer.encodedText="";
visualizer.encodedTextArea.setText("");
visualizer.decodedTextArea.setText("");
visualizer.isEncoding=true;
visualizer.encodeButton.setForeground(TEXT_COLOR);
visualizer.decodeButton.setForeground(TEXT_COLOR);
visualizer.stepButton.setForeground(TEXT_COLOR);
UIManager.put("Button.foreground",Color.BLACK);
visualizer.huffman.getEncoder().getTree().setDebugMode(true);
visualizer.updateStatistics();
});
}
}