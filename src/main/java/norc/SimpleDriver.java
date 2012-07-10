package norc;
/**
 * Provides basic functionality for the agent/environment interaction loop.
 * @author Jeshua Bratman
 */
public class SimpleDriver<T extends State> {
	public Simulator sim;
	public Agent<T> ag;
	public T curr_state;
	public T last_state;
	public int last_action;
	
	public SimpleDriver(Simulator sim, Agent<T> ag){
		this.sim = sim;
		this.ag = ag;
		this.curr_state = null;
		this.last_state = null;
		this.last_action = -1;
	}
	
	/**
	 * Take step in environment and provided resulting
	 * state/reward to agent.
	 */
	
	@SuppressWarnings("unchecked")
	public void step(){		
		curr_state = (T)sim.getState().copy();
		double reward = sim.getReward();
		int action = last_action;
		
		if(last_state == null)
			action = ag.step(curr_state);
		else
			action = ag.step(last_state,action,curr_state,reward);
		
		if(!curr_state.isAbsorbing()){
			sim.takeAction(action);
			last_state = curr_state;
			last_action = action;
		} else{
			sim.initEpisode();
			last_state = null;
			last_action = -1;
		}	
	}
}
