package program;
import gui.MainFrame;

import java.util.ArrayList;

public class FoodSearch extends Simulation{

	public FoodSearch(int xMax, int yMax, int maxRankSize, int numOfAIs,int numOfActiveAIs,int neuronSets[],boolean randomWeights, boolean brainNets,int defaultTrainingRounds,double[][] defaultTargetOutputs,double[][] defaultInputs){
		super(xMax,yMax,maxRankSize,numOfAIs,numOfActiveAIs,neuronSets,randomWeights,brainNets,defaultTrainingRounds,defaultTargetOutputs,defaultInputs);		
	}
	public FoodSearch(int xMax, int yMax) {
		super(xMax, yMax,5,1,1,new int[]{2,10000,3},true,true,50000,new double[][]{
				{1,0,-3},
				{1,0,-3},
				{0,1,3},
				{0,1,3}},new double[][]{
				{1,40},
				{1,40},
				{0,5},
				{0,5}});	
	}

	@Override
	public void evaluate(AI a) {	
		setCurrentAIs(a.getId());
		int maxRounds=1000;
		int countInterval=maxRounds/100;
		MainFrame.mf.setRoundStatusBar(0);
		for(int roundCounter=0;roundCounter<maxRounds;roundCounter++){
			update(false);
			if(roundCounter%countInterval==0){
				MainFrame.mf.addRoundStatusBar(1);
			}
		}	
		MainFrame.mf.setRoundStatusBar(100);
			
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
			boolean seesMeal;
			
			AI a=g.getCurrentAIAt(currentAIs[0]);
			seesMeal=false;				
			Item item;
			for(int m=0; m<items.size();m++){
				item=items.get(m);
				if(item.isTerminated()){
					items.remove(item);
					m--;
				}else{
					if(inFov(a,item)){
						seesMeal=true;
					}
					if(collision(a,item)){
						a.addRoundPoints(10);
						items.remove(item);
						m--;
					}		
				}
			}
			
			input=new double[]{seesMeal?1:0,a.getFov()};
			output=a.feedForward(input);
			
			
			/*boolean movingTowards=false;
			if(output[0]>output[1]){
				a.moveForward(xMax,yMax,0,0,5);
				//movingTowards=seesMeal;
				//if(!movingTowards){
				//	a.addRoundPoints(-100);
				//}
			}else{
				a.addAngle(5);
				//if(movingTowards && output[2]%2==0){
				//	movingTowards=true;
				//}else{
				//	movingTowards=false;
				//}
			}*/
			a.moveForward(xMax,yMax,0,0,output[0]*3);
			a.addAngle((int)Math.round(output[1]*3));
			a.addFov((int)Math.round(output[2]*3));
			
			/*if(movingTowards){
				if(a.getRoundPoints()>9900){
					if(a.getRoundPoints()<10000){
						a.setRoundPoints(10000);
					}
				}else{
					a.addRoundPoints(100);
				}
			}else if(!movingTowards && seesMeal){
				a.addRoundPoints(-100);
			}*/
		}
	}

	@Override
	public void setCurrentAIs(int... id) {
		AI a=g.getCurrentAIAt(currentAIs[0]);
		a.setActive(false);
		a=g.getCurrentAIAt(id[0]);
		a.setActive(true);
		a.setX(500);
		a.setY(250);
		a.setAngle(90);
		a.setFov(15);
		a.setRoundPoints(0);
		
		currentAIs=id;
		
		items=new ArrayList<Item>();
		for(int k1=0;k1<4;k1++){
			for(int k2=0;k2<5;k2++){
				items.add(new Item(200+k1*200,50+k2*100,0,true));
			}
		}
	}
	
}
