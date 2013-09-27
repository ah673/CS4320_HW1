package CS4320_HW1;

import java.util.AbstractMap;
import java.util.Map.Entry;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 */
public class BPlusTree {

	public Node root;
	public static final int D = 2;

	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public String search(int key) {
		return null;
	}

	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(int key, String value) {
		// initial insert to tree
		if (root == null){
			root = new LeafNode(key, value);
			return;
		}
		
		LeafNode leafNode = findLeafNodeForInsert(root, key);
		insertIntoNode(leafNode, key, value);

	}

	private void insertIntoNode(LeafNode leafNode, int key, String value) {
		if (!leafNode.isOverflowed()){
			// LeafNode has enough space
			leafNode.insertSorted(key, value);
		} else {
			Entry<Integer,Node> rightLeaf = splitLeafNode(leafNode);
		}
	}

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf
	 * @return the key/node pair as an Entry
	 */
	public Entry<Integer, Node> splitLeafNode(LeafNode leaf) {

		return null;
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index
	 * @return new key/node pair as an Entry
	 */
	public Entry<Integer, Node> splitIndexNode(IndexNode index) {

		return null;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(int key) {

	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode left, LeafNode right,
			IndexNode parent) {
		return -1;

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode leftIndex,
			IndexNode rightIndex, IndexNode parent) {
		return -1;
	}
	
	/**
	 * Finds the LeafNode the key is to be inserted to
	 * @param key
	 * 			: the key used to find the LeafNode it is to be inserted
	 * @return the LeafNode the key is to be inserted to
	 */
	private LeafNode findLeafNodeForInsert(Node theNode, int key){
		if (theNode == null)
			return null; 
		
		if (theNode.isLeafNode)
			// Found the LeafNode
			return (LeafNode) theNode;
		else {
			// The node is an index node
			IndexNode indexNode = (IndexNode) theNode;
			
			if (key < theNode.keys.get(0)){
				return findLeafNodeForInsert(indexNode.children.get(0), key); 
			}
		}
		return null;
	}

}
