package norc;

public interface QFunction<T extends State> {
	public double[] getQ(T state);
}
