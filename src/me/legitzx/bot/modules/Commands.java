package me.legitzx.bot.modules;

import me.legitzx.bot.Info;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Commands extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");

        Role manager = event.getGuild().getRolesByName("Management", true).get(0);

        if(args[0].equalsIgnoreCase(Info.PREFIX + "roster")) {
            if(!getRoster(event.getChannel(), args[1])) {
                event.getChannel().sendMessage("**Team does not exist**").queue();
            }
        }

        // Management Commands
        if(args[0].equalsIgnoreCase(Info.PREFIX + "createteam")) {
            if(event.getMember().getRoles().contains(manager)) {
                // !createteam <team> <region>
                if(args.length == 3) {
                    if(args[1].length() < 12) {
                        if(args[2].length() <= 2) {
                            //Everything is GOOD
                            if(createTeam(args[1], args[2])) {
                                event.getChannel().sendMessage("**Team Creation was successful**").queue();
                            }
                        } else {
                            invalidArgsCreateTeam(event.getChannel());
                        }
                    } else {
                        invalidArgsCreateTeam(event.getChannel());
                    }
                } else {
                    invalidArgsCreateTeam(event.getChannel());
                }
            } else {
                event.getMessage().delete().reason("Tried to execute !createteam").queueAfter(5, TimeUnit.SECONDS);
                event.getChannel().sendMessage("**You are not a manager!**").complete().delete().queueAfter(5, TimeUnit.SECONDS);
            }
        }

        if(args[0].equalsIgnoreCase(Info.PREFIX + "addleader")) {
            if(event.getMember().getRoles().contains(manager)) {
                // !addleader <team> <ign> <uuid>
                if(args.length == 4) {
                    addLeader(args[1], args[2], args[3], event.getChannel());
                } else {
                    // Invalid args
                }
            } else {
                event.getMessage().delete().reason("Tried to execute !addleader").queueAfter(5, TimeUnit.SECONDS);
                event.getChannel().sendMessage("**You are not a manager!**").complete().delete().queueAfter(5, TimeUnit.SECONDS);
            }
        }

        if(args[0].equalsIgnoreCase(Info.PREFIX + "addplayer")) {
            if(event.getMember().getRoles().contains(manager)) {
                // !addplayer <team> <ign> <uuid>
                if(args.length == 4) {
                    addPlayer(args[1], args[2], args[3], event.getChannel());
                    System.out.println("add player1");
                } else {
                    // Invalid args
                }
            } else {
                event.getMessage().delete().reason("Tried to execute !addplayer").queueAfter(5, TimeUnit.SECONDS);
                event.getChannel().sendMessage("**You are not a manager!**").complete().delete().queueAfter(5, TimeUnit.SECONDS);
            }
        }
    }

    private void addPlayer(String team, String ign, String uuid, TextChannel channel) {
        ResultSet myRs = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        Connection conn = connectSQL();

        String playerFull = ign + "," + uuid + "0"; // 1 means he will get the crown when someone executes !roster teamName // 0 = normal member

        String query = "SELECT * FROM teams WHERE team_name = '" + team + "'";

        try {
            statement = conn.createStatement();
            myRs = statement.executeQuery(query);

            if(!myRs.next()) {  //  https://javarevisited.blogspot.com/2016/10/how-to-check-if-resultset-is-empty-in-Java-JDBC.html
                // Team does not exist

            } else {
                do {
                    ArrayList<String> newList = new ArrayList<>();
                    int check = 0;

                    for(int x = 1; x <= 30; x++) {
                        String player = "";
                        player = myRs.getString("p" + Integer.toString(x));
                        if(player == null && check == 0) { // Makes sure this shit gets ran ONLY once
                            newList.add(playerFull);
                            check++;
                        }
                        newList.add(player);
                    }
                    for(int x = 1; x <= 30; x++) {
                        String newValue = newList.get(x - 1);

                        String player = "p" + Integer.toString(x);

                        query = "UPDATE teams SET " + player + " = ? WHERE team_name = '" + team + "'";

                        preparedStatement = conn.prepareStatement(query);
                        preparedStatement.setString(1, newValue);
                        preparedStatement.executeUpdate();
                    }
                    System.out.println(newList.toString());
                } while(myRs.next());
            }

            myRs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addLeader(String team, String ign, String uuid, TextChannel channel) {
        ResultSet myRs = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        Connection conn = connectSQL();

        String leaderFull = ign + "," + uuid + "1"; // 1 means he will get the crown when someone executes !roster teamName

        String query = "SELECT * FROM teams WHERE team_name = '" + team + "'";

        try {
            statement = conn.createStatement();
            myRs = statement.executeQuery(query);

            if(!myRs.next()) {  //  https://javarevisited.blogspot.com/2016/10/how-to-check-if-resultset-is-empty-in-Java-JDBC.html
                // Team does not exist

            } else {
                do {
                    ArrayList<String> newList = new ArrayList<>();

                    newList.add(leaderFull);
                    for(int x = 1; x <= 30; x++) {
                        String player = "";
                        player = myRs.getString("p" + Integer.toString(x));
                        newList.add(player);
                    }


                    for(int x = 1; x <= 30; x++) {
                        String newValue = newList.get(x - 1);

                        String player = "p" + Integer.toString(x);

                        query = "UPDATE teams SET " + player + " = ? WHERE team_name = '" + team + "'";

                        preparedStatement = conn.prepareStatement(query);
                        preparedStatement.setString(1, newValue);
                        preparedStatement.executeUpdate();
                    }

                    /*
                    team = myRs.getString("team_name");
                    rank = myRs.getInt("rank_number");
                    region = myRs.getString("region");
                    warningPoints = myRs.getInt("warning_points");
                    */


                } while(myRs.next());
            }

            myRs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void invalidArgsCreateTeam(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!createteam {Team name} {Region NA/EU}", "{} = Required", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }



    private boolean getRoster(TextChannel channel, String team) throws NullPointerException { // YIKES THE CODEBASE!!!
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



                    if(p1 == null) {
                        p1 = "";
                    } else {
                        p1 = cleanName(p1); // Gets rid of uuid
                    }

                    if(p2 == null) {
                        p2 = "";
                    } else {
                        p2 = cleanName(p2);
                    }

                    if(p3 == null) {
                        p3 = "";
                    } else {
                        p3 = cleanName(p3);
                    }

                    if(p4 == null) {
                        p4 = "";
                    } else {
                        p4 = cleanName(p4);
                    }

                    if(p5 == null) {
                        p5 = "";
                    } else {
                        p5 = cleanName(p5);
                    }

                    if(p6 == null) {
                        p6 = "";
                    } else {
                        p6 = cleanName(p6);
                    }

                    if(p7 == null) {
                        p7 = "";
                    } else {
                        p7 = cleanName(p7);
                    }

                    if(p8 == null) {
                        p8 = "";
                    } else {
                        p8 = cleanName(p8);
                    }

                    if(p9 == null) {
                        p9 = "";
                    } else {
                        p9 = cleanName(p9);
                    }

                    if(p10 == null) {
                        p10 = "";
                    } else {
                        p10 = cleanName(p10);
                    }

                    if(p11 == null) {
                        p11 = "";
                    } else {
                        p11 = cleanName(p11);
                    }

                    if(p12 == null) {
                        p12 = "";
                    } else {
                        p12 = cleanName(p12);
                    }

                    if(p13 == null) {
                        p13 = "";
                    } else {
                        p13 = cleanName(p13);
                    }

                    if(p14 == null) {
                        p14 = "";
                    } else {
                        p14 = cleanName(p14);
                    }

                    if(p15 == null) {
                        p15 = "";
                    } else {
                        p15 = cleanName(p15);
                    }

                    if(p16 == null) {
                        p16 = "";
                    } else {
                        p16 = cleanName(p16);
                    }

                    if(p17 == null) {
                        p17 = "";
                    } else {
                        p17 = cleanName(p17);
                    }

                    if(p18 == null) {
                        p18 = "";
                    } else {
                        p18 = cleanName(p18);
                    }

                    if(p19 == null) {
                        p19 = "";
                    } else {
                        p19 = cleanName(p19);
                    }

                    if(p20 == null) {
                        p20 = "";
                    } else {
                        p20 = cleanName(p20);
                    }

                    if(p21 == null) {
                        p21 = "";
                    } else {
                        p21 = cleanName(p21);
                    }

                    if(p22 == null) {
                        p22 = "";
                    } else {
                        p22 = cleanName(p22);
                    }

                    if(p23 == null) {
                        p23 = "";
                    } else {
                        p23 = cleanName(p23);
                    }

                    if(p24 == null) {
                        p24 = "";
                    } else {
                        p24 = cleanName(p24);
                    }

                    if(p25 == null) {
                        p25 = "";
                    } else {
                        p25 = cleanName(p25);
                    }

                    if(p26 == null) {
                        p26 = "";
                    } else {
                        p26 = cleanName(p26);
                    }

                    if(p27 == null) {
                        p27 = "";
                    } else {
                        p27 = cleanName(p27);
                    }

                    if(p28 == null) {
                        p28 = "";
                    } else {
                        p28 = cleanName(p4);
                    }

                    if(p29 == null) {
                        p29 = "";
                    } else {
                        p29 = cleanName(p29);
                    }

                    if(p30 == null) {
                        p30 = "";
                    } else {
                        p30 = cleanName(p30);
                    }
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
        builder.setDescription("Rank: " + rank + " | " + "Region: " + region.toUpperCase() + " | " + "Warning Points: " + warningPoints + " | " + "League: MWCL");
        builder.addField("Roster", p1 + " - " + p16 + "\n" + p2 + " - " + p17 + "\n" + p3 + " - " + p18 + "\n" +p4 + " - " + p19 + "\n" +p5 + " - " + p20 + "\n" +p6 + " - " + p21 + "\n" +p7 + " - " + p22 + "\n" +p8 + " - " + p23 + "\n" +p9 + " - " + p24 + "\n" +p10 + " - " + p25 + "\n" +p11 + " - " + p26 + "\n" +p12 + " - " + p27 + "\n" +p13 + " - " + p28 + "\n" +p14 + " - " + p29 + "\n" +p15 + " - " + p30, true);

        channel.sendMessage(builder.build()).queue();

        return true;
    }

    private String cleanName(String name) { //https://stackoverflow.com/questions/7683448/in-java-how-to-get-substring-from-a-string-till-a-character-c
        int index = name.indexOf(",");

        String crown = "\uD83D\uDC51";

        if(name.substring(name.length() - 1).equalsIgnoreCase("1")) {

        }

        String newStr = "";
        if(index != -1) {
            newStr = name.substring(0, index);
        }

        if(name.substring(name.length() - 1).equalsIgnoreCase("1")) { // This person is a leader...
            newStr += crown;
        }
        return newStr;
    }

    private boolean createTeam(String teamName, String region) {
        int rank_number = 0;
        int warning_points = 0;

        // String query = "INSERT INTO teams (team_name, region, rank_number, warning_points, leader) VALUES (`" + teamName +"`, `" + region +"`, `" + rank_number + "`, `" + warning_points + "`, `" + leaderIGN + "," + leaderUUID + "`);";
        String query = "INSERT INTO teams (team_name, region, rank_number, warning_points) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = connectSQL();
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, teamName);
            pst.setString(2, region);
            pst.setInt(3, rank_number);
            pst.setInt(4, warning_points);

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

    private Connection connectSQL() {
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
