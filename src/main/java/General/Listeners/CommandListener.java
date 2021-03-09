package General.Listeners;

import Dertigen.DertigGame;
import General.Util.GameList;
import General.Bot;
import General.Util.Builders;
import General.Util.UserList;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;

public class CommandListener extends ListenerAdapter {

    //command lists
    String[] info = {"info", "zuipbot", "zuipen", "jo", "hoi", "hallo"};
    List<String> infoList = Arrays.asList(info);
    String[] stop = {"stop", "quit"};
    List<String> stopList = Arrays.asList(stop);
    String[] roll = {"roll", "gooi", "30en", "speel", "doodziekebak"};
    List<String> rollList = Arrays.asList(roll);
    String[] cheers = {"🍺", "🍾", "🍷", "🍸", "🍹", "🥂", "🥃", "🥤", "cheers", "proost"};
    List<String> cheersList = Arrays.asList(cheers);
    String[] feedback = {"feedback", "tip", "verbeterpunt", "fix"};
    List<String> feedbackList = Arrays.asList(feedback);

    String[] prefixes = {"!", "-", "~", "/"};

    String[] stopEmotes = {"🛑", "✖"};
    String[] beerEmote = {"🍻"};

    //feedback variables
    File feedbackUserIDFile = Paths.get("FeedbackUser.txt").toFile();
    Long feedbackUserID;

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //check if there is a feedback user
        try {
            if (!feedbackUserIDFile.exists()) {
                System.out.println("there is no user to send feedback to");
            } else {
                feedbackUserID = Long.parseLong(new String(Files.readAllBytes(feedbackUserIDFile.toPath())));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        TextChannel channel = event.getChannel();

        Bot.jda.getPresence().setStatus(OnlineStatus.ONLINE);
        String[] args = event.getMessage().getContentRaw().split(" ");
        for (String prefix : prefixes) {

            //check if the message starts with one of the correct prefixes
            if (args[0].startsWith(prefix)) {

                //remove the prefix
                args[0] = args[0].substring(1);

                //set the idle timer
                TimerTask task = new TimerTask() {
                    public void run() {
                        Bot.jda.getPresence().setStatus(OnlineStatus.IDLE);
                    }
                };

                Timer timer = new Timer();

                //wait 2 minutes before going idle again
                long delay = 120000L;
                timer.schedule(task, delay);

                //commands
                if (infoList.contains(args[0].toLowerCase())) {
                    Builders.sendTasteMessage(event.getChannel(),
                            "Welkom bij Adman!",
                            "Adman is een discord bot gemaakt voor drankspellen. " +
                                    "Momenteel is dertigen het enige spel dat gespeeld kan worden, maar in de " +
                                    "toekomst komen er meer spellen aan! Veel plezier!",
                            "Gebruik om een ronde dertigen te beginnen het commando -gooi. of reageer met \"🍻\" op dit bericht.",
                            beerEmote);
                }

                if (rollList.contains(args[0].toLowerCase())) {
                    if (GameList.hasGame(channel)) {
                        Builders.sendTempError(channel,
                                "- Er is al een ronde bezig in dit textkanaal, ga naar een ander tekstkanaal of stuur \"-stop\".\n",
                                6);
                    } else {
                        if(!UserList.hasUserList(channel)) {
                            DertigGame game = new DertigGame(channel, "dertigen");
                            GameList.setGame(channel, game);
                            GameList.getGame(channel).setPlayerList(event.getAuthor());
                        }
                    }
                }

                if (stopList.contains(args[0].toLowerCase())) {
                    if(GameList.hasGame(channel)) {
                        Builders.sendEmbed(event.getChannel(),
                                "Stoppen",
                                "Weet je zeker dat je de ronde wil stoppen?",
                                "Reageer met \"🛑\" om het spel te stoppen, druk op \"✖\" om het stoppen te annuleren.",
                                stopEmotes,
                                false,
                                false,
                                true);
                    }
                }
                if (feedbackList.contains(args[0].toLowerCase())) {
                        Builders.sendEmbed(event.getChannel(),
                                "Bedankt voor de feedback👍",
                                "Bedankt voor je bericht, we gaan kijken wat we ermee kunnen!",
                                "",
                                null,
                                false,
                                false,
                                false);
                        StringBuilder feedback = new StringBuilder();
                        for(int i = 1; i < args.length; i++){
                            feedback.append(args[i]).append(" ");
                        }
                        if(feedbackUserID != null){
                            Bot.jda.retrieveUserById(feedbackUserID).queue(user ->
                                    user.openPrivateChannel().queue(feedbackChannel ->
                                                Builders.sendPrivateEmbed(
                                                        feedbackChannel,
                                                        "Je hebt nieuwe feedback ontvangen!",
                                                        "\" " + feedback.toString() + " \" Deze feedback komt van: " + event.getAuthor().getAsMention(),
                                                        ""
                                                )
                                            )
                            );
                        }
                }
            }else{
                if(cheersList.contains(args[0].toLowerCase())){
                    Builders.sendTempMessage(channel, "Proost! 🍻", 5);
                }
            }
        }
    }
}
