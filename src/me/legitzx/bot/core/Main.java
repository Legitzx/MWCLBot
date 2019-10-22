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
                .setActivity(Activity.playing("Mega Walls Comp. Team"))
                .build();
    }
}
