package androidgameserver;

import javax.swing.*;
import java.awt.*;

public class TeamWaitingWindow extends JFrame {

    private String mode;
    private JLabel statusLabel;
    private JLabel redTeamLabel;
    private JLabel blueTeamLabel;

    public TeamWaitingWindow(String mode) {
        this.mode = mode;

        setTitle("Attente des joueurs - " + mode);
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Mode " + mode, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));

        statusLabel = new JLabel("En attente de 2 joueurs...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 22));

        JPanel teamsPanel = new JPanel(new GridLayout(1, 2, 20, 20));

        redTeamLabel = new JLabel("Équipe 1 : en attente", SwingConstants.CENTER);
        blueTeamLabel = new JLabel("Équipe 2 : en attente", SwingConstants.CENTER);

        redTeamLabel.setFont(new Font("Arial", Font.BOLD, 20));
        blueTeamLabel.setFont(new Font("Arial", Font.BOLD, 20));

        teamsPanel.add(redTeamLabel);
        teamsPanel.add(blueTeamLabel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(statusLabel, BorderLayout.CENTER);
        panel.add(teamsPanel, BorderLayout.SOUTH);

        add(panel);
    }

    public void updateTeams(String player1, String player2) {
        redTeamLabel.setText("Équipe 1 : " + player1);
        blueTeamLabel.setText("Équipe 2 : " + player2);
        statusLabel.setText("2 joueurs connectés. Lancement dans 10 secondes...");
    }

    public void updateStatus(String text) {
        statusLabel.setText(text);
    }
}