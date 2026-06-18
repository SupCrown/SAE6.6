package androidgameserver.environment.maps;

import androidgameserver.bot.BotBody2D;
import androidgameserver.environment.Environment2DMultiBot;
import androidgameserver.environment.Team;
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

    public MAP_CTP() {
        super();
        this.name = "CTP";
    }

    @Override
    public World makeWorld() {
        super.makeWorld();
        loadAreas();
        return getWorld();
    }

    public void loadAreas() {
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

    private void addArea(Vec2 firstAreaWP, String label, Color color) {
        Area firstArea = new Area(firstAreaWP, AREA_RADIUS, label, color);
        getWorldElements().add(firstArea);
    }

    public List<Area> getAreas()
    {
        return areas;
    }

    public double getDistanteBetween(Vec2 a, Vec2 b) {
        return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 1));
    }

    @Override
    public void postStepOps() {
        super.postStepOps();
        checkBotsPositions();
    }

    private void checkBotsPositions() {
        for (Area area : areas) {
            area.setBlocked(false);
            Team team = null;
            for (BotBody2D bot: bots) {
                if (area.isPointInZone(bot.getWorldPosition())){
                    team = bot.BotTeam;
                    if (!area.isBlocked)
                        area.setBlocked(bot.BotTeam != area.team);
                }
            }
            area.doStep(team);
        }
    }
}
