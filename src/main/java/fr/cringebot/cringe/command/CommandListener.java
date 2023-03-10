

package fr.cringebot.cringe.command;


import com.jcraft.jsch.JSchException;
import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.CommandBuilder.ProfilCommand;
import fr.cringebot.cringe.CommandBuilder.TopCommand;
import fr.cringebot.cringe.Request.*;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.joda.time.Instant;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static fr.cringebot.cringe.objects.imgExtenders.resize;

/**
 * fichier de commandes de base
 */
public class CommandListener {

	private final BotDiscord botDiscord;
	public static boolean isArchived = false;
	/**
	 * intialisation de l'objet
	 *
	 * @param botDiscord
	 * @param commandMap
	 */
	public CommandListener(BotDiscord botDiscord, CommandMap commandMap) {
		this.botDiscord = botDiscord;
	}

	/**
	 * arreter le bot depuis la console NORMALEMENT
	 *
	 * @param jda le bot a arreter
	 */
	@Command(name = "-", type = ExecutorType.CONSOLE)
	private void stop(JDA jda) {
		jda.getPresence().setStatus(OnlineStatus.OFFLINE);
		botDiscord.setRunning(false);
	}

	@Command(name = "removeachievement", type = ExecutorType.USER, permission = Permission.ADMINISTRATOR)
	private void removeAchievement(Message msg, MessageChannel channel, String[] args) {
		try {
			Achievement.removeAchievement(args[0]);
			channel.sendMessage("L'achievement " + args[0] + " a bien été supprimé.").queue();
		} catch (IOException e) {
			e.printStackTrace();
			channel.sendMessage("Une erreur est survenue lors de la suppression de l'achievement.").queue();
		}
	}

	@Command(name = "setactivity", type = ExecutorType.USER, description = "changer l'activité du bot", permission = Permission.ADMINISTRATOR)
	private void setactivity(Message msg) {
		String args[] = msg.getContentRaw().split(" ");
		if (args.length > 2) {
			switch (args[1]) {
				case "playing":
					msg.getJDA().getPresence().setActivity(Activity.playing(args[2]));
					break;
				case "listening":
					msg.getJDA().getPresence().setActivity(Activity.listening(args[2]));
					break;
				case "watching":
					msg.getJDA().getPresence().setActivity(Activity.watching(args[2]));
					break;
				case "streaming":
					msg.getJDA().getPresence().setActivity(Activity.streaming(args[2], "https://www.twitch.tv/"));
					break;
				default:
					msg.getJDA().getPresence().setActivity(Activity.playing(args[2]));
					break;
			}
		}
	}

	@Command(name = "slashupdate", description = "affiche le classement des escouades", type = ExecutorType.USER)
	private void slashupdate(Message message) {
		/*
		message.getJDA().updateCommands().queue();
		message.getJDA().upsertCommand("top", "voir le soreboard des escouades")
				.addOption(OptionType.STRING, "squad", "voir les scoreboards des escouades", true, true)
				.queue();
		message.getJDA().upsertCommand("profil", "voir le profil d'un joueur")
				.addOption(OptionType.USER, "pseudo", "voir le profil d'un joueur", false, false)
				.queue();*/
	}

	@Command(name = "newsquad", description = "ajouter une escouade", type = ExecutorType.USER)
	private void addsquad(Message msg) {
		if (msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR) && !msg.getMentions().getRoles().isEmpty()) {
			try {
				Squads.newSquads(msg.getMentions().getRoles().get(0).getName(), msg.getMentions().getRoles().get(0).getId(), msg.getMentions().getRoles().get(0).getColor());
			} catch (ConnectException e) {
				msg.getChannel().sendMessage("disconnected").queue();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Command(name = "addmember", description = "ajouter un membre inexistant à une escouade", type = ExecutorType.USER, permission = Permission.ADMINISTRATOR)
	private void addMember(Message msg)
	{
		msg.getMentions().getMembers().get(0).getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("vous avez été ajouté à l'escouade " + msg.getMentions().getRoles().get(0).getName()).queue());
		msg.getGuild().addRoleToMember(msg.getMentions().getMembers().get(0), msg.getMentions().getRoles().get(0)).queue();
		try {
			Members.newMembers(msg.getMentions().getMembers().get(0), msg.getMentions().getRoles().get(0).getId());
		} catch (ConnectException e) {
			msg.getChannel().sendMessage("disconnected").queue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		TextChannel tc = msg.getGuild().getTextChannelById("947564791759777792");
		tc.sendMessage(msg.getMentions().getMembers().get(0).getAsMention() + "a rejoint l'équipe "+ msg.getMentions().getRoles().get(0).getName()).queue();
	}

	@Command(name = "bresil", description= "you are going to brazil", type = ExecutorType.USER)
	private void bresil(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "profil", description = "information sur un joueur", type = ExecutorType.USER)
	private void profil(Message msg) {
		Member member = msg.getMember();
		if (msg.getMentions().getMembers().size() > 0) {
			member = msg.getMentions().getMembers().get(0);
		}
		msg.replyEmbeds(ProfilCommand.CommandProfil(member).build()).queue();
	}

	@Command(name = "shop", description = "ouvrir le shopping")
	private void shop(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "gift", description = "des cadeaux ?", type = ExecutorType.USER)
	private void gift(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "top", description = "regarder le classement des escouades")
	private void top(Message msg, String[] args) {
		if (args.length == 0)
			msg.replyEmbeds(TopCommand.CommandTop(null, msg.getGuild(), msg.getMember()).build()).queue();
		else if (args.length == 1)
			msg.replyEmbeds(TopCommand.CommandTop(args[0], msg.getGuild(), msg.getMember()).build()).queue();
		else
			msg.replyEmbeds(new EmbedBuilder().setColor(Color.RED).setTitle("Erreur").setDescription("Nombre d'arguments incorrect").build()).queue();
	}

	@Command(name = "pay", description = "payer un ami", type = ExecutorType.USER)
	private void pay(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "poll", description = "faites des sondages rapidements", type = ExecutorType.USER)
	private void poll(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "role", description = "permettre de creer un role", type = ExecutorType.USER)
	private void role(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "harem", description = "la listes des waifus", type = ExecutorType.USER)
	private void harem(Message msg) throws IOException {
		Members mem = Members.getMember(msg.getMember());
		List<WaifuMembers> waifuMembers = mem.getWaifuMembers();
		System.out.println(waifuMembers);
		ArrayList<MessageEmbed> embeds = new ArrayList<>();
		int i = 0;
		if (waifuMembers.size() == 0)
		{
			msg.getChannel().sendMessage("vous n'avez pas de waifu").queue();
			return;
		}
		while (i < 5 && i < waifuMembers.size())
		{
			embeds.add(waifuMembers.get(i).getEmbed().build());
			i++;
		}
		Button button = Button.primary("harem;next;0;"+mem.getId(), "page suivante");
		msg.getChannel().sendMessageEmbeds(embeds).setActionRow(button).queue();
	}

	@Command(name="archive", description = "archiver un salon", type = ExecutorType.USER, permission = Permission.MESSAGE_SEND)
	private void archive(Message msg) throws IOException {
		File directory = new File("archive");
		if (directory.mkdir())
			System.out.println("[EVENT] directory created");
		else
			System.out.println("[DEBUG] directory already exist");
		File file = new File("archive/" + msg.getChannel().getName() + "_" + new Date(System.currentTimeMillis()).getDay() + "_" + new Date(System.currentTimeMillis()).getMonth() + "_" + new Date(System.currentTimeMillis()).getMonth()  + ".txt");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (isArchived) {
			msg.getChannel().sendMessage("un archivage est déjà en cours").queue();
			return;
		}
		msg.getChannel().sendMessage("archive en cours").queue();
		isArchived = true;
		new Thread(() -> {
			try {
				FileWriter fileWriter = new FileWriter(file);
				boolean finish = true;
				Message pin = msg.getChannel().getHistoryFromBeginning(1).complete().getRetrievedHistory().get(0);
				String endid = msg.getId();
				while (finish) {
					Thread.sleep(1000);
					for (Message message : msg.getChannel().getHistoryAfter(pin, 100).complete().getRetrievedHistory()) {
						fileWriter.write(message.getAuthor().getName() + " : " + message.getContentRaw() + "\n\n");
						if (message.getId().equals(endid)) {
							finish = false;
							break;
						}
						pin = message;
					}
				}
				fileWriter.close();
				msg.getChannel().sendMessage("archivage terminée").queue();
				isArchived = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).start();
	}
	@Command(name = "waifu", description = "instance des waifus", type = ExecutorType.USER)
	private void waifu(Message msg) throws ExecutionException, InterruptedException, IOException, JSchException {
		String args[] = msg.getContentRaw().split(" ");
		if (args.length == 1)
		{
			WaifuMembers w = null;
			try {
				w = Members.catchwaifu(msg.getMember());
			} catch (IOException error) {
				try {
					long waitingTime = Long.parseLong(error.getMessage()) - System.currentTimeMillis();
					msg.getChannel().sendMessage("vous devez attendre " + waitingTime/1000 + " secondes avant de pouvoir récuperer une nouvelle waifu").queue();
				} catch (NumberFormatException e) {
					msg.getChannel().sendMessage(error.getMessage()).queue();
				}
				return;
			}
			EmbedBuilder embedBuilder = new EmbedBuilder();
			embedBuilder.setAuthor(msg.getMember().getEffectiveName(), null, msg.getMember().getUser().getAvatarUrl());
			embedBuilder.setTitle("Nouvelle Waifu ! : " + w.getWaifu().getName() + " Debug mode : ID " + w.getWaifu().getId());
			embedBuilder.setDescription(w.getWaifu().getDescription());
			embedBuilder.setThumbnail(w.getWaifu().getImageurl());
			embedBuilder.setColor(w.getColor());
			embedBuilder.setFooter("Origine de " + w.getWaifu().getOrigin());
			msg.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
		}
		else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("list")) {
				ArrayList<MessageEmbed> embeds = new ArrayList<>();
				List<Waifu> waifus = Waifu.getWaifus();
				int i = 0;
				while (i < 5 && i < waifus.size()) {
					embeds.add(waifus.get(i).getEmbed().build());
					i++;
				}
				net.dv8tion.jda.api.interactions.components.buttons.Button button = net.dv8tion.jda.api.interactions.components.buttons.Button.primary("waifu;next;0", "Suivant");
				msg.getChannel().sendMessageEmbeds(embeds).setActionRow(button).queue();
			}
		}
		else if (args.length == 3) {
			if (args[1].equalsIgnoreCase("id")) {
				Waifu waifu = null;
				try {
					waifu = Waifu.getWaifuById(Integer.parseInt(args[2]));
				} catch (NumberFormatException e) {
					msg.getChannel().sendMessage("L'ID doit être un nombre").queue();
					return;
				} catch (IOException e) {
					if (e.getMessage().equals("null response"))
						msg.getChannel().sendMessage("Cette waifu n'existe pas").queue();
					else
						msg.getChannel().sendMessage("Une erreur est survenue").queue();
					return;
				}
				if (waifu == null) {
					msg.getChannel().sendMessage("Cette waifu n'existe pas").queue();
					return;
				}
				msg.getChannel().sendMessageEmbeds(waifu.getEmbed().build()).queue();
			}
			if (args[1].equalsIgnoreCase("search")) {
				ArrayList<MessageEmbed> embeds = new ArrayList<>();
				List<Waifu> waifus = Waifu.getWaifuByName(args[2]);
				System.out.println(waifus);
				if (waifus.isEmpty())
				{
					msg.getChannel().sendMessage("Aucune waifu ne correspond à votre recherche").queue();
					return;
				}
				int i = 0;
				while (i < 5 && i < waifus.size()) {
					embeds.add(waifus.get(i).getEmbed().build());
					i++;
				}
				msg.getChannel().sendMessageEmbeds(embeds).queue();
			}
		} else {
			msg.getChannel().sendMessage("mauvaise utilisation de la commande\n>waifu [list|id|search] [id|name]").queue();
		}
	}

	@Command(name = "inventory", description = "afficher ton inventaire", type = ExecutorType.USER)
	private void inventory(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();}

	@Command(name = "cki", description = "mais qui est-il !", type = ExecutorType.USER)
	private void cki(Message msg){
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "reset", type = Command.ExecutorType.USER)
	private void reset(Message msg) throws IOException {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "clear", description = "let's clean", type = ExecutorType.USER)
	private void clear(Message msg){
		if (msg.getContentRaw().split(" ").length > 1){
			try {
				int arg = Integer.parseInt(msg.getContentRaw().split(" ")[1]);
				msg.getChannel().getHistoryBefore(msg, arg).queue((mh) -> msg.getChannel().purgeMessages(mh.getRetrievedHistory()));
				msg.delete().queue();
			} catch (IndexOutOfBoundsException | NumberFormatException ignored) {
				msg.getChannel().sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : !clear 5").queue();
			}

		}
		else {
			if (msg.getReferencedMessage() != null) {
				MessageHistory msgs = msg.getChannel().getHistoryAfter(msg.getMessageReference().getMessageId(),100).complete();
				while(msgs.getMessageById(msg.getId()) == null){
					msgs.retrieveFuture(100).complete();
				}
				final int chunkSize = 100;
				final AtomicInteger counter = new AtomicInteger();
				for(List<Message> v : msgs.getRetrievedHistory().stream().dropWhile((m)-> !m.getId().equals(msg.getId())).collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize)).values()){
					msg.getGuildChannel().deleteMessages(v).queue();
				}
			}
		}
	}

	@Command(name = "meteo", type = Command.ExecutorType.USER, description = "donne la météo")
	private void meteo(Guild guild, TextChannel textChannel, Message msg){
		String ville = "shrek";
		if (msg.getContentRaw().split(" ").length != 1)
			ville = msg.getContentRaw().split(" ")[1];
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			URLConnection connection = new URL("https://wttr.in/"+ville+"_3tqp_lang=fr.png").openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");
			BufferedImage im = ImageIO.read(connection.getInputStream());
			im = resize(im, 1032*2, 546*2, 0, 0, true);
			im = resize(im, 1032*2, 546*2, 0, 0, false);
			ImageIO.write(im, "png", baos);
			textChannel.sendFile(baos.toByteArray(), "meteo.png").queue();
		} catch (IOException e) {
			textChannel.sendMessage("météo introuvable").queue();
		}

	}

	@Command(name = "removesquad", description = "supprimer une escouade", type = ExecutorType.USER)
	private void RemoveSquad(Message msg) {
		if (msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
		}
	}

	@Command(name = "gettitles", description = "afficher les titres", type = ExecutorType.USER)
	private void getTitles(Message msg) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Titres disponibles");
		eb.setColor(Color.GREEN);
		StringBuilder sb = new StringBuilder();
		List<String> titles = new ArrayList<>();
		try {
			titles = Members.getTitles(msg.getMember());
		} catch (IOException e) {
			msg.getChannel().sendMessage("erreur de connexion").queue();
		}
		if (titles.isEmpty()) {
			eb.setDescription("Vous n'avez aucun titre");
		} else {
			for (String s : titles) {
				sb.append(s).append("\n");
			}
		}
		msg.getChannel().sendMessageEmbeds(eb.setDescription(sb.toString()).build()).queue();
	}

	@Command(name = "achievements", description = "listes des succès", type = ExecutorType.USER)
	private void Achievement(Message msg) {
		List<Achievement> achievement;
		List<Achievement> memAchievement;
		try {
			achievement = Achievement.getAchievements();
			memAchievement = Members.getAchievements(msg.getMember());
		} catch (IOException e) {
			msg.getChannel().sendMessage("erreur de connexion").queue();
			return;
		}
		List<String> id = new ArrayList<>();
		ArrayList<MessageEmbed> ret = new ArrayList<>();
		for (Achievement a : memAchievement) {
			id.add(a.getId());
		}
		for (Achievement a : achievement) {
			System.out.println(a.getImage());
			EmbedBuilder eb = new EmbedBuilder().setTitle(a.getName()).setDescription(a.getDescription()).setThumbnail(a.getImage()).setFooter("Points : " + a.getPoints() + " | coins : " + a.getCoins()+ " | titre : " + a.getTitle());
			if (id.contains(a.getId()))
				eb.setColor(Color.GREEN);
			else
				eb.setColor(Color.RED);
			ret.add(eb.build());
			if (ret.size() == 10) {
				msg.getChannel().sendMessageEmbeds(ret).queue();
				ret.clear();
			}
		}
		msg.getChannel().sendMessageEmbeds(ret).queue();
	}

	@Command(name = "revokeachievement", description = "révoquer un succès", type = ExecutorType.USER, permission = Permission.ADMINISTRATOR)
	private void revokeAchievement(Message msg, String[] args) throws IOException {
		if (args.length == 2) {
			Members.revokeAchievement(msg.getMentions().getMembers().get(0), args[1]);
			msg.getChannel().sendMessage("succès révoqué").queue();
		} else {
			msg.getChannel().sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : >revokeachievement @user id").queue();
		}
	}

	@Command(name = "resetapi", description = "commande provisoire", type = ExecutorType.USER, permission = Permission.ADMINISTRATOR)
	private void resetapi(Message msg, Guild guild) throws IOException, InterruptedException {
		Role eau =  guild.getRoleById("1013766309156233236");
		Role feu =  guild.getRoleById("1013766252461838506");
		Role air =  guild.getRoleById("1013766463204642897");
		Squads.newSquads("Eau", "1013766309156233236", eau.getColor());
		Squads.newSquads("Feu", "1013766252461838506", feu.getColor());
		Squads.newSquads("Air", "1013766463204642897", air.getColor());
		for (Member mem : guild.getMembers()) {
			if (mem.getRoles().contains(eau))
				Members.newMembers(mem, eau.getId());
			else if (mem.getRoles().contains(feu))
				Members.newMembers(mem, feu.getId());
			else if (mem.getRoles().contains(air))
				Members.newMembers(mem, air.getId());
		}
		List<Members> mems = Members.getMembers();
		for (Members mem : mems)
		{
			mem.addAchievement("1", null, guild);
			if (mem.getMember(guild).getRoles().contains(guild.getRoleById("680431143283458077")))
			{
				mem.addAchievement("2", null, guild);
				mem.addAchievement("3", null, guild);
			}
			if (mem.getMember(guild).getRoles().contains(guild.getRoleById("849925828069687296")))
			{
				mem.addAchievement("2", null, guild);
			}

		}
	}

	@Command(name = "giveachievement", description = "donner un succès", type = ExecutorType.USER, permission = Permission.ADMINISTRATOR)
	private void giveAchievement(String[] args, Message msg)
	{
		if (args.length == 2)
		{
			try {
				Members.addAchievement(msg.getMentions().getMembers().get(0), args[1], msg.getChannel());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			msg.getChannel().sendMessage("mauvais usage de la commande").queue();
	}

	@Command(name = "settitle", description = "définir son titre principal", type = ExecutorType.USER)
	private void setTitle(String[] args, Message msg)
	{
		if (args.length == 1)
		{
			try {
				if (Members.setTitle(msg.getMember(), args[0]))
					msg.getChannel().sendMessage("titre défini").queue();
				else
					msg.getChannel().sendMessage("vous n'avez pas ce titre").queue();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			msg.getChannel().sendMessage("mauvais usage de la commande").queue();
	}
}