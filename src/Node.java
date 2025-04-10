import java.util.Objects;
public class Node{
private int weight;
private int nodeNumber;
private char symbol;
private boolean isNYT;
private Node parent;
private Node leftChild;
private Node rightChild;
public Node(int nodeNumber,boolean isNYT){
this.weight=0;
this.nodeNumber=nodeNumber;
this.symbol='\0';
this.isNYT=isNYT;
this.parent=null;
this.leftChild=null;
this.rightChild=null;
}
public Node(Node parent,int nodeNumber){
this(nodeNumber,false);
this.parent=parent;
this.weight=0;
}
public Node(Node parent,char symbol,int nodeNumber){
this(nodeNumber,false);
this.parent=parent;
this.symbol=symbol;
this.weight=1;
}
public int getWeight(){return weight;}
public int getNodeNumber(){return nodeNumber;}
public char getSymbol(){return symbol;}
public boolean isNYT(){return isNYT;}
public Node getParent(){return parent;}
public Node getLeftChild(){
return leftChild;
}
public Node getRightChild(){
return rightChild;
}
public void setWeight(int weight){this.weight=weight;}
public void setNodeNumber(int nodeNumber){this.nodeNumber=nodeNumber;}
public void setSymbol(char symbol){this.symbol=symbol;}
public void setNYT(boolean NYT){isNYT=NYT;}
public void setParent(Node parent){this.parent=parent;}
public void setLeftChild(Node leftChild){this.leftChild=leftChild;}
public void setRightChild(Node rightChild){this.rightChild=rightChild;}
public boolean isLeaf(){
return leftChild==null&&rightChild==null&&!isNYT;
}
public void incrementWeight(){
this.weight++;
}
public void setExplicitNullChildren(){
this.leftChild=null;
this.rightChild=null;
}
public boolean hasLeftChild(){
return leftChild!=null;
}
public boolean hasRightChild(){
return rightChild!=null;
}
@Override
public String toString(){
String type;
if(isNYT){
type="NYT";
}else if(isLeaf()){
String symStr;
if(Character.isISOControl(symbol)){
symStr="0x"+Integer.toHexString((int)symbol);
}else{
symStr=String.valueOf(symbol);
}
type="LEAF:'"+symStr+"'";
}else{
type="INTERNAL";
}
return String.format("[%s #%d W:%d]",type,nodeNumber,weight);
}
@Override
public boolean equals(Object o){
if(this==o)return true;
if(o==null||getClass()!=o.getClass())return false;
Node node=(Node)o;
return nodeNumber==node.nodeNumber;
}
@Override
public int hashCode(){
return Objects.hash(nodeNumber);
}
}