package me.legitzx.bot.core;

import me.legitzx.bot.modules.Commands;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {
    private static String TOKEN = "NjM1OTgzMDczMjEyODkxMTM2.Xa5N_w.A67ezFGaSczHYbIlRPGXGaEFnVE";

    public static void main(String[] args) throws LoginException {
        new JDABuilder(TOKEN)
                .addEventListeners(new Commands())
                .setActivity(Activity.playing("MWCL"))
                .build();
    }
}

/* MYSQL TABLE
  CREATE TABLE teams (
   id INT AUTO_INCREMENT PRIMARY KEY,
   team_name VARCHAR(12) NOT NULL,
   region VARCHAR(2) NOT NULL,
   rank_number INT,
   warning_points INT,
   conference VARCHAR(1),
   p1 VARCHAR(54),
   p2 VARCHAR(54),
   p3 VARCHAR(54),
   p4 VARCHAR(54),
   p5 VARCHAR(54),
   p6 VARCHAR(54),
   p7 VARCHAR(54),
   p8 VARCHAR(54),
   p9 VARCHAR(54),
   p10 VARCHAR(54),
   p11 VARCHAR(54),
   p12 VARCHAR(54),
   p13 VARCHAR(54),
   p14 VARCHAR(54),
   p15 VARCHAR(54),
   p16 VARCHAR(54),
   p17 VARCHAR(54),
   p18 VARCHAR(54),
   p19 VARCHAR(54),
   p20 VARCHAR(54),
   p21 VARCHAR(54),
   p22 VARCHAR(54),
   p23 VARCHAR(54),
   p24 VARCHAR(54),
   p25 VARCHAR(54),
   p26 VARCHAR(54),
   p27 VARCHAR(54),
   p28 VARCHAR(54),
   p29 VARCHAR(54),
   p30 VARCHAR(54),
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 );
 */