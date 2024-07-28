package me.near85dy.discordIntegrations;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.List;

public class Bot extends ListenerAdapter
{
    String activity = "Nothing";
    String channelID;
    String token;
    JDA jda;

    TextChannel channel;

    public Bot(String token, String channelID)
    {
        this.token = token;
        this.channelID = channelID;
    }

    public void initialize()
    {
        try
        {
            jda = JDABuilder.createLight(token)
                    .setAutoReconnect(true)
                    .setActivity(Activity.playing(activity))
                    .enableIntents(List.of(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES))
                    .addEventListeners(this)
                    .build()
                    .awaitReady();
        }
        catch (InterruptedException e)
        {
            System.out.println(e.getMessage());
        }

        channel = jda.getTextChannelById(channelID);
    }

    public void setActivity(String activity)
    {
        this.activity = activity;
    }

    public void deleteLastMessage()
    {
        if(channel.getLatestMessageId().isEmpty()) return;
        channel.deleteMessageById(channel.getLatestMessageId()).queue();
    }

    public void deleteAllMessages()
    {
        List<Message> messageList = channel.getHistory().retrievePast(25).complete();
        channel.deleteMessages(messageList).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if(event.getAuthor().isBot()) return;
        String message = event.getMessage().getContentDisplay();

        if(message.contains("!clear")) deleteAllMessages();
    }
}
