package CS4320_HW1;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BPlusTree myTree = new BPlusTree(); 
		myTree.insert(5, "5");
		myTree.insert(1, "1");
		myTree.insert(6, "6");
		myTree.insert(2, "2");
		myTree.insert(3, "3");
		myTree.insert(8, "8");
//		Utils.printTree(myTree);
	}

}
