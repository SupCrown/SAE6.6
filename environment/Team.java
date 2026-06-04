package androidgameserver.environment;

import androidgameserver.bot.BotBody2D;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Team {
    private int teamId;
    private String teamName;
    private Color color;
    // CopyOnWriteArrayList : la liste est lue par le thread de rendu (affichage) et modifiée
    // par les threads réseau/simulation. Le copy-on-write évite toute ConcurrentModificationException.
    private final List<BotBody2D> members = new CopyOnWriteArrayList<>();

    public Team(int teamId, String teamName, Color color) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.color = color;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Ajoute un bot à l'équipe ET le relie à elle (référence d'équipe + couleur).
     * On centralise le lien ici pour qu'il soit impossible d'avoir un bot membre
     * d'une équipe sans que son champ équipe pointe vers elle (une seule source de vérité).
     */
    public void addMember(BotBody2D member) {
        if (member == null || members.contains(member))
            return;
        members.add(member);
        member.setBotTeam(this);
        member.setColor(color);
    }

    /** Retire un bot de l'équipe et coupe le lien retour. */
    public void delMember(BotBody2D member) {
        if (members.remove(member) && member.getBotTeam() == this)
            member.setBotTeam(null);
    }

    public boolean contains(BotBody2D member) {
        return members.contains(member);
    }

    public List<BotBody2D> getMembers() {
        return members;
    }
}
