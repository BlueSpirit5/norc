package norc.uct;
import static org.junit.Assert.*;

import norc.State;
import norc.domains.demo.DemoSim;
import norc.domains.demo.DemoState;
import norc.domains.demo.Maze;
import norc.uct.UCT;

import org.junit.Test;

import java.util.Random;


public class TestUCT {
	final int W = Maze.W;
	final int N = Maze.N;
	final int G = Maze.G;
	final int[][] maze1 = 
		        new int[][]{
		            {0,0,0,0},
		            {N,N,N,0},
		            {0,N,N,N|G}		            
		            };
	Random rand = new Random();
	Maze maze = new Maze(maze1);
	int trajectories = 100000;		
    int depth = 20;
    double thresh = .01;

    

	
	@Test
	public void testUCT() {
		int stepsToComplete = 11;
		
		double gamma = .99; 
		DemoSim.maze = maze;
		DemoSim sim = new DemoSim(rand);
		sim.slip_prob = 0;		
		
		UCT planner = new UCT(sim, trajectories, depth,gamma, rand);
		planner.ucb_scaler = 1;	
				
		State st = new DemoState(0,0);
		stepsToComplete = 10;
		planner.planAndAct(st);
		assertEquals(planner.getQ(2),Math.pow(gamma,stepsToComplete),thresh);
		st = new DemoState(0,2);
		stepsToComplete = 2;
		planner.planAndAct(st);
		assertEquals(planner.getQ(2),Math.pow(gamma,stepsToComplete),thresh);	
		for(int i = 0; i<4;i++)
			System.out.printf("%.4f ",planner.getQ(i));
	}
}
