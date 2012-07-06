package jeshua.rl.pgrd.demo;

import java.util.Random;

import jeshua.rl.SimpleDriver;
import jeshua.rl.State;
import jeshua.rl.pgrd.OLGARB_Agent;
import jeshua.rl.pgrd.RewardFunction;
import jeshua.rl.uct.demo.*;

/**
 * Runs OLGARB on simple maze.
 * @author Jeshua Bratman
 */
public class DemoOLGARB {
	public static void main(String[] args) throws InterruptedException {
		Random rand1 = new Random();
		int sz = 6;
		Maze maze = new Maze(Maze.randomMaze(sz, sz, rand1));   
		maze.setCell(sz-1, sz-1, Maze.G);
		DemoSim.maze = maze;		
		DemoSim simReal = new DemoSim(rand1);
	
		
		// simulator for planning
		Random rand2 = new Random();
		double alpha = .001;
		double temperature = .1;
		double gamma = .95;
		//OLGARB with a tabular Q function is simply a tabular policy gradient
		DemoQFunction qf = new DemoQFunction();
		final OLGARB_Agent agent = new OLGARB_Agent(qf,alpha,temperature,gamma,true,rand2);				
		class qrf implements RewardFunction{
			public double getReward(State st1, int action, State st2){
				DemoState st = ((DemoState)st2);
				double[] Q = agent.getQF().evaluate(st).y;
				double max = Double.NEGATIVE_INFINITY;
				for(int i=0;i<DemoSim.num_actions;i++)
					if(Q[i] > max)
						max = Q[i];
				return max;
								
			}
		}
				
		DemoVisualizeR p = new DemoVisualizeR(DemoSim.maze,new qrf());
		SimpleDriver driver = new SimpleDriver(simReal,agent);
		for (int timestep = 0; timestep < 2000000; timestep++) {
			System.out.println(timestep);
			driver.step();			
			DemoState curr_state = (DemoState)driver.curr_state;
			if(timestep > 10000 || ((timestep%30)==0))
			{
				p.redraw(curr_state.x, curr_state.y);
				Thread.sleep(10);
			}
		}

	}
}
