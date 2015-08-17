package program;

import gui.MainFrame;

import java.util.ArrayList;

public class Turret extends Simulation{

	public Turret(int xMax, int yMax, int maxRankSize, int numOfAIs,int numOfActiveAIs,int neuronSets[],boolean randomWeights, boolean brainNets,int defaultTrainingRounds,double[][] defaultTargetOutputs,double[][] defaultInputs){
		super(xMax,yMax,maxRankSize,numOfAIs,numOfActiveAIs,neuronSets,randomWeights,brainNets,defaultTrainingRounds,defaultTargetOutputs,defaultInputs);		
	}
	public Turret(int xMax, int yMax){
		super(xMax, yMax, 10, 6, 2, new int[]{3,10,4},true,true,50000,new double[][]{{1,0,1,0},{1,0,0,1},{1,1,0,0},{1,1,0,1}},new double[][]{{0,0,0},{1,0,0},{0,1,0},{1,1,0}});
	}

	private int fireTimeA1,fireTimeA2,roundCounter;
	private boolean clockwise;
	
	@Override
	public void evaluate(AI a1) {	
		for(int i=0;i<getAIs().size();i++){
			if(a1.getId()==i){
				i++;
				if(i>=getAIs().size()){
					break;
				}
			}
			setCurrentAIs(a1.getId(),i);
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
	}

	@Override
	public void update(boolean timeMode) {
		if(timeMode){
			long currentTime=System.currentTimeMillis();
			timeDif+=currentTime-lastTime;
			lastTime=currentTime;
		}
		if(!timeMode || timeDif>50){
			roundCounter+=1;
			timeDif=0;
			AI a1,a2;
			double[] output=new double[3];
			double[] input=new double[1];
			boolean seesBullet;
			
			a1=g.getCurrentAIAt(currentAIs[0]);
			a2=g.getCurrentAIAt(currentAIs[1]);
			seesBullet=false;				
			Item item;
			for(int m=0; m<items.size();m++){
				item=items.get(m);
				if(item.isTerminated()){
					items.remove(item);
					m--;
				}else{
					item.moveForward(xMax, yMax, 0, 0, 5);
					if(inFov(a1,item)){
						seesBullet=true;
					}
					if(collision(a1,item)){
						a1.addRoundPoints(-3);
						items.remove(item);
						m--;
					}else if(collision(a2,item)){
						a1.addRoundPoints(1);
						items.remove(item);
						m--;
					}
				}
			}
			
			//Input: 1. sees enemy, 2. sees bullet
			//Output: 1. move, 2. clockwise-fast, 3.cw-slow aim, 4. shoot
			input=new double[]{inFov(a1,a2)?1:0,seesBullet?1:0,roundCounter};
			output=a1.feedForward(input);
			
			if(output[3]>0.5 && fireTimeA1>10){
				fire(a1);
				fireTimeA1=-1;
			}
			if(output[2]>0.5){
				a1.addAngle(1);
			}
			if(output[1]>0.5){
				a1.addAngle(10);
			}
			if(output[0]>0.5){
				a1.moveForward(400,yMax,0,0,5);
			}			
			
			if(inFov(a2,a1)){
				if(fireTimeA2>10){
					fire(a2);
					fireTimeA2=-1;
				}
			}else if(clockwise){
				a2.addAngle(-1);
				if(a2.getAngle()<=135){
					clockwise=false;
				}
			}else{
				a2.addAngle(1);
				if(a2.getAngle()>=225){
					clockwise=true;
				}
			}
			
			fireTimeA1++;
			fireTimeA2++;
		}
	}

	@Override
	public void setCurrentAIs(int... id) {
		AI a=g.getCurrentAIAt(currentAIs[0]);
		a.setActive(false);
		a=g.getCurrentAIAt(currentAIs[1]);
		a.setActive(false);
		
		fireTimeA1=0;
		fireTimeA2=0;
		roundCounter=0;
		clockwise=true;
		
		a=g.getCurrentAIAt(id[0]);	
		a.setActive(true);
		a.setX(200);
		a.setY(250);
		a.setAngle(0);
		a.setFov(15);
		a.setRoundPoints(0);
		
		a=g.getCurrentAIAt(id[1]);	
		a.setActive(true);
		a.setX(800);
		a.setY(250);
		a.setAngle(180);
		a.setFov(2);
		a.setRoundPoints(0);
		
		currentAIs=id;
		
		items=new ArrayList<Item>();
	}
	
}
