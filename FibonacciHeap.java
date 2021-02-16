import java.util.ArrayList;
import java.util.Collections;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */


//name1: tamir kashi, id1: 207481136, username1: tamirizhark
//name2: adva helman, id2: 206087900, username2: advahelman
public class FibonacciHeap
{
	public static int totalLinks =0; 
	static int totalCuts =0;
	protected HeapNode min;
	protected int size;
	protected HeapNode first;
	int number_of_trees;
	int number_of_marks; 
	
	public FibonacciHeap() 
	{
		this.min = null;
		this.size=0;
		this.first = null;
		this.number_of_trees=0;
		this.number_of_marks=0;
	}
   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
	public boolean isEmpty()//checks if heap's size is 0, if so it's empty, if not - its not. O(1)
    {
    	if(size==0) 
    	{
    		return true;
    	}
    	return false;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * 
    * Returns the new node created. 
    */
    public HeapNode insert(int key) //inserting new node with key value key to the heap. O(1)
    {    
    	HeapNode node = new HeapNode(key);//creating new node
    	if(this.isEmpty()) //checking if heap is currently empty
    	{
    		this.first = node;
    		this.min = node;
    	}
    	else if(key<this.min.getKey()) //not empty, key is minimal key
    	{
    		this.min = node;//change minimum
    	}
    	this.size++;//update size
    	this.number_of_trees++;//always insert add 1 for the tree number 
    	node.prev = this.first.prev;
    	node.next = this.first;
    	this.first.prev.next = node;
    	this.first.prev = node;
    	this.first = node;
    	return node;
    }

    private void changeParentToNull(HeapNode x,int rank) //utility func, change parent value of all sons of x to null. O(degx)<=O(logn)
    {
    	HeapNode minChild = x.child;
    	for(int i=rank;i>0;i--) //loop for all of x sons
    	{
    		minChild.parent=null;
    		minChild=minChild.next;
    	}
    	
    }     


   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin() {//deleting minimal key's node in heap. O(n) worst-case, O(log(n)) amortized.
    	
    	if(this.size==1) //only 1 node in heap
        	{
        		if(this.min.mark) 
        		{
        			number_of_marks--;
        		}
        		this.size=0;
        		this.first=null;
        		this.min=null;
        		this.number_of_trees=0;
        		return;
        	}
       	// in slide 33 : this.min.prev =  59 , this.min.child=40 , this.min.next = 19 ,this.min.child.prev=35
    	if(this.min.child==null) //no childs to min
    	{
    		this.min.prev.next=this.min.next;
    		this.min.next.prev=this.min.prev;
    		 if(this.min.getKey()==this.first.getKey()) 
    		{
    			this.first=this.first.next;
    			//this.min = null;
    		}
    	}
    	else //min has child/s
    	{
    		if(this.min==this.first) //min is also the first
    		{
    			this.first=this.min.child;
    		}
        	changeParentToNull(this.min,this.min.rank);
        	this.min.next.prev = this.min.child.prev;//conect 19 to 35
        	this.min.child.prev.next = this.min.next; //conect 35 to 19 
        	this.min.prev.next = this.min.child;//conect 39 to 40 
        	this.min.child.prev= this.min.prev;//conect 40 to 39 
    	}
    	Consolidating_linking(this); //do Successive Linking after min deletion 
    	find_new_minimum(this);
    	this.size--; //delete one node
     	return; 
    }


	
    
//find the new minimum after delete min 
private void find_new_minimum(FibonacciHeap fibonacciHeap) //O(logn)
{
	this.number_of_trees=0;//going to update this
	HeapNode newmin = this.first;
	this.number_of_trees++;
	HeapNode temp = this.first.next;
	while(temp.getKey()!=this.first.getKey()) 
	{
		this.number_of_trees++;//updating the number of trees
		if(temp.getKey()<newmin.getKey()) //potentially the new min
		{
			newmin=temp;
		}
		if(temp==temp.next) //no more nexts, meaning no need to continue
		{
			return;
		}
		temp = temp.next;
	}
	this.min=newmin;
}




//this method need to link  every tree in the same rank. O(n) worst case, O(log(n)) amortized.
   private void Consolidating_linking(FibonacciHeap fibonacciHeap) 
   {
	   int counter = 0;
	   HeapNode[] arr = new HeapNode[50];
	   HeapNode node =this.first;
	   HeapNode curr = node.next;
	   arr[node.rank] = node;
	   node.next = node;
	   node.prev = node;
	   
	   while(curr.getKey()!=this.first.getKey()) //while not completed a full loop of all trees
	   {
		   int currRank = curr.rank;
		   HeapNode linked = curr;
		   curr=curr.next;
		   while(arr[currRank]!=null) //there is a tree with same rank in the bucket
		   {
				   linked = link(arr[currRank],linked);//connect them
				   arr[currRank] = null;
				   currRank = linked.rank;   

		   }
		   if(arr[currRank]==null) //nothing in the relevant bucket
		   {
			   arr[currRank] = linked; //insert the linked tree to the array
		   }
	   }
	   HeapNode first = null;
	   for(int i=0; i<arr.length; i++) 
	   {
		if(arr[i]!=null) {//find the first bucket
			first = arr[i];
			this.first=first;
			counter= i+1;
			break;
		}
	   }
		HeapNode temp = first;
		while(counter < arr.length) {
			 if(arr[counter]!=null) {
				 temp.next=arr[counter];
				 arr[counter].prev=temp;
				 temp=temp.next;
			 }
			 counter++;
		}
		first.prev=	temp;
		temp.next=first;
   }
	   

   
   //connecting two trees, O(1)
   private HeapNode link(HeapNode x1, HeapNode x2) {
	   totalLinks++;
	   HeapNode toBeReturned = null;
	   if(x1.getKey()<x2.getKey()) 
	   {//x1 is the smaller key will be the root 
		   toBeReturned =x1;
		   x1.rank++;
		   x1.next=x1;
		   x1.prev=x1;
		   HeapNode x1FirstChild =x1.child;
		   if(x1FirstChild!=null)  //theres at least one child to x1
		   {
			   x1.child=x2;
			   x2.parent=x1;
			   HeapNode x1LastChild = x1FirstChild.prev;
			   x2.next = x1FirstChild;
			   x1LastChild.next=x2;
			   x2.prev=x1LastChild;
			   x1FirstChild.prev =x2; 
		   }
		   else //no childs to x1
		   {
			   x1.child=x2;
			   x2.parent=x1;
			   x2.next=x2;
			   x2.prev=x2;
		   }
	   }
	   else 
	   { // x2 is the smaller key will be the root 
		   toBeReturned=x2;
		   x2.rank++;
		   x2.next=x2;
		   x2.prev=x2;
		   HeapNode x2FirstChild =x2.child;
		   if(x2FirstChild!=null) //there is at least one child to x2
		   {
			   x2.child=x1;
			   x1.parent=x2;
			   HeapNode x2LastChild = x2FirstChild.prev;
			   x1.next = x2FirstChild;
			   x2LastChild.next=x1;
			   x1.prev=x2LastChild;
			   x2FirstChild.prev =x1;
		   }
		   else //no childs to x2
		   {
			   x2.child =x1;
			   x1.parent=x2;
			   x1.next=x1;
			   x1.prev=x1;
		   } 
	   }
	return toBeReturned;   
   }

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()//return minimum node, O(1)
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)//meld two heaps, O(1)
    {
    	this.number_of_trees=this.number_of_trees+heap2.number_of_trees;
    	this.number_of_marks=this.number_of_marks+heap2.number_of_marks;
    	
    	if(this.min.getKey()>heap2.min.getKey()) {//update the minimum (the root) to be the minimum between the trees
    		this.min=heap2.min; 
    	}
    	this.size=this.size+heap2.size;
    	HeapNode last_this = this.first.prev;//the last node in this tree that conact to the first node 
    	HeapNode last_heap2 = heap2.first.prev;//the last node in heap2 tree that conact to the first node 
    	if(last_this!=null&&last_heap2!=null) {
    	last_this.next=heap2.first;
    	heap2.first.prev=last_this;
    	last_heap2.next=this.first;
    	this.first.prev=last_heap2;
    	}
    	
    	  return;  		
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()//O(1)
    {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep() //O(n)
    {
	int[] arr = new int[this.size];
	arr[first.rank]++;//put 1 in the rank of the first tree 
	HeapNode starter = this.first.next;
	while(starter.getKey()!=this.first.getKey()) {//until we back to the start 
		arr[starter.rank]++;
		starter=starter.next;
	}
	
        return arr; 
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) //O(logn) amortized, O(n) worst case 
    {   
    	if(this.min!=null) 
    	{
    		decreaseKey(x,Integer.MAX_VALUE);
        	this.deleteMin();
    	}  	
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)//O(n) worst case, O(1) amortized
    {    
    	x.key = x.getKey()-delta;
    	if(x.parent!=null && x.getKey()<x.parent.getKey())
    	{
    		cascadingCut(x);
    	}
    	if(x.getKey()<this.min.getKey()) 
    	{
    		this.min = x;
    	}
    }
    
    //utility func, doing cuts up the tree as needed
    public void cascadingCut(HeapNode x) //O(n) worst case, O(1) amortized
    {
    	HeapNode y = x.parent;
    	cut(x);
    	if(y.parent!=null) //y is not the root
    	{
    		if(y.mark==false) //y isnt marked
    		{
    			y.mark=true;
    			this.number_of_marks++;
    		}
    		else //y is marked
    		{
    			cascadingCut(y);//recursively to the parent...
    		}
    	}	
    }
    
    //single cut function
    public void cut(HeapNode x) //O(1)
    {
    	this.number_of_trees++;
    	totalCuts++;
    	HeapNode y = x.parent;
    	x.parent=null;
    	if(x.mark ==true) {
    	x.mark = false;
		this.number_of_marks--;
    	}
    	y.rank--;
    	if(x.next == x) 
    	{
    		y.child = null;
    	}
    	else 
    	{
    		y.child = x.next;
    		x.prev.next = x.next;
    		x.next.prev = x.prev;
    	}
    	x.next =this.first;
        x.prev=this.first.prev;
        first.prev.next=x;
        first.prev=x;
        first=x;
    }



   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    
    public int potential() {//O(1)
    	
		return this.number_of_trees + 2*(this.number_of_marks);
    	
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()//O(1)
    {    
    	return totalLinks; 
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()//O(1)
    {    
    	return totalCuts; 
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)). 
    * You are not allowed to change H.
    */
    public static int[] kMin(FibonacciHeap H, int k)//O(kdeg(H))
    {   
    	if(H.isEmpty()) 
    	{
        	int[] arr = new int[0];
    		return arr;
    	}
    	int[] arr = new int[k];
    	FibonacciHeap newHeap = new FibonacciHeap(); //making a new fibHeap
    	HeapNode node = H.first;
    	newHeap.insert(node.getKey());//add the root first
    	H.addAllSons(newHeap,node,0,k); //add relevant nodes to newHeap - nodes of k upper floors of H
    	for(int i=0;i<k;i++) //loop for inserting k minimal values to arr
    	{
    		arr[i] = newHeap.min.getKey();
    		newHeap.deleteMin();
    	}
    	return arr;  	
      }
    
    
    
    private void addAllSons(FibonacciHeap newHeap, HeapNode node, int floor, int k) //O(kdegH)
    {
    	boolean finishedFloor = false;
    	HeapNode firstOnFloor = node.child;
    	HeapNode son = firstOnFloor;
    	while(!finishedFloor) //while didnt finish current floor
    	{
        	newHeap.insert(son.getKey());//insert
    		if(floor+1<=k) //next floor is the k-th
        	{
    			if(son.child!=null) //theres somthing on k-th floor
            	{
            		addAllSons(newHeap, son, floor+1, k);
            	}
        	}
    		son=son.next;
    		if(son == firstOnFloor) //completed a full loop around floor
    		{
    			finishedFloor=true;
    		}	
    	}
    }  
    
 
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode
    {
    	public int key;
    	protected int rank;
    	protected boolean mark;
    	protected HeapNode child;
    	protected HeapNode next;
    	protected HeapNode prev;
    	protected HeapNode parent;
  	
    	public HeapNode(int key) 
    	{
    		this.key = key;
    		this.rank = 0;
    		this.mark = false;
    		this.child = null;
    		this.next = this;
    		this.prev = this;
    		this.parent = null;
    	}
    	public int getKey() 
    	{
    		return this.key;
    	}
    }
    
}

