package norc.pgrd.demo;

import java.util.Random;

import norc.SimpleDriver;
import norc.pgrd.PGRD_UCT;
import norc.uct.demo.*;


/**
 * Runs PGRD UCT on simple maze.
 * @author Jeshua Bratman
 */
public class DemoPGRD {
	public static void main(String[] args) throws InterruptedException {
		// real world
		Random rand1 = new Random();
		int sz = 6;
		Maze maze = new Maze(Maze.randomMaze(sz, sz, rand1));   
		maze.setCell(sz-1, sz-1, Maze.G);
		DemoSim.maze = maze;
		DemoSim simReal = new DemoSim(rand1);
		
		// simulator for planning
		Random rand2 = new Random();
		DemoSim simPlan = new DemoSim(rand2);
		DemoRFunction rf = new DemoRFunction();
				
		int trajectories = 100;		
		int depth = 4;
		double alpha = .0001;
		double temperature = .05;
		double gamma = .95;
		PGRD_UCT pgrd = new PGRD_UCT(simPlan,rf,alpha,temperature,trajectories,depth,gamma,rand2);		
		DemoVisualizeR p = new DemoVisualizeR(DemoSim.maze,pgrd.getRF());
		SimpleDriver driver = new SimpleDriver(simReal,pgrd);
		for (int timestep = 0;; timestep++) {
			driver.step();			
			DemoState curr_state = (DemoState)driver.curr_state;
			if(timestep > 10000 || ((timestep%30)==0)){
				p.redraw(curr_state.x, curr_state.y);
				Thread.sleep(10);
			}
		}
	}
}