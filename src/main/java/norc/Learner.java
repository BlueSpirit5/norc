package norc;

public interface Learner<T extends State> {
	public void learn(T s1, int a, double r, T s2);
}
