import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class HuffmanTree{
private Node root;
private Node nytNode;
private final List<Node>nodeList;
private final Map<Character,Node>symbolToNode;
private int nextNodeNumber;
private boolean debugMode=false;
private List<TreeUpdateListener>listeners=new ArrayList<>();
private static final int MAX_NODE_NUMBER=512;
public interface TreeUpdateListener{
void onTreeUpdate(HuffmanTree tree,String updateType);
}
public void addListener(TreeUpdateListener listener){
listeners.add(listener);
}
private void notifyListeners(String updateType){
for(TreeUpdateListener listener:listeners){
listener.onTreeUpdate(this,updateType);
}
}
public HuffmanTree(){
nextNodeNumber=MAX_NODE_NUMBER;
nytNode=new Node(nextNodeNumber--,true);
nytNode.setExplicitNullChildren();
root=nytNode;
symbolToNode=new HashMap<>();
nodeList=new ArrayList<>();
nodeList.add(root);
listeners=new ArrayList<>();
if(debugMode){
System.out.println("DEBUG [Tree] Initialized with NYT root: "+root);
}
}
private void ensureInitialNYTNodeReady(){
if(root==nytNode&&nytNode.getLeftChild()==null&&nytNode.getRightChild()==null){
if(debugMode){
System.out.println("DEBUG [Tree] Setting up initial NYT node with null children");
}
nytNode.setExplicitNullChildren();
}
}
public void setDebugMode(boolean enabled){
this.debugMode=enabled;
System.out.println("DEBUG mode "+(enabled?"enabled":"disabled")+" for Huffman Tree");
}
public void processSymbol(char symbol){
ensureInitialNYTNodeReady();
if(debugMode){
String symStr;
if(Character.isISOControl(symbol)){
symStr="0x"+Integer.toHexString((int)symbol);
}else{
symStr=String.valueOf(symbol);
}
System.out.println("\n=========================================");
System.out.println("DEBUG [Tree] >>> Processing Symbol: '"+symStr+"' <<<"+" (ASCII: "+(int)symbol+")");
System.out.println("=========================================");
System.out.println("Current Tree State BEFORE processing:");
printTree();
}
Node nodeToUpdateFrom;
if(symbolToNode.containsKey(symbol)){
if(debugMode){
System.out.println("DEBUG [Tree] Symbol '"+symbol+"' exists. Finding its leaf node.");
}
Node leafNode=symbolToNode.get(symbol);
nodeToUpdateFrom=leafNode;
}else{
if(debugMode){
System.out.println("DEBUG [Tree] Symbol '"+symbol+"' is NEW. Adding to tree.");
}
Node oldNYT=this.nytNode;
Node newInternal=new Node(oldNYT.getParent(),nextNodeNumber--);
nodeList.add(newInternal);
Node newLeaf=new Node(newInternal,symbol,nextNodeNumber--);
nodeList.add(newLeaf);
symbolToNode.put(symbol,newLeaf);
Node newNYT=new Node(nextNodeNumber--,true);
newNYT.setParent(newInternal);
nodeList.add(newNYT);
newInternal.setLeftChild(newNYT);
newInternal.setRightChild(newLeaf);
newInternal.setWeight(1);
if(oldNYT==root){
root=newInternal;
if(debugMode)System.out.println("DEBUG [Tree] New internal node "+newInternal+" becomes the root.");
}else{
Node parent=oldNYT.getParent();
newInternal.setParent(parent);
if(parent.getLeftChild()==oldNYT){
parent.setLeftChild(newInternal);
}else{
parent.setRightChild(newInternal);
}
if(debugMode)System.out.println("DEBUG [Tree] Linked new internal node "+newInternal+" under parent "+parent);
}
this.nytNode=newNYT;
nodeToUpdateFrom=newInternal;
if(debugMode){
System.out.println("DEBUG [Tree] Added Nodes: Internal="+newInternal+", Leaf="+newLeaf+", NewNYT="+newNYT);
System.out.println("DEBUG [Tree] Tree state after adding nodes (before path update):");
printTree();
}
}
updateTreePath(nodeToUpdateFrom);
if(debugMode){
String symStr;
if(Character.isISOControl(symbol)){
symStr="0x"+Integer.toHexString((int)symbol);
}else{
symStr=String.valueOf(symbol);
}
System.out.println("DEBUG [Tree] <<< Finished Processing Symbol: '"+symStr+"' >>>");
System.out.println("Final Tree State:");
printTree();
}
}
private void updateTreePath(Node node){
if(debugMode){
System.out.println("\nDEBUG [Tree] --- Starting Update Path from: "+node+" ---");
}
while(node!=null){
if(debugMode)System.out.println("DEBUG [Tree] Current node on path: "+node+" (Weight before inc: "+node.getWeight()+")");
Node leader=findHighestNodeInWeightBlock(node.getWeight(),node);
if(leader!=null&&node!=leader&&leader!=node.getParent()&&node!=root){
if(debugMode){
System.out.println("DEBUG [Tree] *** SWAP Condition Met for Node "+node+" ***");
System.out.println("DEBUG [Tree] Leader of block W="+node.getWeight()+" is "+leader);
System.out.println("DEBUG [Tree] Performing swap between "+node+" and "+leader);
}
swapNodes(node,leader);
if(debugMode){
System.out.println("DEBUG [Tree] Swap complete. Continuing update path from node "+node+" (now in new position).");
}
}else{
if(debugMode){
System.out.println("DEBUG [Tree] No swap needed for node "+node+" at weight "+node.getWeight()+".");
if(node==root)System.out.println("DEBUG [Tree] Reason: Node is root.");
else if(leader!=null&&node==leader)System.out.println("DEBUG [Tree] Reason: Node is already the leader.");
else if(leader!=null&&leader==node.getParent())System.out.println("DEBUG [Tree] Reason: Leader is parent.");
else if(leader==null)System.out.println("DEBUG [Tree] Reason: No other node in this weight block found or leader is ancestor.");
}
}
node.incrementWeight();
if(debugMode){
System.out.println("DEBUG [Tree] Incremented weight of node "+node+" to "+node.getWeight());
}
Node parent=node.getParent();
if(debugMode&&parent!=null){
System.out.println("DEBUG [Tree] Moving up update path to parent: "+parent);
}else if(debugMode){
System.out.println("DEBUG [Tree] Reached root or null parent. Update path finished.");
}
node=parent;
}
if(debugMode){
System.out.println("--- Update Path Complete ---");
}
}
private Node findHighestNodeInWeightBlock(int weight,Node nodeToExcludeAncestorsOf){
Node blockLeader=null;
int highestNodeNumber=-1;
if(debugMode){
System.out.println("DEBUG [Tree] Finding Block Leader: Weight="+weight+", Relative to Node="+nodeToExcludeAncestorsOf);
}
for(Node candidate:nodeList){
if(candidate.getWeight()==weight&&!candidate.isNYT()){
if(!isAncestor(candidate,nodeToExcludeAncestorsOf)&&!isAncestor(nodeToExcludeAncestorsOf,candidate)){
if(candidate.getNodeNumber()>highestNodeNumber){
highestNodeNumber=candidate.getNodeNumber();
blockLeader=candidate;
if(debugMode){
System.out.println("DEBUG [Tree] Found new potential leader for weight "+weight+": "+blockLeader);
}
}
}else{
if(debugMode&&isAncestor(candidate,nodeToExcludeAncestorsOf)){
System.out.println("DEBUG [Tree] Skipping candidate "+candidate+": Is ANCESTOR of "+nodeToExcludeAncestorsOf);
}
if(debugMode&&isAncestor(nodeToExcludeAncestorsOf,candidate)){
System.out.println("DEBUG [Tree] Skipping candidate "+candidate+": Is DESCENDANT of "+nodeToExcludeAncestorsOf);
}
}
}
}
if(debugMode){
if(blockLeader!=null){
System.out.println("DEBUG [Tree] Final Block Leader for Weight "+weight+": "+blockLeader);
}else{
System.out.println("DEBUG [Tree] No suitable Block Leader found for Weight "+weight);
}
}
return blockLeader;
}
private void swapNodes(Node node1,Node node2){
if(node1==null||node2==null||node1==node2||node1==root||node2==root){
if(debugMode)System.out.println("DEBUG [Tree] SWAP ABORTED: Pre-condition failed (null, same, or root).");
return;
}
Node parent1=node1.getParent();
Node parent2=node2.getParent();
if(parent1==null||parent2==null||parent1==node2||parent2==node1){
if(debugMode)System.out.println("DEBUG [Tree] SWAP ABORTED: Invalid parent relationship for swap.");
return;
}
if(debugMode){
System.out.println("\nDEBUG [Tree] +++ Initiating Swap +++");
System.out.println("DEBUG [Tree] Swapping: "+node1+" with "+node2);
System.out.println("DEBUG [Tree] Parent1: "+parent1+", Parent2: "+parent2);
System.out.println("DEBUG [Tree] Tree BEFORE swap:");
printTree();
}
boolean isLeftChild1=parent1.getLeftChild()==node1;
boolean isLeftChild2=parent2.getLeftChild()==node2;
if(isLeftChild1)parent1.setLeftChild(node2);
else parent1.setRightChild(node2);
if(isLeftChild2)parent2.setLeftChild(node1);
else parent2.setRightChild(node1);
node1.setParent(parent2);
node2.setParent(parent1);
int tempNumber=node1.getNodeNumber();
node1.setNodeNumber(node2.getNodeNumber());
node2.setNodeNumber(tempNumber);
if(node1.isLeaf())symbolToNode.put(node1.getSymbol(),node1);
if(node2.isLeaf())symbolToNode.put(node2.getSymbol(),node2);
if(debugMode){
System.out.println("DEBUG [Tree] Tree AFTER swap:");
printTree();
System.out.println("DEBUG [Tree] +++ Swap Complete +++\n");
}
}
private boolean isAncestor(Node potentialAncestor,Node node){
if(potentialAncestor==null||node==null||potentialAncestor==node){
return false;
}
Node current=node.getParent();
while(current!=null){
if(current==potentialAncestor){
return true;
}
current=current.getParent();
}
return false;
}
public void printTree(){
if(!debugMode)return;
if(root==null){
System.out.println("Tree is empty");
return;
}
System.out.println("-------------------- Tree Structure --------------------");
printNodeRecursive(root,0);
System.out.println("--------------------------------------------------------");
}
private void printNodeRecursive(Node node,int level){
if(node==null)return;
StringBuilder indent=new StringBuilder();
for(int i=0;i<level;i++){
indent.append("|  ");
}
String nodeInfo=node.toString();
if(node==root)nodeInfo+=" [ROOT]";
if(node==nytNode)nodeInfo+=" [Current NYT]";
System.out.println(indent.toString()+"+- "+nodeInfo);
if(node.getLeftChild()!=null||node.getRightChild()!=null){
printNodeRecursive(node.getLeftChild(),level+1);
printNodeRecursive(node.getRightChild(),level+1);
}
}
public Node getRoot(){return root;}
public Node getNYTNode(){return nytNode;}
public Node getNodeForSymbol(char symbol){return symbolToNode.get(symbol);}
public Node getNode(char symbol){
return symbolToNode.get(symbol);
}
public void addSymbol(char symbol){
processSymbol(symbol);
}
public int getSymbolCount(){
return symbolToNode.size();
}
public void prepareForFirstSymbol(){
if(symbolToNode.isEmpty()&&root==nytNode){
ensureInitialNYTNodeReady();
}
}
private void ensureInitialTreeStructure(){
if(symbolToNode.isEmpty()){
ensureInitialNYTNodeReady();
if(debugMode){
System.out.println("DEBUG [Tree] Prepared empty tree for first symbol");
}
}
}
public String getSymbolEncoding(char symbol){
StringBuilder encoding=new StringBuilder();
ensureInitialTreeStructure();
if(debugMode){
System.out.println("DEBUG [Tree] Getting encoding for symbol: '"+symbol+"'");
}
if(symbolToNode.containsKey(symbol)){
Node node=symbolToNode.get(symbol);
getPathFromRoot(node,encoding);
}else{
getPathFromRoot(nytNode,encoding);
String asciiCode=String.format("%8s",Integer.toBinaryString(symbol)).replace(' ','0');
encoding.append(asciiCode);
}
return encoding.toString();
}
private void getPathFromRoot(Node node,StringBuilder path){
if(node==root){
return;
}
List<Character>reversePath=new ArrayList<>();
Node current=node;
while(current!=root){
Node parent=current.getParent();
if(parent==null){
break;
}
if(parent.getLeftChild()==current){
reversePath.add('0');
}else if(parent.getRightChild()==current){
reversePath.add('1');
}
current=parent;
}
for(int i=reversePath.size()-1;i>=0;i--){
path.append(reversePath.get(i));
}
}
public void setVisualizationMode(boolean enabled){
debugMode=enabled;
System.out.println("VISUALIZATION mode "+(enabled?"enabled":"disabled")+" for Huffman Tree");
}
}