package me.legitzx.bot.modules;

import me.legitzx.bot.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;

public class Commands extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        /*
        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRs = null;

        try {
            // 1. Get a connection to database
            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "student" , "student");

            // 2. Create a statement
            myStmt = myConn.createStatement();

            // 3. Execute SQL query
            myRs = myStmt.executeQuery("select * from employees");

            // 4. Process the result set
            while (myRs.next()) {
                System.out.println(myRs.getString("last_name") + ", " + myRs.getString("first_name"));
            }
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
        */


        String[] args = event.getMessage().getContentRaw().split(" ");

        if(args[0].equalsIgnoreCase(Info.PREFIX + "roster")) {
            if(!getRoster(event.getChannel(), args[1])) {
                event.getChannel().sendMessage("**Team does not exist**").queue();
            }
        }

        // TODO: ADD EMBEDS for all the elses
        if(args[0].equalsIgnoreCase(Info.PREFIX + "createteam")) {
            // args[1] = teamName  args[2] = Region  args[3] = teamLeaderIGN  args[4] = teamLeaderUUID
            if(args.length == 5) {
                if(args[1].length() < 12) {
                    if(args[2].length() <= 2) {
                        if(args[3].length() <= 16) {
                            if(args[4].length() <= 36) {
                                //Everything is GOOD
                                if(createTeam(args[1], args[2], args[3], args[4])) {
                                    event.getChannel().sendMessage("**Team Creation was successful**").queue();
                                }
                            } else {
                                event.getChannel().sendMessage("Invalid args.").queue();
                            }
                        } else {
                            event.getChannel().sendMessage("Invalid args.").queue();
                        }
                    } else {
                        event.getChannel().sendMessage("Invalid args.").queue();
                    }
                } else {
                    event.getChannel().sendMessage("Invalid args.").queue();
                }
            } else {
                event.getChannel().sendMessage("Invalid args.").queue();
            }
        }
    }

    public boolean getRoster(TextChannel channel, String team) {
        int rank = 0;
        String region = "";
        int warningPoints = 0;

        ResultSet myRs = null;
        Statement statement = null;
        Connection conn = connectSQL();

        String p1 = "";
        String p2 = "";
        String p3 = "";
        String p4 = "";
        String p5 = "";
        String p6 = "";
        String p7 = "";
        String p8 = "";
        String p9 = "";
        String p10 = "";
        String p11 = "";
        String p12 = "";
        String p13 = "";
        String p14 = "";
        String p15 = "";
        String p16 = "";
        String p17 = "";
        String p18 = "";
        String p19 = "";
        String p20 = "";
        String p21 = "";
        String p22 = "";
        String p23 = "";
        String p24 = "";
        String p25 = "";
        String p26 = "";
        String p27 = "";
        String p28 = "";
        String p29 = "";
        String p30 = "";


        String query = "SELECT * FROM teams WHERE team_name = '" + team + "'";

        try {
            statement = conn.createStatement();
            myRs = statement.executeQuery(query);

            if(!myRs.next()) {  //  https://javarevisited.blogspot.com/2016/10/how-to-check-if-resultset-is-empty-in-Java-JDBC.html
                // Team does not exist
                return false;
            } else {
                do {
                    team = myRs.getString("team_name");
                    rank = myRs.getInt("rank_number");
                    region = myRs.getString("region");
                    warningPoints = myRs.getInt("warning_points");

                    p1 = myRs.getString("p1");
                    p2 = myRs.getString("p2");
                    p3 = myRs.getString("p3");
                    p4 = myRs.getString("p4");
                    p5 = myRs.getString("p5");
                    p6 = myRs.getString("p6");
                    p7 = myRs.getString("p7");
                    p8 = myRs.getString("p8");
                    p9 = myRs.getString("p9");
                    p10 = myRs.getString("p10");
                    p11 = myRs.getString("p11");
                    p12 = myRs.getString("p12");
                    p13 = myRs.getString("p13");
                    p14 = myRs.getString("p14");
                    p15 = myRs.getString("p15");
                    p16 = myRs.getString("p16");
                    p17 = myRs.getString("p17");
                    p18 = myRs.getString("p18");
                    p19 = myRs.getString("p19");
                    p20 = myRs.getString("p20");
                    p21 = myRs.getString("p21");
                    p22 = myRs.getString("p22");
                    p23 = myRs.getString("p23");
                    p24 = myRs.getString("p24");
                    p25 = myRs.getString("p25");
                    p26 = myRs.getString("p26");
                    p27 = myRs.getString("p27");
                    p28 = myRs.getString("p28");
                    p29 = myRs.getString("p29");
                    p30 = myRs.getString("p30");
                } while(myRs.next());
            }

            myRs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setTitle(team);
        builder.setDescription("Rank: " + rank + " | " + "Region: " + region + " | " + "Warning Points: " + warningPoints + " | " + "League: CWCL");
        builder.addField("Roster", p1 + " - " + p16 + "\n" + p2 + " - " + p17 + "\n" + p3 + " - " + p18 + "\n" +p4 + " - " + p19 + "\n" +p5 + " - " + p20 + "\n" +p6 + " - " + p21 + "\n" +p7 + " - " + p22 + "\n" +p8 + " - " + p23 + "\n" +p9 + " - " + p24 + "\n" +p10 + " - " + p25 + "\n" +p11 + " - " + p26 + "\n" +p12 + " - " + p27 + "\n" +p13 + " - " + p28 + "\n" +p14 + " - " + p29 + "\n" +p15 + " - " + p30, true);

        channel.sendMessage(builder.build()).queue();

        return true;
    }

    public boolean createTeam(String teamName, String region, String leaderIGN, String leaderUUID) {
        int rank_number = 0;
        int warning_points = 0;

        String fullName = leaderIGN + "," + leaderUUID;
        // String query = "INSERT INTO teams (team_name, region, rank_number, warning_points, leader) VALUES (`" + teamName +"`, `" + region +"`, `" + rank_number + "`, `" + warning_points + "`, `" + leaderIGN + "," + leaderUUID + "`);";
        String query = "INSERT INTO teams (team_name, region, rank_number, warning_points, leader) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = connectSQL();
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, teamName);
            pst.setString(2, region);
            pst.setInt(3, rank_number);
            pst.setInt(4, warning_points);
            pst.setString(5, fullName);

            int i = pst.executeUpdate();

            pst.close();
            conn.close();


            if(i == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Insert completed.");
        }
        return false;
    }

    public Connection connectSQL() {
        Connection myConn = null;

        try {
            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mwclbot?serverTimezone=UTC", "root" , "root");
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
        return myConn;
    }


}
