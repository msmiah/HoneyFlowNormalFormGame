package normal_form_game;

import normal_form_solver.FollowerGreedySolver;
import normal_form_solver.FollowerRandomSolver;
import normal_form_solver.StacklebergSolver;
import normal_form_solver.StacklebergSolverTwo;


public class NormalFormGame {

	private double[] realFlowValue = { 1, 1};
	private double[] honeyFlowValue = { 1, 1 };
	private double[] costOfhoneyFlow = { 0.1, 0.1 };
	private int[] numOfRealEachType = { 2, 2};
	private int[] upperBoundOfHoneyFlow = { 2, 3 };

	public double getRealProbability(int index, int numHF) {
		double rNum = getNumberOfRealHost(index);
		return (rNum / (rNum + numHF));

	}

	public double getDefenderUtilty(int index, int hNum) {
		double rVal = -getRealValue(index);
		double hVal = getHFValue(index);
		double cost = - (getHFCost(index) * hNum);
		int rNum = getNumberOfRealHost(index);
		double Px = (double)rNum / (double)(rNum + hNum);
		return ((Px * rVal) + ((1 - Px) * hVal) + cost);
	}

	public double getAttackerUtilty(int index, int hNum) {
		double rVal = getRealValue(index);
		double hVal = -getHFValue(index);
		int rNum = getNumberOfRealHost(index);
		double Px = (double)rNum /(double) (rNum + hNum);
		return ((Px * rVal) + ((1 - Px) * hVal));

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
	public int getRandomBetweenRange(int min, int max){
	    double x = (Math.random()*((max-min)+1))+min;
	    return (int)x;
	}
	

	public static void main(String[] args) {
		NormalFormGame nfGame = new NormalFormGame();
		
		System.out.println("***************************** Stackleberg Solver*******************************");
		StacklebergSolver stSolver = new StacklebergSolver(nfGame);
		stSolver.solveGame();
		stSolver.printtStrategyVars();
		stSolver.printOpponentStrategyVars();
		
		
		System.out.println("****************************** Follower Random Solver ************************");
		FollowerRandomSolver randomSolver = new FollowerRandomSolver(nfGame);
		randomSolver.solveGame();
		randomSolver.printtStrategyVars();
		randomSolver.printOpponentStrategyVars();
		
		
		System.out.println("****************************** Follower Greedy Solver ************************");
		FollowerGreedySolver greedySolver = new FollowerGreedySolver(nfGame);
		greedySolver.solveGame();
		greedySolver.printtStrategyVars();
		greedySolver.printOpponentStrategyVars();

	}

}
