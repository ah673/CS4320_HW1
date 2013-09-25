import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class IndexNode extends Node {

	// m nodes
	protected ArrayList<Node> children; // m+1 children

	public IndexNode(int key, Node child0, Node child1) {
		isLeafNode = false;
		keys = new ArrayList<Integer>();
		keys.add(key);
		children = new ArrayList<Node>();
		children.add(child0);
		children.add(child1);
	}

	public IndexNode(List<Integer> newKeys, List<Node> newChildren) {
		isLeafNode = false;

		keys = new ArrayList<Integer>(newKeys);
		children = new ArrayList<Node>(newChildren);

	}

	/**
	 * insert the entry into this node at the specified index so that it still
	 * remains sorted
	 * 
	 * @param e
	 * @param index
	 */
	public void insertSorted(Entry<Integer, Node> e, int index) {
		int key = e.getKey();
		Node child = e.getValue();
		if (index >= keys.size()) {
			keys.add(key);
			children.add(child);
		} else {
			keys.add(index, key);
			children.add(index+1, child);
		}
	}

}
