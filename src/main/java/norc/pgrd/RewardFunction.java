package norc.pgrd;
import norc.State;

public interface RewardFunction<TState extends State>{
	/**
	 * Get the reward for a given state, action, state triple
	 */
	public double getReward(TState s1, int a, TState s2);	
}
