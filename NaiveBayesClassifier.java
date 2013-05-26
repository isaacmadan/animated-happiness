import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class NaiveBayesClassifier {

	private ArrayList<DataVector> trainingData;
	private HashMap<Integer, Integer> priors;
	
	private ArrayList<HashMap<Integer, HashMap<Integer, Integer>>> inputVarCondCounts; //input var # -> input var value -> output var value
	
	public NaiveBayesClassifier(ArrayList<DataVector> trainingData) {
		this.trainingData = trainingData;
		inputVarCondCounts = new ArrayList<HashMap<Integer, HashMap<Integer, Integer>>>();
		priors = new HashMap<Integer, Integer>();
		
		priors.put(0, 0);
		priors.put(1, 0);
		for(int i = 0; i < trainingData.size(); i++) {
			DataVector vec = trainingData.get(i);
			if(vec.getOutputVar() == 0) //prior counts (number of each output variable)
				priors.put(0, priors.get(0) + 1);
			else
				priors.put(1, priors.get(1) + 1);
		}

		for(int i = 0; i < trainingData.get(0).getInputVars().length; i++) { //input var #
			int inputVarCondY0 = 0;
			int inputVarCondY1 = 0;
			for(int j = 0; j < trainingData.size(); j++) { //data vecs
				DataVector vec = trainingData.get(j);
				int inputVar = vec.getInputVars()[i];
				int outputVar = vec.getOutputVar();
				
				HashMap<Integer, HashMap<Integer, Integer>> inputVarValues;
				if(i >= inputVarCondCounts.size()) {
					inputVarValues = new HashMap<Integer, HashMap<Integer, Integer>>();
					
					HashMap<Integer, Integer> condCounts0 = new HashMap<Integer, Integer>();
					condCounts0.put(0, 0);
					condCounts0.put(1, 0);
					
					HashMap<Integer, Integer> condCounts1 = new HashMap<Integer, Integer>();
					condCounts1.put(0, 0);
					condCounts1.put(1, 0);
					
					inputVarValues.put(0, condCounts0);
					inputVarValues.put(1, condCounts1);
					
					inputVarCondCounts.add(i, inputVarValues);
				}
				else {
					inputVarValues = inputVarCondCounts.get(i);
				}
				
				if(inputVarValues.containsKey(inputVar)) {
					HashMap<Integer, Integer> condCounts = inputVarValues.get(inputVar);
					condCounts.put(outputVar, condCounts.get(outputVar) + 1);
				}
			}
		}
		
		/**
		System.out.printf("%.2f \n", (float)inputVarCondCounts.get(0).get(0).get(0)/(float)priors.get(0));
		System.out.printf("%.2f \n", (float)inputVarCondCounts.get(0).get(1).get(1)/(float)priors.get(1));
		System.out.printf("%.2f \n", (float)inputVarCondCounts.get(0).get(1).get(0)/(float)priors.get(0));
		System.out.printf("%.2f \n", (float)inputVarCondCounts.get(0).get(0).get(1)/(float)priors.get(1));
		System.out.printf("%.2f \n", (float)inputVarCondCounts.get(1).get(0).get(0)/(float)priors.get(0));
		System.out.printf("%.2f \n", (float)inputVarCondCounts.get(1).get(1).get(1)/(float)priors.get(1));
		System.out.printf("%.2f \n", (float)inputVarCondCounts.get(1).get(1).get(0)/(float)priors.get(0));
		System.out.printf("%.2f \n", (float)inputVarCondCounts.get(1).get(0).get(1)/(float)priors.get(1));
		
		System.out.println("Priors --> 0: " + priors.get(0) + ",  1: " + priors.get(1));
		**/
	}

	public int[] classify(ArrayList<DataVector> testingData) {
		
		int[] classes = new int[testingData.size()];
		
		for(int i = 0; i < testingData.size(); i++) {
			
			double[] probs = new double[testingData.get(i).getInputVars().length];
			
			int maxClass = 0;
			double maxProb = 0;
			for(int j = 0; j < 2; j++) { //the 2 output vars				
				for(int k = 0; k < testingData.get(i).getInputVars().length; k++) { //each input var in vector
					int numerator = inputVarCondCounts.get(k).get(testingData.get(i).getInputVars()[k]).get(j);
					int denominator = priors.get(j);
					probs[k] = (double)numerator/(double)denominator;
				}
				
				//prob
				double prob = 1;
				for(int x = 0; x < probs.length; x++) {
					prob *= probs[x];
				}
				prob *= (double)priors.get(j)/(double)(testingData.size());
				
				if(prob > maxProb) {
					maxProb = prob;
					maxClass = j;
				}
			}
			classes[i] = maxClass;
		}
		
		return classes;
	}
	
	public static ArrayList<DataVector> parseData(String filename) {
		ArrayList<DataVector> data = new ArrayList<DataVector>();
		int numOutputVars = 0;
		int numDataSets = 0;
		
		BufferedReader reader = null;
		try {
			String line;
			int lineIndex = 0;
			reader = new BufferedReader(new FileReader("data/" + filename));
 
			while ((line = reader.readLine()) != null) {
				if(lineIndex == 0) numOutputVars = Integer.parseInt(line);
				else if(lineIndex == 1) numDataSets = Integer.parseInt(line);
				else {
					String[] tokens = line.split("[ :]");
					
					int outputVar = Integer.parseInt(tokens[tokens.length-1]);
					int[] inputVars = new int[tokens.length-2];
					for(int i = 0; i < tokens.length-2; i++) {
						inputVars[i] = Integer.parseInt(tokens[i]);
					}
					
					DataVector vec = new DataVector(outputVar, inputVars);
					data.add(vec);
				}
				lineIndex++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return data;
	}
	
	public void evaluate(int[] classes, ArrayList<DataVector> testingData) {
		
		int numTested0 = 0;
		int numTested1 = 0;
		
		int numCorrect0 = 0;
		int numCorrect1 = 0;
		
		for(int i = 0; i < testingData.size(); i++) {
			DataVector vec = testingData.get(i);
			if(vec.getOutputVar() == 0)
				numTested0++;
			else
				numTested1++;
			
			if(classes[i] == vec.getOutputVar()) {
				if(vec.getOutputVar() == 0)
					numCorrect0++;
				else
					numCorrect1++;
			}
		}
		
		System.out.println("Class 0: tested " + numTested0 + ", correctly classified " + numCorrect0);
		System.out.println("Class 1: tested " + numTested1 + ", correctly classified " + numCorrect1);
		System.out.println("Overall: tested " + (numTested0+numTested1) + ", correctly classified " + (numCorrect0+numCorrect1));
		System.out.println("Accuracy = " + ((float)(numCorrect0+numCorrect1)/(float)(numTested0+numTested1)));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<DataVector> trainingData = parseData("vote-train.txt");
		NaiveBayesClassifier nbc = new NaiveBayesClassifier(trainingData);
		
		ArrayList<DataVector> testingData = parseData("vote-test.txt");
		int[] classes = nbc.classify(testingData);
		nbc.evaluate(classes, testingData);
	}

}
