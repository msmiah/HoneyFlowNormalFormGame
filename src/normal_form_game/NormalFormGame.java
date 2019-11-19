package normal_form_game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import Utils.Utils;
import normal_form_solver.DefenderNoDeceptionStrategy;
import normal_form_solver.DefenderRandomStrategy;
import normal_form_solver.DefenderUniformRandom;
import normal_form_solver.FollowerGreedySolver;
import normal_form_solver.FollowerRandomSolver;
import normal_form_solver.StacklebergSolver;
import normal_form_solver.StacklebergSolverTwo;



public class NormalFormGame {

	private double[] realFlowValue = { 0.5, 0.4, 0.3, 0.4, 0.4};
	private double[] honeyFlowValue = {0.2 , 0.2, 0.1, 0.2, 0.2};
	private double[] costOfhoneyFlow = { 0.01, 0.01, 0.01, 0.01, 0.01};
	private int[] numOfRealEachType = { 10, 10, 10, 10, 10};
	private int[] upperBoundOfHoneyFlow = {50, 50, 50, 50, 10};
	private String mFilename;
	private BufferedWriter mBw = null;
	private File mFile;
	Random random;
	
	public NormalFormGame() {
	
	}
	public double getRealProbability(int index, int numHF) {
		double rNum = getNumberOfRealHost(index);
		return (rNum / (rNum + numHF));

	}

	public double getDefenderUtilty(int index, int hNum) {
		double rVal = -getRealValue(index);
		double hVal = getHFValue(index);
		int rNum = getNumberOfRealHost(index);
		double Px = (double)rNum / (double)(rNum + hNum);
		return ((Px * rVal) + ((1 - Px) * hVal));
	}

	public double getAttackerUtilty(int index, int hNum) {
		double rVal = getRealValue(index);
		double hVal = -getHFValue(index);
		int rNum = getNumberOfRealHost(index);
		double Px = (double)rNum /(double) (rNum + hNum);
		return ((Px * rVal) + ((1 - Px) * hVal));

	}
	public double[] getRealValue() {
		return realFlowValue;
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
	    
	    return random.nextInt(max - min + 1) + min;
	}
	
	public double getDoubleRandomBetweenRange(double min, double max){
		  if (!(min < max)) {
	            throw new IllegalArgumentException("invalid range: [" + min + ", " + max + ")");
	        }
	        return min + uniform() * (max-min);
	}
	
	public  double uniform() {
        return random.nextDouble();
    }
	
	 public void writeToFile() {
		 
	 }

	public static void main(String[] args) throws IOException {
		
		NormalFormGame nfGame = new NormalFormGame();
       // DefenderNoDeceptionStrategy noDecpSovler = new DefenderNoDeceptionStrategy(nfGame);
        //System.out.println("val = " + noDecpSovler.getGameValue());
		//DefenderUniformRandom defUniRand = new DefenderUniformRandom(nfGame);
		//System.out.println( "defVal" +  defUniRand.getGameValue());
		//DefenderRandomStrategy dfRand = new DefenderRandomStrategy(nfGame);
		//System.out.println("Val :" + dfRand.getGameValue());
		
        
		
		String content = "";
		System.out.println("***************************** Stackleberg Solver*******************************");
		StacklebergSolver stSolver = new StacklebergSolver(nfGame);
		stSolver.solveGame();
		System.out.println("***************************** No-Deception Solver*******************************");
	    DefenderNoDeceptionStrategy noDecpSovler = new DefenderNoDeceptionStrategy(nfGame);
	    System.out.println("Defender Utility: " +noDecpSovler.getGameValue());
		System.out.println("***************************** Defender Uniform Random Solver*******************************");
	    DefenderRandomStrategy dfRand = new DefenderRandomStrategy(nfGame);
	    System.out.println("Defender Utility: " + dfRand.getGameValue());
	}	

}
