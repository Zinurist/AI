package program;
import java.util.ArrayList;
import java.util.Random;

public class FovTest extends Simulation{

	public FovTest(int xMax, int yMax, int maxRankSize, int numOfAIs,int numOfActiveAIs,int neuronSets[],boolean randomWeights, boolean brainNets,int defaultTrainingRounds,double[][] defaultTargetOutputs,double[][] defaultInputs){
		super(xMax,yMax,maxRankSize,numOfAIs,numOfActiveAIs,neuronSets,randomWeights,brainNets,defaultTrainingRounds,defaultTargetOutputs,defaultInputs);		
	}
	public FovTest(int xMax, int yMax) {
		super(xMax, yMax,1,1,1,new int[]{1,2},true,false,50000,new double[][]{{1,0},{0,1}},new double[][]{{1},{0}});	
	}

	@Override
	public void evaluate(AI a) {	
		setCurrentAIs(a.getId());
		for(int roundCounter=0;roundCounter<0;roundCounter++){
			update(false);
		}	
		a.setPoints(a.getRoundPoints());
		items=new ArrayList<Item>();
	}

	private boolean decreasing=true;
	@Override
	public void update(boolean timeMode) {
		if(timeMode){
			long currentTime=System.currentTimeMillis();
			timeDif+=currentTime-lastTime;
			lastTime=currentTime;
		}
		if(!timeMode || timeDif>40){
			timeDif=0;
			
			AI a=g.getCurrentAIAt(currentAIs[0]);			
			Item item;
			for(int m=0; m<items.size();m++){
				item=items.get(m);
				if(item.isTerminated()){
					items.remove(item);
					m--;
				}else{
					if(inFov(a,item)){
						System.out.println("TRUE: "+item.getX()+" - "+item.getY());
						item.setTerminated(true);
					}	
				}
			}
			
			a.addAngle(2);
			if(decreasing){
				a.addFov(-1);
				decreasing=a.getFov()>5;
			}else{
				a.addFov(1);
				decreasing=!(a.getFov()<40);
			}
			
			if(a.getAngle()==0){
				Random r=new Random();
				for(int i=0;i<10000;i++){
					items.add(new Item(r.nextInt(xMax),r.nextInt(yMax),0,true));
				}
			}			
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
		Random r=new Random();
		for(int i=0;i<10000;i++){
			items.add(new Item(r.nextInt(xMax),r.nextInt(yMax),0,true));
		}
	}
	
}
