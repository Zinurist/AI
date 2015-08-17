package graph.program;

public class Node {

	private String name;
	private int x,y;
	private boolean activeNode;
	
	/**
	 * Sets the name of the node.
	 * @param name the new name
	 */
	public void setName(String name){
		this.name=name;
	}
	
	/**
	 * Sets the x-coordinate of the node.
	 * @param name the new x-coordinate
	 */
	public void setX(int x){
		this.x=x;
	}
	
	/**
	 * Sets the y-coordinate of the node.
	 * @param name the new y-coordinate
	 */
	public void setY(int y){
		this.y=y;
	}
	
	/**
	 * Inverts the boolean activeNode
	 */
	public void changeActiveNode(){
		activeNode=!activeNode;
	}
	
	/**
	 * Returns the name of the node.
	 * @return String name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns the x-coordinate of the node.
	 * @return int x
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * Returns the y-coordinate of the node.
	 * @return int y
	 */
	public int getY(){
		return y;
	}
	
	
	/**
	 * Returns true, if this is the active node.
	 * @return boolean activeNode
	 */
	public boolean isActiveNode(){
		return activeNode;
	}
	
	/**
	 * Constructor.
	 * @param name name of the node
	 * @param x x-coordinate of the node
	 * @param y y-coordinate of the node
	 */
	public Node(String name, int x, int y){
		setName(name);
		setX(x);
		setY(y);
		activeNode=false;
	}
}
