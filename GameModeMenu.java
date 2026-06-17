package androidgameserver;

import javax.swing.*;
import java.awt.*;

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
        JButton dmButton = new JButton("Death Match");

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
        dmButton.addActionListener(e -> launchGame("DM"));
    }

    private void launchGame(String mode) {
        dispose();

        System.out.println("Mode choisi : " + mode);

        AndroidGameServer.selectedMode = mode;
        AndroidGameServer.startServer();
    }
}
