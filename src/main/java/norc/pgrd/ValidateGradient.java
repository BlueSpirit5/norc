package norc.pgrd;

import java.util.Arrays;
import java.util.Random;

import norc.pgrd.DifferentiableFunction1D.OutputAndGradient;
import norc.pgrd.DifferentiableFunction2D.OutputAndJacobian;


/**
 * Finite Difference evaluation of gradients and Jacobians. 
 * @author Jeshua Bratman
 */
public class ValidateGradient {
	
	public static <T> boolean validate(DifferentiableFunction1D<T> f)
  	 {return validate(f,0.000001,new Random());}
	public static <T> boolean validate(DifferentiableFunction1D<T> f, double threshold)
  	 {return validate(f,threshold,new Random());}
	/**
	 * Compares functional gradient computation to finite difference estimate
	 * @param f         -- function to evaluate
	 * @param threshold -- comparison threshold
	 * @param rand      
	 * @return success or failure
	 */
	public static <T> boolean validate(DifferentiableFunction1D<T> f,double threshold, Random rand){	
		double delta = 0.01;		
		int num_trials = 10;
		
		boolean success = true;
		for(int trials=0;trials<num_trials;trials++){
			T input = f.generateRandomInput(rand);
			double[] theta = f.getParams();
			double[] theta1 = f.getParams().clone();
			OutputAndGradient output = f.evaluate(input);
			double[] dy = output.dy.clone();			
		  if(output.logspace)
        for(int i=0;i<output.dy.length;i++)
            dy[i] = Math.exp(dy[i]);
		  
			//compute empirical gradient
			double[] dy_hat = new double[theta.length];
			Arrays.fill(dy_hat,0);
			for(int tind=0;tind<theta.length;tind++){
				//evaluate at theta[tind] += delta
				theta[tind] = theta1[tind] + delta;
				f.setParams(theta);
				double y1 = f.evaluate(input).y;
				//evaluate at theta[tind] -= delta
				theta[tind] = theta1[tind] - delta;
				f.setParams(theta);
				double y2 = f.evaluate(input).y;
				//check difference between errors
				dy_hat[tind] = (y1-y2)/(2*delta);
				//reset
				theta[tind] = theta1[tind];
			}
			//reset theta
			f.setParams(theta1);
			double numer=0,denom=0;
			for(int i=0;i<theta.length;i++){			
			  numer += Math.pow(dy[i] - dy_hat[i],2);
				denom += Math.pow(dy[i] + dy_hat[i],2);
			}
			double diff = numer/denom;
			success = success & (diff < threshold);

			//------
			//return results
			if(!success){
				System.out.print("dy: ");
				for(int i=0;i<Math.min(theta.length,10);i++)System.out.printf("%.3f ",dy[i]);
				if(theta.length > 10)
					System.out.println("...");
				else
					System.out.println("");
				System.out.print("dh: ");
				for(int i=0;i<Math.min(theta.length,10);i++)System.out.printf("%.3f ",dy_hat[i]);
				if(theta.length > 10)
					System.out.println("...");
				else
					System.out.println();
				System.out.println("Difference: "+diff);
				break;
			}
		}
		return success;
	}
	
	

	public static <T> boolean validate(DifferentiableFunction2D<T> f)
	{return validate(f,0.0001,new Random());}
	public static <T> boolean validate(DifferentiableFunction2D<T> f,double threshold)
	{return validate(f,threshold,new Random());}
	
	
	/**
	 * Compare functional Jacobian computation to finite difference estimate.
	 * @param f          -- vector-valued function with Jacobian evaluation 
	 * @param threshold  -- comparison threshold
	 * @param rand
	 * @return success or failure
	 */
	public static <T> boolean validate(DifferentiableFunction2D<T> f,double threshold, Random rand){
		double delta = 0.0001;		
		int num_trials = 10;		
		boolean success = true;
		
		for(int trials=0;trials<num_trials;trials++){
			T input = f.generateRandomInput(rand);

			//compute analytic gradient
			OutputAndJacobian out = f.evaluate(input);
			double[][] dy = new double[out.dy.length][out.dy[0].length];
			for(int i=0;i<dy.length;i++)
			  for(int j=0;j<dy[i].length;j++)
			    if(out.logspace)
			      dy[i][j] = Math.exp(out.dy[i][j]);
			    else
			      dy[i][j] = out.dy[i][j];
			
			//compute finite difference gradient
			double[][] dh = fdGrad(f,delta,input);
			double numer=0,denom=0;
			for(int i=0;i<dh.length;i++){
				for(int aind=0;aind<dh.length;aind++){	
					numer += Math.pow(dy[aind][i] - dh[aind][i],2);
					denom += Math.pow(dy[aind][i] + dh[aind][i],2);
				}
			}
			double diff;
			if(denom == 0 && numer == 0)
				diff = 0;
			else
				diff = numer/denom;
			success = success & (diff < threshold);
			//------
			//return results
			if(!success){
				int max_show = 10;
				//----------------
				for(int q = 0; q<Math.min(2, dh.length);q++){
					int num_shown = 0;

					for(int i=0;i<dh.length;i++){
						if(dy[0][i] != dh[0][i]){
							num_shown++;
							System.out.printf("dy[%d]: %.3f ",i,dy[q][i]);
						}
						if(num_shown >= max_show) break;
					}
					if(num_shown >= max_show) System.out.println("...");
					else System.out.println("");	
				}
				System.out.println("..");
				//----------------
				for(int q = 0; q<Math.min(2, dh.length);q++){
					int num_shown = 0;
					for(int i=0;i<dh.length;i++){
						if(dy[0][i] != dh[0][i]){
							num_shown++;
							System.out.printf("dh[%d]: %.3f ",i,dh[q][i]);
						}
						if(num_shown >= max_show) break;
					}
					if(num_shown >= max_show) System.out.println("...");
					else System.out.println("");
				}
				System.out.println("..");
				System.out.println("Difference: "+diff);
				break;
			}			
		}
		return success;		
	}
	
	//==================================================
	public static <T> double[][] fdGrad(DifferentiableFunction2D<T> f,double delta, T input){
		OutputAndJacobian out = f.evaluate(input);
		double[][] dh = new double[out.dy.length][out.dy[0].length];
		final double[] theta1 = f.getParams().clone();
		double[] theta = f.getParams().clone();
		for(int tind=0;tind<theta.length;tind++){
			//evaluate at theta[tind] += delta
			theta[tind] = theta1[tind] + delta;
			f.setParams(theta);
			double[] y1 = f.evaluate(input).y.clone();
			//evaluate at theta[tind] -= delta
			theta[tind] = theta1[tind] - delta;
			f.setParams(theta);
			double[] y2 = f.evaluate(input).y.clone();
			//check difference between errors
			for(int aind=0;aind<dh.length;aind++){
				dh[aind][tind] = (y1[aind]-y2[aind])/(2*delta);
			}
			//reset
			theta[tind] = theta1[tind];
		}
		//reset theta
		f.setParams(theta1);
		return dh;
	}
}
