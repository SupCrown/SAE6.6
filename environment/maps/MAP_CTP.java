package androidgameserver.environment.maps;

import androidgameserver.environment.Environment2DMultiBot;
import androidgameserver.environment.worldElements.Area;
import androidgameserver.environment.worldElements.WorldElement;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MAP_CTP extends MAP_FFA {
    public static final int AREA_RADIUS = 6;
    public List<WorldElement> areas = new ArrayList<WorldElement>();

    public MAP_CTP() {
        super();
        this.name = "CTP";
    }

    @Override
    public World makeWorld()
    {
        super.makeWorld();
        loadAreas();
        return getWorld();
    }

    public void loadAreas()
    {
        // générer la première zone aléatoirement
        Vec2 firstAreaWP = generatePositionInBoundaries(AREA_RADIUS);
        System.out.println(firstAreaWP);
        Area firstArea = new Area(firstAreaWP, AREA_RADIUS, Color.BLUE);
        areas.add(firstArea);
        getWorldElements().add(firstArea);

        // générer la deuxième zone sans qu'elle chevauche la première
        Vec2 secondAreaWP = generatePositionInBoundaries(AREA_RADIUS);
        while (getDistanteBetween(firstAreaWP, secondAreaWP) <= AREA_RADIUS * 2){
            secondAreaWP = generatePositionInBoundaries(AREA_RADIUS);
        }
        System.out.println(secondAreaWP);
        Area secondArea = new Area(secondAreaWP, AREA_RADIUS, Color.RED);
        areas.add(secondArea);
        getWorldElements().add(secondArea);

    }

    public List<WorldElement> getAreas()
    {
        return areas;
    }

    public double getDistanteBetween(Vec2 a, Vec2 b)
    {
        return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 1));
    }
}
