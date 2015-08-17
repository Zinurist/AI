package program;

import java.util.Random;

public class BrainNet extends AI {

	/**
	 * By definition:neuronMatrix outputs: 0-(outputs-1) inputs: outputs-(inputs+outputs-1)
	 * also: "to" not "from": neuronMatrix[0][1]=5 -> sending from 0 to 1 5 times the input
	 */
	private double[][] neuronMatrix;
	/**
	 * By definition:inputStorage[neuron][0]=current, inputStorage[neuron][0]=delayed input
	 */
	private double[][] inputStorage;
	private int feedingSteps,numInput,numOutput;
	
	/**
	 * @param feedingSteps steps of feedforward before output read
	 * @param numOfHiddenNeurons
	 * @param numInput
	 * @param numOutput
	 * @param weightRange wieghtRang=3 -> -3 to +3
	 * @param connectionProb probability to create a connection between neurons as percentage
	 */
	public BrainNet(int feedingSteps,int numOfHiddenNeurons, int numInput, int numOutput, double weightRange, int connectionProb, String name, int id){
		super(null,name,id);
		neuronMatrix=new double[numOfHiddenNeurons+numInput+numOutput][numOfHiddenNeurons+numInput+numOutput];
		
		Random r=new Random();
		for(int i=0; i<neuronMatrix.length;i++){
			for(int k=0;k<neuronMatrix.length;k++){
				if(i==k || r.nextInt(100)>connectionProb){
					neuronMatrix[i][k]=0.0D;
				}else{
					neuronMatrix[i][k]=(r.nextDouble()-0.5)*2*weightRange;
				}
			}
		}
		
		inputStorage=new double[neuronMatrix.length][2];
		this.feedingSteps=feedingSteps;
		this.numInput=numInput;
		this.numOutput=numOutput;
		
		//GraphFrame.mf = new GraphFrame(g);
		//GraphFrame.mf.setVisible(true);
		//printBrain();
	}
	
	public BrainNet(int feedingSteps, int numInput, int numOutput, double[][] neuronMatrix, String name, int id){
		super(null,name,id);
		this.neuronMatrix=neuronMatrix;
		this.feedingSteps=feedingSteps;
		this.numInput=numInput;
		this.numOutput=numOutput;
		inputStorage=new double[neuronMatrix.length][2];
	}
	
	@Override
	public BrainNet copy(int id,int genCounter){
		double[][] newMatrix=new double[neuronMatrix.length][neuronMatrix.length];
		for(int i=0;i<newMatrix.length;i++){
			for(int k=0;k<newMatrix.length;k++){
				newMatrix[i][k]=neuronMatrix[i][k];
			}
		}
		BrainNet b=new BrainNet(feedingSteps, numInput, numOutput,newMatrix,id+"_"+genCounter,id);
		b.setPoints(getPoints());
		return b;
	}
	
	@Override
	public int calcNumOfNeurons() {
		if(neuronMatrix==null){
			return 0;
		}
		
		return neuronMatrix.length;
	}
	
	@Override
	public double[] feedForward(double[] input){
		//By definition:neuronMatrix outputs: 0-(outputs-1) inputs: outputs-(inputs+outputs-1)
		for(int i=numOutput; i<numInput+numOutput;i++){
			for(int k=0; k<neuronMatrix[i].length;k++){
				inputStorage[k][0]+=neuronMatrix[i][k]*input[i-numOutput];//input multiplied by the weight and then stored to the inputStorage
			}
		}
		
		double[] output=new double[numOutput];
		double sendingValue;
		for(int step=0; step<feedingSteps;step++){
			for(int neuron=0; neuron<neuronMatrix.length;neuron++){
				sendingValue=Neuron.transferFunction(inputStorage[neuron][0]);
				if(neuron<numOutput){
					//output-neuron: just gives input as transf.
					output[neuron]=sendingValue;
				}
				for(int aim=0; aim<neuronMatrix.length;aim++){
					inputStorage[aim][1]+=neuronMatrix[neuron][aim]*sendingValue;//input multiplied by the weight and then stored to the inputStorage
				}
			}
			//push input for next step
			for(int neuron=0; neuron<neuronMatrix.length;neuron++){
				inputStorage[neuron][0]=inputStorage[neuron][1];
				inputStorage[neuron][1]=0;
			}
		}
		return output;
	}
	
	public int getNumOutput(){
		return numOutput;
	}
	
	public int getNumInput(){
		return numInput;
	}
	
	public double[][] getNeuronMatrix(){
		return neuronMatrix;
	}
	
	public void setNeuronMatrix(double[][] neuronMatrix){
		this.neuronMatrix=neuronMatrix;
	}
	
	/**
	 * Overwritten to reset inputStorage
	 */
	@Override
	public void setActive(boolean active){
		this.active=active;
		inputStorage=new double[neuronMatrix.length][2];
	}
	
	@Override
	public void backPropagate(double[] targetOutput,double[] input,boolean printInfo){
		System.out.println("Not supported.");
	}
	
	public void printBrain(){
		String msg=getName()+": \nneuronMatrix: \n    ";
		for(int i=0; i<neuronMatrix.length;i++){
			msg+="| "+i+"  ";
		}
		msg+="\n";
		for(int i=0; i<neuronMatrix.length;i++){
			msg+="----------------------------------------------------------------------------------\n";
			msg+="  "+i+" ";
			for(int k=0; k<neuronMatrix.length;k++){
				double d=((int)(neuronMatrix[i][k]*10))/10.0D;
				if(d>=0){
					msg+="| "+d;
				}else{
					msg+="|"+d;
				}
			}
			msg+="\n";
		}
		msg+="\ninputStorage:\nn| 0 | 1 \n";
		for(int i=0; i<neuronMatrix.length;i++){
			msg+=i+"|"+((int)(inputStorage[i][0]*10))/10.0D+"|"+((int)(inputStorage[i][1]*10))/10.0D+"\n";
		}
		msg+="END\n";
		System.out.println(msg);
	}

}
