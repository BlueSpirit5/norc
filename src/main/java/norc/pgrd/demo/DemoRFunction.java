package norc.pgrd.demo;

import java.util.Random;

import norc.SAS;
import norc.State;
import norc.domains.demo.DemoSim;
import norc.domains.demo.DemoState;
import norc.pgrd.DifferentiableRFunction;


/**
 * Very simple reward function:
 *   Provides reward R(s1,a,s2) = objective_reward(s2) + theta[s2]
 *   
 *   Notice, with 1 step of planning this allows the reward function
 *   to encode a complete policy. 
 * @author jeshua
 *
 */
public class DemoRFunction implements DifferentiableRFunction<DemoState> {
	private int num_params;
	private double[] theta;
	private double[] dtheta;
	
	public DemoRFunction(){
		this.num_params = DemoSim.maze.width() * DemoSim.maze.height();
		this.theta = new double[num_params];
		this.dtheta = new double[num_params];
	}
	
	/**
	 * Reward is theta[s2] + objective_reward
	 */
	@Override
	public double getReward(State s1, int a, State s2) {
		DemoState s = (DemoState)s2;
		double objective_reward = DemoSim.getReward(s);
		int t = s.y * DemoSim.maze.width() + s.x;		
		return this.theta[t] + objective_reward; 
	}

	@Override
	public int numParams() {		
		return num_params;
	}

	@Override
	public void setParams(double[] theta) {
		for(int i=0;i<num_params;i++)
			this.theta[i] = theta[i];
	}

	@Override
	public double[] getParams() {
		return theta;
	}

	
	/**
	 * Gradient is just a vector with a 1 in position for state 2 and
	 * 0's everywhere else
	 */
	@Override
	public double[] getGradR(DemoState state1, int action, DemoState state2) {
		int t = state2.y * DemoSim.maze.width() + state2.x;
		for(int i=0;i<this.num_params;i++)
			this.dtheta[i] = 0;
		this.dtheta[t] = 1;
		return this.dtheta;
	}

	
	@Override
	public OutputAndGradient evaluate(SAS<DemoState> inp) {
		OutputAndGradient ret = new OutputAndGradient();
		ret.y = this.getReward(inp.state1, inp.action, inp.state2);
		ret.dy = this.getGradR(inp.state1, inp.action, inp.state2);
		return ret;
	}
	
	@Override
	public SAS<DemoState> generateRandomInput(Random rand){
		SAS<DemoState> inp = new SAS<DemoState>();
		inp.state1 = new DemoState(rand.nextInt(DemoSim.maze.width()),rand.nextInt(DemoSim.maze.height()));
		inp.action = rand.nextInt(DemoSim.num_actions);
		//random state2
		inp.state2 = new DemoState(rand.nextInt(DemoSim.maze.width()),rand.nextInt(DemoSim.maze.height()));
		return inp;
	}
}
