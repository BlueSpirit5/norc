package jeshua.rl.pgrd;

import java.util.Arrays;
import java.util.Random;

import jeshua.rl.Simulator;
import jeshua.rl.State;
import jeshua.rl.pgrd.DifferentiableRFunction.SASTriple;
import jeshua.rl.uct.UCT;
import jeshua.rl.uct.UCTNodes.*;

public class RewardDifferentiableUCT extends UCT implements DifferentiableQFunction {

  protected double[][] dQdt;
  protected double[][] dRdt;
  protected double[] dQdt_tmp;
  protected int num_params;
  protected DifferentiableRFunction rf;

  /**
   * Construct a UCT planner. 
   * 
   * @param sim          -- Simulator for use in planning
   * @param diffRF       -- Differentiable reward function - contains parameters theta
   * @param trajectories -- Number of sample trajectories in UCT planning
   * @param depth        -- Maximum depth of trajectories
   * @param gamma        -- Discount factor
   * @param random       -- Random number generator for choosing planning actions
   */
  public RewardDifferentiableUCT(Simulator sim, DifferentiableRFunction diffRF, 
		  int trajectories, int depth, double gamma, Random random) {
    super(sim, trajectories, depth, gamma, random);
    this.rf = diffRF;
    this.num_params = diffRF.numParams();
    this.dQdt_tmp = new double[this.num_params];
    this.dQdt = new double[super.numActions][this.num_params];
    this.dRdt = new double[super.maxDepth+1][this.num_params];
  }

  public void setUCBScalar(double c){
	  this.ucb_scaler = c;
  }
  
  
  /**
   * Plan starting from given state and return the Q value and Jacobian
   * 
   * @param state
   * @return OutputAndGradient2d
   *  y = Q(s,*)
   *       estimated state-action value function at state s
   *       |actions| length array
   *  dy = dQ(s,*)/dTheta
   *       Jacobian of the Q function w.r.t. reward function parameters theta
   *       |actions| x |theta| matrix where dQdt[a][i] is dQ(s,a) / dtheta_i
   */
  public OutputAndJacobian evaluate(State state) {
    cache.clearHash();		
    this.rootState = state.copy();
    this.root = cache.checkout(rootState,0);
    for (int i = 0; i < numTrajectories; ++i) {
      simulator.setState(state.copy());
      Arrays.fill(dQdt_tmp, 0);
      plan(state.copy(), root, 0);
    }    
    
    OutputAndJacobian ret = new OutputAndJacobian();
    ret.dy = this.dQdt;
    ret.y = this.root.Q;    
    return ret;
  }


  /**
   * Recursive UCT planning step and gradient calculation.
   */
  protected double plan(State state, UCTStateNode node, int depth) {
    // BASE CASES:
    if (state.isAbsorbing()) {// end of episode
      Arrays.fill(dRdt[depth],0);
      return endEpisodeValue;
    } else if (depth >= maxDepth) {// leaf node
      Arrays.fill(dRdt[depth],0);
      return leafValue;
    }
    // UCT RECURSION:
    else {
      // simulate an action
      int action = getPlanningAction(node);
      simulator.takeAction(action);
      // take snapshot of current state of simulator			
      State state2 = simulator.getState().copy();	 
      double r = this.rf.getReward(state,action,state2);
      UCTStateNode child = node.getChildNode(action, state2,depth+1);
      // calculate Q via recursion
      double q = r + gamma * plan(state2, child, depth + 1);
      
      // update counts
      node.sCount++;
      int sa_count = ++node.saCounts[action];

      /**
       * dQ = 
       *     sum over trajectories t
       *         sum over samples (s_t,a_t,s_{t+1})
       *            \gamma^t dR(s_t,a_t,s_{t+1})
       */

      //get dR from reward function object
      double[] gr = this.rf.getGradR(state, action, state2);
      for(int i=0;i<num_params;i++) dRdt[depth][i] = gr[i];
      //dQ = dR + gamma * dQ
      for(int i = 0; i < dQdt_tmp.length; ++i) dQdt_tmp[i] *= gamma;
      for(int i=0;i<num_params;i++) dQdt_tmp[i] += dRdt[depth][i];			

      //update rolling averages of Q
      double alpha = 1.0/sa_count;
      node.Q[action] += (q - node.Q[action]) * alpha;

      if (depth == 0) {//done with trajectory
        //calculate sample of dQ for this trajectory and update rolling average
        for (int i = 0; i < num_params; ++i)
          dQdt[action][i] += alpha * (dQdt_tmp[i] - dQdt[action][i]);            	
      }
      //return for this trajectory
      return q;
    }
  }

  //======================================================================
  //Differentiable Function Interface

  @Override
  public OutputAndJacobian evaluate(Object state) {
	  return evaluate((State)state);
  }  
  @Override
  public int numParams() {
	  return this.rf.numParams();
  }
  @Override
  public void setParams(double[] theta) {
	  this.rf.setParams(theta);
  }
  @Override
  public double[] getParams() {
	  return this.rf.getParams();
  }
  @Override
  public Object generateRandomInput(Random rand) {
	  SASTriple rt = (SASTriple)rf.generateRandomInput(rand);
	  return rt.state2;
  }
}
