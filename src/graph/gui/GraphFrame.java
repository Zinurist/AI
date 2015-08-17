package graph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import graph.program.Graph;
import graph.program.Node;
import graph.program.Save;

@SuppressWarnings("serial")
public class GraphFrame extends JFrame {
	
	/**
	 * When asking for saving/loading location or when displaying error messages using JOptionPanes, this GraphFrame will be used as parent for the JOptionPanes
	 */
	public static GraphFrame mf;
	
	private Node activeNode;
	private DrawGraph dg;
	private JPanel contentPane, pNodeEdit, pControls;
	private JLabel lNodeEdit,lWeight,lInfo;
	private JTable tEdges;
	private JButton bAdd, bDeleteNode;
	private JToggleButton bChangeEdge, bShortestPath;
	private JTextField tfNode;
	private JSpinner sWeight;
	private JMenuBar menu;
	private JMenu mFile;
	private JMenuItem miSave,miLoad,miReset,miExit;
	private JScrollPane spEdges,spDrawGraph;
	

	/**
	 * Returns the active node.
	 * @return Node activeNode
	 */
	public Node getActiveNode(){
		return activeNode;
	}
	
	/**
	 * Sets the active node. The boolean of the new and the old active node is inverted.
	 * @param n the new active node
	 */
	public void setActiveNode(Node n){
		if(activeNode!=null){
			activeNode.changeActiveNode();
		}
		activeNode=n;
		if(activeNode!=null){
			activeNode.changeActiveNode();
			lNodeEdit.setText("Selected node: \""+activeNode.getName()+"\"");
		}else{
			lNodeEdit.setText("No node selected");
			bChangeEdge.setSelected(false);
			bShortestPath.setSelected(false);
		}
	}
	
	public Graph getGraph(){
		return dg.getGraph();
	}
	
	public void setGraph(Graph g){
		dg.setGraph(g);
		tEdges.setModel(g);
		spDrawGraph.repaint();
	}
	
	/**
	 * Create the frame.
	 */
	public GraphFrame(Graph g) {
		if(g==null){
			g=new Graph(0,0,true);
		}
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		
		dg=new DrawGraph(g);
		dg.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 3), "Graph"));
		dg.addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent e) {
				if(!bChangeEdge.isSelected() && !bShortestPath.isSelected()){
					int x=e.getX();
					int y=e.getY();
					Node n;
					for(int i=0; i<dg.getGraph().getNumOfNodes();i++){
						n=dg.getGraph().getNodeAt(i);
						if(n.isActiveNode()){
							n.setX(x);
							n.setY(y);
							repaint();
							break;
						}	
					}
				}
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				/*int x=e.getX();
				int y=e.getY();
				Node n;
				for(int i=0; i<g.getNumOfNodes();i++){
					n=g.getNodeAt(i);
					double d=Math.sqrt((x-n.getX())*(x-n.getX())+(y-n.getY())*(y-n.getY()));
					if(d<=16.0){
						lInfo.setText("Node \""+n.getName()+"\"");
						break;
					}
					lInfo.setText("");
				}*/
			}		
		});
		dg.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {
				int x=e.getX();
				int y=e.getY();
				Node n;
				boolean foundNode=false;
				for(int i=0; i<dg.getGraph().getNumOfNodes();i++){
					n=dg.getGraph().getNodeAt(i);
					double d=Math.sqrt((x-n.getX())*(x-n.getX())+(y-n.getY())*(y-n.getY()));
					if(d<=16.0){
						if(bChangeEdge.isSelected()){
							int weight=(Integer) sWeight.getValue();
							if(dg.getGraph().changeEdge(activeNode.getName(), n.getName(), weight)){
								lInfo.setText("Changed the weight of the edge between \""+activeNode.getName()+"\" and \""+n.getName()+"\" to "+weight);
							}
						}else if(bShortestPath.isSelected()){
							lInfo.setText("Shortest path from \""+activeNode.getName()+"\" to \""+n.getName()+"\": "+dg.getGraph().shortestPath(activeNode.getName(), n.getName()));
						}else{
							setActiveNode(n);
							foundNode=true;
						}
						repaint();
						break;
					}
				}
				if(!foundNode && !bChangeEdge.isSelected()){
					for(int i=0; i<dg.getGraph().getNumOfNodes();i++){
						n=dg.getGraph().getNodeAt(i);
						if(n.isActiveNode()){
							n.setX(x);
							n.setY(y);
							repaint();
							break;
						}	
					}
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {}
		});
		
		spDrawGraph=new JScrollPane(dg);
		spDrawGraph.setPreferredSize(new Dimension(500,500));
		
		lInfo=new JLabel(" ");
		lInfo.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(2,2,2,2), new LineBorder(Color.BLACK)));
		
		sWeight=new JSpinner(new SpinnerNumberModel(1,0,100,1));
		lWeight=new JLabel("Weight: ");
		
		lNodeEdit=new JLabel();
		tEdges=new JTable(g);
		tEdges.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		spEdges=new JScrollPane(tEdges);

		bChangeEdge=new JToggleButton("Change weight of an edge");
		bChangeEdge.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(bChangeEdge.isSelected()){
					if(bShortestPath.isSelected()){
						bShortestPath.setSelected(false);
					}
					lInfo.setText("Click on the node to which the edge goes");
				}
			}
		});
		bShortestPath=new JToggleButton("Calculate shortest path to a node");
		bShortestPath.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(bShortestPath.isSelected()){
					if(bChangeEdge.isSelected()){
						bChangeEdge.setSelected(false);
					}
					lInfo.setText("Click on the destination node");
				}
			}
		});
		bDeleteNode=new JButton("Delete selected node");
		bDeleteNode.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lInfo.setText("Removed node \""+activeNode.getName()+"\"");
				dg.getGraph().removeNode(activeNode.getName());
				setActiveNode(null);
				repaint();
			}
		});
		bAdd=new JButton("Add Node");
		bAdd.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!tfNode.getText().equals("")){
					if(dg.getGraph().addNode(tfNode.getText())){
						repaint();
						lInfo.setText("Added Node \""+tfNode.getText()+"\"");
					}else{
						JOptionPane.showMessageDialog(dg, "There is already an node with this name!", "Duplicate names", JOptionPane.ERROR_MESSAGE);
					}
				}else{
					JOptionPane.showMessageDialog(dg, "Empty name!", "Invalid name", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		tfNode=new JTextField("Enter node name");
		
		miSave=new JMenuItem("Save graph");
		miSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
			Save s=new Save("");
				try {
					String path=JOptionPane.showInputDialog(GraphFrame.mf, "Where do you want to save your level?", "Saving Location", JOptionPane.QUESTION_MESSAGE);
					if(path!=null && !path.isEmpty()){
						if(!path.endsWith(".txt")){
							path+=".txt";
						}	
						s=new Save(path);
						if(s.getFile().exists()){
							int opt=JOptionPane.showConfirmDialog(GraphFrame.mf, "This file already exists. Do you want to override it?", "Override File", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
							if(opt==JOptionPane.OK_OPTION){
								s.deleteFile();
								dg.getGraph().save(s);
							}
						}else{
							dg.getGraph().save(s);
						}
					}
				} catch (Exception e) {
					JOptionPane.showConfirmDialog(mf, "Error: "+e.getMessage(), "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		miLoad=new JMenuItem("Load graph");
		miLoad.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean failed=true;
				while(failed){
					try {
						String path=JOptionPane.showInputDialog(mf, "Path of the level-file:", "Load Level", JOptionPane.QUESTION_MESSAGE);
						if(path!=null && !path.isEmpty()){
							if(!path.endsWith(".txt")){
								path+=".txt";
							}	
							Save s=new Save(path);
							dg.getGraph().load(s);
						}
						failed=false;
					} catch (Exception e) {
						int g=JOptionPane.showConfirmDialog(mf, "Error: "+e.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
						if(g==JOptionPane.CANCEL_OPTION){
							failed=false;
						}
					}
				}
				repaint();
			}
		});
		miReset=new JMenuItem("Reset graph");
		miReset.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dg.getGraph().reset(dg.getGraph().getNodes().length);
				setActiveNode(null);
			}
		});
		miExit=new JMenuItem("Exit");
		miExit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		mFile=new JMenu("File");
		mFile.add(miSave);
		mFile.add(miLoad);
		mFile.add(miReset);
		mFile.addSeparator();
		mFile.add(miExit);
		
		menu=new JMenuBar();
		menu.add(mFile);
		
		
		pControls=new JPanel();
		GroupLayout gl=new GroupLayout(pControls);
		pControls.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		gl.setHorizontalGroup(
				gl.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addGroup(gl.createSequentialGroup()
							.addComponent(bChangeEdge)
							.addComponent(lWeight)
							.addComponent(sWeight)
					)
					.addComponent(bShortestPath)
					.addComponent(bDeleteNode)
					.addGroup(gl.createSequentialGroup()
							.addComponent(tfNode)
							.addComponent(bAdd)
					)
		);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
					.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(bChangeEdge)
							.addComponent(lWeight)
							.addComponent(sWeight)
					)
					.addComponent(bShortestPath)
					.addComponent(bDeleteNode)
					.addGroup(gl.createParallelGroup()
							.addComponent(tfNode)
							.addComponent(bAdd)
					)
		);

		pNodeEdit=new JPanel();
		pNodeEdit.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(2, 5, 2, 5, Color.BLUE), new EmptyBorder(3,3,3,3)));
		
		pNodeEdit.setLayout(new BorderLayout());
		pNodeEdit.add(lNodeEdit,BorderLayout.NORTH);
		pNodeEdit.add(spEdges,BorderLayout.CENTER);
		pNodeEdit.add(pControls,BorderLayout.SOUTH);
		
		contentPane.add(spDrawGraph, BorderLayout.CENTER);
		contentPane.add(lInfo,BorderLayout.SOUTH);
		contentPane.add(pNodeEdit, BorderLayout.EAST);
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));		
		setActiveNode(g.getNodeAt(0));
		setContentPane(contentPane);
		setJMenuBar(menu);
		pack();
	}

}
