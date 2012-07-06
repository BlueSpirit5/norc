package jeshua.rl.pgrd;
import jeshua.rl.State;

public interface RewardFunction{
	/**
	 * Get the reward for a given state, action, state triple
	 */
	public double getReward(State s1, int a, State s2);	
}
