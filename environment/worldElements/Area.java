package androidgameserver.environment.worldElements;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.awt.*;

public class Area extends TriggerZone {

    public Area(Vec2 worldPos, float size, String label) {
        super(worldPos, 0, size, label);
    }
}
