package com.booksaw.betterTeams.util;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for sending the json payload to discord.
 * @author Ceymikey
 */
public class DiscordHookUtil {

    private String url;
    private String title;
    private String description;
    private int color = 12370112; // Default color - gray
    private List<Field> fields = new ArrayList<>();

    private DiscordHookUtil(EmbedBuilder embedBuilder) {
        this.url = embedBuilder.url;
        this.title = embedBuilder.title;
        this.description = embedBuilder.description;
        this.color = embedBuilder.color;
        this.fields = embedBuilder.fields;
    }

    public static class EmbedBuilder {
        private String url;
        private String title;
        private String description;
        private int color = 12370112; // Default color - gray
        private List<Field> fields = new ArrayList<>();

        public EmbedBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedBuilder setColor(int color) {
            this.color = color;
            return this;
        }

        public EmbedBuilder addField(String name, String value) {
            fields.add(new Field(name, value));
            return this;
        }

        public DiscordHookUtil inject() {
            return new DiscordHookUtil(this);
        }
    }

    public static class Field {
        public String name;
        public String value;

        public Field(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    /* Injects the payload */
    public void sendEmbed() {
        try {
            JSONObject embed = new JSONObject();
            embed.put("title", title);
            embed.put("description", description);
            embed.put("color", color);

            JSONArray fieldsArray = new JSONArray();
            for (Field field : fields) {
                JSONObject fieldObject = new JSONObject();
                fieldObject.put("name", field.name);
                fieldObject.put("value", field.value);
                fieldsArray.put(fieldObject);
            }
            embed.put("fields", fieldsArray);

            JSONObject payload = new JSONObject();
            payload.put("embeds", new JSONArray().put(embed));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(new StringEntity(payload.toString()));

            httpClient.execute(httpPost);
            httpClient.close();
        } catch (Exception e) {
            System.out.println("Failed to send webhook message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}