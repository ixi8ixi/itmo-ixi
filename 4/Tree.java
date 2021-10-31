import java.io.BufferedWriter;
import java.io.IOException;

class Node {
    private Node leftChild;
    private Node rightChild;
    private String word;
    private int amount;

    Node(String word) {
        this.leftChild = null;
        this.rightChild = null;
        this.word = word;
        this.amount = 0;
    }

    public String getWord() {
        return this.word;
    }

    public int getAmount() {
        return this.amount;
    }

    public Node getLeftChild() {
        return this.leftChild;
    }

    public Node getRightChild() {
        return this.rightChild;
    }

    public void setLeftChild(String word) {
        this.leftChild = new Node(word);
    }

    public void setRightChild(String word) {
        this.rightChild = new Node(word);
    }

    public void incrAmount() {
        this.amount += 1;
    }

    @Override
    public String toString() {
        return "(" + this.word + ", " + this.amount + ")";
    }


}

public class Tree {
    Node firstNode;

    Tree() {
        firstNode = null;
    }

    public Node getFirstNode() {
        return firstNode;
    }

    public void appendNode(String word) {
        if (firstNode == null) {
            firstNode = new Node(word);
        }
        Node currentNode = firstNode; 

        while (true) {
            int compTo = word.compareTo(currentNode.getWord());
            if (compTo == 0) {
                currentNode.incrAmount();
                break;
            } else if (compTo < 0) {
                if (currentNode.getLeftChild() == null) {
                    currentNode.setLeftChild(word);
                }
                currentNode = currentNode.getLeftChild();
            } else {
                if (currentNode.getRightChild() == null) {
                    currentNode.setRightChild(word);
                }
                currentNode = currentNode.getRightChild();
            }
        }
    }

/*    public int nodesNumber(Node startNode) {
        if (startNode != null) {
            return 1 + nodesNumber(startNode.getLeftChild()) + nodesNumber(startNode.getRightChild());
        } else {
            return 0;
        }
    }*/

    public void drawTree(Node startNode) {
        if (startNode != null) {
            drawTree(startNode.getLeftChild());
            System.out.println(startNode.getWord() + " " + startNode.getAmount());
            drawTree(startNode.getRightChild());
        }
    }

    public void drawTreeToFile(Node startNode, BufferedWriter out) throws IOException {
        if (startNode != null) {
            drawTreeToFile(startNode.getLeftChild(), out);
            out.write(startNode.getWord() + " " + startNode.getAmount() + "\n");
            drawTreeToFile(startNode.getRightChild(), out);
        }
    }
}