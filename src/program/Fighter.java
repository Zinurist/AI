package program;

import gui.MainFrame;

import java.util.ArrayList;

public class Fighter extends Simulation {

	public Fighter(int xMax, int yMax, int maxRankSize, int numOfAIs,int numOfActiveAIs,int neuronSets[],boolean randomWeights, boolean brainNets,int defaultTrainingRounds,double[][] defaultTargetOutputs,double[][] defaultInputs){
		super(xMax,yMax,maxRankSize,numOfAIs,numOfActiveAIs,neuronSets,randomWeights,brainNets,defaultTrainingRounds,defaultTargetOutputs,defaultInputs);		
	}
	public Fighter(int xMax, int yMax) {
		super(xMax, yMax, 20, 2,2,new int[]{3,10000,4},true,true,60000,new double[][]{{0,0,0,1},{0,0,0,1},{1,0,0,1},{1,0,0,1},{0,1,0,0},{0,1,0,0},{1,0,1,0}},new double[][]{{1,0,1},{1,0,0},{1,0,1},{1,0,0},{0,1,0},{0,1,1},{0,0,1}});		
	}
	
	
	private int fireTimeA1,fireTimeA2;
	
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
				
			//if(a1.getRoundPoints()>g.getCurrentAIAt(i).getRoundPoints()){
			//	a1.addPoints(1);
			//}
			a1.addPoints(a1.getRoundPoints());
			items=new ArrayList<Item>();
		}
		a1.setPoints(a1.getPoints()/numOfAIs);
	}

	@Override
	public void update(boolean timeMode) {
		if(timeMode){
			long currentTime=System.currentTimeMillis();
			timeDif+=currentTime-lastTime;
			lastTime=currentTime;
		}
		if(!timeMode || timeDif>10){
			timeDif=0;
			AI a1,a2;
			double[] output1,output2;
			boolean seesEnemy,seesBullet1,seesBullet2,angleChange1,angleChange2;
			
			a1=g.getCurrentAIAt(currentAIs[0]);
			a2=g.getCurrentAIAt(currentAIs[1]);
			seesBullet1=false;
			seesBullet2=false;
			
			Item item;
			for(int m=0; m<items.size();m++){
				item=items.get(m);
				if(item.isTerminated()){
					items.remove(item);
				}else{
					item.moveForward(xMax, yMax, 0, 0, 1);
					if(collision(a1,item)){
						if(a2.getRoundPoints()>=300){
							a2.addRoundPoints(1000);
						}
						if(a1.getRoundPoints()>300){
							a1.addRoundPoints(-330);
						}else{
							a1.setRoundPoints(0);
						}
						items.remove(item);
					}else if(collision(a2,item)){
						if(a1.getRoundPoints()>=300){
							a1.addRoundPoints(1000);
						}
						if(a2.getRoundPoints()>300){
							a2.addRoundPoints(-330);
						}else{
							a2.setRoundPoints(0);
						}
						items.remove(item);
					}
						
					if(inFov(a1,item)){
						seesBullet1=true;
					}
					if(inFov(a2,item)){
						seesBullet2=true;
					}
				}
			}
			
			//Input: 1.sees enemy(2), 2.sees bullet(2), 3.fire loaded(2)
			//Output: 1.move forward(2), 2.turn right(2), 3.turn left(3), 4.fire(2)
			seesEnemy=inFov(a1,a2);
			output1=a1.feedForward(new double[]{seesEnemy?1:0,seesBullet1?1:0,fireTimeA1>=10?1:0});
			seesEnemy=inFov(a2,a1);
			output2=a2.feedForward(new double[]{seesEnemy?1:0,seesBullet2?1:0,fireTimeA2>=10?1:0});
			
			if(output1[0]%2>0.5){
				a1.moveForward(400,yMax,0,0,1);
				if(a1.getRoundPoints()<100){
					a1.addRoundPoints(1);
				}
			}
			if(output1[1]%2>0.5){
				a1.addAngle(5);
				angleChange1=true;
				if(a1.getRoundPoints()<300){
					a1.addRoundPoints(1);
				}
			}
			if(output1[2]%2>0.5){
				a1.addAngle(-5);
				angleChange1=false;
				if(a1.getRoundPoints()>0){
					a1.addRoundPoints(-1);
				}
			}
			if(output1[3]%2>0.5 && fireTimeA1>40){
				fire(a1);
				fireTimeA1=-1;
			}
			if(output2[0]%2>0.5){
				a2.moveForward(xMax,yMax,600,0,1);
				if(a2.getRoundPoints()<100){
					a2.addRoundPoints(1);
				}
			}
			if(output2[1]%2>0.5){
				a2.addAngle(5);
				angleChange1=true;
				if(a2.getRoundPoints()<300){
					a2.addRoundPoints(1);
				}
			}
			if(output2[2]%2>0.5){
				a2.addAngle(-5);
				angleChange1=false;
				if(a2.getRoundPoints()>0){
					a2.addRoundPoints(-1);
				}
			}
			if(output2[3]%2>0.5 && fireTimeA2>40){
				fire(a2);
				fireTimeA2=-1;
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
		
		a=g.getCurrentAIAt(id[0]);	
		a.setX(300);
		a.setY(250);
		a.setAngle(0);
		a.setFov(15);
		a.setActive(true);
		a.setRoundPoints(0);
		
		a=g.getCurrentAIAt(id[1]);	
		a.setX(700);
		a.setY(250);
		a.setAngle(180);
		a.setFov(15);
		a.setActive(true);
		a.setRoundPoints(0);
		
		currentAIs=id;
		
		items=new ArrayList<Item>();
	}

}
