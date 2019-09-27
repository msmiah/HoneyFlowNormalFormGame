package normal_form_game;

import normal_form_solver.StacklebergSolver;

public class NormalFormGame {

	private double[] realFlowValue = { 15, 10 };
	private double[] honeyFlowValue = { 15, 10 };
	private double[] costOfhoneyFlow = { 2, 1 };
	private int[] numOfRealEachType = { 3, 15 };
	private int[] upperBoundOfHoneyFlow = {1, 2};
	public double getRealProbability(int index, int numHF) {	
		double rNum = getNumberOfRealHost(index);
		return  (rNum / (rNum + numHF));
		
	}
	public double getRealValue(int i) {
		return realFlowValue[i];
	}
	
	public double getHFValue(int i) {
		return honeyFlowValue[i];
	}

	public double getHFCost(int i) {
		return costOfhoneyFlow[i];
	}
	
	public int getNumberOfRealHost(int i) {
		return numOfRealEachType[i];
	}
	
	public int getUpperBound(int i) {
		return upperBoundOfHoneyFlow[i];
	}
	
	public static void main(String[] args) {
	   NormalFormGame nfGame = new NormalFormGame();
	   StacklebergSolver stSolver = new StacklebergSolver(nfGame);
	   stSolver.solveGame();
	   stSolver.printtStrategyVars();
	   stSolver.printOpponentStrategyVars();
		
	}
 
}
