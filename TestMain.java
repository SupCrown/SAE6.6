package androidgameserver;

import androidgameserver.bot.BotBody2D;
import androidgameserver.environment.Environment2DMultiBot;
import androidgameserver.environment.Team;

import java.awt.*;

public class TestMain {
    public static void main(String[] args) {
        Team testTeam = new Team(1,"test", Color.red);
        BotBody2D testBot = new BotBody2D(new Environment2DMultiBot());
        BotBody2D testBot2 = new BotBody2D(new Environment2DMultiBot());

        testTeam.addMember(testBot);
        testTeam.addMember(testBot2);
        System.out.println(testTeam.getMembers());
        testTeam.contains(testBot2);
        testTeam.delMember(testBot2);
        testTeam.contains(testBot2);
        System.out.println(testTeam.getMembers());

    }
}
