package graph.program;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import graph.gui.GraphFrame;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class Graph implements TableModel {

	private Node[] nodes;
	private double[][] edges;
	private int numOfNodes,defaultValue,numOfNodesPerRow;
	private boolean[] visited;
	private double[] weight;
	private int[] isFrom;
	private final boolean directed;
	
	private List<TableModelListener> tl=new ArrayList<TableModelListener>();
	
	/**
	 * Constructor.
	 * @param num the maximum number of nodes the graph will have
	 */
	public Graph(int numMaxNodes, int defaultValue, boolean directed){
		this.directed=directed;
		this.defaultValue=defaultValue;
		reset(numMaxNodes);
	}
	
	/**
	 * Returns the node at the given index.
	 * @param nodeIndex index of the node
	 * @return node
	 */
	public Node getNodeAt(int nodeIndex){
		try{
			return nodes[nodeIndex];
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * Returns the amount of nodes in the graph.
	 * @return int numOfNodes
	 */
	public int getNumOfNodes(){
		return numOfNodes;
	}
	
	/**
	 * Returns the array in which all nodes are saved.
	 * @return Node[] nodes
	 */
	public Node[] getNodes(){
		return nodes;
	}
	
	/**
	 * Returns the array in which all edges and their weights are saved.
	 * @return int[][] edges
	 */
	public double[][] getEdges(){
		return edges;
	}
	
	public void setEdges(double[][] edges){
		this.edges=edges;
		notifyListeners();
	}
	
	/**
	 * Returns the weight of the edge between the given nodes.
	 * @param fromIndex first node
	 * @param toIndex second node
	 * @return int weight
	 */
	public double getEdge(int fromIndex, int toIndex){
		return edges[fromIndex][toIndex];
	}
	
	/**
	 * Adds a node to the graph.
	 * @param name name of the node
	 */
	public boolean addNode(String name){
		if(getNodeIndex(name)==-1){
			nodes[numOfNodes]=new Node(name,50+80*(numOfNodes%numOfNodesPerRow),50+80*(numOfNodes/numOfNodesPerRow));
			numOfNodes++;
			notifyListeners();
			return true;
		}
		return false;
	}
	
	public Dimension getNeededDimension() {
		return new Dimension( 50+80*(numOfNodesPerRow)+30, 50+80*(numOfNodesPerRow+1)+30);
	}
	
	/**
	 * Adds a node to the graph.
	 * @param name name of the node
	 * @param x x-coordinate of the node
	 * @param y y-coordinate of the node
	 */
	public boolean addNode(String name, int x, int y){
		if(getNodeIndex(name)==-1){
			nodes[numOfNodes]=new Node(name,x,y);
			numOfNodes++;
			notifyListeners();
			return true;
		}
		return false;
	}
	
	/**
	 * Changes the edge between the 2 given nodes.
	 * @param from first node
	 * @param to second node
	 * @param weight weight of the edge (<0 if it is non-existent, 0 if the nodes are the same)
	 */
	public boolean changeEdge(String from, String to, double weight){
		int indexFrom=getNodeIndex(from);
		if(indexFrom!=-1){
			int indexTo=getNodeIndex(to);
			if(indexTo!=-1 && indexTo!=indexFrom){
				edges[indexFrom][indexTo]=weight;
				if(!directed){
					edges[indexTo][indexFrom]=weight;
				}
				notifyListeners();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Changes the edge between the 2 given nodes.
	 * @param from first node, index
	 * @param to second node, index
	 * @param weight weight of the edge (<0 if it is non-existent, 0 if the nodes are the same)
	 */
	public boolean changeEdge(int indexFrom, int indexTo, double weight){
		try{
			edges[indexFrom][indexTo]=weight;
			if(!directed){
				edges[indexTo][indexFrom]=weight;
			}
			notifyListeners();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Removes the given node.
	 * @param name name of the node
	 * @return true, if removal was successful
	 */
	public void removeNode(String name){
		int index=getNodeIndex(name);
		for(int i=index;i<numOfNodes;i++){
			nodes[i]=nodes[i+1];
			for(int k=0;k<numOfNodes;k++){
				edges[i][k]=edges[i+1][k];
				edges[k][i]=edges[k][i+1];
			}
		}
		nodes[numOfNodes]=null;
		for(int k=0;k<numOfNodes;k++){
			edges[numOfNodes][k]=-1;
			edges[k][numOfNodes]=-1;
		}
		numOfNodes--;
		notifyListeners();
	}
	
	/**
	 * Returns the index of the specified node.
	 * @param name name of the node
	 * @return int index
	 */
	public int getNodeIndex(String name){
		for(int i=0;i<numOfNodes;i++){
			if(nodes[i].getName().equals(name)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Mainly used by the method "shortestPath". Determines the closest non-visited node.
	 * @return node
	 */
	public int minNode(){
		int index=0;
		double lowNum=Double.MAX_VALUE;
		
		for(int i=0;i<numOfNodes;i++){
			if(!visited[i] && lowNum>weight[i]){
				lowNum=weight[i];
				index=i;
			}
		}
		
		return index;
	}
	
	/**
	 * Calculates the shortest path between two nodes. Using the Dijkstra-algorithm.
	 * @param start starting node
	 * @param target ending node
	 * @return returns the shortest path or the error message "No paths found."
	 */
	public String shortestPath(String start, String target){		
		int startIndex=getNodeIndex(start);
		if(startIndex!=-1){
			int targetIndex=getNodeIndex(target);
			if(targetIndex!=-1 && targetIndex!=startIndex){
				int nodeIndex;
				double curDistance;
				
				for(int i=0; i<numOfNodes;i++){
					visited[i]=false;
					weight[i]=Integer.MAX_VALUE;
				}
				
				weight[startIndex]=0;
				isFrom[startIndex]=startIndex;
				
				for(int i=0; i<numOfNodes;i++){
					nodeIndex=minNode();
					visited[nodeIndex]=true;
					
					for(int k=0; k<numOfNodes;k++){
						if(edges[nodeIndex][k]>0 && !visited[k]){
							curDistance=weight[nodeIndex]+edges[nodeIndex][k];
							
							if(curDistance<weight[k]){
								weight[k]=curDistance;
								isFrom[k]=nodeIndex;
							}
						}
					}	
				}
				String path=target;
				nodeIndex=targetIndex;
				while(nodeIndex!=startIndex){
					nodeIndex=isFrom[nodeIndex];
					path=nodes[nodeIndex].getName()+"/"+path;
				}
				if(weight[targetIndex]==Integer.MAX_VALUE){
					return "No paths found";
				}else{
					return path+", weight: "+weight[targetIndex];
				}
				
			}
		}
		return "No paths found.";
	}
	
	/**
	 * Returns the shortest path from the starting node, which visits all other nodes and ends up at the starting node again.
	 * @param start name of the starting node
	 * @return shortest path
	 */
	public String shortestPath(String start){
		String path="No paths found.";
		int startIndex=getNodeIndex(start);
		if(startIndex!=-1){
			
		}
		return path;
	}
	
	/**
	 * Prints all possible paths between two nodes. Uses the "findPaths"-method.
	 * @param start starting node
	 * @param target target node
	 */
	public void paths(String start, String target){
		int startIndex=getNodeIndex(start);
		if(startIndex!=-1){
			int targetIndex=getNodeIndex(target);
			if(targetIndex!=-1 && targetIndex!=startIndex){
				for(int i=0; i<numOfNodes;i++){
					visited[i]=false;
				}
				
				findPaths(startIndex,targetIndex,start,0);
			}
		}
	}
	
	/**
	 * Mainly used by the method "paths".
	 * @param nodeIndex index of starting node
	 * @param targetIndex index of target node
	 * @param path starting node
	 * @param weight weight of the edge
	 */
	public void findPaths(int nodeIndex, int targetIndex, String path, double weight){
		if(targetIndex==nodeIndex){
			System.out.println(path+", weight: "+weight);
		}else{
			visited[nodeIndex]=true;
			for(int i=0; i<numOfNodes;i++){
				if(edges[nodeIndex][i]>0 && !visited[i]){
					findPaths(i,targetIndex,path+"/"+nodes[i].getName(),weight+edges[nodeIndex][i]);
				}
			}
			visited[nodeIndex]=false;
		}	
	}
	
	/**
	 * Resets the graph.
	 * @param numMaxNodes length of the nodes-array
	 */
	public void reset(int numMaxNodes){
		numOfNodes=0;
		numOfNodesPerRow=(int) Math.sqrt(numMaxNodes);
		nodes=new Node[numMaxNodes];
		visited=new boolean[numMaxNodes];
		weight=new double[numMaxNodes];
		isFrom=new int[numMaxNodes];
		for(int i=0;i<numMaxNodes;i++){
			nodes[i]=null;
			visited[i]=true;
			weight[i]=0;
		}
		
		edges=new double[numMaxNodes][numMaxNodes];	
		for(int row=0;row<numMaxNodes;row++){
			for(int column=0;column<numMaxNodes;column++){
				if(row==column){
					edges[row][column]=0;
				}else{
					edges[row][column]=defaultValue;
				}
			}
		}
		notifyListeners();
	}
	
	/**
	 * Loads a graph from the given path.
	 * @param path path of the graph-file
	 */
	public void load(Save s)throws RuntimeException{
		s.openScanner();
		String[] line;
		line=s.readLine(0).split("\\s");
		int numMaxNodes=Integer.parseInt(line[0]);
		reset(numMaxNodes);
		int numNodes=Integer.parseInt(line[1]);
		for(int i=0; i<numNodes;i++){
			line=s.readLine(i+1).split("\\s");
			addNode(line[0],Integer.parseInt(line[1]),Integer.parseInt(line[2]));
		}
		GraphFrame.mf.setActiveNode(nodes[0]);
		try{
			line=s.readLine(numNodes+1).split("\\s");
			while(line[0]!=null){
				changeEdge(line[0],line[1],Double.parseDouble(line[2]));
			
				numNodes++;
				line=s.readLine(numNodes+1).split("\\s");
			}
		}catch(Exception e){}
		s.closeScanner();
	}
	
	/**
	 * Saves a graph to the given path.
	 * @param path path of the graph-file
	 */
	public void save(Save s)throws RuntimeException{
		s.createFile();
		s.openFileWriter();
		String graph=nodes.length+" "+numOfNodes+"\r\n";
		String graph2="";
		Node n;
		for(int i=0; i<numOfNodes;i++){
			n=getNodeAt(i);
			graph+=n.getName()+" "+n.getX()+" "+n.getY()+"\r\n";
			int k;
			if(directed){
				k=0;
			}else{
				k=i+1;
			}
			for(;k<numOfNodes;k++){
				if(edges[i][k]!=0.0){
					graph2+=getNodeAt(i).getName()+" "+getNodeAt(k).getName()+" "+edges[i][k]+"\r\n";
				}
			}
		}
		s.write("", graph+graph2);
		s.closeFileWriter();
		JOptionPane.showConfirmDialog(GraphFrame.mf, "Saved successfully!", "Done", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Notifies all TableModelListeners.
	 */
	public void notifyListeners(){
		if(GraphFrame.mf!=null && GraphFrame.mf.getActiveNode()!=null){
			for(TableModelListener l: tl){
				l.tableChanged(new TableModelEvent(this));
			}
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex){
		case 0:
			return String.class;
		case 1:
			return Integer.class;
		default:
			return null;
		}
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch(columnIndex){
		case 0:
			return "to node: ";
		case 1:
			return "weight: ";
		default:
			return null;
		}
	}

	@Override
	public int getRowCount() {
		return numOfNodes;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(GraphFrame.mf.getActiveNode()!=null){
			switch(columnIndex){
			case 0:
				return nodes[rowIndex].getName();
			case 1:
				return edges[this.getNodeIndex(GraphFrame.mf.getActiveNode().getName())][rowIndex];
			default:
				return null;
			}
		}else{
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(columnIndex==1){
			return true;
		}
		return false;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try{
			int weight=(Integer)aValue;
			edges[this.getNodeIndex(GraphFrame.mf.getActiveNode().getName())][rowIndex]=weight;
			edges[rowIndex][this.getNodeIndex(GraphFrame.mf.getActiveNode().getName())]=weight;
		}catch(Exception e){}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		tl.add(l);	
	}
	
	@Override
	public void removeTableModelListener(TableModelListener l) {
		tl.remove(l);
	}
	
}
