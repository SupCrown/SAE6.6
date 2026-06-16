package androidgameserver.environment.worldElements;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Area extends StaticWorldElement{

    public Area(Vec2 worldPos, float size, World w) {
        super(worldPos, 0, size);
        sd.shape = new CircleShape();
        sd.shape.setRadius(size);
        sd.friction = 0.0f;
        sd.density = 2.0f;
        registerToWorld();
    }
}
