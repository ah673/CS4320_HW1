package CS4320_HW1;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BPlusTree myTree = new BPlusTree(); 
		myTree.insert(2, "2");
		myTree.insert(4, "4");
		myTree.insert(5, "5");
		myTree.insert(7, "7");
		myTree.insert(8, "8");
		myTree.insert(9, "9");
		myTree.insert(10, "10");
		myTree.insert(11, "11");
		myTree.insert(12, "12");
		myTree.insert(13, "13");
		Utils.printTree(myTree);
	}

}
