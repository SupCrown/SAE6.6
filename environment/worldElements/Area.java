package androidgameserver.environment.worldElements;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.awt.*;

public class Area extends StaticWorldElement{

    public Area(Vec2 worldPos, float size, Color c) {
        super(worldPos, 0, size);
        sd.shape = new CircleShape();
        sd.shape.m_radius = size;
        sd.friction = 0.0f;
        sd.density = 2.0f;
        setColor(c);
        registerToWorld();
    }
}
