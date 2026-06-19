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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import androidgameserver.bot.WarBot;
import androidgameserver.environment.worldElements.*;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;

import androidgameserver.bot.BotBody2D;
import androidgameserver.bot.elements.A_Kicker;
import androidgameserver.bot.elements.Actuator;

import androidgameserver.environment.Environment2D;
import androidgameserver.environment.Environment2DMultiBot;

public class Env2DViewer extends JPanel{
	private static final long serialVersionUID = -5562812187406952123L;
	
	Environment2DMultiBot env;
	int offsetX = 40,offsetY = 40;
	int mPrevX = 0,mPrevY = 0;
	float scale = 1.0f;
	int drawGizmos = 0;
	boolean drawPath = false;
	boolean drawBackGround = true;
	int pathSampling = 10;
	boolean drawNames = true;
	boolean debugStrings = false;
	boolean rightClick = false;
	boolean leftClick = false;
	Font font = new Font("Default", Font.BOLD, 20);
	Font winnerFont = new Font("Default", Font.BOLD,70);
    FontMetrics fontMetrics = getFontMetrics(font);
    FontMetrics winnerFontMetrics = getFontMetrics(winnerFont);
	DecimalFormat decFormat = new DecimalFormat("#.######");
	int strokeThickness = 3;
	BasicStroke stroke = new BasicStroke(strokeThickness);
	BasicStroke defaultstroke = new BasicStroke(1);
	boolean fill = false;
	
	JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
	
	public Env2DViewer(Environment2DMultiBot w)
	{
		setPreferredSize(new Dimension(800,550));
		env = w;     
		if(env!=null)
		{
			int width = Math.abs((int)(w.getCorners()[1].x - w.getCorners()[0].x));
			scale = 800.0f/width;
			offsetX = -(int)(w.getCorners()[0].x*scale);
			offsetY = (int)(w.getCorners()[0].y*scale);			
		}
		
		JButton namesButton = new JButton("Names");
		namesButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				drawNames = !drawNames;
				repaint();
			}
		});
		toolbar.add(namesButton);
		
		JButton thickPlus = new JButton("t+");
		JButton thickMinus = new JButton("t-");
		thickPlus.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {//ugh
				strokeThickness++;
				stroke = new BasicStroke(strokeThickness);
			}
		});
		thickMinus.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(strokeThickness > 1)
				{
					strokeThickness--;
					stroke = new BasicStroke(strokeThickness);
				}
			}
		});
		toolbar.add(thickPlus);
		toolbar.add(thickMinus);
		
		JButton fillButton = new JButton("Fill");
		fillButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				fill = !fill;
			}
		});
		toolbar.add(fillButton);
		
		JButton resetButton = new JButton("Reset Sim");
		resetButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				env.manualReset();
			}
		});
		toolbar.add(resetButton);
		
		JButton genButton = new JButton("Generate map");
		genButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				env.regenerateAll();
				env.manualReset();
			}
		});
		toolbar.add(genButton);
		
		JButton drawBackground = new JButton("Show background");
		drawBackground.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				drawBackGround = !drawBackGround;
			}
		});
		toolbar.add(drawBackground);
		
		toolbar.setFloatable(false);
		this.add(toolbar);
		
        
        this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == 1)
				{
					rightClick = false;
				}
				else if(e.getButton() == 3)
				{
					leftClick = false;
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				mPrevX = e.getX();
				mPrevY = e.getY();		
				if(e.getButton() == 1)
				{
					rightClick = true;
				}
				else if(e.getButton() == 3)
				{
					leftClick = true;
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			@Override
			public void mouseClicked(MouseEvent arg0) {				
			}

		});
        this.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getWheelRotation() > 0)
					scale += 0.1;
				else if (scale > 0.1)
					scale -= 0.1;

				repaint();
			}
		});
        this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				offsetX +=e.getX()-mPrevX;
				offsetY +=e.getY()-mPrevY;
				mPrevX = e.getX();
				mPrevY = e.getY();
				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}
		});
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    	((Graphics2D)g).setStroke(stroke);
        g.setColor(ColorTheme.backgroundColor);
        g.fillRect(0, 0, this.getWidth(),this.getHeight());
        g.setFont(font);
        if(env!=null)
        { 
        	if(drawBackGround)
        		drawBackground(g);
        	if(env.getBots() != null)
        	{
        		ArrayList<BotBody2D> botsSnapshot = new ArrayList<>(env.getBots());
        		for (BotBody2D b : botsSnapshot)
        			drawBot(g, b);
        	}
            
            drawBorders(g);
            drawObstacles(g);
            drawWorldElements(g);
			g.setColor(ColorTheme.infoFontColor);
			// Score 
			// if(rewardScore != null)
			//	g.drawString("Reward : " + decFormat.format(rewardScore.getCurrentScore()),toPix(env.getCorners()[2].x)+offsetX,toPix(env.getCorners()[2].y)+offsetY+100);	
			// g.drawString("tick : " + env.getTick(),toPix(env.getCorners()[2].x)+offsetX,toPix(env.getCorners()[2].y)+offsetY+150);	
			ArrayList<String> messages = new ArrayList<>(env.getMessageStrings());
			for (int i = 0; i < messages.size(); i++) {
				g.drawString(messages.get(i), toPix(env.getCorners()[2].x+20)+offsetX + 20, toPix(env.getCorners()[2].y)+offsetY+ (i * 20));
			}
			String winMsg = env.getWinMessage();
			if (winMsg != null) {
				g.setColor(ColorTheme.debrisColor);
				g.setFont(winnerFont);
				g.drawString(winMsg,
						offsetX-(winnerFontMetrics.stringWidth(winMsg)/2),
						offsetY-(winnerFontMetrics.getHeight()/2));
			}
			}

    	((Graphics2D)g).setStroke(defaultstroke);
    }

	private void drawBackground(Graphics g) {
		 g.setColor(ColorTheme.backgroundPatternColor);
		 for(int i = toPix(env.getCorners()[0].x)+offsetX ; i < toPix(env.getCorners()[1].x)+offsetX; i+=toPix(4))
			 g.drawLine(i, toPix(env.getCorners()[0].y)+offsetY,i, toPix(env.getCorners()[3].y)+offsetY);
		 for(int i = toPix(env.getCorners()[0].y)+offsetY ; i > toPix(env.getCorners()[3].y)+offsetY; i-=toPix(4))
			 g.drawLine(toPix(env.getCorners()[0].x)+offsetX,i, toPix(env.getCorners()[1].x)+offsetX,i);
	}

	private void centerCamera(BotBody2D bot)
    {
    	offsetX =  -toPix(bot.body.getPosition().x) + (this.getWidth() / 2);
		offsetY = -toPix(bot.body.getPosition().y) + (this.getHeight() / 2);
		mPrevX = offsetX;
		mPrevY = offsetY;
    }

	private void drawBot(Graphics g, BotBody2D bot) {
		if(bot.body != null)
		{
			if (drawGizmos>0)
			{
				for(Actuator a: bot.getActuators())
				{
					if(a.worldPosition!=null)
					{
						if(a instanceof A_Kicker)
							drawKicker(g,(A_Kicker)a);
					}
				}
			}
			if(bot.customSkin != null) // quick and dirty
				drawBotBodyCustom(g, bot);
			drawBotBodyArrow(g, bot); // draw arrow on top (mitigate unfair skins)
			//drawBotBodyCircle(g, bot);			
		}
    }
	
	private void drawBotBodyCustom(Graphics g, BotBody2D bot) {
    	g.setColor(bot.getColor());
		Mat22 rot =  Mat22.createRotationalTransform(bot.getAngle());
    	int bsize = toPix((float)bot.size);
    	Vec2 botpos = new Vec2(toPix(bot.body.getPosition().x), toPix(bot.body.getPosition().y));
    	Vec2 first = botToWorldCoord(bot.customSkin.get(0), bsize, rot, botpos);
    	Vec2 prev = first;
    	Vec2 next;
    	for(int i = 1 ; i < bot.customSkin.size() ; i++)
    	{
    		next = botToWorldCoord(bot.customSkin.get(i), bsize, rot, botpos);
    		g.drawLine((int)prev.x+offsetX,(int) prev.y+offsetY,(int)next.x+offsetX, (int)next.y+offsetY);
    		prev = next;
    	}
    	next = first;
		g.drawLine((int)prev.x+offsetX,(int) prev.y+offsetY,(int)next.x+offsetX, (int)next.y+offsetY);
	}

	private Vec2 botToWorldCoord(Vec2 vec2, int bsize, Mat22 rot, Vec2 botpos) {
		Vec2 ret = vec2.mul(bsize);
		rot.mulToOut(ret, ret);
		ret.addLocal(botpos);
		return ret;
	}

	private void drawBotBodyArrow(Graphics g, BotBody2D bot) {
    	g.setColor(bot.getColor());
    	int bsize = toPix((float)bot.size);

    	float cos = (float) Math.cos(bot.body.getAngle());
    	float sin = (float) Math.sin(bot.body.getAngle());
    	
    	int botpixX = toPix(bot.body.getPosition().x);
    	int botpixY = toPix(bot.body.getPosition().y);

    	int frontx =(int) (((int)(cos*(bsize*1.5))) + botpixX);
    	int fronty =(int) ((int)(sin*(bsize*1.5)) + botpixY);
    
    	 cos = (float) Math.cos(bot.body.getAngle() + MathUtils.HALF_PI + MathUtils.QUARTER_PI);
    	 sin = (float) Math.sin(bot.body.getAngle() + MathUtils.HALF_PI + MathUtils.QUARTER_PI);
    	
    	int rx =(int) ((int)((cos)*bsize*1.5) + botpixX);
    	int ry =(int) ((int)((sin)*bsize*1.5) + botpixY);

   	 	cos = (float) Math.cos(bot.body.getAngle() - MathUtils.HALF_PI - MathUtils.QUARTER_PI);
   	 	sin = (float) Math.sin(bot.body.getAngle() - MathUtils.HALF_PI - MathUtils.QUARTER_PI);
   	 
    	int lx =(int) ((int)((cos)*bsize*1.5) + botpixX);
    	int ly =(int) ((int)((sin)*bsize*1.5) + botpixY);

		g.drawLine(frontx+offsetX, fronty+offsetY,rx+offsetX, ry+offsetY);
		g.drawLine(frontx+offsetX, fronty+offsetY,lx+offsetX, ly+offsetY);
    	
		if(drawNames)
		{
	    	g.setColor(bot.getTransparentColor());
	    	g.drawString(bot.getName(),toPix(bot.body.getPosition().x)+offsetX-(fontMetrics.stringWidth(bot.getName())/2), toPix(bot.body.getPosition().y-(float)(bot.size*2))+offsetY);	
		}
		if(bot.message != null)
		{
			if(bot.messageTime + AppDefines.messageDuration > env.getTick())
			{
		    	g.setColor(bot.getTransparentColor());
		    	g.drawString(bot.message,toPix(bot.body.getPosition().x)+offsetX-(fontMetrics.stringWidth(bot.message)/2), toPix(bot.body.getPosition().y+(float)(bot.size*2))+offsetY);	
			}
		}
	}

	private void drawBotBodyCircle(Graphics g, BotBody2D bot) {
    	g.setColor(bot.getColor());
    	int bsize = toPix((float)bot.size);
    	if (fill)
			g.fillOval(toPix(bot.body.getPosition().x-(float)(bot.size))+offsetX, toPix(bot.body.getPosition().y-(float)(bot.size))+offsetY, bsize*2, bsize*2);
		else
			g.drawOval(toPix(bot.body.getPosition().x-(float)(bot.size))+offsetX, toPix(bot.body.getPosition().y-(float)(bot.size))+offsetY, bsize*2, bsize*2);
    	
		int tx =(int)(Math.cos(bot.body.getAngle())*(bsize));
		int ty =(int)(Math.sin(bot.body.getAngle())*(bsize));
    	if (fill)
        	g.setColor(Color.black);

		g.drawLine(toPix(bot.body.getPosition().x)+offsetX, toPix(bot.body.getPosition().y)+offsetY,toPix(bot.body.getPosition().x)+offsetX+tx, toPix(bot.body.getPosition().y)+offsetY+ty);
    	g.setColor(bot.getColor());
		if(drawNames)
		{
	    	g.setColor(bot.getColor());
	    	g.drawString(bot.getName(),toPix(bot.body.getPosition().x-(float)(bot.size))+offsetX, toPix(bot.body.getPosition().y-(float)(bot.size))+offsetY);	
		}
	}

	private void drawKicker(Graphics g, A_Kicker s) {
		g.setColor(ColorTheme.sensorSecondaryColor);
		g.drawLine(toPix(s.boundingBoxPointx(1))+offsetX,toPix(s.boundingBoxPointy(1))+offsetY,toPix(s.boundingBoxPointx(2))+offsetX,toPix(s.boundingBoxPointy(2))+offsetY);
		g.drawLine(toPix(s.boundingBoxPointx(2))+offsetX,toPix(s.boundingBoxPointy(2))+offsetY,toPix(s.boundingBoxPointx(3))+offsetX,toPix(s.boundingBoxPointy(3))+offsetY);
		g.drawLine(toPix(s.boundingBoxPointx(3))+offsetX,toPix(s.boundingBoxPointy(3))+offsetY,toPix(s.boundingBoxPointx(4))+offsetX,toPix(s.boundingBoxPointy(4))+offsetY);
		g.drawLine(toPix(s.boundingBoxPointx(4))+offsetX,toPix(s.boundingBoxPointy(4))+offsetY,toPix(s.boundingBoxPointx(1))+offsetX,toPix(s.boundingBoxPointy(1))+offsetY);

		g.setColor(ColorTheme.defaultBotColor);
		int tx =(int)(Math.cos(s.worldAngle)*toPix(s.kickerReach));
		int ty =(int)(Math.sin(s.worldAngle)*toPix(s.kickerReach));
		g.drawLine(toPix(s.getWorldPosition().x)+offsetX,toPix(s.getWorldPosition().y)+offsetY,toPix(s.getWorldPosition().x)+offsetX+tx, toPix(s.getWorldPosition().y)+offsetY+ty);
	}

	private void drawObstacles(Graphics g) {
		for(VirtualWorldElement we: env.getObstacles())
		{
			if(we.getClass().toString().equals(ObstacleStaticBox.class.toString())||we.getClass().toString().equals(ObstacleMovingBox.class.toString()))
				drawStaticBox(g,(ObstacleStaticBox)we,ColorTheme.obstacleColor);
			else if(we.getClass().toString().equals(ObstacleStaticCyninder.class.toString()))
				drawCircle(g,(WorldElement) we,ColorTheme.obstacleColor);
		}
	}
	
	private void drawWorldElements(Graphics g) {
		ArrayList<VirtualWorldElement> elements = new ArrayList<>(env.getWorldElements());
		for (VirtualWorldElement we : elements) {
			if(we instanceof TriggerZone)
			{
				drawStaticBox(g,(TriggerZone)we, ((TriggerZone) we).color);
				g.drawString(((TriggerZone)we).name, toPix(((TriggerZone)we).worldPosition.x-((TriggerZone)we).size)+offsetX, toPix(((TriggerZone)we).worldPosition.y-((TriggerZone)we).size)+offsetY);
			    if (we instanceof Area) {
                    drawCaptureBar(g, (Area)we, ((Area) we).color);
                }
            }
			else if(we instanceof TargetObject)
				drawCircle(g,(WorldElement)we,((WorldElement)we).getColor());
			else if(we instanceof Projectile)
				drawCircle(g,(WorldElement)we,ColorTheme.projectileColor);
			else if(we instanceof Debris)
				drawCircleFilled(g,(WorldElement)we,ColorTheme.debrisColor);
			else
				drawOther(g,(VirtualWorldElement)we,ColorTheme.other);
		}
	}
	
	 private void drawOther(Graphics g, VirtualWorldElement w, Color color) {
		 drawCircleManual(g,w.getWorldPosition(), w.size,color);
	 }

	 private void drawCircleManual(Graphics g, Vec2 position, float size, Color col) {
			g.setColor(col);
			int bsize = (int) toPix(size);
			if (fill)
				g.fillOval(toPix(position.x-size)+offsetX-strokeThickness, toPix(position.y-size)+offsetY-strokeThickness, (bsize+strokeThickness)*2,  (bsize+strokeThickness)*2);
			else
				g.drawOval(toPix(position.x-size)+offsetX, toPix(position.y-size)+offsetY, bsize*2, bsize*2);		
			//g.fillOval(toPix(we.body.getPosition().x-(float)(we.size))+offsetX, toPix(we.body.getPosition().y-(float)(we.size))+offsetY, bsize*2, bsize*2);	
	}

	 private void drawCross(Graphics g, VirtualWorldElement w, Color color) {
	    	g.setColor(color);
	    	g.drawLine(toPix(w.worldPosition.x)+offsetX, toPix(w.worldPosition.y - w.size)+offsetY,toPix(w.worldPosition.x)+offsetX, toPix(w.worldPosition.y + w.size)+offsetY);
	    	g.drawLine(toPix(w.worldPosition.x- w.size)+offsetX, toPix(w.worldPosition.y )+offsetY,toPix(w.worldPosition.x+ w.size)+offsetX, toPix(w.worldPosition.y )+offsetY);
	 }

		private void drawCircle(Graphics g, WorldElement we, Color orange) {
			g.setColor(orange);
			int bsize = toPix((float)we.size);
			if (fill)
				g.fillOval(toPix(we.body.getPosition().x-(float)(we.size))+offsetX-strokeThickness, toPix(we.body.getPosition().y-(float)(we.size))+offsetY-strokeThickness, (bsize+strokeThickness)*2,  (bsize+strokeThickness)*2);
			else
				g.drawOval(toPix(we.body.getPosition().x-(float)(we.size))+offsetX, toPix(we.body.getPosition().y-(float)(we.size))+offsetY, bsize*2, bsize*2);		
			//g.fillOval(toPix(we.body.getPosition().x-(float)(we.size))+offsetX, toPix(we.body.getPosition().y-(float)(we.size))+offsetY, bsize*2, bsize*2);	
		}

		private void drawCircleFilled(Graphics g, WorldElement we, Color orange) {
			g.setColor(orange);
			int bsize = toPix((float)we.size);
			g.fillOval(toPix(we.body.getPosition().x-(float)(we.size))+offsetX-strokeThickness, toPix(we.body.getPosition().y-(float)(we.size))+offsetY-strokeThickness, (bsize+strokeThickness)*2,  (bsize+strokeThickness)*2);
		}

	private void drawStaticBox(Graphics g, VirtualWorldElement we, Color c) {
		
		g.setColor(c);
		Vec2 points[] = new Vec2[4];
		points[0]= new Vec2(-we.size,we.size);
		points[1]= new Vec2(we.size,we.size);
		points[2]= new Vec2(we.size,-we.size);
		points[3]= new Vec2(-we.size,-we.size);
		for(int i = 0 ; i < 4 ; i++)
			points[i].set(we.getWorldPoint(points[i]));

        g.drawLine(toPix(points[0].x)+offsetX, toPix(points[0].y)+offsetY,toPix(points[1].x)+offsetX, toPix(points[1].y)+offsetY);
        g.drawLine(toPix(points[1].x)+offsetX, toPix(points[1].y)+offsetY,toPix(points[2].x)+offsetX, toPix(points[2].y)+offsetY);
        g.drawLine(toPix(points[2].x)+offsetX, toPix(points[2].y)+offsetY,toPix(points[3].x)+offsetX, toPix(points[3].y)+offsetY);
        g.drawLine(toPix(points[3].x)+offsetX, toPix(points[3].y)+offsetY,toPix(points[0].x)+offsetX, toPix(points[0].y)+offsetY);
        if(fill)
        {
            g.drawLine(toPix(points[0].x)+offsetX, toPix(points[0].y)+offsetY,toPix(points[2].x)+offsetX, toPix(points[2].y)+offsetY);
            g.drawLine(toPix(points[3].x)+offsetX, toPix(points[3].y)+offsetY,toPix(points[1].x)+offsetX, toPix(points[1].y)+offsetY);
        }
	}

    private void drawCaptureBar(Graphics g, Area a, Color c) {
        g.setColor(c);
        Vec2 points[] = new Vec2[3];
        points[0] = new Vec2(- a.size,-a.size - 6);
        points[1] = new Vec2(a.capture / 5.0f - a.size , - a.size - 6);
        points[2] = new Vec2(20 - a.size,-a.size - 6);

        for(int i = 0 ; i < 3 ; i++)
            points[i].set(a.getWorldPoint(points[i]));

        if (a.capture > 0)
            g.drawLine(toPix(points[0].x)+offsetX, toPix(points[0].y)+offsetY,toPix(points[1].x)+offsetX, toPix(points[1].y)+offsetY);
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 120));
        g.drawLine(toPix(points[0].x)+offsetX, toPix(points[0].y)+offsetY,toPix(points[2].x)+offsetX, toPix(points[2].y)+offsetY);
    }

	private void drawBorders(Graphics g) {
		 g.setColor(ColorTheme.obstacleColor);
		 g.drawLine(toPix(env.getCorners()[0].x)+offsetX, toPix(env.getCorners()[0].y)+offsetY,toPix(env.getCorners()[1].x)+offsetX, toPix(env.getCorners()[1].y)+offsetY);
		 g.drawLine(toPix(env.getCorners()[1].x)+offsetX, toPix(env.getCorners()[1].y)+offsetY,toPix(env.getCorners()[2].x)+offsetX, toPix(env.getCorners()[2].y)+offsetY);
		 g.drawLine(toPix(env.getCorners()[2].x)+offsetX, toPix(env.getCorners()[2].y)+offsetY,toPix(env.getCorners()[3].x)+offsetX, toPix(env.getCorners()[3].y)+offsetY);
		 g.drawLine(toPix(env.getCorners()[3].x)+offsetX, toPix(env.getCorners()[3].y)+offsetY,toPix(env.getCorners()[0].x)+offsetX, toPix(env.getCorners()[0].y)+offsetY);
	}

	int toPix(float val)
    {
    	return (int)(val*scale);
    }
	
	float fromPix(int val)
    {
    	return ((float)val/scale);
    }

	public static class ColorTheme
	{
		public static Color backgroundPatternColor = Color.green.darker().darker().darker().darker();
		public static Color debrisColor = Color.red;
		public static Color backgroundColor = Color.black;
		public static Color defaultBotColor = Color.red;
		public static Color obstacleColor = Color.green;
		public static Color infoFontColor = Color.green;
		public static Color virtualColor = Color.orange;
		public static Color projectileColor = Color.yellow;
		public static Color sensorSecondaryColor = Color.darkGray;
		public static Color other = Color.blue;
		// Les couleurs d'équipe sont aléatoires (cf. Environment2DMultiBot.createTeam) et appliquées
		// à chaque bot via Team.addMember -> bot.setColor, donc drawBot() peut se contenter de bot.getColor().
	}
	
    public Environment2D getEnvironment()
    {
    	return env;
    }
}
