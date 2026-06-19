package androidgameserver.environment.maps;

import androidgameserver.ClientHandler;
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

/**
 * Mode Capture the Points : deux équipes fixes (Rouge et Bleu) s'affrontent pour capturer
 * deux zones (ALPHA et BETA). Une zone progresse vers 100 % quand une seule équipe l'occupe ;
 * elle est bloquée (grisée) si les deux équipes s'y trouvent simultanément.
 * La première équipe à contrôler les deux zones à 100 % gagne la partie.
 */
public class MAP_CTP extends MAP_FFA {
    public static final int AREA_RADIUS = 18;
    public List<Area> areas = new ArrayList<>();

    private static final Object[][] TEAM_SETUP = {
            {0, "Red", Color.RED},
            {1, "Blue", new Color(50, 120, 255)},
    };


    public MAP_CTP() {
        super();
        this.name = "CTP";
        // Création des équipes fixes dès la construction de la carte : elles existent
        // donc avant la connexion du premier bot (cf. addBot ci-dessous).
        for (Object[] spec : TEAM_SETUP)
            getOrCreateTeam((int) spec[0], (String) spec[1], (Color) spec[2]);
    }

    @Override
    public World makeWorld() {
        super.makeWorld();
        loadAreas();
        return getWorld();
    }

    /**
     * Génére deux zones une cyan et une rose, éloignées de 3 fois le rayon d'une zone
     */
    public void loadAreas() {
        // générer la première zone aléatoirement
        Vec2 firstAreaWP = generatePositionInBoundaries(AREA_RADIUS+2);
        addArea(firstAreaWP, "ALPHA", Color.CYAN);

        // générer la deuxième zone sans qu'elle chevauche la première
        Vec2 secondAreaWP = generatePositionInBoundaries(AREA_RADIUS+2);
        while (getDistanteBetween(firstAreaWP, secondAreaWP) <= AREA_RADIUS * 3){
            secondAreaWP = generatePositionInBoundaries(AREA_RADIUS+2);
        }
        addArea(secondAreaWP, "BETA", Color.PINK);
    }

    /**
     * Ajoute une zone à une à la liste des zones et des éléments virtuels
     * @param wp position de la zone
     * @param label nom de la zone
     * @param color couleur de la zone
     */
    private void addArea(Vec2 wp, String label, Color color) {
        Area area = new Area(wp, AREA_RADIUS, label, color);
        areas.add(area);
        getWorldElements().add(area);
    }

    /** Renvoie l'équipe ayant le moins de membres (la première en cas d'égalité). */
    private Team pickSmallestTeam() {
        Team smallest = null;
        for (Team t : getTeams())
            if (smallest == null || t.getMembers().size() < smallest.getMembers().size())
                smallest = t;
        return smallest;
    }

    /**
     * Calculer la distance entre deux points
     * @param a premier point
     * @param b deuxième point
     * @return la distance entre les deux points
     */
    public double getDistanteBetween(Vec2 a, Vec2 b) {
        return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 1));
    }

    @Override
    public void postStepOps() {
        super.postStepOps();
        checkBotsPositions();
        if (endGame()) {
            setWinMessage(areas.getFirst().team.getTeamName() + " wins !");
            paused = true;
        }
    }

    /**
     * Regarde si les deux zones sont capturées à 100% par la même équipe
     * @return true si toutes les zones sont capturées pa la même équipe
     */
    private boolean endGame() {
        Team team = null;
        for (Area area : areas) {
            if (area.team != null) {
                if (team == null) {
                    team = area.team;
                }

                if (!team.equals(area.team)) {
                    return false;
                }

                if (area.capture < 100) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return team != null;
    }

    /**
     * Calcul l'état des zones et appel leur étape
     */
    private void checkBotsPositions() {
        for (Area area : areas) {
            area.setBlocked(false);
            Team team = null;
            boolean isBlocked = false;
            for (BotBody2D bot: bots) {
                if (area.isPointInZone(bot.getWorldPosition())){
                    if (team == null) {
                        team = bot.getBotTeam();
                    }

                    isBlocked = isBlocked || team != bot.getBotTeam();
                }
            }
            area.setBlocked(isBlocked);
            area.doStep(team);
        }
    }

    /**
     * Connexion d'un bot : on l'affecte à l'équipe la moins peuplée pour garder les
     * effectifs équilibrés (contrairement au FFA où chaque bot a sa propre équipe).
     */
    @Override
    synchronized public BotBody2D addBot(ClientHandler clientHandler) {
        Team team = pickSmallestTeam();
        BotBody2D bot = makeBot(ClientHandler.makeDefaultName(), clientHandler, team);
        bots.add(bot);
        if (clientHandler != null)
            clientHandler.setBot(bot);
        return bot;
    }
}
