package androidgameserver.environment.worldElements;


public interface ControlledElement {
	public default void step() {};	
	public default void reset() {};	
}
