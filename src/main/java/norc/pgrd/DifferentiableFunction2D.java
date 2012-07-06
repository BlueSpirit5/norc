package norc.pgrd;
import java.util.Random;
/**
 * DifferentiableFunction2D represents a vector-valued function for which we
 * can evaluate the function and Jacobian w.r.t. its parameters theta.
 *
 * Implementing this interface allows the use of gradient validation.
 * 
 * @author Jeshua Bratman
 */
public interface DifferentiableFunction2D extends ParameterizedFunction{	
	
	/**
	 * Evaluate this function's output and the Jacobian at a given input. 
	 * @param input - arbitrary input object 
	 */
	public OutputAndJacobian evaluate(Object input);
	public static class OutputAndJacobian{
	  //y is the output of this vector-valued function
		public double[]   y;
		//dy is jacobian or log jacobian of this function
		public double[][] dy; //dy[i][j] = dy[i]/dtheta[j]
		public boolean logspace = false; //is gradient in logspace?
	}
	/**
	 * Provide a sample random input object to the function. Required for
	 * gradient checking code.
	 */
	public Object generateRandomInput(Random rand);
}
