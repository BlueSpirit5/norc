package norc.pgrd;

import java.util.Random;

import norc.Agent;
import norc.Simulator;
import norc.State;
import norc.Utils;


public class Agent_PGRDUCT<T extends State> implements Agent<T> {

	private RewardDifferentiableUCT<T> planner;
	private SoftmaxPolicy<T>           policy;
	private OLGARB<T>                  policy_gradient;

	private DifferentiableRFunction<T> rf;
	public DifferentiableRFunction<T> getRF(){return rf;}

	private Random random;	

	/**
	 * Policy Gradient for Reward Design using UCT and OLGARB
	 * @param sim          -- simulator to give to planner
	 * @param rf           -- reward function
	 * @param gamma        -- reward discount factor
	 * @param alpha        -- policy gradient learning rate
	 * @param temperature  -- softmax policy temperature
	 * @param depth        -- uct planning depth
	 * @param trajectories -- uct planning trajectory count
	 */
	public Agent_PGRDUCT(Simulator sim, DifferentiableRFunction<T> rf,
			double alpha, double temperature,
			int trajectories, int depth, 
			double gamma, Random random){
		this.random = random;
		planner         = new RewardDifferentiableUCT<T>(sim, rf, trajectories, depth, gamma, random);
		policy          = new SoftmaxPolicy<T>(planner, temperature);
		policy_gradient = new OLGARB<T>(policy,alpha,gamma,false);	
		this.rf = rf;
	}

	/**
	 * Plans from given state, updates reward parameters, returns chosen action
	 * @param st     -- current state
	 * @param reward -- objective reward sample
	 * @return chosen action
	 */
	public int step(T st1){
		policy.evaluate(st1);
		return Utils.sampleMultinomial(policy.getCurrentPolicy().y,this.random);
	}
	public int step(T st1, int a1, double reward, T st2){
		this.policy_gradient.learn(st1, a1, reward);		
		int new_action;
		if(st2.isAbsorbing()){
			this.policy_gradient.initEpisode();
			new_action = -1;
		} else
			new_action = step(st2);		
		return new_action;
	}
}
