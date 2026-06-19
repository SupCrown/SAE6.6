package androidgameserver.environment.worldElements;

import androidgameserver.environment.CollisionDefines;
import androidgameserver.environment.Team;
import org.jbox2d.common.Vec2;

import java.awt.*;


public class Area extends TriggerZone {

    public int capture;
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

    public void doStep(Team team) {
        if (!isBlocked && team != null) {
            if (this.team == null) {
                this.team = team;
                this.color = team.getColor();
                capture++;
            } else if (this.team.equals(team) && capture < 100) {
                capture++;
            } else {
                if (--capture == 0) {
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
