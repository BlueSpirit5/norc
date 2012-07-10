package norc;
public  class SAS<T extends State>{    
	public T state1;
	public int action;
	public T state2;
	public SAS(){}
	public SAS(T state1, int action, T state2){
		this.state1 = state1;
		this.state2 = state2;
		this.action = action;
	}
}