
public class DataVector {

	private int outputVar;
	private int[] inputVars;
	
	public DataVector(int outputVar, int[] inputVars) {
		this.outputVar = outputVar;
		this.inputVars = inputVars;
	}
	
	public int getOutputVar() {
		return this.outputVar;
	}
	
	public int[] getInputVars() {
		return this.inputVars;
	}
	
	public String toString() {
		String sb = "";
		for(int i = 0; i < inputVars.length; i++) {
			sb += String.valueOf(inputVars[i]) + " ";
		}
		sb += ": " + String.valueOf(outputVar);
		return sb;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
