package normal_form_solver;

import java.util.Arrays;

import Utils.Utils;
import normal_form_game.NormalFormGame;

public class DefenderRandomStrategy {
	double[] opponentExValue;
	NormalFormGame nfg;
	public DefenderRandomStrategy(NormalFormGame nfg) {
		this.nfg = nfg;
		opponentExValue  = new double[Utils.ATTACKER_ACTIONS];
		initializedDataStructure();
		
		
	}
	
	private void initializedDataStructure() {
		calculateOpponentPayoff();
	
	}
	
	private void calculateOpponentPayoff() {
		for(int i = 0; i < Utils.TOTAL_TYPE_OF_VULNERABILITIES; i++) {
			int max = nfg.getUpperBound(i);
			opponentExValue[i] = nfg.getAttackerUtilty(i, max);
		}
		 Arrays.sort(opponentExValue); 
	}
	
	private double calculateCost() {
		double cost = 0.0;
		for(int i = 0; i < Utils.TOTAL_TYPE_OF_VULNERABILITIES; i++) {
			int max = nfg.getUpperBound(i);
			cost += (max * nfg.getHFCost(i));
		}
		return cost;		
	}
	
	public double getGameValue() {
		double defExVal = opponentExValue[Utils.ATTACKER_ACTIONS-1] + calculateCost(); 
		return  -defExVal;
	}


}
