package norc.pgrd;

import norc.SAS;
import norc.State;

/**
 * DifferentiableRewardFunction is a parameterized reward function for use in
 * gradient reward design methods e.g. PGRD
 * 
 * It is a RewardFunction (has getReward(state,action,state)
 * It is a DifferentiableFunction1D because it is a scalar valued function and has 
 *     an evaluate function to get reward and reward gradient
 * @author jeshua
 *
 */
public interface DifferentiableRFunction<TState extends State> 
extends RewardFunction<TState>, DifferentiableFunction1D<SAS<TState>>{	
	/**
	 * Compute gradient of the reward function w.r.t. parameters theta for a given (s,a,s) triple.
	 * @param state1
	 * @param action
	 * @param state2
	 * @return numParams length vector
	 */
    public double[] getGradR(TState state1, int action, TState state2);
      
}
