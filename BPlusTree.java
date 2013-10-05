package CS4320_HW1;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
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
		// look for leaf node that should contain key
		LeafNode leaf = findLeafNodeWithKey(root, key);

		// look for value within leaf
		for (int i = 0; i < leaf.keys.size(); i++){
			if (leaf.keys.get(i) == key){
				return leaf.values.get(i); 
			}
		}
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
		
		// find appropriate parent index node and leaf node for key
		IndexNode idxNode = findIndexNode(root, key); 
		if (idxNode == null) {
			System.out.println("idx node is null");
		}
		LeafNode leafNode; 
		if (idxNode == null) 
			leafNode = findLeafNodeForInsert(root, key);
		else 
			leafNode = findLeafNodeForInsert(idxNode, key); 

		insertIntoTree(idxNode, leafNode, key, value);

	}

	/**
	 * Helper method to insert() to account for cases of overflow
	 * @param idxNode
	 * @param leafNode
	 * @param key
	 */
	private void insertIntoTree(IndexNode idxNode, LeafNode leafNode, int key, String value) {
		// insert into Leaf
		leafNode.insertSorted(key, value);
		
		// Case: inserting into leaf overflows leaf
		if (leafNode.isOverflowed()){
			System.out.println("you've overflowed me!");
			Entry<Integer,Node> rightSplit = splitLeafNode(leafNode);
			int splittingKey = rightSplit.getKey(); 
			System.out.println("splitting key " + splittingKey);
			LeafNode rightLeafNode = (LeafNode) rightSplit.getValue(); 
			manageLeafNodePointers(leafNode, rightLeafNode);
			// copy up splitting key
			if (idxNode == null){
				System.out.println("but idxnode is null");
				root = new IndexNode(splittingKey, leafNode, rightLeafNode);
			}
			else {
				idxNode.insertSorted(rightSplit, splittingKey); 
			}
		}
	}

	/**
	 * Rearranges pointers for the leftLeaf's nextLeaf pointer. 
	 * @param leftLeaf 
	 * @param rightLeaf
	 */
	private void manageLeafNodePointers(LeafNode leftLeaf, LeafNode rightLeaf) {
		if (leftLeaf.nextLeaf == null )
			leftLeaf.nextLeaf = rightLeaf;
		else {
			rightLeaf.nextLeaf = leftLeaf.nextLeaf;
			leftLeaf.nextLeaf = rightLeaf; 
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
		int RIGHT_BUCKET_SIZE = D+1;
		 
		ArrayList<Integer> rightKeys = new ArrayList<Integer>(RIGHT_BUCKET_SIZE); 
		ArrayList<String> rightValues = new ArrayList<String>(RIGHT_BUCKET_SIZE);
		
		rightKeys.addAll(leaf.keys.subList(D, leaf.keys.size()));
		rightValues.addAll(leaf.values.subList(D, leaf.values.size())); 
	
		// delete the right side from the left
		leaf.keys.subList(D, leaf.keys.size()).clear();
		leaf.values.subList(D, leaf.values.size()).clear();
		
		LeafNode rightNode = new LeafNode(rightKeys, rightValues);
		return new AbstractMap.SimpleEntry<Integer, Node>(rightNode.keys.get(0), rightNode);

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
		
		if (theNode.isLeafNode){
			// Found the LeafNode
			return (LeafNode) theNode;
		}
		else {
			// The node is an index node
			IndexNode indexNode = (IndexNode) theNode;
			
			if (key < theNode.keys.get(0)){
				return findLeafNodeForInsert(indexNode.children.get(0), key); 
			}
			else if (key > theNode.keys.get(theNode.keys.size() - 1)) {
				return findLeafNodeForInsert(indexNode.children.get(indexNode.children.size() - 1), key);
			}
			else {
				System.out.println("in last else");
				ListIterator<Integer> iterator = indexNode.keys.listIterator();
				while (iterator.hasNext()){
					if (iterator.next() > key){
						System.out.println(iterator.previousIndex());
					}
				}
			}
		}
		return null;
	}
	
	private IndexNode findIndexNode(Node node, int key){ 
		if (!node.isLeafNode){
			IndexNode idxNode = (IndexNode)node;
			if (idxNode.children.get(0) instanceof LeafNode)
				return idxNode;
			else {
				if (key < idxNode.keys.get(0))
					return findIndexNode(idxNode.children.get(0), key);
				else if (key >= idxNode.keys.get(node.keys.size() - 1))
					return findIndexNode(idxNode.children.get(idxNode.children.size() - 1 ), key);
				else {
					 ListIterator<Node> iterator = idxNode.children.listIterator(); 
					 while (iterator.hasNext()){
						 if (iterator.next().keys.get(0) > key){
							 System.out.println("key is " + key);
							 return (IndexNode)iterator.previous();
						 }
					 }
				}
			}
		}
		return null;
	}

}
