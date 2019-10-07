package normal_form_solver;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;
import normal_form_game.NormalFormGame;

import java.io.NotActiveException;
import java.util.ArrayList;

import Utils.Utils;

public class StacklebergSolverTwo {

	IloCplex cplex;
	IloLinearNumExpr objective;
	NormalFormGame mNFG;
	ArrayList<IloNumVar>[] strategyVars;
	IloNumVar[] opponentStrategyVars;
	ArrayList<IloNumVar>[] zVars;
	double valueOfGame;
	int player1 = 1;
	int player2 = 2;

	public StacklebergSolverTwo(NormalFormGame nfg) {
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
		zVars = new ArrayList[Utils.ATTACKER_ACTIONS];
		for (int i = 0; i < Utils.TOTAL_TYPE_OF_VULNERABILITIES; i++) {
			strategyVars[i] = new ArrayList<IloNumVar>();
		}
		for (int i = 0; i < Utils.ATTACKER_ACTIONS; i++) {
			zVars[i] = new ArrayList<IloNumVar>();
		}
		opponentStrategyVars = new IloNumVar[Utils.ATTACKER_ACTIONS];
		createVariablesAndConstraints();
		setObjectiveParams();
		setBrConstraints();
		SetObjective();

	}

	public IloNumVar linearization(IloNumVar var1, IloNumVar var2) throws IloException {

		IloLinearNumExpr lz1 = cplex.linearNumExpr();
		IloLinearNumExpr lz2 = cplex.linearNumExpr();
		IloLinearNumExpr lz3 = cplex.linearNumExpr();
		IloNumVar z = cplex.numVar(0, 1, var1 + " -> " + var2);
		/* Z-A+x<=1 */
		lz1.addTerm(1, z);
		lz1.addTerm(-1, var1);
		cplex.addLe(lz1, 0, "lZ1" + var1 + var2);

		/* z- x <= 0 */
		lz2.addTerm(1, z);
		lz2.addTerm(-1, var2);
		cplex.addLe(lz2, 0, "lZ1" + var1 + var2);
		/* A+x-Z <= 1 */
		lz3.addTerm(-1, z);
		lz3.addTerm(1, var1);
		lz3.addTerm(1, var2);
		cplex.addLe(lz3, 1, "TLZ3" + var1+var2);
		return z;
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
		IloLinearNumExpr sumA_i = cplex.linearNumExpr();
		for (int i = 0; i < Utils.ATTACKER_ACTIONS; i++) {
			IloNumVar v = cplex.numVar(0, 1, IloNumVarType.Bool, "a_" + i);
			sumA_i.addTerm(1, v);
			opponentStrategyVars[i] = v;
		}
		cplex.addEq(sumA_i, 1, "Attacker strategies");

	}

	public void setObjectiveParams() throws IloException {
		int noAttackAction = opponentStrategyVars.length - 1;
		for (int i = 0; i < opponentStrategyVars.length - 1; i++) {
			double rVal = mNFG.getRealValue(i);
			double hVal = mNFG.getHFValue(i);
			objective.addTerm(hVal, opponentStrategyVars[i]);
			for (int k = 0; k < strategyVars[i].size(); k++) {
				double totVal = -(rVal + hVal);
				double pr = mNFG.getRealProbability(i, k);
				double value = (totVal * pr)- (mNFG.getHFCost(i) * k);
				IloNumVar zVar = linearization(strategyVars[i].get(k), opponentStrategyVars[i]);
				objective.addTerm(value, zVar);
				zVars[i].add(zVar);

			}
		}

		for (int i = 0; i < Utils.TOTAL_TYPE_OF_VULNERABILITIES; i++) {
			for (int k = 0; k < strategyVars[i].size(); k++) {
				double value = -(mNFG.getHFCost(i) * k);
				IloNumVar zVar = linearization(strategyVars[i].get(k), opponentStrategyVars[noAttackAction]);
				objective.addTerm(value, zVar);
				zVars[noAttackAction].add(zVar);
			}
		}
	}

	public void setBrConstraints() throws IloException {

		IloLinearNumExpr lhs = cplex.linearNumExpr();
		lhs.addTerm(0, opponentStrategyVars[opponentStrategyVars.length - 1]);
		for (int i = 0; i < opponentStrategyVars.length - 1; i++) {
			double rVal = mNFG.getRealValue(i);
			double hVal = mNFG.getHFValue(i);
			double tmp = hVal * (-1);
			lhs.addTerm(tmp, opponentStrategyVars[i]);
			for (int k = 0; k < zVars[i].size(); k++) {
				double totVal = (rVal + hVal);
				double pr = mNFG.getRealProbability(i, k);
				double value = (totVal * pr);
				lhs.addTerm(value, zVars[i].get(k));

			}
		}
		cplex.addGe(lhs, 0, "BR-No-attack");
		for (int i = 0; i < strategyVars.length; i++) {
			IloLinearNumExpr rhs = cplex.linearNumExpr();
			double rVal = mNFG.getRealValue(i);
			double hVal = mNFG.getHFValue(i);
			double tmp = hVal * (-1);
			rhs.setConstant(tmp);
			for (int k = 0; k < zVars[i].size(); k++) {
				double totVal = (rVal + hVal);
				double pr = mNFG.getRealProbability(i, k);
				double value = (totVal * pr);
				rhs.addTerm(value, strategyVars[i].get(k));

			}
			cplex.addGe(lhs, rhs, "BR" + i);
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
		for (IloNumVar v : opponentStrategyVars) {
			try {
				if (null != v)
					System.out.println(v.getName() + ": \t" + cplex.getValue(v));
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (IloException e) {
				e.printStackTrace();
			}
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
