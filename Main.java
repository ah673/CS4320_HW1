package CS4320_HW1;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BPlusTree myTree = new BPlusTree(); 
		myTree.insert(5, "5");
		myTree.insert(10, "10");
		myTree.insert(15, "15");
		myTree.insert(20, "20");
		myTree.insert(30, "30");
		myTree.insert(25, "25");
//		Utils.printTree(myTree);
	}

}
