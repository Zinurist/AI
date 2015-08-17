package program;

import graph.gui.GraphFrame;
import graph.program.Graph;
import gui.MainFrame;
import gui.NetworkFrame;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public abstract class Simulation implements Runnable {

	protected int xMax,yMax,numOfAIs;
	protected Generation g;
	protected GraphFrame gf;
	protected NetworkFrame nf;
	protected boolean running;
	protected boolean brainNets;
	protected List<Item> items;
	protected int numOfNeuronSets; 
	/**
	 * This is only used in non-brainNets for setting training-data. It assumes that every AI has the same number of neurons in a similar structure. BrainNets may have different number of Neurons!
	 */
	protected int[] neuronSets;
	/**
	 * modes:
	 * 0:watch AI
	 * 1:mutate Generation=allocate points, choose superiors, mutate
	 * 2:automutation
	 * 3:train AIs
	 * default: wait
	 */
	protected int mode,trainingRounds;
	protected int[] currentAIs;
	protected double[][] targetOutputs,inputs;
	protected long lastTime,timeDif;
	private boolean capture,robotFailed,trainingPossible;
	private int imgCount;
	private Robot r;
	
	public Simulation(int xMax, int yMax, int maxRankSize, int numOfAIs,int numOfActiveAIs,int neuronSets[],boolean randomWeights, boolean brainNets,int defaultTrainingRounds,double[][] defaultTargetOutputs,double[][] defaultInputs){
		this.xMax=xMax;
		this.yMax=yMax;
		this.numOfAIs=numOfAIs;
		this.neuronSets=neuronSets;
		g=new Generation(maxRankSize,numOfAIs,neuronSets,randomWeights,brainNets);
		this.brainNets=brainNets;
		
		
		/*old: graph get initialized here, but now thats down when the graph is supposed to be displayed
		if(brainNets){
			int length=neuronSets[1]+neuronSets[0]+neuronSets[neuronSets.length-1];
			Graph g=new Graph(length,0,true); 
			for(int i=0;i<length;i++){
				if(i<neuronSets[neuronSets.length-1]){
					g.addNode("*"+i);
				}else if(i<neuronSets[0]+neuronSets[neuronSets.length-1]){
					g.addNode(i+"*");
				}else{
					g.addNode("*"+i+"*");
				}
			}//TODO
			gf=new GraphFrame(g);
			GraphFrame.mf=gf;
		}*/
		gf=new GraphFrame(null);
		nf=new NetworkFrame();
		GraphFrame.mf=gf;
		
		trainingPossible=false;
		try{
			if(!brainNets && defaultTrainingRounds>0){
				setTrainingRounds(defaultTrainingRounds);
				setTargetOutputs(defaultTargetOutputs);
				setInputs(defaultInputs);
				trainingPossible=true;
			}
		}catch(RuntimeException re){
			System.out.println(re.getMessage());
		}
		//not using setMode: frame not created yet
		mode=-1;
		
		running=false;
		items=new ArrayList<Item>();
		
		currentAIs=new int[numOfActiveAIs];
		for(int i=0;i<numOfActiveAIs;i++){
			currentAIs[i]=i;
		}
		
		setCurrentAIs(currentAIs);
		
		capture=false;
		imgCount=0;
		try {
			r=new Robot();
			robotFailed=false;
		} catch (AWTException e) {
			r=null;
			robotFailed=true;
			e.printStackTrace();
		}
	}
	
	public void setTrainingPossible(boolean trainingPossible){
		this.trainingPossible=trainingPossible;
	}
	
	public void setTrainingRounds(int rounds){
		trainingRounds=rounds;
	}
	
	public void setTargetOutputs(double[][] targetOutputs){
		if(targetOutputs.length==0){
			throw new RuntimeException("Array targetOutputs is empty!");
		}
		int l=targetOutputs[0].length;
		for(int i=1; i<targetOutputs.length;i++){
			if(targetOutputs[i].length!=l){
				throw new RuntimeException("Wrong targetOutputs length!");
			}
		}
		if(neuronSets[neuronSets.length-1]==l){
			this.targetOutputs=targetOutputs;
		}else{
			throw new RuntimeException("Wrong targetOutputs length!");
		}
	}
	
	public void setInputs(double[][] inputs){
		if(inputs.length==0){
			throw new RuntimeException("Array inputs is empty!");
		}
		int l=inputs[0].length;
		for(int i=1; i<inputs.length;i++){
			if(inputs[i].length!=l){
				throw new RuntimeException("Wrong inputs length!");
			}
		}
		if(neuronSets[0]==l){
			this.inputs=inputs;
		}else{
			throw new RuntimeException("Wrong inputs length!");
		}
	}
	
	public void stop(){
		running=false;
	}
	
	@Override
	public void run() {
		//first time evaluation
		for(AI a:g.getAIs()){
			a.setPoints(0);
			evaluate(a);
		}
		
		running=true;
		while(running){
			switch(mode){
			case 0:
				MainFrame.mf.setStatus("Running the simulation");
				update(true);
				if(capture){
					capture();
				}
				break;
			case 1:
				MainFrame.mf.setStatus("Mutating...");
				mutate(true,0,30,6);
				setMode(-1);
				break;
			case 2:
				MainFrame.mf.setStatus("Mutating...");
				while(mode==2){
					mutate(true,0,30,6);
				}
				break;
			case 3:
				MainFrame.mf.setStatus("Training...");
				train(trainingRounds,targetOutputs,inputs,false);
				//no nextGen for training, evaluate option should be used instead
				//mutate(false,0,0,0);
				
				setMode(-1);
				break;
			case 4:
				//inlcudes nextGen
				MainFrame.mf.setStatus("Evaluating...");
				mutate(false,0,0,0);
				setMode(-1);
				break;
			case 5:
				MainFrame.mf.setStatus("Training...");
				train(trainingRounds,targetOutputs,inputs,true);
				//no nextGen for training, evaluate option should be used instead
				//mutate(false,0,0,0);
				
				setMode(-1);
				break;
			default:
				MainFrame.mf.setStatus("Paused");
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			MainFrame.mf.update();
		}
	}
	
	public abstract void evaluate(AI a);
	public abstract void update(boolean timeMode);
	/**
	 * Prepares the new AIs and sets them active as well as deactivating the currentAI before
	 * @param id
	 */
	public abstract void setCurrentAIs(int... id);
	
	public void mutate(boolean mutation,int crossOverType,int pCrossOver,int pMutation){
		MainFrame.mf.setStatusBar(0);
		
		MainFrame.mf.setStatusBar(0);
		MainFrame.mf.updateCounters();
		int addedAmount=Math.round(90 / (g.getAIs().size()+g.getSuperiors().size()));

		if(mutation){
			g.evolve(crossOverType,pCrossOver,pMutation);
		}else{
			g.nextGen();
		}
		
		for(AI a:g.getAIs()){
			a.setPoints(0);
			evaluate(a);
			MainFrame.mf.addStatusBar(addedAmount);
		}
		//reevaluating of old guys
		for(AI a:g.getSuperiors()){
			a.setPoints(0);
			evaluate(a);
			//ids change after nextGen, therefore set all to false inb4
			a.setActive(false);
			MainFrame.mf.addStatusBar(addedAmount);
		}
		
		setCurrentAIs(currentAIs);
		
		g.notifyListeners();
		MainFrame.mf.setStatusBar(100);
	}
	
	/**
	 * Determines, if the 2 given entities overlap using their radius.
	 * Not checking for null pointers.
	 * @param e1 first entity
	 * @param e2 second entity
	 * @return true, if the 2 entities overlap
	 */
	public boolean collision(Entity e1,Entity e2){
		double maxDistance=(double)(e1.getRadius()+e2.getRadius());
		return (Math.sqrt((e1.getX()-e2.getX())*(e1.getX()-e2.getX()) + (e1.getY()-e2.getY())*(e1.getY()-e2.getY()))<maxDistance);
	}
	
	/**
	 * Checks whether a line crosses the point.
	 * For the line the direction in which entity "line" is looking is used.
	 * Not checking for null pointers.
	 * @param line the "eye"
	 * @param point the point which might cross the line
	 * @return true, if they cross
	 */
	public boolean inLine(Entity line,Entity point){
		//y=mx+t -> t=y-mx
		double m=-Math.tan(Math.toRadians(line.getAngle()));// y inverted
		double t=line.getY()-m*line.getX();
		double a=point.getX();
		double b=point.getY();
		double x=(a+b*m-m*t)/(m*m+1);
		double d=Math.sqrt((a-x)*(a-x) + (b-m*x-t)*(b-m*x-t));
		if(d<point.getRadius()){
			return true;
		}
		return false;
	}
	
	/**
	 * Determines, whether the entity line can see entity point.
	 * Not checking for null pointers.
	 * @param line the "eye"
	 * @param point the entity, which might be seen
	 * @return true, if point is in the fov of line
	 */
	public boolean inFov(Entity line,Entity point){
		double xl=line.getX();
		double yl=line.getY();
		double xp=point.getX();
		double yp=point.getY();
		int an=line.getAngle();
		int fov=line.getFov();
		boolean insight=true;
		
		if(xl>xp){
			insight=(an+fov)>90 && (an-fov)<270;
		}else{
			insight=(an-fov)<=90 || (an+fov)>=270;
		}
		
		if(insight && yl>yp){
			insight=((an+fov)>0 || (an+fov)>360) && (an-fov)<180;
		}else if(insight && yl<=yp){
			insight=(an+fov)>=180 && ((an-fov)<=360 || (an-fov)<=0);
		}
		
		if(insight){
			//y=mx+t -> t=y-mx
			//line g/h
			double mg=-Math.tan(Math.toRadians(line.getAngle()+line.getFov()));// y inverted -> -Math......
			double tg=yl-mg*xl;
			
			double mh=-Math.tan(Math.toRadians(line.getAngle()-line.getFov()));// y inverted -> -Math......
			double th=yl-mh*xl;
			
			//get y-limits: y=mx+t
			double yLim1=mg*xp+tg;
			double yLim2=mh*xp+th;
			//round
			yLim1=Math.round(yLim1*1000)/1000.0;
			yLim2=Math.round(yLim2*1000)/1000.0;
			
			boolean ycorrect=false;		
			if(xl>xp){
				if(yl>yp && yLim2>yl){
					ycorrect=yp<yLim1;	
				}else if(yl<yp && yLim1<yl){
					ycorrect=yp>yLim2;
				}else{ 
					ycorrect=(yp<yLim1 && yp>yLim2);
				}
			}else if(xl<xp){
				if(yl>yp && yLim1>yl){
					ycorrect=yp<yLim2;	
				}else if(yl<yp && yLim2<yl){
					ycorrect=yp>yLim1;	
				}else{
					ycorrect=(yp>yLim1 && yp<yLim2);
				}
			}else{
				if(yl>yp){
					ycorrect=yp<yLim1;	
				}else if(yl<yp){
					ycorrect=yp>yLim1;	
				}
			}
			
			//get x-limits: x=(y-t)/m
			double xLim1=(yp-tg)/mg;
			double xLim2=(yp-th)/mh;
			xLim1=Math.round(xLim1*1000)/1000.0;
			xLim2=Math.round(xLim2*1000)/1000.0;
			
			boolean xcorrect=false;
			if(yl>yp){
				if(xl>xp && xLim1>xl){
					xcorrect=xp<xLim2;	
				}else if(xl<xp && xLim2<xl){
					xcorrect=xp>xLim1;
				}else{ 
					xcorrect=(xp>xLim1 && xp<xLim2);
				}
			}else if(yl<yp){
				if(xl>xp && xLim2>xl){
					xcorrect=xp<xLim1;	
				}else if(xl<xp && xLim1<xl){
					xcorrect=xp>xLim2;	
				}else{
					xcorrect=(xp<xLim1 && xp>xLim2);
				}
			}else{
				if(xl>xp){
					xcorrect=xp<xLim1;	
				}else if(xl<xp){
					xcorrect=xp>xLim1;	
				}
			}
			return ycorrect&&xcorrect;
		}
		return false;
	}
	
	/**
	 * Determines, whether the entity eye can see entity point. Contrary to inFov, this method doesnt use the fov of the entity, but instead a line going in the direction, in which the eye is looking at.
	 * Not checking for null pointers.
	 * @param eye the "eye"
	 * @param point the entity, which might be seen
	 * @return true, if point is in the fov of eye
	 */
	public boolean inSight(Entity eye,Entity point){
		//y=mx+t -> t=y-mx
		boolean seesPoint=false;
		boolean dirY=true;
		boolean dirX=true;
		if(eye.getX()>point.getX()){
			dirX=eye.getAngle()>=45 && eye.getAngle()<=315;
		}else{
			dirX=eye.getAngle()<=135 || eye.getAngle()>=225;
		}
		
		if(eye.getY()>point.getY()){
			dirY=eye.getAngle()>=315 || eye.getAngle()<=225;
		}else{
			dirY=eye.getAngle()<=45 || eye.getAngle()>=135;
		}
		if(dirX && dirY){
			seesPoint=inLine(eye,point);
		}
		return seesPoint;
	}
	
	public void train(int rounds,double[][] targetOutputs,double[][] inputs,boolean all){
		if(trainingPossible){
			MainFrame.mf.setStatusBar(0);
			int addedAmount=(100/g.getAIs().size());
			addedAmount=all?addedAmount/2:addedAmount;
			for(AI a:g.getAIs()){
				for(int roundCounter=0;roundCounter<rounds;roundCounter++){		
					//double[] targetOutput=new double[]{roundCounter%2==0?1:0,roundCounter%2==0?0:1};
					//double[] input=new double[]{roundCounter%2==0?1:0};
					double[] targetOutput=targetOutputs[roundCounter%targetOutputs.length];
					double[] input=inputs[roundCounter%inputs.length];
					a.feedForward(input);
					//roundCounter%rounds/10==0 || (roundCounter%rounds/10 -1)==0
					a.backPropagate(targetOutput, input, false);	
				}
				//reevaluate
				a.setPoints(0);
				evaluate(a);
				MainFrame.mf.addStatusBar(addedAmount);
			}
			
			if(all){
				addedAmount=(100/g.getSuperiors().size()) /2 ;
				for(AI a:g.getSuperiors()){
					for(int roundCounter=0;roundCounter<rounds;roundCounter++){		
						//double[] targetOutput=new double[]{roundCounter%2==0?1:0,roundCounter%2==0?0:1};
						//double[] input=new double[]{roundCounter%2==0?1:0};
						double[] targetOutput=targetOutputs[roundCounter%targetOutputs.length];
						double[] input=inputs[roundCounter%inputs.length];
						a.feedForward(input);
						//roundCounter%rounds/10==0 || (roundCounter%rounds/10 -1)==0
						a.backPropagate(targetOutput, input, false);	
					}
					a.setPoints(0);
					evaluate(a);
					MainFrame.mf.addStatusBar(addedAmount);
				}
			}
			g.notifyListeners();
			
			/*OUTPUT for log
			String stargetOutputs="{   ";
			String sinputs="{   ";
			for(int i=0; i<targetOutputs.length;i++){
				stargetOutputs+="{ ";
				sinputs+="{ ";
				for(int k=0;k<targetOutputs[i].length;k++){
					stargetOutputs+=targetOutputs[i][k]+" ";
				}
				for(int k=0;k<inputs[i].length;k++){
					sinputs+=inputs[i][k]+" ";
				}
				stargetOutputs+="}   ";
				sinputs+="}   ";
			}
			stargetOutputs+="}";
			sinputs+="}";
			MainFrame.mf.addLog("Training: "+trainingRounds+" rounds, target: "+stargetOutputs+", inputs: "+sinputs);*/
			MainFrame.mf.setStatusBar(100);
		}
	}
	
	public void fire(AI a){
		Item item=new Item(a.getX(),a.getY(),a.getAngle(),true);
		item.moveForward(xMax,yMax,0,0,Entity.ITEM_RAD+Entity.AI_RAD);
		items.add(item);
	}
	
	public void setMode(int mode){
		this.mode=mode;
		MainFrame.mf.enableButtons(mode);
	}
	
	public int getXMax(){
		return xMax;
	}
	
	public int getYMax(){
		return yMax;
	}
	
	public int getNumOfAIs(){
		return numOfAIs;
	}
	
	public Generation getGeneration(){
		return g;
	}
	
	public int getGenCounter(){
		return g.getGenCounter();
	}
	
	public List<Item> getItems(){
		return items;
	}

	public List<AI> getAIs() {
		return g.getAIs();
	}
	
	public List<AI> getSuperiors() {
		return g.getSuperiors();
	}
	
	public int[] getCurrentAIs(){
		return currentAIs;
	}

	public boolean isRunning() {
		return running;
	}
	
	public boolean hasBrainNets() {
		return brainNets;
	}
	
	public void toggleCapture(){
		capture=!capture;
		MainFrame.mf.addLog("Capture mode turned "+(capture?"on":"off")+".");
	}
	
	public void capture(){
		if(!robotFailed){
			Point p=MainFrame.mf.getDW().getLocationOnScreen();
			BufferedImage img=r.createScreenCapture(new Rectangle(p.x,p.y,MainFrame.mf.getDW().getWidth(),MainFrame.mf.getDW().getHeight()));
			try {
				ImageIO.write(img,"jpg",new File(MainFrame.mf.getSaveLoc()+imgCount+".jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			imgCount++;
		}else{
			System.out.println("No robot");
		}
	}

	public void showGraph() {
		if(brainNets){	
			BrainNet curAI=(BrainNet) g.getCurrentAIAt(currentAIs[0]);
			int length=curAI.calcNumOfNeurons();
			Graph graph=new Graph(length,0,true); 
			for(int i=0;i<length;i++){
				//By definition:neuronMatrix outputs: 0-(outputs-1) inputs: outputs-(inputs+outputs-1)
				
				if(i<curAI.getNumOutput()){
					graph.addNode("*"+i);
				}else if(i<curAI.getNumOutput()+curAI.getNumInput()){
					graph.addNode(i+"*");
				}else{
					graph.addNode("*"+i+"*");
				}
			}
			gf.setGraph(graph);
			GraphFrame.mf=gf;
			
		
			gf.getGraph().setEdges( curAI.getNeuronMatrix() );
			gf.setVisible(true);
			gf.repaint();
		}else{
			/*
			int opt=JOptionPane.showConfirmDialog(null, "This Generation isn't using BrainNets and needs to be converted to BrainNets first. Convert now?", "Display BrainNet", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			if(opt==JOptionPane.YES_OPTION){
				convertToBrainNet();
			}
			*/
			
			AI curAI=g.getCurrentAIAt(currentAIs[0]);
			nf.setAI(curAI);
			nf.setVisible(true);
			nf.repaint();
			
		}
	}
	
	public void applyNet() {
		if(brainNets){	
			if(gf.isVisible()){
				BrainNet a=(BrainNet)g.getCurrentAIAt(currentAIs[0]);
				a.setNeuronMatrix(gf.getGraph().getEdges());
				JOptionPane.showMessageDialog(null, "Applying BrainNet was successful!", "Applying BrainNet", JOptionPane.INFORMATION_MESSAGE);
			}else{
				int opt=JOptionPane.showConfirmDialog(null, "BrainNet must be displayed first! Display it now?", "Apply BrainNet", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
				if(opt == JOptionPane.YES_OPTION){
					showGraph();
				}
			}
		}else{
			int opt=JOptionPane.showConfirmDialog(null, "This Generation isn't using BrainNets and needs to be converted to BrainNets first. Convert now?", "Display BrainNet", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			if(opt==JOptionPane.YES_OPTION){
				convertToBrainNet();
			}
		}
	}
	
	public void convertToBrainNet(){
		boolean success=g.convertToBrainNet();
		
		if(success){
			brainNets=true;
			JOptionPane.showMessageDialog(null, "Converting to BrainNets  was successful!", "Conversion to BrainNet", JOptionPane.INFORMATION_MESSAGE);
			MainFrame.mf.addLog("Generation was successfully converted to BrainNet.");
		}else{
			JOptionPane.showMessageDialog(null, "Converting to BrainNets failed.", "Conversion to BrainNet", JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
