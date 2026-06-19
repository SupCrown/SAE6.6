package androidgameserver.environment.maps;

import androidgameserver.AppDefines;
import androidgameserver.ClientHandler;
import androidgameserver.bot.BotBody2D;
import androidgameserver.bot.WarBot;
import androidgameserver.environment.Team;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Team Death Match : deux équipes fixes s'affrontent. Chaque bot qui se connecte
 * est affecté à l'équipe la moins peuplée (équilibrage automatique). Le score d'une
 * équipe est la somme des kills de ses membres ; la première équipe à atteindre
 * {@link AppDefines#scoreLimit} points gagne le match.
 *
 * On hérite de {@link MAP_FFA} pour réutiliser toute la logique de guerre (monde,
 * obstacles, placement des bots, respawn, pénalités). Le tir ami est déjà géré en
 * amont par {@code Environment2DMultiBot.areAllies()} : deux coéquipiers ne se
 * blessent pas.
 */
public class MAP_TDM extends MAP_FFA {

    /** Définition des équipes fixes du mode (id, nom, couleur). */
    private static final Object[][] TEAM_SETUP = {
            {0, "Red", Color.RED},
            {1, "Blue", new Color(50, 120, 255)},
    };

    /** Équipe gagnante une fois le score limite atteint (null tant que le match est en cours). */
    private Team winningTeam = null;

    public MAP_TDM() {
        super();
        this.name = "TDM";
        // Création des équipes fixes dès la construction de la carte : elles existent
        // donc avant la connexion du premier bot (cf. addBot ci-dessous).
        for (Object[] spec : TEAM_SETUP)
            getOrCreateTeam((int) spec[0], (String) spec[1], (Color) spec[2]);
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

    /** Renvoie l'équipe ayant le moins de membres (la première en cas d'égalité). */
    private Team pickSmallestTeam() {
        Team smallest = null;
        for (Team t : getTeams())
            if (smallest == null || t.getMembers().size() < smallest.getMembers().size())
                smallest = t;
        return smallest;
    }

    /** Score d'une équipe = somme des kills de ses membres. */
    private int teamScore(Team t) {
        int total = 0;
        for (BotBody2D b : t.getMembers())
            if (b instanceof WarBot)
                total += ((WarBot) b).getScore();
        return total;
    }

    @Override
    public void postStepOps() {
        // On appelle directement les opérations de base (pénalités + respawns) au lieu de
        // super.postStepOps(), qui afficherait le classement individuel propre au FFA.
        checkPenalty();
        checkDeadBots();

        clearMessages();
        if (AppDefines.scoreLimit > 0)
            addMessage("Team Death Match (Match in " + AppDefines.scoreLimit + " points):");
        else
            addMessage("Team Death Match :");

        // Classement des équipes par score décroissant (copie : on ne réordonne pas la liste partagée).
        List<Team> ranked = new ArrayList<>(getTeams());
        ranked.sort(Comparator.comparingInt(t -> -teamScore(t)));

        for (Team t : ranked) {
            int score = teamScore(t);
            addMessage(score + "   " + t.getTeamName() + " (" + t.getMembers().size() + ")");
            // Détail des membres de l'équipe.
            for (BotBody2D b : t.getMembers()) {
                WarBot wb = (WarBot) b;
                if (wb.isDead()) {
                    long remainingSeconds = Math.max(0, (wb.getRespawnTick() - getTick()) / 50);
                    addMessage("    " + wb.getScore() + "   ✝ " + remainingSeconds + "s   " + b.getName());
                } else {
                    addMessage("    " + wb.getScore() + "   ♥ " + wb.getLives() + "   " + b.getName());
                }
            }

            // Première équipe à franchir la limite : elle gagne le match.
            if (winningTeam == null && AppDefines.scoreLimit > 0 && score >= AppDefines.scoreLimit)
                winningTeam = t;
        }

        if (winningTeam != null) {
            setWinMessage(winningTeam.getTeamName() + " wins !");
            paused = true;
        }
    }

    @Override
    public void reset() {
        super.reset();
        winningTeam = null;
    }
}
