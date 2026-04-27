/*******************************************************************************
 * EvoAgents : A simulation platform for agents using the MIND architecture
 * Copyright (c) 2016, 2020 Suro François (suro@lirmm.fr)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package androidgameserver;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AppFrame {
	static JMenuItem MI_GEN_OPT = new JMenuItem("Options");
	static JMenuItem MI_GEN_EXIT = new JMenuItem("Exit");
	static JMenuItem MI_HLP_ABOUT = new JMenuItem("About");
	

	static JMenuItem MI_BOT_DUMB = new JMenuItem("Add Dumb AI");
	static JMenuItem MI_BOT_RANDOM = new JMenuItem("Add Random AI");
	static JMenuItem MI_BOT_REMOVEALL = new JMenuItem("Remove all AIs");
	
	static JButton FOLLOW = new JButton("Follow text");
	static JFrame frame = null;
	static JPanel menuPanel;
	MenuListener listener;
	StreamToJText streamControl;

	@SuppressWarnings("serial")
	public AppFrame()
	{
	       	frame = new JFrame("AndroidGameServer");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setPreferredSize(new Dimension(1024,600));
			JMenuBar mb = new JMenuBar();
			frame.setJMenuBar(mb);
			frame.setLayout(new BorderLayout());
			MenuListener listener = new MenuListener(frame);

			menuPanel = new JPanel();

			
			JMenu gen = new JMenu("General");
			mb.add(gen);
			
			MI_GEN_OPT.addActionListener(listener);
			gen.add(MI_GEN_OPT);
			MI_GEN_EXIT.addActionListener(listener);
			gen.add(MI_GEN_EXIT);

			JMenu bots = new JMenu("AI Bots");
			mb.add(bots);
			MI_BOT_DUMB.addActionListener(listener);
			bots.add(MI_BOT_DUMB);
			MI_BOT_RANDOM.addActionListener(listener);
			bots.add(MI_BOT_RANDOM);
			MI_BOT_REMOVEALL.addActionListener(listener);
			bots.add(MI_BOT_REMOVEALL);
			
			
			JMenu hlp = new JMenu("Help");
			mb.add(hlp);

			MI_HLP_ABOUT.addActionListener(listener);
			hlp.add(MI_HLP_ABOUT);
	
	        JTextArea textArea = new JTextArea();
	        JScrollPane scrollPane = new JScrollPane(textArea);

	        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
			frame.getContentPane().add(menuPanel, BorderLayout.NORTH);	
			
			streamControl = new StreamToJText(textArea);
			FOLLOW.addActionListener(new ActionListener() {			
				@Override
				public void actionPerformed(ActionEvent e) {
					streamControl.switchFollow();
				}
			});
			menuPanel.add(FOLLOW);
			
	        PrintStream printStream = new PrintStream(streamControl);
	        System.setOut(printStream);
	        System.setErr(printStream);
	        frame.pack();
	        frame.setVisible(true);
	}
	
	private class MenuListener implements ActionListener {
		public MenuListener(JFrame inwin) {}
		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem) e.getSource();
			if(source.equals(MI_GEN_EXIT)){
				AndroidGameServer.stop = true;
				System.exit(0);
			}
			else if(source.equals(MI_GEN_OPT)){
			    JLabel labGM = new JLabel("Game mode :");
			    JComboBox<String> selGM = new JComboBox<String>(AppDefines.gameModes);
			    selGM.setSelectedItem(AppDefines.gameModes[0]);
			    
				JLabel labTimeout = new JLabel("Connection timeout (ms. 0 for infinite) :");
			    JTextField fieldTimeout = new JTextField(Integer.toString(AppDefines.connectionTimeoutDelay));
			    
				JLabel labROF = new JLabel("Rate of fire (delay in ms) :");
			    JTextField fieldROF = new JTextField(Integer.toString(AppDefines.bot_RateOfFire));

				JLabel labPenalty = new JLabel("Respawn (delay in ms) :");
			    JTextField fieldPenalty = new JTextField(Long.toString(AppDefines.penaltyTime));

				JLabel labMsgDelay = new JLabel("Message display time (in ms) :");
			    JTextField fieldMsgDelay = new JTextField(Long.toString(AppDefines.messageDuration));

				JLabel labMaxScore = new JLabel("Score Limit :");
			    JTextField fieldMaxScore = new JTextField(Long.toString(AppDefines.scoreLimit));

			    JLabel labArenaWidth = new JLabel("Arena width :");
			    JTextField fieldArenaWidth = new JTextField(Float.toString(AppDefines.arenaWidth));

			    JLabel labArenaHeight = new JLabel("Arena Height :");
			    JTextField fieldArenaHeight = new JTextField(Float.toString(AppDefines.arenaHeight));
			    
			    JLabel labObstacleProb = new JLabel("Obstacle probability :");
			    JTextField fieldObstacleProb = new JTextField(Double.toString(AppDefines.ObstacleProbability));
			    
				Object[] array = {labGM,selGM,labTimeout,fieldTimeout,
						labROF,fieldROF,labPenalty,fieldPenalty, labMsgDelay,
						fieldMsgDelay, labMaxScore, fieldMaxScore, labArenaWidth,
						fieldArenaWidth, labArenaHeight, fieldArenaHeight,
						labObstacleProb, fieldObstacleProb};
				int sel = JOptionPane.showConfirmDialog(frame, array, "Game settings",JOptionPane.OK_CANCEL_OPTION);
				if(sel == 0)
				{
				    AppDefines.connectionTimeoutDelay =Integer.parseInt(fieldTimeout.getText());
				    AppDefines.bot_RateOfFire = Integer.parseInt(fieldROF.getText());
				    AppDefines.penaltyTime = Integer.parseInt(fieldPenalty.getText());
				    AppDefines.messageDuration = Integer.parseInt(fieldMsgDelay.getText());
				    AppDefines.scoreLimit = Integer.parseInt(fieldMaxScore.getText());	
				    AppDefines.arenaHeight = Float.parseFloat(fieldArenaHeight.getText());	
				    AppDefines.arenaWidth = Float.parseFloat(fieldArenaWidth.getText()); 
				    AppDefines.ObstacleProbability = Double.parseDouble(fieldObstacleProb.getText()); 
				}
			}
			else if(source.equals(MI_BOT_DUMB)){
				AndroidGameServer.addDumbAI();
			}
			else if(source.equals(MI_BOT_RANDOM)){
				AndroidGameServer.addRandomAI();
			}
			else if(source.equals(MI_BOT_REMOVEALL)){
				AndroidGameServer.removeBots();
			}			
			else if(source.equals(MI_HLP_ABOUT)){
				JLabel lab1 = new JLabel("Android game server");
				JLabel lab2 = new JLabel("Copyright (C) 2022  SURO François");
				JLabel lab3 = new JLabel("GNU GENERAL PUBLIC LICENSE");
				JLabel lab4 = new JLabel("Version 3, 29 June 2007");
				JLabel lab5 = new JLabel("");
				JLabel lab6 = new JLabel("This program comes with ABSOLUTELY NO WARRANTY");
				JLabel lab7 = new JLabel("This is free software, and you are welcome to redistribute it");
				JLabel lab8 = new JLabel("under certain conditions");
		
				Object[] array = {lab1,lab2,lab3,lab4,lab5,lab5,lab6,lab7,lab8};
				JOptionPane.showMessageDialog(frame, array,"About",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

}
