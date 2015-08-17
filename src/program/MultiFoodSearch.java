package program;
import gui.MainFrame;

import java.util.ArrayList;

public class MultiFoodSearch extends Simulation{

	public MultiFoodSearch(int xMax, int yMax, int maxRankSize, int numOfAIs,int numOfActiveAIs,int neuronSets[],boolean randomWeights, boolean brainNets,int defaultTrainingRounds,double[][] defaultTargetOutputs,double[][] defaultInputs){
		super(xMax,yMax,maxRankSize,numOfAIs,numOfActiveAIs,neuronSets,randomWeights,brainNets,defaultTrainingRounds,defaultTargetOutputs,defaultInputs);		
	}
	public MultiFoodSearch(int xMax, int yMax) {
		super(xMax, yMax,5,4,4,new int[]{6,5,5,5,5,5,5,5,2},false,false,50000,new double[][]{{1,0},{0,1},{1,0},{0,1},{0,0}},new double[][]{{1,0,1,0,1,0},{0,1,0,1,0,1},{0,1,0,0,1,0},{1,0,1,1,0,1},{0,0,0,0,0,0}});	
	}

	@Override
	public void evaluate(AI a) {
		if(a.getId()==numOfAIs-1){
			setCurrentAIs(null);
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
			
			for(int i=0;i<numOfAIs;i++){
				a=g.getCurrentAIAt(i);
				a.setPoints(a.getRoundPoints());
			}
			items=new ArrayList<Item>();
		}
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
			boolean seesMeal,seesAI;
			int numMeals,numAIs;
			
			for(int i=0;i<numOfAIs;i++){
				AI a=g.getCurrentAIAt(i);
				seesMeal=false;	
				seesAI=false;
				numMeals=0;
				numAIs=0;
				Item item;
				for(int m=0; m<items.size();m++){
					item=items.get(m);
					if(item.isTerminated()){
						items.remove(item);
						m--;
					}else{
						if(inFov(a,item)){
							seesMeal=true;
							numMeals++;
						}
						if(collision(a,item)){
							a.addRoundPoints(10);
							items.remove(item);
							m--;
						}		
					}
				}
				for(int m=0; m<4;m++){
					if(i!=m && inFov(a,g.getCurrentAIAt(m))){
						numAIs++;
						seesAI=true;
					}
				}
				input=new double[]{seesMeal?1:0,seesMeal?0:1,numMeals,seesAI?1:0,seesAI?0:1,numAIs};
				output=a.feedForward(input);
				
				
				boolean movingTowards=false;
				if(output[0]>output[1]){
					a.moveForward(xMax,yMax,0,0,5);
					movingTowards=seesMeal;
					if(!movingTowards){
						a.addRoundPoints(-100);
					}
				}else {
					a.addAngle(5);
					if(movingTowards && output[2]%2==0){
						movingTowards=true;
					}else{
						movingTowards=false;
					}
				}
				if(movingTowards){
					if(a.getRoundPoints()>9900){
						if(a.getRoundPoints()<10000){
							a.setRoundPoints(10000);
						}
					}else{
						a.addRoundPoints(100);
					}
				}else if(!movingTowards && seesMeal){
					a.addRoundPoints(-100);
				}
			}
		}
	}

	@Override
	public void setCurrentAIs(int... id) {
		AI a=g.getCurrentAIAt(0);
		a.setActive(true);
		a.setX(500);
		a.setY(300);
		a.setAngle(90);
		a.setRoundPoints(0);
		
		a=g.getCurrentAIAt(1);
		a.setActive(true);
		a.setX(1000);
		a.setY(300);
		a.setAngle(90);
		a.setRoundPoints(0);
		
		a=g.getCurrentAIAt(2);
		a.setActive(true);
		a.setX(500);
		a.setY(600);
		a.setAngle(90);
		a.setRoundPoints(0);
		
		a=g.getCurrentAIAt(3);
		a.setActive(true);
		a.setX(1000);
		a.setY(600);
		a.setAngle(90);
		a.setRoundPoints(0);
		
		items=new ArrayList<Item>();
		for(int k1=0;k1<12;k1++){
			for(int k2=0;k2<8;k2++){
				items.add(new Item(100+k1*100,50+k2*100,0,true));
			}
		}
	}
	
}
