package gui;

import javax.swing.JFrame;

import program.AI;

public class NetworkFrame extends JFrame{

	private NetworkDraw nd;
	
	
	public NetworkFrame(){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		nd=new NetworkDraw();
		
		setContentPane(nd);
		pack();
	}
	
	
	public void setAI(AI a){
		nd.setAI(a);
	}
	
}
