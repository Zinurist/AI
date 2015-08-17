package program;


public class Item extends Entity{

	private boolean terminated;
	
	public Item(double x, double y, int angle, boolean active){
		super(x,y,angle,active);
		terminated=false;
	}
	
	public void setTerminated(boolean terminated){
		this.terminated=terminated;
	}
	
	public boolean isTerminated(){
		return terminated;
	}
	
	public int getRadius(){
		return Entity.ITEM_RAD;
	}
	
	@Override
	public void setRelativePosition(double x, double y, int xMax, int yMax, int xMin, int yMin){
		addX(x);
		addY(y);		
		if(getX()-ITEM_RAD<xMin || getX()+ITEM_RAD>xMax || getY()-ITEM_RAD<yMin || getY()+ITEM_RAD>yMax){
			setTerminated(true);
		}
	}
		
}
