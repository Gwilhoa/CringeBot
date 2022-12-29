package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.Request.Members;
import fr.cringebot.cringe.Request.Request;
import fr.cringebot.cringe.Request.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TopCommand {
    public static EmbedBuilder CommandTop(String name, Guild guild, Member sender) {
        if (name != null && name.equals("scoreboard"))
            name = null;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (name == null) {
            embedBuilder.setTitle("Classement des escouades");
            List<Squads> sq = null;
            try {
                sq = Squads.getSquads();
            } catch (ConnectException e) {
                return Request.DisconnectedEmbed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (sq != null) {
                sq.sort((o1, o2) -> o2.getPointsTotal() - o1.getPointsTotal());
                EmbedBuilder finalEmbedBuilder = embedBuilder;
                sq.forEach(squads -> {
                    List<Members> mem = null;
                    try {
                        mem = Squads.getMembers(squads);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (mem != null) {
                        mem.sort((o1, o2) -> o2.getPoints() - o1.getPoints());
                        if (squads.getPointsTotal() == 0) {
                            finalEmbedBuilder.addField(squads.getName() + " - Top 1 : ---", "Points : pas encore de point | Points d'équipe : " + squads.getPointsGiven(), false);

                        } else {
                            finalEmbedBuilder.addField(squads.getName() + " - Top 1 : " + mem.get(0).getName(), "Points : " + squads.getPointsTotal() + " | Points d'équipe : " + squads.getPointsGiven(), false);
                        }
                    } else {
                        finalEmbedBuilder.addField(squads.getName(), "Points : " + squads.getPointsTotal() + " | Points d'équipe : " + squads.getPointsGiven(), false);
                    }
                });
                embedBuilder.setColor(sq.get(0).getColor());
            } else {
                embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Classement des escouades");
                embedBuilder.setDescription("aucune escouade n'a été trouvée");
                embedBuilder.setColor(Color.RED);
            }
        } else if (name.equalsIgnoreCase("general")) {
            List<Members> mem = null;
            try {
                mem = Members.getMembers();
            } catch (ConnectException e) {
                return Request.DisconnectedEmbed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mem.sort((o1, o2) -> o2.getPoints() - o1.getPoints());
            embedBuilder.setTitle("Classement général");
            AtomicInteger i = new AtomicInteger(1);
            EmbedBuilder finalEmbedBuilder1 = embedBuilder;
            displaymember(sender, mem, i, finalEmbedBuilder1);
            embedBuilder.setColor(mem.get(0).getSquad().getColor());
            return embedBuilder;
        }  else {
            List<Role> roleList = guild.getRolesByName(name, true);
            if (roleList.size() != 1)
            {
                embedBuilder.setTitle("Classement de l'escouade " + name);
                embedBuilder.setDescription("L'escouade n'a pas été trouvée ou plusieurs role tiens le meme nom");
                embedBuilder.setColor(Color.RED);
            }
            else
            {
                Role role = roleList.get(0);
                List<Members> mem = null;
                try {
                    mem = Squads.getMembers(Squads.getSquads(role.getId()));
                } catch (ConnectException e) {
                    return Request.DisconnectedEmbed();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mem != null) {
                    mem.sort((o1, o2) -> o2.getPoints() - o1.getPoints());
                    embedBuilder.setTitle("Classement de l'escouade " + name);
                    embedBuilder.setColor(role.getColor());
                    EmbedBuilder finalEmbedBuilder = embedBuilder;
                    AtomicInteger i = new AtomicInteger(1);
                    displaymember(sender, mem, i, finalEmbedBuilder);
                } else {
                    embedBuilder.setTitle("Classement de l'escouade " + name);
                    embedBuilder.setDescription("aucun membre n'a été trouvé");
                    embedBuilder.setColor(Color.RED);
                }
            }
        }
        return embedBuilder;
    }

    private static void displaymember(Member sender, List<Members> mem, AtomicInteger i, EmbedBuilder finalEmbedBuilder1) {
        mem.forEach(members -> {
            if (i.get() <= 10) {
                if (members.getId().equals(sender.getId()))
                    finalEmbedBuilder1.addField("▶️  "+i.get() + " - " + members.getName() + "  ◀️", "**Points : " + members.getPoints() + "**", false);
                else
                    finalEmbedBuilder1.addField(i.get() + " - " + members.getName(), "Points : " + members.getPoints(), false);
                i.getAndIncrement();
            }
        });
    }
}

