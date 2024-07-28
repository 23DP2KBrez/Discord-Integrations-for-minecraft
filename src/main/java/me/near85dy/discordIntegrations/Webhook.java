package me.near85dy.discordIntegrations;

import org.bukkit.Bukkit;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.ContentType;

import java.io.IOException;

public class Webhook
{
    private final String url;

    public Webhook(String webHookURL)
    {
        url = webHookURL;
    }

    public void sendMessage(String content)
    {
        try (CloseableHttpClient httpClient = HttpClients.createDefault())
        {
            HttpPost request = new HttpPost(url);
            request.addHeader("Content-Type", "application/json");

            request.setEntity(new StringEntity(content, ContentType.APPLICATION_JSON.withCharset("UTF-8")));

            try(CloseableHttpResponse response = httpClient.execute(request))
            {
                if (response.getStatusLine().getStatusCode() != 204)
                {
                    Bukkit.getLogger().severe("Failed to send webhook message. Response code: " + response.getStatusLine().getStatusCode());
                }
            }
        }
        catch (IOException e)
        {
            Bukkit.getLogger().severe("Failed to send webhook message: " + e.getMessage());
        }
    }
}
