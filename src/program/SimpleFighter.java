package program;

import gui.MainFrame;

import java.util.ArrayList;

public class SimpleFighter extends Simulation {

	public SimpleFighter(int xMax, int yMax, int maxRankSize, int numOfAIs,int numOfActiveAIs,int neuronSets[],boolean randomWeights, boolean brainNets,int defaultTrainingRounds,double[][] defaultTargetOutputs,double[][] defaultInputs){
		super(xMax,yMax,maxRankSize,numOfAIs,numOfActiveAIs,neuronSets,randomWeights,brainNets,defaultTrainingRounds,defaultTargetOutputs,defaultInputs);		
	}
	public SimpleFighter(int xMax, int yMax) {
		//super(xMax, yMax, 10, 15,2,new int[]{4,5,5,5,5},true,60000,new double[][]{{0,0,0,1,0},{0,0,0,1,0},{1,0,0,1,0},{1,0,0,1,0},{0,1,0,0,0},{0,1,0,0,0},{1,0,1,0,0}},new double[][]{{1,0,15,1},{1,0,15,0},{1,0,15,1},{1,0,15,0},{0,1,15,0},{0,1,15,1},{0,0,15,1}});		
		super(xMax, yMax, 30, 20,2,new int[]{4,50,50,50,100,100,100,50,50,50,5},true,false,60000,new double[][]{{1,0,0,1,-5},{-1,1,0,1,-5},{1,1,0,1,5},{1,1,0,1,5},{1,1,0,1,5},{1,1,0,1,5},{-1,1,0,1,5},{-1,1,0,1,5},{-1,1,0,1,-5},{0,0,0,1,-5},{0,0,0,1,-5},{0,0,0,1,-5}},new double[][]{{1,0,40,1},{1,1,40,0},{0,0,40,0},{0,0,5,0},{0,0,40,1},{0,0,40,1},{0,1,40,1},{0,1,5,1},{1,1,5,1},{1,0,5,1},{1,0,5,0},{1,0,5,1}});		
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
			
			
			if(a1.getRoundPoints()>g.getCurrentAIAt(i).getRoundPoints()){
				a1.addPoints(3);
			}else if(a1.getRoundPoints()==g.getCurrentAIAt(i).getRoundPoints()){
				a1.addPoints(1);
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
		if(!timeMode || timeDif>100){
			timeDif=0;
			AI a1,a2;
			double[] output1,output2;
			boolean seesEnemy,seesBullet1,seesBullet2;
			
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
					item.moveForward(xMax, yMax, 0, 0, 3);
					if(collision(a1,item)){
						a2.addRoundPoints(1);
						items.remove(item);
					}else if(collision(a2,item)){
						a1.addRoundPoints(1);
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
			
			//Input: 1.sees enemy(2), 2.sees bullet(2), 3.fov, 4.fire loaded(2)
			//Output: 1.move forward(2), 2.turn left(2), 3.turn right(3), 4.fire(2), 5.fovchange
			seesEnemy=inFov(a1,a2);
			output1=a1.feedForward(new double[]{seesEnemy?1:0,seesBullet1?1:0,a1.getFov(),fireTimeA1>=10?1:0});
			seesEnemy=inFov(a2,a1);
			output2=a2.feedForward(new double[]{seesEnemy?1:0,seesBullet2?1:0,a2.getFov(),fireTimeA2>=10?1:0});
			
			a1.moveForward(400,yMax,0,0,output1[0]*3);
			a1.addAngle((int)Math.round(output1[1]*10)*3);
			a1.addAngle(-(int)Math.round(output1[2]*10)*3);
			if(output1[3]>0.5 && fireTimeA1>40){
				fire(a1);
				fireTimeA1=-1;
			}
			a1.addFov((int)Math.round(output1[4]*5));
			
			a2.moveForward(xMax,yMax,600,0,output2[0]*3);
			a2.addAngle((int)Math.round(output2[1]*10)*3);
			a2.addAngle(-(int)Math.round(output2[2]*10)*3);
			if(output2[3]>0.5 && fireTimeA2>40){
				fire(a2);
				fireTimeA2=-1;
			}
			a2.addFov((int)Math.round(output2[4]*5));
			
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
