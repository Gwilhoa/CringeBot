package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class Gift {
    public static Hashtable<EmbedBuilder, String> sendGift(String code, Member mb) throws IOException, InterruptedException {
        File f = new File("save/gift/"+code);
        String retS;
        Hashtable<EmbedBuilder, String> ret = new Hashtable<>();
        EmbedBuilder eb = new EmbedBuilder();
        if (f.exists() && f.isFile())
        {
            eb.setColor(Color.green).setTitle("Nouveau cadeau !")
                    .setDescription("tiens " + mb.getAsMention() + " j'ai trouvé ça");
            eb.setImage("https://png.pngtree.com/png-vector/20191122/ourlarge/pngtree-red-gift-box-vector-illustration-with-cute-design-isolated-on-white-png-image_2016770.jpg");
            retS = new BufferedReader(new FileReader(f)).readLine();
            f.delete();
        } else
        {
            eb.setColor(Color.RED).setTitle("Échec").setDescription("désolé j'ai rien trouvé");
            retS = null;
        }
        eb.setFooter(mb.getId());
        ret.put(eb, retS);
        return ret;
    }
    public static void openGift(ButtonInteractionEvent e) throws InterruptedException {
        String ret = e.getButton().getId().substring("gift_".length());
        Member member = e.getMember();
        switch (ret.split(";")[0]) {
            case "coins":
                Squads.getstats(member).addCoins(Long.parseLong(ret.split(";")[1]));
                e.getChannel().sendMessage("tu as gagné " + ret.split(";")[1] + " B2C").queue();
            case "waifu":
                Squads.getstats(member).newWaifu(Integer.parseInt(ret.split(";")[1]), e.getMessage());
                break;
            case "squad":
                Squads.addPoints(member, Long.parseLong(ret.split(";")[1]));
                e.getChannel().sendMessage("l'escouade "
                        + Squads.getSquadByMember(member).getName()
                        + " gagne "
                        + Long.parseLong(ret.split(";")[1])
                        + " points").queue();
        }
        e.reply("oui").complete().deleteOriginal().queue();
        e.getMessage().editMessageEmbeds(new EmbedBuilder().setTitle("Cadeau ouvert").setDescription("ce cadeau à déja été ouvert").build()).setActionRow(new ButtonImpl("gift", "ouvrir", ButtonStyle.SUCCESS, true, null)).queue();
    }
}