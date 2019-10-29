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

        if(args[0].equalsIgnoreCase(Info.PREFIX + "rankings")) {
            // !rankings
            if(args.length == 1) {
                getRankings(event.getChannel());
            } else {
                invalidArgsRankings(event.getChannel());
            }
        }

        if(args[0].equalsIgnoreCase(Info.PREFIX + "roster")) {
            if(args.length == 2) {
                if(!getRoster(event.getChannel(), args[1])) {
                    event.getChannel().sendMessage("**Team does not exist**").queue();
                }
            } else {
                invalidArgsRoster(event.getChannel());
            }
        }

        if(args[0].equalsIgnoreCase(Info.PREFIX + "player")) {
            // !player <ign>
            if(args.length == 2) {
                playerSearch(args[1], event.getChannel());
            } else {
                invalidArgsPlayer(event.getChannel());
            }
        }



        // Management Commands
        if(args[0].equalsIgnoreCase(Info.PREFIX + "createteam")) {
            if(event.getMember().getRoles().contains(manager)) {
                // !createteam <team> <region> <conference(A/B)>
                if(args.length == 4) {
                    if(args[1].length() < 12) {
                        if(args[2].length() <= 2) {
                            if(args[3].length() == 1) {
                                //Everything is GOOD
                                if(createTeam(args[1], args[2], args[3])) {
                                    event.getChannel().sendMessage("**Team Creation was successful**").queue();
                                } else {
                                    event.getChannel().sendMessage("**Team Creation was unsuccessful! There may be another team with that name already.**").queue();
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
                    invalidArgsAddLeader(event.getChannel());
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
                } else {
                    invalidArgsAddPlayer(event.getChannel());
                }
            } else {
                event.getMessage().delete().reason("Tried to execute !addplayer").queueAfter(5, TimeUnit.SECONDS);
                event.getChannel().sendMessage("**You are not a manager!**").complete().delete().queueAfter(5, TimeUnit.SECONDS);
            }
        }

        if(args[0].equalsIgnoreCase(Info.PREFIX + "removeplayer")) {
            if(event.getMember().getRoles().contains(manager)) {
                // !removeplayer <team> <ign> <uuid>
                if(args.length == 3) {
                    removePlayer(args[1], args[2], event.getChannel());
                } else {
                    invalidArgsRemovePlayer(event.getChannel());
                }
            } else {
                event.getMessage().delete().reason("Tried to execute !removeplayer").queueAfter(5, TimeUnit.SECONDS);
                event.getChannel().sendMessage("**You are not a manager!**").complete().delete().queueAfter(5, TimeUnit.SECONDS);
            }
        }

        if(args[0].equalsIgnoreCase(Info.PREFIX + "disband")) {
            if(event.getMember().getRoles().contains(manager)) {
                // !disband <team>
                if(args.length == 2) {
                    disbandteam(args[1], event.getChannel());
                } else {
                    invalidArgsDisband(event.getChannel());
                }
            } else {
                event.getMessage().delete().reason("Tried to execute !disband").queueAfter(5, TimeUnit.SECONDS);
                event.getChannel().sendMessage("**You are not a manager!**").complete().delete().queueAfter(5, TimeUnit.SECONDS);
            }
        }

        if(args[0].equalsIgnoreCase(Info.PREFIX + "promoterank")) {
            if(event.getMember().getRoles().contains(manager)) {
                // !promoterank <team>
                if(args.length == 2) {
                    promoteRank(args[1], event.getChannel());
                } else {
                    invalidArgsPromoteRank(event.getChannel());
                }
            } else {
                event.getMessage().delete().reason("Tried to execute !promoterank").queueAfter(5, TimeUnit.SECONDS);
                event.getChannel().sendMessage("**You are not a manager!**").complete().delete().queueAfter(5, TimeUnit.SECONDS);
            }
        }

        if(args[0].equalsIgnoreCase(Info.PREFIX + "demoterank")) {
            if(event.getMember().getRoles().contains(manager)) {
                // !demoterank <team>
                if(args.length == 2) {
                    demoteRank(args[1], event.getChannel());
                } else {
                    invalidArgsDemoteRank(event.getChannel());
                }
            } else {
                event.getMessage().delete().reason("Tried to execute !demoterank").queueAfter(5, TimeUnit.SECONDS);
                event.getChannel().sendMessage("**You are not a manager!**").complete().delete().queueAfter(5, TimeUnit.SECONDS);
            }
        }
    }

    private void getRankings(TextChannel channel) {
        Connection conn = connectSQL();
        ArrayList<String> teams = new ArrayList<>();
        ArrayList<Integer> points = new ArrayList<>();
        StringBuilder str = new StringBuilder();

        String query = "SELECT * FROM teams ORDER by rank_number DESC";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet myRs = preparedStatement.executeQuery();

           while(myRs.next()) {
               teams.add(myRs.getString("team_name"));
               points.add(myRs.getInt("rank_number"));
           }

           EmbedBuilder builder = new EmbedBuilder();
           builder.setColor(Color.RED);
           builder.setTitle("Rankings");
           for(int x = 0; x < teams.size(); x++) {
               str.append(Integer.toString(x + 1) + ". " +teams.get(x) + " - " + Integer.toString(points.get(x)) + "\n");
           }

           builder.setDescription(str.toString());
           channel.sendMessage(builder.build()).queue();

            preparedStatement.close();
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void demoteRank(String team, TextChannel channel) {
        Connection conn = connectSQL();

        String query = "UPDATE teams SET rank_number = rank_number - 1 WHERE team_name = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, team);

            int i = preparedStatement.executeUpdate();

            if(i == 1) {
                channel.sendMessage("**Team: ``" + team + "`` was moved down a rank.**").queue();
            } else {
                channel.sendMessage("**Team not found.**").queue();
            }

            preparedStatement.close();
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void promoteRank(String team, TextChannel channel) {
        Connection conn = connectSQL();

        String query = "UPDATE teams SET rank_number = rank_number + 1 WHERE team_name = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, team);

            int i = preparedStatement.executeUpdate();

            if(i == 1) {
                channel.sendMessage("**Team: ``" + team + "`` was moved up a rank.**").queue();
            } else {
                channel.sendMessage("**Team not found.**").queue();
            }

            preparedStatement.close();
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void playerSearch(String ign, TextChannel channel) {
        Connection conn = connectSQL();
        String team = "";

        String query = "SELECT * FROM teams WHERE p1 LIKE ? OR p2 LIKE ? OR p3 LIKE ? OR p4 LIKE ? OR p5 LIKE ? OR p6 LIKE ? OR p7 LIKE ? OR p8 LIKE ? OR p9 LIKE ? OR p10 LIKE ? OR p11 LIKE ? OR p12 LIKE ? OR p13 LIKE ? OR p14 LIKE ? OR p15 LIKE ? OR p16 LIKE ? OR p17 LIKE ? OR p18 LIKE ? OR p19 LIKE ? OR p20 LIKE ? OR p21 LIKE ? OR p22 LIKE ? OR p23 LIKE ? OR p24 LIKE ? OR p25 LIKE ? OR p26 LIKE ? OR p27 LIKE ? OR p28 LIKE ? OR p29 LIKE ? OR p30 LIKE ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            for(int x = 1; x <= 30; x++) {
                preparedStatement.setString(x, ign + "%"); // https://www.tutorialspoint.com/sql/sql-like-clause.htm
            }

            ResultSet myRs = preparedStatement.executeQuery();

            if(!myRs.next()) {
                // Guy dosent exist
                channel.sendMessage("**Player: ``" + ign + "`` does not exist!**").queue();
            } else {
                do {
                    team = myRs.getString("team_name");
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle(ign);
                    builder.setColor(Color.RED);
                    builder.addField("Team:", team, true);
                    channel.sendMessage(builder.build()).queue();
                } while(myRs.next());
            }

            preparedStatement.close();
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void disbandteam(String team, TextChannel channel) {
        Connection conn = connectSQL();

        String query = "DELETE FROM teams WHERE team_name = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, team);
            int i = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            if(i == 1) {
                channel.sendMessage("**Team: ``" + team + "`` was successfully disbaned!**").queue();
            } else {
                channel.sendMessage("**Team: ``" + team + "`` was unsuccessfully disbaned!**").queue();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void removePlayer(String team, String ign, TextChannel channel) {
        ResultSet myRs = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        Connection conn = connectSQL();

        String query = "SELECT * FROM teams WHERE team_name = '" + team + "'";

        try {
            statement = conn.createStatement();
            myRs = statement.executeQuery(query);

            if(!myRs.next()) {  //  https://javarevisited.blogspot.com/2016/10/how-to-check-if-resultset-is-empty-in-Java-JDBC.html
                // Team does not exist
                channel.sendMessage("**Team does not exist!**").complete().delete().queueAfter(5, TimeUnit.SECONDS);
            } else {
                do {
                    ArrayList<String> newList = new ArrayList<>();
                    int count = 0;
                    boolean check = false;
                    for(int x = 1; x <= 30; x++) {
                        String player = "";
                        String shortPlayer = "";
                        player = myRs.getString("p" + Integer.toString(x)); // Player will be null if there is no player in the column
                        if(player != null) {
                            shortPlayer = cleanName(player, false);
                        }
                        if(!shortPlayer.equalsIgnoreCase(ign)) {
                            newList.add(player);
                        } else {
                            count++;
                            check = true;
                        }

                        if(check && (player == null || x == 30)) {
                            for(int i = 0; i < count; i++) { // When we remove one person from the team, we need to add another value to the end of the array
                                newList.add(null);
                                check = false;
                            }
                        }
                    }
                    System.out.println(newList.size());
                    for(int x = 1; x <= 30; x++) {
                        String newValue = newList.get(x - 1);

                        String player = "p" + Integer.toString(x);

                        query = "UPDATE teams SET " + player + " = ? WHERE team_name = '" + team + "'";

                        preparedStatement = conn.prepareStatement(query);
                        preparedStatement.setString(1, newValue);
                        preparedStatement.executeUpdate();
                    }
                    channel.sendMessage("**Make sure to type: ``!roster " + team + "`` to make sure ``" + ign + "`` was removed!**").queue();
                } while(myRs.next());
            }

            myRs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
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
                channel.sendMessage("**Team does not exist!**").complete().delete().queueAfter(5, TimeUnit.SECONDS);
            } else {
                do {
                    ArrayList<String> newList = new ArrayList<>();
                    ArrayList<String> realPlayer = new ArrayList<>();
                    int check = 0;

                    realPlayer.add(playerFull);
                    for(int x = 1; x <= 30; x++) {
                        String player = "";
                        player = myRs.getString("p" + Integer.toString(x));
                        if(player == null && check == 0) { // Makes sure this shit gets ran ONLY once
                            newList.add(playerFull);
                            check++;
                        }
                        newList.add(player);
                        if(player != null) {
                            realPlayer.add(player);
                        }
                    }

                    if(realPlayer.size() > 30) {
                        channel.sendMessage("**This team already has ``30`` players! Try: ``!removeplayer <Team> <IGN>`` to make room!**").queue();
                    } else {
                        for(int x = 1; x <= 30; x++) {
                            String newValue = newList.get(x - 1);

                            String player = "p" + Integer.toString(x);

                            query = "UPDATE teams SET " + player + " = ? WHERE team_name = '" + team + "'";

                            preparedStatement = conn.prepareStatement(query);
                            preparedStatement.setString(1, newValue);
                            preparedStatement.executeUpdate();
                        }
                        channel.sendMessage("**Player: ``" + ign +"`` successfully added to Team: ``" + team + "``.**").queue();
                    }
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
                    ArrayList<String> realPlayer = new ArrayList<>(); // Used for measuring the amount of players on a team!

                    newList.add(leaderFull);
                    realPlayer.add(leaderFull);
                    for(int x = 1; x <= 30; x++) {
                        String player = "";
                        player = myRs.getString("p" + Integer.toString(x));
                        newList.add(player);
                        if(player != null) {
                            realPlayer.add(player);
                        }
                    }

                    if(realPlayer.size() > 30) {
                        channel.sendMessage("**This team already has ``30`` players! Try: ``!removeplayer <Team> <IGN>`` to make room!**").queue();
                    } else {
                        for(int x = 1; x <= 30; x++) {
                            String newValue = newList.get(x - 1);

                            String player = "p" + Integer.toString(x);

                            query = "UPDATE teams SET " + player + " = ? WHERE team_name = '" + team + "'";

                            preparedStatement = conn.prepareStatement(query);
                            preparedStatement.setString(1, newValue);
                            preparedStatement.executeUpdate();
                        }

                        channel.sendMessage("**Leader:``" + ign +"`` successfully added to Team: ``" + team + "``.**").queue();
                    }
                } while(myRs.next());
            }

            myRs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void invalidArgsRoster(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!roster {Team name}", "{} = Required", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }

    private void invalidArgsCreateTeam(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!createteam {Team name} {Region NA/EU} {Confernce A/B}", "{} = Required", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }

    private void invalidArgsAddLeader(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!addleader {Team name} {IGN} {UUID}", "{} = Required", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }

    private void invalidArgsAddPlayer(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!addplayer {Team name} {IGN} {UUID}", "{} = Required", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }

    private void invalidArgsRemovePlayer(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!removeplayer {Team name} {IGN}", "{} = Required", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }

    private void invalidArgsDisband(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!disband {Team name}", "{} = Required", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }

    private void invalidArgsPlayer(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!player {IGN}", "{} = Required", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }

    private void invalidArgsPromoteRank(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!promoterank {Team name}", "{} = Required", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }

    private void invalidArgsDemoteRank(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!demoterank {Team name}", "{} = Required", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }

    private void invalidArgsRankings(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invalid Arguments");
        builder.addField("!rankings", "{", true);
        builder.setColor(Color.RED);
        channel.sendMessage(builder.build()).queue();
    }



    private boolean getRoster(TextChannel channel, String team) throws NullPointerException { // YIKES THE CODEBASE!!!
        int rank = 0;
        String region = "";
        int warningPoints = 0;
        String conference = "";

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
                    conference = myRs.getString("conference");


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
                        p1 = cleanName(p1, true); // Gets rid of uuid
                    }

                    if(p2 == null) {
                        p2 = "";
                    } else {
                        p2 = cleanName(p2, true);
                    }

                    if(p3 == null) {
                        p3 = "";
                    } else {
                        p3 = cleanName(p3, true);
                    }

                    if(p4 == null) {
                        p4 = "";
                    } else {
                        p4 = cleanName(p4, true);
                    }

                    if(p5 == null) {
                        p5 = "";
                    } else {
                        p5 = cleanName(p5, true);
                    }

                    if(p6 == null) {
                        p6 = "";
                    } else {
                        p6 = cleanName(p6, true);
                    }

                    if(p7 == null) {
                        p7 = "";
                    } else {
                        p7 = cleanName(p7, true);
                    }

                    if(p8 == null) {
                        p8 = "";
                    } else {
                        p8 = cleanName(p8, true);
                    }

                    if(p9 == null) {
                        p9 = "";
                    } else {
                        p9 = cleanName(p9, true);
                    }

                    if(p10 == null) {
                        p10 = "";
                    } else {
                        p10 = cleanName(p10, true);
                    }

                    if(p11 == null) {
                        p11 = "";
                    } else {
                        p11 = cleanName(p11, true);
                    }

                    if(p12 == null) {
                        p12 = "";
                    } else {
                        p12 = cleanName(p12, true);
                    }

                    if(p13 == null) {
                        p13 = "";
                    } else {
                        p13 = cleanName(p13, true);
                    }

                    if(p14 == null) {
                        p14 = "";
                    } else {
                        p14 = cleanName(p14, true);
                    }

                    if(p15 == null) {
                        p15 = "";
                    } else {
                        p15 = cleanName(p15, true);
                    }

                    if(p16 == null) {
                        p16 = "";
                    } else {
                        p16 = cleanName(p16, true);
                    }

                    if(p17 == null) {
                        p17 = "";
                    } else {
                        p17 = cleanName(p17, true);
                    }

                    if(p18 == null) {
                        p18 = "";
                    } else {
                        p18 = cleanName(p18, true);
                    }

                    if(p19 == null) {
                        p19 = "";
                    } else {
                        p19 = cleanName(p19, true);
                    }

                    if(p20 == null) {
                        p20 = "";
                    } else {
                        p20 = cleanName(p20, true);
                    }

                    if(p21 == null) {
                        p21 = "";
                    } else {
                        p21 = cleanName(p21, true);
                    }

                    if(p22 == null) {
                        p22 = "";
                    } else {
                        p22 = cleanName(p22, true);
                    }

                    if(p23 == null) {
                        p23 = "";
                    } else {
                        p23 = cleanName(p23, true);
                    }

                    if(p24 == null) {
                        p24 = "";
                    } else {
                        p24 = cleanName(p24, true);
                    }

                    if(p25 == null) {
                        p25 = "";
                    } else {
                        p25 = cleanName(p25, true);
                    }

                    if(p26 == null) {
                        p26 = "";
                    } else {
                        p26 = cleanName(p26, true);
                    }

                    if(p27 == null) {
                        p27 = "";
                    } else {
                        p27 = cleanName(p27, true);
                    }

                    if(p28 == null) {
                        p28 = "";
                    } else {
                        p28 = cleanName(p28, true);
                    }

                    if(p29 == null) {
                        p29 = "";
                    } else {
                        p29 = cleanName(p29, true);
                    }

                    if(p30 == null) {
                        p30 = "";
                    } else {
                        p30 = cleanName(p30, true);
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
        builder.setDescription("Rank: " + rank + " | " + "Region: " + region.toUpperCase() + " | " + "Warning Points: " + warningPoints + " | " + "Conference: " + conference +  " | " + "League: MWCL");
        builder.addField("Roster", p1 + " - " + p16 + "\n" + p2 + " - " + p17 + "\n" + p3 + " - " + p18 + "\n" +p4 + " - " + p19 + "\n" +p5 + " - " + p20 + "\n" +p6 + " - " + p21 + "\n" +p7 + " - " + p22 + "\n" +p8 + " - " + p23 + "\n" +p9 + " - " + p24 + "\n" +p10 + " - " + p25 + "\n" +p11 + " - " + p26 + "\n" +p12 + " - " + p27 + "\n" +p13 + " - " + p28 + "\n" +p14 + " - " + p29 + "\n" +p15 + " - " + p30, true);

        channel.sendMessage(builder.build()).queue();

        return true;
    }

    private String cleanName(String name, boolean checkForLeader) { //https://stackoverflow.com/questions/7683448/in-java-how-to-get-substring-from-a-string-till-a-character-c
        int index = name.indexOf(",");

        String crown = "\uD83D\uDC51";

        String newStr = "";
        if(index != -1) {
            newStr = name.substring(0, index);
        }

        if((name.substring(name.length() - 1).equalsIgnoreCase("1")) && checkForLeader) { // This person is a leader...
            newStr += crown;
        }
        return newStr;
    }

    private boolean createTeam(String teamName, String region, String confernce) {
        int rank_number = 0;
        int warning_points = 0;

        try { // This code checks whether that team already exists
            int exist = 0;

            Connection conn = connectSQL();
            String query = "SELECT count(*) FROM teams WHERE team_name = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, teamName);
            ResultSet myRs = statement.executeQuery();
            if(myRs.next()) {
                exist = myRs.getInt(1);
            }
            if(exist > 0) {
                return false;
            }

            conn.close();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // String query = "INSERT INTO teams (team_name, region, rank_number, warning_points, leader) VALUES (`" + teamName +"`, `" + region +"`, `" + rank_number + "`, `" + warning_points + "`, `" + leaderIGN + "," + leaderUUID + "`);";
        String query = "INSERT INTO teams (team_name, region, rank_number, warning_points, conference) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = connectSQL();
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, teamName);
            pst.setString(2, region);
            pst.setInt(3, rank_number);
            pst.setInt(4, warning_points);
            pst.setString(5, confernce);

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
            //System.out.println("Insert completed.");
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
