package androidgameserver;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre de sélection du mode de jeu affichée au démarrage de l'application.
 * Propose trois boutons (FFA, CTP, TDM) et délègue le lancement à
 * {@link AndroidGameServer#startServerDirect()} ou {@link AndroidGameServer#startServerWithWaitingWindow(TeamWaitingWindow)}.
 */
public class GameModeMenu extends JFrame {

    public GameModeMenu() {
        setTitle("Menu");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("MENU", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));

        JLabel subtitle = new JLabel("Choisissez votre mode de jeu", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        topPanel.add(title);
        topPanel.add(subtitle);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        JButton ffaButton = new JButton("Free for all");
        JButton ctpButton = new JButton("Capture the points");
        JButton dmButton = new JButton("Team Death Match");

        ffaButton.setPreferredSize(new Dimension(300, 100));
        ctpButton.setPreferredSize(new Dimension(300, 100));
        dmButton.setPreferredSize(new Dimension(300, 100));

        Font buttonFont = new Font("Arial", Font.BOLD, 15);
        ffaButton.setFont(buttonFont);
        ctpButton.setFont(buttonFont);
        dmButton.setFont(buttonFont);

        buttonPanel.add(ffaButton);
        buttonPanel.add(ctpButton);
        buttonPanel.add(dmButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        add(panel);

        ffaButton.addActionListener(e -> launchGame("FFA"));
        ctpButton.addActionListener(e -> launchGame("CTP"));
        dmButton.addActionListener(e -> launchGame("TDM"));
    }

    /**
     * Ferme ce menu, mémorise le mode choisi et démarre le serveur.
     * Le FFA démarre directement ; CTP et TDM attendent d'abord 2 joueurs minimum.
     * @param mode identifiant du mode ("FFA", "CTP" ou "TDM")
     */
    private void launchGame(String mode) {
        dispose();

        System.out.println("Mode choisi : " + mode);
        AndroidGameServer.selectedMode = mode;

        if (mode.equals("FFA")) {
            new Thread(() -> AndroidGameServer.startServerDirect()).start();
        } else {
            TeamWaitingWindow waitingWindow = new TeamWaitingWindow(mode);
            waitingWindow.setVisible(true);

            new Thread(() -> AndroidGameServer.startServerWithWaitingWindow(waitingWindow)).start();
        }
    }
}
