package androidgameserver.environment.worldElements;

import org.jbox2d.common.Vec2;

public interface SituatedElement2D {
	public void setWorldPosition(Vec2 pos, double ang);
	public Vec2 getWorldPosition();
	public void setAngle(float ang);
	public default float getAngle() {return 0.0f;}	
}
