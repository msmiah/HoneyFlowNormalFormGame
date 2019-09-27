package normal_form_game;

import normal_form_solver.StacklebergSolver;

public class NormalFormGame {

	private double[] realFlowValue = { 10, 15 };
	private double[] honeyFlowValue = { 5, 7 };
	private double[] costOfhoneyFlow = { 1, 2 };
	private int[] numOfRealEachType = { 5, 3 };
	private int[] upperBoundOfHoneyFlow = {2, 1};
	/*
	public double getDefenderUtilty(int index, int hNum) {
		double rVal = - getRealValue(index);
		double hVal = getHFValue(index);
		double cost = getHFCost(index);
		int rNum = getNumberOfRealHost(index);
		double Px = rNum / (rNum + hNum);
		return ((Px * rVal) + ((1 - Px) * (hVal - cost)));
	}

	public double getAttackerUtilty(int index, int hNum) {
		double rVal = getRealValue(index);
		double hVal = -getHFValue(index);
		int rNum = getNumberOfRealHost(index);
		double Px = rNum / (rNum + hNum);
		return ((Px * rVal) + ((1 - Px) * hVal));
		
	}
	*/
	
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
		
	}
 
}
