package me.near85dy.discordIntegrations;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import org.bukkit.scheduler.BukkitRunnable;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.time.LocalTime;

enum ServerStatus
{
    online,
    offline
}

public final class DiscordIntegrations extends JavaPlugin implements Listener
{
    FileConfiguration config;
    Webhook webhook;
    Bot bot;
    String content;
    String serverName;
    ServerStatus serverStatus;

    //Config.yml
    String webHookURL;
    String botToken;
    String channelID;
    
    int updateCooldown;
    int lineColor;

    @Override
    public void onEnable()
    {
        serverStatus = ServerStatus.online;
        loadConfig();

        webhook = new Webhook(webHookURL);
        bot = new Bot(botToken, channelID);

        bot.initialize();
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                bot.deleteLastMessage();
                prepareJSON();
                webhook.sendMessage(content);
            }
        }.runTaskTimerAsynchronously(this, 0, updateCooldown);
    }

    @Override
    public void onDisable()
    {
        serverStatus = ServerStatus.offline;
        lineColor = 16711680;
        bot.deleteLastMessage();
        prepareJSON();
        webhook.sendMessage(content);
    }

    private void prepareJSON()
    {
        String description =
                "╔═════════════════════\n" +
                "║ Server status: "+getStatus()+"\n" +
                "║ Server version: "+Bukkit.getServer().getMinecraftVersion()+"\n" +
                "║ Player on server: "+Bukkit.getServer().getOnlinePlayers().size()+"/"+Bukkit.getMaxPlayers()+"\n" +
                "║ Server TPS: "+ Bukkit.getServer().getTPS()[0] +"\n" +
                "║ Updated: " + LocalTime.now();


        JSONObject mainContent = new JSONObject();
        mainContent.put("content", null);

        JSONObject embedContent = new JSONObject();
        embedContent.put("title", "Информация о сервере | " + serverName);
        embedContent.put("description", description);
        embedContent.put("color", lineColor);

        JSONArray embedsArray = new JSONArray();
        embedsArray.add(embedContent);

        mainContent.put("embeds", embedsArray);
        content = mainContent.toJSONString();
    }

    private void loadConfig()
    {
        saveDefaultConfig();
        config = getConfig();

        updateCooldown =    config.getInt("update-cooldown");
        lineColor =         config.getInt("line-color");

        webHookURL =        config.getString("webhook-url");
        botToken =          config.getString("bot-token");
        channelID =         config.getString("channel-id");
        serverName =         config.getString("server-name");
    }

    private String getStatus()
    {
        if(serverStatus == ServerStatus.online)
            return "online";
        return "offline";
    }
}
