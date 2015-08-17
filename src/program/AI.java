package program;

import java.util.Random;


public class AI extends Entity{

	private Neuron[][] n;
	private final String name;
	private int points,roundPoints,id;
	private boolean master;
	private double error,recentAverageError,recentAverageSmoothingFactor;
	
	
	public AI(Neuron[][] n, String name, int id){
		super();
		this.n=n;
		this.name=name;
		master=false;
		points=0;
		roundPoints=0;
		this.id=id;
	}
	
	public AI(int[] neuronSets, String name, int id, boolean randomWeights){
		super();
		this.name=name;
		master=false;
		points=0;
		roundPoints=0;
		this.id=id;
		
		Random r=new Random();
		double[] weights;
		n=new Neuron[neuronSets.length-1][];
		for(int k=0;k<n.length;k++){
			n[k]=new Neuron[neuronSets[k+1]];
			weights=new double[neuronSets[k]];	
			for(int l=0;l<n[k].length;l++){
				for(int g=0;g<weights.length;g++){
					if(randomWeights){
						weights[g]=r.nextDouble()*2-1;
					}else{
						weights[g]=1.0;
					}
				}
				n[k][l]=new Neuron(weights,r.nextDouble()*2-1,r.nextDouble()*2-1);
			}
		}
		
	}
	
	public int calcNumOfNeurons(){
		if(n==null){
			return 0;
		}
		int sum=0;
		for(Neuron[] nn:n){
			sum+=nn.length;
		}
		return sum; 
	}
	
	public void addPoints(int points){
		this.points+=points;
	}
	
	public void addRoundPoints(int roundPoints){
		this.roundPoints+=roundPoints;
	}
	
	public void setMaster(boolean master){
		this.master=master;
	}
	
	public void setPoints(int points) {
		this.points=points;
	}
	
	public void setRoundPoints(int roundPoints) {
		this.roundPoints=roundPoints;
	}	
	
	public Neuron[][] getN(){
		return n;
	}
	
	public int getPoints(){
		return points;
	}
	
	public int getRoundPoints(){
		return roundPoints;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean isMaster() {
		return master;
	}
	
	public int getRadius(){
		return Entity.AI_RAD;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int newId) {
		id=newId;
	}
	
	public AI copy(int id,int genCounter){
		Neuron[][] nNew=new Neuron[n.length][];
		for(int i=0;i<nNew.length;i++){
			nNew[i]=new Neuron[n[i].length];
			for(int k=0;k<nNew[i].length;k++){
				nNew[i][k]=n[i][k].copy();
			}
		}
		AI a=new AI(nNew,id+"_"+genCounter,id);
		a.setPoints(points);
		return a;
	}
	
	public double[] feedForward(double[] input){
		double[] output=input;
		for(int i=0; i<n.length;i++){
			output=new double[n[i].length];
			for(int k=0; k<n[i].length;k++){
				output[k]=n[i][k].feedForward(input);
			}
			input=output;
		}
		return output;
	}
	
	public void backPropagate(double[] targetOutput,double[] input,boolean printInfo){
		//RMS Root Mean Square Error
		Neuron[] outputSet=n[n.length-1];
		error=0;
		for(int i=0; i<outputSet.length;i++){
			error=targetOutput[i]-outputSet[i].getOutput();
			error*=error;
		}
		error/=outputSet.length;
		error=Math.sqrt(error);
		
		recentAverageError= (recentAverageError*recentAverageSmoothingFactor+error) / (recentAverageSmoothingFactor+1.0F);
		
		for(int i=0; i<outputSet.length;i++){
			outputSet[i].calcOutputGradients(targetOutput[i]);
		}
		
		for(int set=0; set<n.length-1;set++){
			for(int neuron=0; neuron<n[set].length;neuron++){
				n[set][neuron].calcHiddenGradients(n[set+1],neuron);
			}
		}
		
		for(int set=n.length-1; set>=0;set--){
			double[] outputOfPrevSet;
			if(set==0){
				outputOfPrevSet=input;
			}else{
				outputOfPrevSet=new double[n[set-1].length];
				for(int i=0; i<outputOfPrevSet.length;i++){
					outputOfPrevSet[i]=n[set-1][i].getOutput();
				}
			}
			for(int neuron=0; neuron<n[set].length;neuron++){
				n[set][neuron].updateInputWeights(outputOfPrevSet);
			}
		}	
		
		if(printInfo){
			System.out.println("--------------------------------------------------");
			System.out.println("AI: "+getName());
			System.out.println("INPUT: "+input[0]);
			System.out.println("OUTPUT: "+n[n.length-1][0].getOutput()+" "+n[n.length-1][1].getOutput());
			System.out.println("TARGETOUTPUT: "+targetOutput[0]+" "+targetOutput[1]);
			System.out.println("ERROR: "+recentAverageError);
		}
	}
	
	@Override
	public void setRelativePosition(double x, double y, int xMax, int yMax, int xMin, int yMin){
		addX(x);
		addY(y);
		if(getX()-AI_RAD<xMin){
			setX(xMin+AI_RAD);
		}else if(getX()+AI_RAD>xMax){
			setX(xMax-AI_RAD);
		}
		
		if(getY()-AI_RAD<yMin){
			setY(yMin+AI_RAD);
		}else if(getY()+AI_RAD>yMax){
			setY(yMax-AI_RAD);
		}
	}
	
	@Override
	public String toString(){
		return name;
	}

}
