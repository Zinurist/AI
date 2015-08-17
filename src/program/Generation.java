package program;

import gui.MainFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class Generation implements TableModel{

	public static int IMPROVEMENT_COUNT=0;
	public static int MASTER_COUNT=0;
	public static int WINS_COUNT=0;
	private List<AI> AIs,superiors,lastGen;
	private int maxRankSize,genCounter;//TODO fmax
	private List<TableModelListener> tl=new ArrayList<TableModelListener>();
	
	
	/**
	 * neuronSets: num of inputs in index 0, num of neurons in the rest -->neuronSets.length = numOfNeuronSets+1
	 * @param numOfAIs
	 * @param numOfNeuronSets
	 * @param neuronSets
	 * @param cMax
	 * @param cMin
	 */
	public Generation(int maxRankSize,int numOfAIs, int[] neuronSets, boolean randomWeights, boolean brainNets){
		AIs=new ArrayList<AI>(numOfAIs);
		lastGen=new ArrayList<AI>(numOfAIs);
		superiors=new ArrayList<AI>(maxRankSize);
		this.maxRankSize=maxRankSize;
		genCounter=1;
		
		AI a;
		for(int i=0; i<numOfAIs;i++){
			if(brainNets){
				a=new BrainNet(10,neuronSets[1],neuronSets[0],neuronSets[neuronSets.length-1],3,10,i+"_1",i);
			}else{
				a=new AI(neuronSets,i+"_1",i,randomWeights);
			}
			AIs.add(i,a);
			lastGen.add(i, a);
		}
		notifyListeners();
	}
	
	public Generation(List<AI> AIs,List<AI> superiors){
		this.AIs=AIs;
		notifyListeners();
	}
	
	public void setAIs(List<AI> AIs){
		this.AIs=AIs;
		notifyListeners();
	}
	
	public List<AI> getAIs(){
		return AIs;
	}
	
	public List<AI> getLastGen(){
		return lastGen;
	}
	
	public List<AI> getSuperiors(){
		return superiors;
	}
	
	public AI getAIAt(int index){
		return AIs.get(index);
	}
	
	public AI getCurrentAIAt(int index){
		if(index>=AIs.size()){
			return superiors.get(index-AIs.size());
		}else{
			return AIs.get(index);
		}
	}
	
	public AI getSuperiorAIAt(int index){
		return superiors.get(index);
	}

	public int getAIPointsOf(int id){
		return AIs.get(id).getPoints();
	}
	
	public int getGenCounter(){
		return genCounter;
	}
	
	/**
	 * 
	 * @param crossOverType 0: uniforme, 1: cross over
	 * @param pCrossOver only used in uniforme-crossOverType
	 * @param pMutation 1/pMutation is the probability to mutate!!!
	 */
	public void evolve(int crossOverType,int pCrossOver,int pMutation){
		AI a,mate;
		List<AI> newAIs=new ArrayList<AI>(AIs.size());
		Random r=new Random();
		Neuron[][] n;
		
		for(int i=0; i<AIs.size();i++){
			a=AIs.get(i);
			AI newSup;
			for(int index=0;index<maxRankSize;index++){
				
				if(superiors.size()<maxRankSize || a.getPoints()>superiors.get(index).getPoints()){
					newSup=a.copy(i, genCounter);
					superiors.add(index,newSup);
					if(superiors.size()>maxRankSize){
						superiors.remove(superiors.size()-1);
					}
					MainFrame.mf.addLog("New superior: "+newSup.getName()+" with "+newSup.getPoints()+" points.");
					IMPROVEMENT_COUNT++;
					break;
				}
			}
			
			
			if(a.getPoints()<lastGen.get(i).getPoints()){//not enough points to surpass
				newAIs.add(i, lastGen.get(i).copy(i,genCounter+1));
			}else{
				newAIs.add(i,a.copy(i,genCounter+1));
			}
		}
		for(int i=0; i<superiors.size();i++){
			superiors.get(i).setId(AIs.size()+i);
		}
		
		
		for(int index=0; index<newAIs.size();index++){
			a=newAIs.get(index);
			n=a.getN();
			if(r.nextBoolean()){//decide if mate is from superiors or from current ais
				do{
					mate=superiors.get(r.nextInt(superiors.size()));
				}while(mate.getName().equals(a.getName()));
			}else{
				do{
					mate=AIs.get(r.nextInt(AIs.size()));
				}while(mate.getName().equals(a.getName()));
			}
			
			//mating:
			if(crossOverType==0){
				for(int i=0; i<n.length; i++){
					for(int k=0; k<n[i].length;k++){
						if(r.nextInt(100)<=pCrossOver){
							n[i][k]=mate.getN()[i][k].copy();
						}
					}
				}
			}else if(crossOverType==1){
				int breakSet,breakNeuron;
				do{
					breakSet=r.nextInt(n.length);
					breakNeuron=r.nextInt(n.length);
				}while((breakSet==0 && breakNeuron==0) || (breakSet==n.length-1 && breakNeuron==n[n.length-1].length-1));
				
				for(int i=breakSet; i<n.length; i++){
					for(int k=0; k<n[i].length;k++){
						if(i==breakSet){
							k=breakNeuron;
						}
						n[i][k]=mate.getN()[i][k].copy();
					}
				}
			}else{
				int breakSet1,breakNeuron1,breakSet2,breakNeuron2;
				do{
					breakSet1=r.nextInt(n.length);
					breakNeuron1=r.nextInt(n.length);
				}while((breakSet1==0 && breakNeuron1==0) || (breakSet1==n.length-1 && breakNeuron1==n[n.length-1].length-1));
				
				do{
					breakSet2=r.nextInt(n.length);
					breakNeuron2=r.nextInt(n.length);
				}while((breakSet2==breakSet1 && breakNeuron2==breakSet1) || (breakSet2==0 && breakNeuron2==0) || (breakSet2==n.length-1 && breakNeuron2==n[n.length-1].length-1));
				int num;
				if(breakSet1>breakSet2){
					num=breakSet1;
					breakSet1=breakSet2;
					breakSet2=num;
				}else if(breakSet1==breakSet2 && breakNeuron1>breakNeuron2){
					num=breakNeuron1;
					breakNeuron1=breakNeuron2;
					breakNeuron2=num;
				}
				
				for(int i=breakSet1; i<=breakSet2; i++){// "<=" !!
					for(int k=0; k<n[i].length;k++){
						if(i==breakSet1){
							k=breakNeuron1;
						}
						n[i][k]=mate.getN()[i][k].copy();
						if(k==breakNeuron2){
							break;
						}
					}
				}
			}
			
			//mutating:
			double[] weights;
			for(int i=0; i<n.length; i++){
				for(int k=0; k<n[i].length;k++){
					if(r.nextInt(pMutation)==0){
						weights=n[i][k].getWeights();
						for(int g=0;g<weights.length;g++){
							weights[g]=r.nextDouble();
						}
					}
				}
			}
			a.setPoints(0);
		}
		lastGen=AIs;
		AIs=newAIs;
		genCounter++;
		//end
	}

	public void nextGen() {
		List<AI> newAIs=new ArrayList<AI>(AIs.size());
		for(int i=0; i<AIs.size();i++){
			AI a=AIs.get(i);
			AI superior=null;
			for(int index=0;index<maxRankSize;index++){	
				boolean listNotFull=false;
				try{
					superior=superiors.get(index);
				}catch(Exception e){
					listNotFull=true;
				}
				
				if(listNotFull || a.getPoints()>superior.getPoints()){
					superiors.add(index,a);
					if(superiors.size()>maxRankSize){
						superiors.remove(superiors.size()-1);
					}
					MainFrame.mf.addLog("New superior: "+a.getName()+" with "+a.getPoints()+" points.");
					IMPROVEMENT_COUNT++;
					break;
				}
			}
			
			AI newAI=a.copy(i, genCounter+1);
			newAI.setPoints(0);
			newAIs.add(newAI);	
		}
		for(int i=0; i<superiors.size();i++){
			superiors.get(i).setId(AIs.size()+i);
		}
		
		lastGen=AIs;
		AIs=newAIs;
		genCounter++;
	}

	public void notifyListeners(){
		for(TableModelListener l: tl){
			l.tableChanged(new TableModelEvent(this));
		}
	}
	
	@Override
	public void addTableModelListener(TableModelListener t) {
		tl.add(t);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		switch(column){
		case 0:
			return String.class;
		case 1:
			return Integer.class;
		default:
			return null;
		}
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		switch(column){
		case 0:
			return "AI name";
		case 1:
			return "Points";
		default:
			return null;
		}
	}

	@Override
	public int getRowCount() {
		return AIs.size()+superiors.size();
	}

	@Override
	public Object getValueAt(int r, int c) {
		if(r<AIs.size()+superiors.size()){
			boolean sup= r >= AIs.size();
			switch(c){
			case 0:
				if(sup){
					return superiors.get(r-AIs.size()).getName()+" (Superior)";
				}else{
					return AIs.get(r).getName();
				}
			case 1:	
				if(sup){
					return superiors.get(r-AIs.size()).getPoints();
				}else{
					return AIs.get(r).getPoints();
				}
			default:
				return "error";
			}
		}else{
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener t) {
		tl.remove(t);
	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {}

	public boolean convertToBrainNet() {
		// TODO
		return false;
	}
	
}
