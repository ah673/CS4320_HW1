
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BPlusTree myTree = new BPlusTree(); 
		myTree.insert(5, "5");
		Utils.printTree(myTree);
	}

}
