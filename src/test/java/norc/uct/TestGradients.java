package norc.uct;

import static org.junit.Assert.*;

import java.util.Random;


import norc.domains.demo.DemoSim;
import norc.domains.demo.DemoState;
import norc.pgrd.RewardDifferentiableUCT;
import norc.pgrd.SoftmaxPolicy;
import norc.pgrd.ValidateGradient;
import norc.pgrd.demo.DemoQFunction;
import norc.pgrd.demo.DemoRFunction;

import org.junit.Test;

public class TestGradients {

	@Test
	public void testDemoRFunctionGradient() {
		DemoRFunction rf = new DemoRFunction();
		assertTrue(ValidateGradient.validate(rf));		
	}
	@Test
	public void testDemoQFunctionGradient() {
	  DemoQFunction qf = new DemoQFunction();
	  assertTrue(ValidateGradient.validate(qf));	    
	}
	@Test
	public void testSoftmaxGradient() {
	  DemoQFunction qf = new DemoQFunction();
	  SoftmaxPolicy<DemoState> policy = new SoftmaxPolicy<DemoState>(qf, 10);
	  assertTrue(ValidateGradient.validate(policy));      
	}
	
	@Test
	public void testUCTGradient() {
		Random rand = new Random(142);		
		DemoSim sim = new DemoSim(rand);
		DemoRFunction rf = new DemoRFunction();
		int trajectories = 1000;	
		for(int depth = 1; depth<3; depth++){
			System.out.println("Depth: "+depth);
			double gamma = .95;	
			RewardDifferentiableUCT<DemoState> planner = 
					new RewardDifferentiableUCT<DemoState> (sim, rf, trajectories, depth, gamma, rand);			
			assertTrue(ValidateGradient.validate(planner,.01,rand));
		}
	}
	
	@Test
  public void testPGRDGradient() {
    Random rand = new Random(43243);   
    DemoSim sim = new DemoSim(rand);
    DemoRFunction rf = new DemoRFunction();
    int trajectories = 1000;   
    int depth = 1;
    double temperature = .02;
    double gamma = .95; 
    RewardDifferentiableUCT<DemoState>  planner = 
        new RewardDifferentiableUCT<DemoState> (sim, rf, trajectories, depth, gamma, rand);
    SoftmaxPolicy<DemoState>  policy = new SoftmaxPolicy<DemoState> (planner, temperature);
    assertTrue(ValidateGradient.validate(policy,0.001,rand));    
  }
}
