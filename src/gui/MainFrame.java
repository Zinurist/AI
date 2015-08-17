package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;





import javax.swing.ListSelectionModel;

import chess.game.Chess;
import program.ChessBattle;
import program.FovTest;
import program.MultiFoodSearch;
import program.Multiplier;
import program.SimpleFighter;
import program.Simulation;
import program.Fighter;
import program.FoodSearch;
import program.Generation;
import program.PictureDrawing;
import program.Turret;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	
	public static MainFrame mf;
	
	private JPanel contentPane,pControls,pMenu,pTable,pLog,pStatus;
	private JButton bTrainGen,bTrainAll, bMutateGen,bTableUpdate,bEvaluateGen,bDisplayNet,bApplyNet;
	private JToggleButton bPause,bAutomutate,bCapture;
	private JLabel lStatus;
	private JProgressBar pbStatus,pbRoundStatus;
	private JTextField tfcmd;
	private JTextArea tLog;
	private JTable[] aiTables;
	private JScrollPane[] spTables;
	private JScrollPane spLog;
	private DrawWorld dw;
	private Simulation s;
	private String saveLoc;
	
	/**
	 * Launches the application and creates a default graph.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					//Simulation s=new Fighter(1000,500);
					Simulation s=new SimpleFighter(1000,500);
					//Simulation s=new Multiplier(1000,500);
					//Simulation s=new PictureDrawing(10000,5000);
					//Simulation s=new Turret(1000,500);
					//Simulation s=new FoodSearch(1000,500);
					//Simulation s=new MultiFoodSearch(1500,900);
					//Simulation s=new FovTest(1000,500);
					//Simulation s=new ChessBattle(1000,500);
					mf = new MainFrame(s,"D:\\Test\\AI\\");
					mf.setVisible(true);
					new Thread(s).start();

					s.setMode(2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * Create the frame.
	 */
	public MainFrame(final Simulation s, String saveLoc) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		
		
		dw=new DrawWorld(s);
		dw.addMouseWheelListener(new MouseWheelListener(){
			@Override
			public void mouseWheelMoved(MouseWheelEvent m) {
				dw.addZoom(m.getPreciseWheelRotation()*5);
			}
		});
		dw.addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent m) {
				dw.addDrag(m.getX(), m.getY());
			}
			@Override
			public void mouseMoved(MouseEvent m) {
				dw.setOldMousePosition(m.getX(), m.getY());
			}
		});
		
		this.s=s;
		this.saveLoc=saveLoc;

		bCapture=new JToggleButton("Capture");
		bCapture.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				s.toggleCapture();
			}
		});
		bPause=new JToggleButton("Pause");
		bPause.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(bPause.isSelected()){
					s.setMode(-1);
				}else{
					s.setMode(0);
				}
			}
		});
		bAutomutate=new JToggleButton("Automutate");
		bAutomutate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(bAutomutate.isSelected()){
					s.setMode(2);
				}else{
					s.setMode(-1);
				}
			}
		});
		bMutateGen=new JButton("Mutate generation");
		bMutateGen.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				s.setMode(1);
			}
		});
		bTrainGen=new JButton("Train generation");
		bTrainGen.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				s.setMode(3);
			}
		});
		bTrainAll=new JButton("Train all");
		bTrainAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				s.setMode(5);
			}
		});
		bEvaluateGen=new JButton("Evaluate");
		bEvaluateGen.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				s.setMode(4);
			}
		});
		bDisplayNet=new JButton("Display Network");
		bDisplayNet.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				s.showGraph();
			}
		});
		bApplyNet=new JButton("Apply BrainNet");
		bApplyNet.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				s.applyNet();
			}
		});
		/*if(!s.hasBrainNets()){
			bDisplayNet.setEnabled(false);
			bApplyNet.setEnabled(false);
		}*/
		
		
		pControls=new JPanel();
		GroupLayout gl=new GroupLayout(pControls);
		pControls.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		gl.setHorizontalGroup(
				gl.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(bPause)
					.addGroup(gl.createSequentialGroup()
							.addComponent(bMutateGen)
							.addComponent(bAutomutate)
					)
					.addGroup(gl.createSequentialGroup()
							.addComponent(bTrainGen)
							.addComponent(bTrainAll)
					)
					.addGroup(gl.createSequentialGroup()
							.addComponent(bCapture)
							.addComponent(bEvaluateGen)
					)
					.addGroup(gl.createSequentialGroup()
							.addComponent(bDisplayNet)
							.addComponent(bApplyNet)
					)
		);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
					.addComponent(bPause)
					.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(bMutateGen)
							.addComponent(bAutomutate)
					)
					.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(bTrainGen)
							.addComponent(bTrainAll)
					)
					.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(bCapture)
							.addComponent(bEvaluateGen)
					)
					.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(bDisplayNet)
							.addComponent(bApplyNet)
					)
		);
		gl.linkSize(bPause,bAutomutate,bMutateGen,bTrainGen,bTrainAll,bCapture,bEvaluateGen,bDisplayNet,bApplyNet);
		
		pTable=new JPanel();
		pTable.setLayout(new GridLayout());
		
		aiTables=new JTable[s.getCurrentAIs().length];
		spTables=new JScrollPane[aiTables.length];
		for(int i=0; i<aiTables.length;i++){
			aiTables[i]=new JTable(s.getGeneration());
			aiTables[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			aiTables[i].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			spTables[i]=new JScrollPane(aiTables[i]);
			pTable.add(spTables[i]);
		}
		
		bTableUpdate=new JButton("Watch AI");
		bTableUpdate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int[] id=new int[aiTables.length];
				boolean error=false;
				tablecheck:for(int i=0; i<id.length;i++){
					int ainum=aiTables[i].getSelectedRow();
					if(ainum<0){
						error=true;
						break tablecheck;
					}else{
						for(int t=0;t<i;t++){
							if(id[t]==ainum){
								error=true;
								break tablecheck;
							}
						}
					}
					id[i]=ainum;
				}
				if(!error){
					s.setCurrentAIs(id);
				}
			}
		});	

		
		
		lStatus=new JLabel("Paused");
		lStatus.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lStatus.setFont(new Font("Arial", Font.PLAIN,17));
		
		pbStatus=new JProgressBar();
		pbStatus.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		pbRoundStatus=new JProgressBar();
		pbRoundStatus.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		
		pStatus =new JPanel(new GridLayout(1,0));
		pStatus.add(lStatus);
		pStatus.add(pbStatus);	
		pStatus.add(pbRoundStatus);	
		
		tLog=new JTextArea("---Log---");
		tLog.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tLog.setFont(new Font("Arial", Font.PLAIN,22));
		tLog.setEditable(false);
		tLog.setLineWrap(true);
		tLog.setRows(100);
		spLog=new JScrollPane(tLog);
		spLog.setPreferredSize(new Dimension(200,200));
		
		
		tfcmd=new JTextField("");
		tfcmd.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tfcmd.setFont(new Font("Arial", Font.PLAIN,22));
		tfcmd.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent k) {
				if(k.getKeyCode()==KeyEvent.VK_ENTER){
					String cmd=tfcmd.getText();
					String[] cmdSplit;
					if(!cmd.isEmpty()){
						tfcmd.setText("");
						if(cmd.startsWith("rounds")){
							cmdSplit=cmd.split(" +");
							try{
								int rounds=Integer.parseInt(cmdSplit[1]);
								s.setTrainingRounds(rounds);
								cmd="Command successful: \""+cmd+"\"";
							}catch(Exception e){
								cmd="Error: "+e.getMessage()+": \""+cmd+"\"";
								e.printStackTrace();
							}
						}else if(cmd.startsWith("targets") || cmd.startsWith("inputs")){
							cmdSplit=cmd.split(" +");
							String[] values;
							double[][] doubles=new double[cmdSplit.length-1][];
							try{
								for(int i=1;i<cmdSplit.length;i++){
									values=cmdSplit[i].split("[(),]");
									doubles[i-1]=new double[values.length-1];
									for(int g=1; g<values.length;g++){
										doubles[i-1][g-1]=Double.parseDouble(values[g]);
										System.out.println(doubles[i-1][g-1]);
									}
								}
								if(cmd.startsWith("t")){
									s.setTargetOutputs(doubles);
								}else{
									s.setInputs(doubles);
								}
								cmd="Command successful: \""+cmd+"\"";
							}catch(Exception e){
								cmd="Error: "+e.getMessage()+": \""+cmd+"\"";
								e.printStackTrace();
							}
							
						}else{
							cmd="Error: Illegal command: \""+cmd+"\"";
						}
						
						addLog(cmd);
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent k) {}
			@Override
			public void keyTyped(KeyEvent k) {}	
		});
		
		pLog=new JPanel(new BorderLayout());
		pLog.add(pStatus, BorderLayout.NORTH);
		pLog.add(spLog, BorderLayout.CENTER);
		pLog.add(tfcmd, BorderLayout.SOUTH);
		pLog.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));	
		
		pMenu=new JPanel(new BorderLayout());
		pMenu.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		pMenu.setPreferredSize(new Dimension(310,dw.getHeight()));
		
		pMenu.add(pControls, BorderLayout.NORTH);
		pMenu.add(pTable, BorderLayout.CENTER);
		pMenu.add(bTableUpdate, BorderLayout.SOUTH);
		contentPane.add(dw, BorderLayout.CENTER);
		contentPane.add(pMenu, BorderLayout.EAST);
		contentPane.add(pLog,BorderLayout.SOUTH);
		setContentPane(contentPane);
		pack();
		
		bPause.setSelected(true);
		updateCounters();
	}
	
	public void enableButtons(int mode){
		boolean paused = mode==-1;
		bTrainGen.setEnabled(paused);
		bTrainAll.setEnabled(paused);
		bMutateGen.setEnabled(paused);
		bTableUpdate.setEnabled(paused);
		bEvaluateGen.setEnabled(paused);
		bPause.setEnabled(mode==0 || paused);
		bAutomutate.setEnabled(mode==2 || paused);
		bCapture.setEnabled(paused);
		//if(!s.hasBrainNets()){
		bDisplayNet.setEnabled(paused);
		bApplyNet.setEnabled(paused);
		//}
		
		if(paused){
			bPause.setSelected(true);
		}
	}
	
	public void update(){
		dw.repaint();
	}
	
	public void setStatus(String status){
		lStatus.setText(status);
	}
	
	public void addStatusBar(int value){
		pbStatus.setValue(value+pbStatus.getValue());
	}
	
	public void setStatusBar(int value){
		pbStatus.setValue(value);
	}
	
	public void addRoundStatusBar(int value){
		pbRoundStatus.setValue(value+pbRoundStatus.getValue());
	}
	
	public void setRoundStatusBar(int value){
		pbRoundStatus.setValue(value);
	}
	
	public void addLog(String status){
		Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		tLog.setText("["+sdf.format(cal.getTime())+"]: "+status+"\n\r"+tLog.getText());
		tLog.setCaretPosition(0);
		//tLog.setRows(100);
	}
	
	public void updateCounters(){	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					setTitle("Improvements: "+Generation.IMPROVEMENT_COUNT+", Wins: "+Generation.WINS_COUNT+", Masters: "+Generation.MASTER_COUNT+", "+s.getGenCounter()+". Generation");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public String getSaveLoc() {
		return saveLoc;
	}


	public DrawWorld getDW() {
		return dw;
	}
}
