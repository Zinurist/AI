package gui;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import program.AI;

public class NetworkDraw extends JPanel{

	private AI a;
	
	public NetworkDraw(){
		setPreferredSize(new Dimension(500,500));
		a=new AI(new int[]{1},"err",-1,false);
	}
	
	public void setAI(AI a){
		this.a=a;
	}
	
	@Override
	public void paintComponent(Graphics g){
		
	}
	
}
