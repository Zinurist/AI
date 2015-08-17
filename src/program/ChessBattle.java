package program;

import gui.MainFrame;

import java.util.ArrayList;

import chess.game.Chess;
import chess.gui.ChessFrame;

public class ChessBattle extends Simulation {

	public ChessBattle(int xMax, int yMax, int maxRankSize, int numOfAIs,int numOfActiveAIs,int neuronSets[],boolean randomWeights, boolean brainNets,int defaultTrainingRounds,double[][] defaultTargetOutputs,double[][] defaultInputs){
		super(xMax,yMax,maxRankSize,numOfAIs,numOfActiveAIs,neuronSets,randomWeights,brainNets,defaultTrainingRounds,defaultTargetOutputs,defaultInputs);	
		chess=new Chess();
		cf=new ChessFrame(chess);
	}
	public ChessBattle(int xMax, int yMax) {
		//super(xMax, yMax, 10, 15,2,new int[]{4,5,5,5,5},true,60000,new double[][]{{0,0,0,1,0},{0,0,0,1,0},{1,0,0,1,0},{1,0,0,1,0},{0,1,0,0,0},{0,1,0,0,0},{1,0,1,0,0}},new double[][]{{1,0,15,1},{1,0,15,0},{1,0,15,1},{1,0,15,0},{0,1,15,0},{0,1,15,1},{0,0,15,1}});		
		super(xMax, yMax, 10, 5,2,new int[]{66,200,200,200,200,200,200,4},true,false,60000,null,null);		
		
		//TODO read move log and save to training stuff
		try{
			Save s=new Save("log.txt");
			s.openScanner();
			int rows=s.getNumberOfRows();
			
			double[][] tout=new double[20*rows][4];
			double[][] tin=new double[20*rows][66];
			String line;
			String[] nums;
			Chess c;
			for(int i=0; i<rows;i++){
				c=new Chess();
				line=s.readLine(i);
				nums=line.split(" ");
				for(int k=0; k<20;k++){
					
					for(int x=0; x<8; x++){
						for(int y=0; y<8; y++){
							if(k%2==0){
								tin[k+i*20][(x*8+y)]=c.getPieceAt(x,y);
							}else{
								tin[k+i*20][(x*8+y)]=c.getPieceAt(x,7-y);
							}
						}
					}
					
					tout[k+i*20][0]=Integer.parseInt(nums[0+k*4]);
					tout[k+i*20][1]=Integer.parseInt(nums[1+k*4]);
					tout[k+i*20][2]=Integer.parseInt(nums[2+k*4]);
					tout[k+i*20][3]=Integer.parseInt(nums[3+k*4]);
					//System.out.println(tout[k+i*20][0]+" "+tout[k+i*20][1]+" "+tout[k+i*20][2]+" "+tout[k+i*20][3]);
					//System.out.println(c.click((int)tout[k+i*20][0],(int)tout[k+i*20][1])+" "+c.click((int)tout[k+i*20][2],(int)tout[k+i*20][3]));
					c.click((int)tout[k+i*20][0],(int)tout[k+i*20][1]);
					c.click((int)tout[k+i*20][2],(int)tout[k+i*20][3]);
				}
			}
			
			s.closeScanner();
			
			setTargetOutputs(tout);
			setInputs(tin);
			setTrainingPossible(true);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		cf=new ChessFrame(chess);
		cf.setVisible(true);
	}
	
	private ChessFrame cf;
	private Chess chess;
	
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
			
			int maxRounds=20;
			double countInterval=maxRounds/100;
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
		if(!timeMode || timeDif>100){
			timeDif=0;
			AI a1,a2;
			double[] output,input;
			int[] moves;
			int tries,cmState,allowedtries;
			
			a1=g.getCurrentAIAt(currentAIs[0]);
			a2=g.getCurrentAIAt(currentAIs[1]);
			cf.restart(chess);
			
			moves=new int[4];
			allowedtries=10;
			
			
			try {
				if(timeMode){
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//AI 1
			cmState=chess.getCheckMateState();
			tries=0;
			if(cmState<3){
				input=new double[66];
				
				if(cmState==1){
					input[64]=1;
				}
				
				for(int x=0; x<8; x++){
					for(int y=0; y<8; y++){
						input[(x*8+y)]=chess.getPieceAt(x,y);
					}
				}
				
				do{
					input[65]=tries;
					output=a1.feedForward(input);
					tries++;
					for(int i=0; i<4; i++){
						moves[i]=Math.abs((int) Math.round(output[i]))%8;
					}
					//System.out.println("AI 1 Move: "+moves[0]+"-"+moves[1]+" to "+moves[2]+"-"+moves[3]+" -- try "+tries);
				}while(tries<allowedtries && !chess.click(moves[0],moves[1]) && !chess.click(moves[2],moves[3]));
				if(tries==allowedtries){
					System.out.println("AI 1 Move failure!");
				}else{
					a1.addRoundPoints(1);
				}
			}
			
			try {
				if(timeMode){
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//AI 2
			cmState=chess.getCheckMateState();
			tries=0;
			if(cmState<3){
				input=new double[66];
				
				if(cmState==2){
					input[64]=1;
				}
				
				for(int x=0; x<8; x++){
					for(int y=0; y<8; y++){
						input[(x*8+y)]=chess.getPieceAt(x,7-y);
					}
				}
				
				do{
					input[65]=tries;
					output=a2.feedForward(input);
					tries++;
					for(int i=0; i<4; i++){
						moves[i]=Math.abs((int) Math.round(output[i]))%8;
					}
					//System.out.println("AI 2 Move: "+moves[0]+"-"+moves[1]+" to "+moves[2]+"-"+moves[3]+" -- try "+tries);
				}while(tries<allowedtries && !chess.click(moves[0],moves[1]) && !chess.click(moves[2],moves[3]));
				if(tries==allowedtries){
					System.out.println("AI 2 Move failure!");
				}else{
					a1.addRoundPoints(1);
				}
			}
			
			
		}
	}

	@Override
	public void setCurrentAIs(int... id) {
		AI a=g.getCurrentAIAt(currentAIs[0]);
		a.setActive(false);
		a=g.getCurrentAIAt(currentAIs[1]);
		a.setActive(false);
		
		a=g.getCurrentAIAt(id[0]);	
		a.setActive(true);
		a.setRoundPoints(0);
		
		a=g.getCurrentAIAt(id[1]);	
		a.setActive(true);
		a.setRoundPoints(0);
		
		currentAIs=id;
		
		chess=new Chess();
	}
	
}
