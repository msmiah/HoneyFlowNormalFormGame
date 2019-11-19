package normal_form_solver;

import java.util.Arrays;

import Utils.Utils;
import normal_form_game.NormalFormGame;

public class DefenderNoDeceptionStrategy {
	double[] defenderPayoff;
	int idx;
	NormalFormGame nfg;
	public DefenderNoDeceptionStrategy(NormalFormGame nfg) {
		defenderPayoff = new double[Utils.ATTACKER_ACTIONS];
		this.nfg = nfg;
		init();
		
	}
	public void init() {
		double[] rValues =  nfg.getRealValue();
		for(int i = 0; i<rValues.length; i++) {
			defenderPayoff[i] = rValues[i];
		}
		//idx = getRandomBetweenRange(0, Utils.ATTACKER_ACTIONS-1);
		 Arrays.sort(defenderPayoff); 
	}
	public int getRandomBetweenRange(int min, int max){
	    double x = (Math.random()*((max-min)+1))+min;
	    return (int)x;
	}
	
	public double getGameValue() {
		return -defenderPayoff[Utils.ATTACKER_ACTIONS-1];
	}

}
