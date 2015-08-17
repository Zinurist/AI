package graph.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import graph.program.Graph;
import graph.program.Node;

@SuppressWarnings("serial")
public class DrawGraph extends JPanel {

	private Graph gr;

	/**
	 * Constructor.
	 * @param gr graph which should be drawn
	 */
	public DrawGraph(Graph gr){
		setGraph(gr);
	}
	
	public Graph getGraph(){
		return gr;
	}
	
	public void setGraph(Graph g){
		gr=g;
		setPreferredSize(g.getNeededDimension());
	}
	
	@Override
	public void paintComponent(Graphics g){
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setFont(new Font("Courier", Font.PLAIN,16));
		int x,y,w;
		double weight;
		Node n,n2;
		
		g.setColor(Color.LIGHT_GRAY);
		for(int r=0; r<gr.getNumOfNodes();r++){
			n=gr.getNodeAt(r);
			if(!n.isActiveNode()){
				x=n.getX();
				y=n.getY();	
				for(int c=0; c<gr.getNumOfNodes();c++){
					weight=gr.getEdge(r,c);
					if(weight!=0){
						n2=gr.getNodeAt(c);
						g.drawLine(x, y, n2.getX(), n2.getY());
					}
				}
			}
		}
		
		g.setColor(Color.BLACK);
		for(int r=0; r<gr.getNumOfNodes();r++){
			n=gr.getNodeAt(r);
			if(n.isActiveNode()){
				x=n.getX();
				y=n.getY();	
				for(int c=0; c<gr.getNumOfNodes();c++){
					weight=gr.getEdge(r,c);
					if(weight!=0){
						n2=gr.getNodeAt(c);
						g.drawLine(x, y, n2.getX(), n2.getY());
					}
				}
			}
		}
		
		for(int i=0; i<gr.getNumOfNodes();i++){
			n=gr.getNodeAt(i);
			x=n.getX();
			y=n.getY();
			w=30;//16+n.getName().length()*14;
			if(n.isActiveNode()){
				g.setColor(Color.CYAN);
			}else{
				g.setColor(Color.WHITE);
			}	
			g.fillOval(x-w/2, y-16, w, 32);
			g.setColor(Color.BLACK);
			g.drawOval(x-w/2, y-16, w, 32);
			g.drawString(n.getName(), x-(n.getName().length()*4), y+4);
		}
		
	}
}
