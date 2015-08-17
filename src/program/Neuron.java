package program;

public class Neuron {

	//eta: network learning rate (0.0: slow, 0.2: med, 1.0: fast), alpha: momentum (0.0: no m, 0.5: moderate m)
	private static double eta=0.2;
	private static double alpha=0.5;
	
	public static double transferFunction(double sum){//output:-1.0 --- 1.0
		return Math.tanh(sum);
	}

	public static double transferFunctionDerivative(double sum){//actual derivative: 1-tanh^2(x)
		return 1-sum*sum;
	}
	
	
	
	protected double[] weights,deltaWeights;
	protected double minToFire,biasInput,gradient,output;	
	
	public Neuron(double[] weights, double biasInput, double minToFire){
		this.weights=weights;
		this.deltaWeights=new double[weights.length];
		this.biasInput=biasInput;
		this.minToFire=minToFire;
	}
	
	public double[] getWeights(){
		return weights;
	}
	
	public double getMinToFire(){
		return minToFire;
	}
	
	public double getBiasInput(){
		return biasInput;
	}
	
	public double getGradient(){
		return gradient;
	}
	
	public double getOutput(){
		return output;
	}
	
	public void setWeights(double[] weights){
		this.weights=weights;
	}
	
	public void setMinToFire(double minToFire){
		this.minToFire=minToFire;
	}
	
	public void setBiasInput(double biasInput){
		this.biasInput=biasInput;
	}
	
	public Neuron copy(){
		double[] newC=new double[weights.length];
		for(int i=0;i<weights.length;i++){
			newC[i]=weights[i];
		}
		return new Neuron(newC,biasInput,minToFire);
	}
	
	public double feedForward(double[] input) {
		if(input.length==weights.length){
			double sum=0;
			for(int i=0; i<input.length;i++){
				sum+=input[i]*weights[i];
			}
			sum+=biasInput;
			output=transferFunction(sum);
			return output;
		}
		return 0;
	}

	public void calcOutputGradients(double targetOutput) {
		gradient=(targetOutput-output) * transferFunctionDerivative(output);
	}
	
	public void calcHiddenGradients(Neuron[] nextSet,int neuronIndex) {
		double sum=0;
		for(int n=0; n<nextSet.length;n++){
			sum+=nextSet[n].getWeights()[neuronIndex];
		}
		
		
		gradient=sum * transferFunctionDerivative(output);
	}

	public void updateInputWeights(double[] outputOfPrevSet) {
		double oldDeltaWeight,newDeltaWeight;
		for(int i=0; i<deltaWeights.length;i++){
			oldDeltaWeight=deltaWeights[i];
			newDeltaWeight=  eta*outputOfPrevSet[i]*gradient  +  alpha*oldDeltaWeight;
			deltaWeights[i]=newDeltaWeight;
			weights[i]+=newDeltaWeight;
		}
		
		
		
	}
}
