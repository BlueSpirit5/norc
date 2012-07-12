package norc.pgrd;

import norc.QFunction;
import norc.State;

/**
 * Planner that computes Q value and gradients w.r.t. parameters theta.
 * @author Jeshua Bratman
 */
public interface DifferentiableQFunction<T extends State> extends DifferentiableFunction2D<T>, QFunction<T> {		
	/**
	 * Update Q values and Q value gradients for a given state.
     *  y = Q(s,*)
     *       estimated state-action value function at state s
	 *       |actions| length array
	 *  dy = dQ(s,*)/dTheta
	 *       Jacobian of the Q function w.r.t. reward function parameters theta
	 *       |actions| x |theta| matrix where dQdt[a][i] is dQ(s,a) / dtheta_i
	 **/
	public OutputAndJacobian evaluate(T st);
}
