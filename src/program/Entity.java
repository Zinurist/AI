package program;

public abstract class Entity {
	
	public final static int AI_RAD=20;
	public final static int ITEM_RAD=5;
	
	protected double x,y;
	protected int angle,fov;
	protected boolean active;
	
	
	public Entity(){
		active=false;
		x=0;
		y=0;
		angle=0;
		fov=15;
	}
	
	public Entity(double x, double y, int angle, boolean active){
		this.active=active;
		this.x=x;
		this.y=y;
		this.angle=angle;
		fov=15;
	}
	
	public void setX(double x){
		this.x=x;
	}
	
	public void addX(double x){
		this.x+=x;
	}
	
	public void setY(double y){
		this.y=y;
	}	
	
	public void addY(double y){
		this.y+=y;
	}
	
	public void setAngle(int angle){
		this.angle=angle;
	}
	
	public void setFov(int fov){
		this.fov=fov;
	}
	
	public void addAngle(int angle){
		this.angle+=angle;
		this.angle=this.angle%360;
		if(angle<0){
			angle=360+angle;
		}
	}
	
	public void addFov(int fovC){
		fov+=fovC;
		if(fov<1){
			fov=1;
		}else if(fov>45){
			fov=45;
		}
	}
	
	public void setActive(boolean active){
		this.active=active;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public int getAngle(){
		return angle;
	}
	
	public int getFov(){
		return fov;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void moveForward(int xMax, int yMax, int xMin, int yMin, double amount){
		double x=(double)Math.cos(Math.toRadians(getAngle()))*amount;
		double y=-(double)Math.sin(Math.toRadians(getAngle()))*amount;
		setRelativePosition(x, y, xMax, yMax, xMin, yMin);
	}
	
	public abstract int getRadius();
	public abstract void setRelativePosition(double x, double y, int xMax, int yMax, int xMin, int yMin);
}
