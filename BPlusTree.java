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
		}
		
		Entry<Integer, Node> overflowed = insertHelper(root, key, value);
		if (overflowed != null){
			// overflow emerged to root level
			root = new IndexNode(overflowed.getKey(), root, overflowed.getValue());
		}

	}
	private Entry<Integer, Node> insertHelper(Node node, int key, String value){
		Entry<Integer,Node> overflow = null; 
		if (node.isLeafNode){
			LeafNode leaf = (LeafNode) node; 
			leaf.insertSorted(key, value);
			if (leaf.isOverflowed()){
				Entry<Integer, Node> rightSplit = splitLeafNode(leaf);
				LeafNode rightLeaf = (LeafNode) rightSplit.getValue();
				manageSiblingPtrs(leaf, rightLeaf);
				return rightSplit;
			}
			return null; 
		}
		else {
			IndexNode idxNode = (IndexNode) node; 
			if (key < node.keys.get(0)) 
				overflow = insertHelper(idxNode.children.get(0), key, value);
			else if (key >= node.keys.get(idxNode.keys.size() - 1))
				overflow = insertHelper(idxNode.children.get(idxNode.children.size() - 1), key, value); 
			else {
				ListIterator<Node> iterator = idxNode.children.listIterator(); 
				 if (iterator.next().keys.get(0) > key){
					 overflow = insertHelper((IndexNode)iterator.previous(), key, value);
				 }
			}
		}
		if (overflow != null){
			IndexNode idxNode = (IndexNode)node;
			
			// TO DO !! figure out where to put overflowed node
			idxNode.insertSorted(overflow, overflow.getKey());
			if (idxNode.isOverflowed()){
				Entry<Integer, Node> rightSplit = splitIndexNode(idxNode);
				return rightSplit;
			}
			return null;
		}
		return overflow;
		
	}


	/**
	 * Rearranges pointers for the leftLeaf's nextLeaf pointer. 
	 * @param leftLeaf 
	 * @param rightLeaf
	 */
	private void manageSiblingPtrs(LeafNode leftLeaf, LeafNode rightLeaf) {
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
		int BUCKET_SIZE = D; 
		ArrayList<Integer> rightKeys = new ArrayList<Integer>(BUCKET_SIZE); 
		ArrayList<Node> rightChildren = new ArrayList<Node>(BUCKET_SIZE + 1);
		
		rightKeys.addAll(index.keys.subList(D+1, index.keys.size()));
		rightChildren.addAll(index.children.subList(D+1, index.children.size())); 
		
		// push up the new index
		IndexNode rightNode = new IndexNode(rightKeys, rightChildren);
		AbstractMap.SimpleEntry<Integer, Node> splitted = new AbstractMap.SimpleEntry<Integer, Node>(index.keys.get(D), rightNode);
				

		// delete the right side from the left
		index.keys.subList(D, index.keys.size()).clear();
		index.children.subList(D+1, index.children.size()).clear();
		
		return splitted;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(int key) {
		int index = deleteHelper(null, root, key);
		if (index != -1){
			root.keys.remove(index);
			if (root.keys.size() == 0){
				root = ((IndexNode) root).children.get(0);
			}
		}
	}

	private int deleteHelper(IndexNode parent, Node node, int key) {
		int indexToDelete = -1; 
		
		// find index of node in parent
		int indexInParent = -1; 
		if (parent != null){
			for (indexInParent = 0; indexInParent < parent.children.size(); indexInParent++){
				if (parent.children.get(indexInParent) == node){
					break; 
				}
			}
		}
		
		if (node.isLeafNode){
			LeafNode leafNode = (LeafNode) node; 

			for (int i = 0; i < leafNode.keys.size(); i++){
				if (leafNode.keys.get(i) == key){
					// delete key from leafNode's keys
					leafNode.keys.remove(i); 
					// delete the associated value
					leafNode.values.remove(i); 
					break;
				}
			}
			
			// check for underflow
			if (leafNode.isUnderflowed()){
				// find index leafnode resides in parent
				if (indexInParent - 1 >= 0){
					// node has left child
					LeafNode left = (LeafNode) parent.children.get(indexInParent -1);
					return handleLeafNodeUnderflow(left, leafNode, parent);
				} else {
					// node does not have left child
					LeafNode right = (LeafNode) parent.children.get(indexInParent + 1); 
					return handleLeafNodeUnderflow(leafNode, right, parent);
				}
				
			}
			return -1; // delete did not cause underflow
			
		} 
		
		else {
			// Node is an IndexNode 
			IndexNode idxNode = (IndexNode) node; 
			if (key < idxNode.keys.get(0)){
				indexToDelete = deleteHelper(idxNode, idxNode.children.get(0), key);
			}
			else if (key >= idxNode.keys.get(idxNode.keys.size() - 1)){
				indexToDelete = deleteHelper(idxNode, idxNode.children.get(idxNode.children.size() - 1), key);
			}
			else {
				ListIterator<Node> iterator = idxNode.children.listIterator(); 
				if (iterator.next().keys.get(0) > key){
					indexToDelete = deleteHelper(idxNode, iterator.previous(), key); 
				}
			}
		}
		
		// see if there is an index to delete remaining
		if (indexToDelete != -1){
			if (node == root ){
				return indexToDelete; 
			}
			node.keys.remove(indexToDelete);
			
			// if removal caused underflow 
			if (node.isUnderflowed()){
				// determine if node has left sibling
				IndexNode left = (IndexNode)node; 
				IndexNode right = (IndexNode)node; 
				if (indexInParent - 1 >= 0){
					left = (IndexNode) parent.children.get(indexInParent - 1);  
				} else {
					right = (IndexNode) parent.children.get(indexInParent + 1);  
				}
				return handleIndexNodeUnderflow(left, right, parent);  
			}
		}
		
		return -1; 
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
		
		int oldSplitKey = right.keys.get(0); 
		
		// merge
		if (left.keys.size() + right.keys.size() < 2*D){
			left.keys.addAll(right.keys); 
			left.values.addAll(right.values);
			
			// delete the other node
			int indexInParent = parent.keys.indexOf(oldSplitKey) + 1;
			parent.children.remove(indexInParent); 
			
			// make changes to the node's nextPointers
			manageSiblingPtrs(left, (LeafNode)parent.children.get(indexInParent));
			
			return parent.keys.indexOf(oldSplitKey); 
			
		}
		
		// distribute
		if (left.isUnderflowed()){
			// get the minimum key value of right
			left.insertSorted(right.keys.remove(0), right.values.remove(0));
		} else {
			// get maximum key value of left
			right.insertSorted(left.keys.remove(left.keys.size()-1), left.values.remove(left.keys.size()-1));
		}
		
		// update the parent's index key
		parent.keys.set(parent.keys.indexOf(oldSplitKey), right.keys.get(0)); 
		
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
		int separatingKey;
		int index; 
		
		// find separating key value from parent  
		for (index = 0; index < parent.keys.size(); index++){
			if (parent.children.get(index) == leftIndex && parent.children.get(index+1) == rightIndex){
				break; 
			}
		}
		
		separatingKey = parent.keys.get(index);  
		
		// Action : merge
		if (leftIndex.keys.size() + rightIndex.keys.size() < 2*D){
			// move separating key down 
			leftIndex.keys.add(separatingKey); 
			leftIndex.keys.addAll(rightIndex.keys);
			
			leftIndex.children.addAll(rightIndex.children);
			
			// delete the right side
			parent.children.remove(parent.children.indexOf(rightIndex));
			return index; 
		
		}
		
		// Distribution
		if (leftIndex.isUnderflowed()){
			// move separating key down to leftIndex
			leftIndex.keys.add(separatingKey);
			// move leftmost key from right up 
			parent.keys.set(index, rightIndex.keys.remove(0)); 
		}
		else if (rightIndex.isUnderflowed()) {
			// move separating key down to rightIndex
			rightIndex.keys.add(0, separatingKey); 
			// move rightmost key from leftIndex up
			parent.keys.set(parent.keys.size()-1, leftIndex.keys.remove(leftIndex.keys.size() - 1));
		}
		
		return -1;
	}
	
	/**
	 * Finds the LeafNode the key is to be inserted to
	 * @param key
	 * 			: the key used to find the LeafNode it is to be inserted
	 * @return the LeafNode the key is to be inserted to
	 */
	private LeafNode findLeafNodeWithKey(Node theNode, int key){
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
				return findLeafNodeWithKey(indexNode.children.get(0), key);
			}
			else if (key >= theNode.keys.get(theNode.keys.size() - 1)) {
				return findLeafNodeWithKey(indexNode.children.get(indexNode.children.size() - 1), key);
			}
			else {
				ListIterator<Integer> iterator = indexNode.keys.listIterator();
				while (iterator.hasNext()){
					if (iterator.next() > key){
						return findLeafNodeWithKey(indexNode.children.get(iterator.previousIndex()), key); 
					}
				}
			}
		}
		return null;
	}

}
