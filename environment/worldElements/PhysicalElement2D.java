package androidgameserver.environment.worldElements;


public interface PhysicalElement2D {
	public default void addContact(PhysicalElement2D obj) {};
	public default void removeContact(PhysicalElement2D obj) {};
}
