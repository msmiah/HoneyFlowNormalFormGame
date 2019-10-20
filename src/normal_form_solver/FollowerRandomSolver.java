package normal_form_solver;
import java.util.ArrayList;

import Utils.Utils;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;
import normal_form_game.NormalFormGame;
public class FollowerRandomSolver {


	IloCplex cplex;
	IloLinearNumExpr objective;
	NormalFormGame mNFG;
	ArrayList<IloNumVar>[] strategyVars;
	int[] opponentStrategyVars;
	String[] opponentStrategyNames;
	double valueOfGame;
	int player1 = 1;
	int player2 = 2;

	public FollowerRandomSolver(NormalFormGame nfg) {
		this.mNFG = nfg;
		try {
			cplex = new IloCplex();
			initializeDataStructure();
		} catch (IloException e) {
			System.out.println("Error CPLEX setup failed");
		}
	}

	@SuppressWarnings("unchecked")
	public void initializeDataStructure() throws IloException {
		objective = cplex.linearNumExpr();
		strategyVars = new ArrayList[Utils.TOTAL_TYPE_OF_VULNERABILITIES];
		for (int i = 0; i < Utils.TOTAL_TYPE_OF_VULNERABILITIES; i++) {
			strategyVars[i] = new ArrayList<IloNumVar>();
		}
		opponentStrategyVars = new int[Utils.ATTACKER_ACTIONS];
		opponentStrategyNames = new String[Utils.ATTACKER_ACTIONS];
		setAttackerRandomAction();
		setOpponentActionName();
		createVariablesAndConstraints();
		setObjectiveParams();
		SetObjective();

	}

	public int getRandomBetweenRange(int min, int max){
	    double x = (Math.random()*((max-min)+1))+min;
	    return (int)x;
	}
	public void setAttackerRandomAction() {
		int idx = getRandomBetweenRange(0, Utils.ATTACKER_ACTIONS-1);
		opponentStrategyVars[idx]++;
	}
	
	public void setOpponentActionName() {
		for(int i = 0; i < Utils.ATTACKER_ACTIONS; i++)
		{
			opponentStrategyNames[i] = "a_"+ i +": ";
		}
	}

	public void createVariablesAndConstraints() throws IloException {
		for (int i = 0; i < Utils.TOTAL_TYPE_OF_VULNERABILITIES; i++) {
			IloLinearNumExpr sumX_i = cplex.linearNumExpr();
			for (int j = 0; j <= mNFG.getUpperBound(i); j++) {
				IloNumVar v = cplex.numVar(0, 1, "V_" + i + " : x_" + j);
				strategyVars[i].add(v);
				sumX_i.addTerm(1, v);
			}
			cplex.addEq(sumX_i, 1, "Defender Strategy at V" + i);
		}

	}

	public void setObjectiveParams() throws IloException {
		int noAttackAction = opponentStrategyVars.length - 1;
		for (int i = 0; i < opponentStrategyVars.length - 1; i++) {
			for (int k = 0; k < strategyVars[i].size(); k++) {
				double value = mNFG.getDefenderUtilty(i, k) * opponentStrategyVars[i];
				objective.addTerm(value, strategyVars[i].get(k));
				double cost = -(mNFG.getHFCost(i) * k);
				objective.addTerm(cost, strategyVars[i].get(k));

			}
		}

		for (int i = 0; i < Utils.TOTAL_TYPE_OF_VULNERABILITIES; i++) {
			for (int k = 0; k < strategyVars[i].size(); k++) {
				double value = -(mNFG.getHFCost(i) * k)* opponentStrategyVars[noAttackAction];
				objective.addTerm(value, strategyVars[i].get(k));
			}
		}
	}


	public void solveGame() {
		try {
			if (cplex.solve()) {
				valueOfGame = cplex.getObjValue();
				System.out.println("Defender's utility : " + valueOfGame);
			}

		} catch (IloException e) {
			e.printStackTrace();
			System.out.println("Error SequenceFormLPSolver::solveGame: solve exception");
		}
	}

	private void setCplexParams(double tol) {
		try {
			cplex.setParam(IloCplex.IntParam.RootAlg, IloCplex.Algorithm.Barrier);
			cplex.setParam(IloCplex.DoubleParam.EpOpt, tol);
			cplex.setParam(IloCplex.DoubleParam.BarEpComp, tol);
			cplex.setParam(IloCplex.IntParam.BarCrossAlg, -1);
			cplex.setParam(IloCplex.DoubleParam.TiLim, 1e+75);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	private void SetObjective() throws IloException {
		setCplexParams(1e-6);
		cplex.addMaximize(objective);
	}

	public void printOpponentStrategyVars() {

		System.out.println("..........................Attacker's Strategies.......................");
		for (int i = 0; i < Utils.ATTACKER_ACTIONS; i++) {
			System.out.println(opponentStrategyNames[i] + "   " + opponentStrategyVars[i]);
			
		}
	}

	public void printtStrategyVars() {

		System.out.println("..........................Defender's Strategies.......................");
		for (int i = 0; i < Utils.TOTAL_TYPE_OF_VULNERABILITIES; i++) {
			System.out.println("            ******** Vulnerability Type = "+ i + "   **************");
			for (int j =0; j< strategyVars[i].size(); j++) {
				try {
					IloNumVar v = strategyVars[i].get(j);
					if (null != v)
						System.out.println(v.getName() + ": \t" + cplex.getValue(v));
				} catch (UnknownObjectException e) {
					e.printStackTrace();
				} catch (IloException e) {
					e.printStackTrace();
				}
			}
		}
	}


	

}
