package jeshua.rl;

/**
 * Interface to finite action agent.
 * @author Jeshua Bratman
 */
public interface Agent {
	/**
	 * Take action from start state.
	 * @return chosen action
	 */
	public int step(State st1);
	
	/**
	 * Previous state st1, previous action a1, received reward r
	 * @return
	 */
	public int step(State st1, int a1, State st2, double r);
}
