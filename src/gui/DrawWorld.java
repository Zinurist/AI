package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import program.AI;
import program.Entity;
import program.Item;
import program.Simulation;

@SuppressWarnings("serial")
public class DrawWorld extends JPanel {
	
	private Simulation s;
	private double zoom;
	private int dragX,dragY,oldMouseX,oldMouseY;

	/**
	 * Constructor.
	 * @param gr graph which should be drawn
	 */
	public DrawWorld(Simulation s1){
		this.s=s1;
		setPreferredSize(new Dimension(1000,500));
		zoom=60;
		dragX=0;
		dragY=0;
		oldMouseX=Integer.MAX_VALUE;
		oldMouseY=0;
	}
	
	public void addZoom(double value){
		zoom+=value;
		if(zoom<10){
			zoom=10;
		}
	}
	
	public void addDrag(int mouseX, int mouseY){
		if(zoom<100){
			dragX+=(mouseX-oldMouseX)/(zoom/150);
			dragY+=(mouseY-oldMouseY)/(zoom/150);
		}else{
			dragX+=mouseX-oldMouseX;
			dragY+=mouseY-oldMouseY;
		}
		
		/*
		if(dragX<0){
			dragX=0;
		}else if(dragX>(s.getXMax()-getWidth())){
			dragX=s.getXMax();
		}
		if(dragY<0){
			dragY=0;
		}else if(dragY>(s.getYMax()-getHeight())){
			dragY=s.getYMax();
		}
		*/
		setOldMousePosition(mouseX, mouseY);
	}
	
	public void setOldMousePosition(int mouseX, int mouseY){
		oldMouseX=mouseX;
		oldMouseY=mouseY;
	}
	
	@Override
	public void paintComponent(Graphics g){
		double realzoom=zoom/100;
		int itemRad= (int) Math.round(Entity.ITEM_RAD*realzoom);
		int aiRad= (int) Math.round(Entity.AI_RAD*realzoom);
		g.clearRect(0,0,getWidth(),getHeight());
		
		g.setColor(Color.GREEN);
		Item item;
		int x;
		int y;
		for(int i=0; i<s.getItems().size();i++){
			item=s.getItems().get(i);
			if(item.isActive()){
				x=(int)Math.round((item.getX()+dragX)*realzoom);
				y=(int)Math.round((item.getY()+dragY)*realzoom);
				g.fillOval(x-itemRad,y-itemRad,itemRad*2,itemRad*2);
			}
		}
		
		g.setColor(Color.BLACK);		
		for(AI a:s.getAIs()){
			if(a.isActive()){
				x=(int)Math.round((a.getX()+dragX)*realzoom);
				y=(int)Math.round((a.getY()+dragY)*realzoom);
				g.drawLine(x, y,  x+(int)Math.round(Math.cos(Math.toRadians(a.getAngle()+a.getFov()))*2000), y-(int)Math.round(Math.sin(Math.toRadians(a.getAngle()+a.getFov()))*2000));
				g.drawLine(x, y,  x+(int)Math.round(Math.cos(Math.toRadians(a.getAngle()-a.getFov()))*2000), y-(int)Math.round(Math.sin(Math.toRadians(a.getAngle()-a.getFov()))*2000));
			}
		}
		for(AI a:s.getSuperiors()){
			if(a.isActive()){
				x=(int)Math.round((a.getX()+dragX)*realzoom);
				y=(int)Math.round((a.getY()+dragY)*realzoom);
				g.drawLine((int)Math.round(x), y, x+(int)Math.round(Math.cos(Math.toRadians(a.getAngle()+a.getFov()))*2000), y-(int)Math.round(Math.sin(Math.toRadians(a.getAngle()+a.getFov()))*2000));
				g.drawLine((int)Math.round(x), y, x+(int)Math.round(Math.cos(Math.toRadians(a.getAngle()-a.getFov()))*2000), y-(int)Math.round(Math.sin(Math.toRadians(a.getAngle()-a.getFov()))*2000));
			}
		}
		
		for(AI a:s.getAIs()){
			if(a.isActive()){
				x=(int)Math.round((a.getX()+dragX)*realzoom);
				y=(int)Math.round((a.getY()+dragY)*realzoom);
				g.setColor(a.isMaster()?Color.CYAN:Color.RED);
				g.fillOval(x-aiRad,y-aiRad,aiRad*2,aiRad*2);
				g.setColor(Color.BLACK);
				g.fillArc(x-aiRad,y-aiRad,aiRad*2,aiRad*2,a.getAngle()-a.getFov()*2,a.getFov()*4);
				g.drawString(a.getName()+", "+a.getRoundPoints(), x-aiRad, y-aiRad-5);//x-Entity.AI_RAD y-Entity.AI_RAD
			}
		}
		for(AI a:s.getSuperiors()){
			if(a.isActive()){
				x=(int)Math.round((a.getX()+dragX)*realzoom);
				y=(int)Math.round((a.getY()+dragY)*realzoom);
				g.setColor(a.isMaster()?Color.CYAN:Color.RED);
				g.fillOval(x-aiRad,y-aiRad,aiRad*2,aiRad*2);
				g.setColor(Color.BLACK);
				g.fillArc(x-aiRad,y-aiRad,aiRad*2,aiRad*2,a.getAngle()-a.getFov()*2,a.getFov()*4);
				g.drawString(a.getName()+", "+a.getRoundPoints(), x-aiRad, y-aiRad-5);//x-Entity.AI_RAD y-Entity.AI_RAD
			}
		}
		
		g.setColor(Color.BLACK);
		g.drawRect((int)Math.round((0+dragX)*realzoom), (int)Math.round((0+dragY)*realzoom), (int)Math.round((s.getXMax()-1)*realzoom),(int)Math.round((s.getYMax()-1)*realzoom));
		g.drawString("Zoom: "+zoom/100, 5, this.getHeight()-5);
		
	}
}
