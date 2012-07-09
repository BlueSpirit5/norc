package norc;

/**
 * State used for planning. Must have equality and hashcode functions as well as a copy function
 * so we can manipulate and compare states while planning.
 * @author Jeshua Bratman
 */
public interface State {
	public abstract boolean equals(Object other);

	public abstract int hashCode();

	public abstract State copy();

	public abstract boolean isAbsorbing();
}