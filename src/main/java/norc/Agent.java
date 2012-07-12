package norc;

/**
 * Interface to finite action agent.
 * @author Jeshua Bratman
 */
public interface Agent<T extends State> {
	/**
	 * Take action from start state.
	 * @return chosen action
	 */
	public int step(T st1);
	
	/**
	 * Previous state st1, previous action a1, received reward r
	 * @return
	 */
	public int step(T st1, int a1,  double r, T st2);
}
