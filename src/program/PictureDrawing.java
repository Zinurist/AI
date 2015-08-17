package program;

import gui.MainFrame;

import java.util.ArrayList;

public class PictureDrawing extends Simulation {

	public PictureDrawing(int xMax, int yMax, int maxRankSize, int numOfAIs,int numOfActiveAIs,int neuronSets[],boolean randomWeights, boolean brainNets,int defaultTrainingRounds,double[][] defaultTargetOutputs,double[][] defaultInputs){
		super(xMax,yMax,maxRankSize,numOfAIs,numOfActiveAIs,neuronSets,randomWeights,brainNets,defaultTrainingRounds,defaultTargetOutputs,defaultInputs);		
	}
	public PictureDrawing(int xMax, int yMax){
		super(xMax, yMax, 0, 4, 1, new int[]{3,5,4},true,true,50000,new double[][]{{1,1,0,0}},new double[][]{{500,250,90}});
	}
	
	@Override
	public void evaluate(AI a) {
		setCurrentAIs(a.getId());
		int maxRounds=100;
		int countInterval=maxRounds/100;
		MainFrame.mf.setRoundStatusBar(0);
		for(int roundCounter=0;roundCounter<maxRounds;roundCounter++){
			update(false);
			if(roundCounter%countInterval==0){
				MainFrame.mf.addRoundStatusBar(1);
			}
		}	
		MainFrame.mf.setRoundStatusBar(100);
		
		a.setActive(false);
		try {
			MainFrame.mf.update();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//capture();
		a.setPoints(a.getRoundPoints());
		items=new ArrayList<Item>();
	}

	@Override
	public void update(boolean timeMode) {
		if(timeMode){
			long currentTime=System.currentTimeMillis();
			timeDif+=currentTime-lastTime;
			lastTime=currentTime;
		}
		if(!timeMode || timeDif>50){
			timeDif=0;
			double[] output=new double[3];
			double[] input=new double[1];
			//boolean seesItem=false;	
			
			AI a=g.getCurrentAIAt(currentAIs[0]);				
			/*Item item;
			for(int m=0; m<items.size();m++){
				item=items.get(m);
				if(inFov(a,item)){
					seesItem=true;
				}
			}*/
			
			//Input: 1.x, 2.y, 3.angle, (4.seesitem)
			//Output: 1. move, 2. clockwise, 3. countercw, 4. paint
			input=new double[]{a.getX(),a.getY(),a.getAngle()};//,seesItem?1:0};
			output=a.feedForward(input);
			
			if(output[0]>0.5){
				a.moveForward(xMax,yMax,0,0,5);
			}
			if(output[1]>0.5){
				a.addAngle(10);
			}
			if(output[2]>0.5){
				a.addAngle(-10);
			}
			if(output[3]>0.5){
				fire(a);
				a.addRoundPoints(1);
			}
		}
	}

	@Override
	public void setCurrentAIs(int... id) {
		AI a=g.getCurrentAIAt(currentAIs[0]);
		a.setActive(false);
		a=g.getCurrentAIAt(id[0]);
		a.setActive(true);
		a.setX(5000);
		a.setY(2500);
		a.setAngle(90);
		a.setFov(15);
		a.setRoundPoints(0);
		
		currentAIs=id;
		
		items=new ArrayList<Item>();
	}
}
