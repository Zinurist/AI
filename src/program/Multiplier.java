package program;

import gui.MainFrame;

import java.util.ArrayList;
import java.util.Random;

public class Multiplier extends Simulation {

	public Multiplier(int xMax, int yMax, int maxRankSize, int numOfAIs,int numOfActiveAIs,int neuronSets[],boolean randomWeights, boolean brainNets,int defaultTrainingRounds,double[][] defaultTargetOutputs,double[][] defaultInputs){
		super(xMax,yMax,maxRankSize,numOfAIs,numOfActiveAIs,neuronSets,randomWeights,brainNets,defaultTrainingRounds,defaultTargetOutputs,defaultInputs);		
	}
	public Multiplier(int xMax, int yMax) {
		//super(xMax, yMax, 10, 15,2,new int[]{4,5,5,5,5},true,60000,new double[][]{{0,0,0,1,0},{0,0,0,1,0},{1,0,0,1,0},{1,0,0,1,0},{0,1,0,0,0},{0,1,0,0,0},{1,0,1,0,0}},new double[][]{{1,0,15,1},{1,0,15,0},{1,0,15,1},{1,0,15,0},{0,1,15,0},{0,1,15,1},{0,0,15,1}});		
		super(xMax, yMax, 10, 3,1,new int[]{2,3,3,1},true,false,60000,
				new double[][]{
				{0},
				{0},
				{0},
				{0},
				{0},
				{1},
				{2},
				{3},
				{4},
				{5},
				{6},
				{7},
				{2},
				{4},
				{6},
				{8},
				{10},
				{6},
				{9}},
				new double[][]{
				{1,0},
				{0,2},
				{0,4},
				{0,3},
				{0,7},
				{1,1},
				{1,2},
				{1,3},
				{4,1},
				{5,1},
				{1,6},
				{1,7},
				{2,1},
				{2,2},
				{3,2},
				{2,4},
				{5,2},
				{3,2},
				{3,3}});		
	}
	
	@Override
	public void evaluate(AI a1) {
		setCurrentAIs(a1.getId());
		
		int maxRounds=2000;
		int countInterval=maxRounds/100;
		MainFrame.mf.setRoundStatusBar(0);
		for(int roundCounter=0;roundCounter<maxRounds;roundCounter++){
			update(false);
			if(roundCounter%countInterval==0){
				MainFrame.mf.addRoundStatusBar(1);
			}
		}	
		MainFrame.mf.setRoundStatusBar(100);
		
		
		a1.addPoints(a1.getRoundPoints());
		items=new ArrayList<Item>();
	}

	@Override
	public void update(boolean timeMode) {
		if(timeMode){
			long currentTime=System.currentTimeMillis();
			timeDif+=currentTime-lastTime;
			lastTime=currentTime;
		}
		if(!timeMode || timeDif>100){
			timeDif=0;
			
			AI a1=g.getCurrentAIAt(currentAIs[0]);
			int num1,num2;
			Random r=new Random();
			num1=r.nextInt(10);
			num2=r.nextInt(10);
			double[] output1=a1.feedForward(new double[]{num1,num2});
			int result=(int) (output1[0]*10);	
			if(num1*num2==result){
				System.out.println("\n"+num1+" * "+num2+" = "+result);
				a1.addRoundPoints(1);
			}else{
				System.out.print(result+",");
			}
		}
	}

	@Override
	public void setCurrentAIs(int... id) {
		AI a=g.getCurrentAIAt(currentAIs[0]);
		a.setActive(false);
		
		a=g.getCurrentAIAt(id[0]);	
		a.setX(300);
		a.setY(250);
		a.setAngle(0);
		a.setFov(15);
		a.setActive(true);
		a.setRoundPoints(0);
		
		currentAIs=id;
		
		items=new ArrayList<Item>();
	}
}
