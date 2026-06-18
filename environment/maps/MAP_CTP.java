package androidgameserver.environment.maps;

import androidgameserver.environment.Environment2DMultiBot;
import androidgameserver.environment.worldElements.Area;
import androidgameserver.environment.worldElements.VirtualWorldElement;
import androidgameserver.environment.worldElements.WorldElement;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MAP_CTP extends MAP_FFA {
    public static final int AREA_RADIUS = 18;
    public List<Area> areas = new ArrayList<>();

    public MAP_CTP()
    {
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
        Vec2 firstAreaWP = generatePositionInBoundaries(AREA_RADIUS+2);
        addArea(firstAreaWP, "ALPHA", Color.CYAN);

        // générer la deuxième zone sans qu'elle chevauche la première
        Vec2 secondAreaWP = generatePositionInBoundaries(AREA_RADIUS+2);
        while (getDistanteBetween(firstAreaWP, secondAreaWP) <= AREA_RADIUS * 4){
            secondAreaWP = generatePositionInBoundaries(AREA_RADIUS+2);
        }
        addArea(secondAreaWP, "BETA", Color.PINK);
    }

    private void addArea(Vec2 firstAreaWP, String ALPHA, Color cyan) {
        Area firstArea = new Area(firstAreaWP, AREA_RADIUS, ALPHA);
        firstArea.color = cyan;
        areas.add(firstArea);
        getWorldElements().add(firstArea);
    }

    public List<Area> getAreas()
    {
        return areas;
    }

    public double getDistanteBetween(Vec2 a, Vec2 b)
    {
        return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 1));
    }
}
