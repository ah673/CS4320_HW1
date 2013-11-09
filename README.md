Database - B+ Tree implementation
=================================

Background
------------
A B+ tree is balanced tree structure in which the index nodes direct the search and the leaf nodes contain the data entry.

Assignment was to implement the following functions as well as a description of the implementation:

Search
-------
Given a key, return the associated String value.

This was implemented recursively to find the leaf node containing the number. 
Then a scan through the leaf node was used to find the key. 
A better implementation would be to use binary search, since the keys are sorted.

Insert
-------
Give a key/value pair, insert it into the right leaf node, without violating the B+ tree constraints noted above. 
In insertion, there are three distinct cases to think about:
2.2
* the target node has available space for one more key. (done)
* the target node is full, but its parent has space for one more key. (leaf overflow)
* the target node and its parent are both full. (leaf overflow and index node overflow)

This followed a recursive insert. If an insert causes an overflow, the median key is propagated up to the parent where an insert is attempted. 
If this causes an overflow, it will continue to propagate up. It may reach the root in which case a split of the root is necessary. 

Delete
-------
Given a key, delete the corresponding key/value pair without violating the B+ tree constraints noted above. This is a mirror image of the insertion.
There are also three main cases to think about (after deletion):
* the target node is not underflowed (at least half full). (done)
* the target node is underflowed and node.keys.size()+sibling.keys.size() < 2*D, merge them.
* the target node is underflowed and not able to be merged, try redistributing with its siblings.

In deletion, we introduce the concept of siblings. The sibling of a node N is a node that is immediately to the left or right of N and has the same parent as N. In this project, we look for the left sibling of N first. If N does not have a left sibling, then we use its right sibling.

Delete was implemented recursively whereby a delete may cause an underflow. Redistribution is tried first.
If redistribution would leave any node underflowed, a merge is required. This can potentially underflow parent nodes. 
Thus, the underflow is recursively handled. Root underflow is acceptable. If the root becomes empty, the root is reassigned. 
