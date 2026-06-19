package androidgameserver.environment.worldElements;

import androidgameserver.environment.CollisionDefines;
import androidgameserver.environment.Team;
import org.jbox2d.common.Vec2;

import java.awt.*;


/**
 * Zone de capture utilisée dans le mode CTP.
 * Sa progression ({@code capture} de 0 à 100) augmente quand une seule équipe l'occupe
 * et diminue en l'absence de bots. La zone est « bloquée » (figée, grisée) quand deux
 * équipes adverses s'y trouvent en même temps.
 */
public class Area extends TriggerZone {

    public float capture;
    public Team team;
    public boolean isBlocked;
    public Color initialColor;

    public Area(Vec2 worldPos, float size, String label, Color color) {
        super(worldPos, 0, size, label);
        capture = 0;
        isBlocked = false;
        initialColor = color;
        this.color = color;
    }

    /** Met à jour la progression de capture selon l'équipe présente dans la zone ce tick. */
    public void doStep(Team team) {
        if (!isBlocked && team != null) {
            if (this.team == null) {
                this.team = team;
                this.color = team.getColor();
                capture += 0.1f;
            } else if (this.team.equals(team) && capture < 100) {
                capture += 0.1f;
            } else {
                capture -= 0.1f;
                if (capture < 0) {
                    this.team = null;
                }
            }
        }
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
        // une fois bloquée, une zone est grise, sinon si une équipe est présente, elle prend la couleur de celle-ci, sinon elle retrouve sa couleur de base.
        if (this.isBlocked) {
            color = Color.GRAY;
        } else {
            if (team != null) {
                color = team.getColor();
            } else {
                color = initialColor;
            }
        }
    }
}
