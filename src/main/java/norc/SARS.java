package norc;
public class SARS<T extends State> {
	public T s,s2;
	public int a;
	public double r;
	public SARS(T s1, int a, double r, T s2){
		this.s = s1;
		this.a = a;
		this.r = r;
		this.s2 = s2;
	}
}
