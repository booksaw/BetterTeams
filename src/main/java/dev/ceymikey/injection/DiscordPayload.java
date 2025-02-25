/*
 * Copyright 2024 Ceymikey. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ceymikey.injection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.ceymikey.exceptions.FailedEndpointException;
import dev.ceymikey.exceptions.InjectionFailureException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DiscordPayload {

    public static void inject(EmbedBuilder builder) {
        if (builder.getUrl() == null || builder.getUrl().isEmpty()) {
            try {
                throw new FailedEndpointException();
            } catch (FailedEndpointException e) {
                throw new RuntimeException(e);
            }
        }

        if ((builder.getTitle() == null || builder.getTitle().isEmpty())
                && (builder.getDescription() == null || builder.getDescription().isEmpty())
                && (builder.getFields() == null || builder.getFields().isEmpty())) {
            try {
                throw new InjectionFailureException();
            } catch (InjectionFailureException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            JsonObject embed = new JsonObject();
            embed.addProperty("title", builder.getTitle());
            embed.addProperty("description", builder.getDescription());
            embed.addProperty("color", builder.getColor());

            if (builder.getThumbnailUrl() != null && !builder.getThumbnailUrl().isEmpty()) {
                JsonObject thumbnail = new JsonObject();
                thumbnail.addProperty("url", builder.getThumbnailUrl());
                embed.add("thumbnail", thumbnail);
            }

            JsonArray fieldsArray = new JsonArray();
            for (EmbedBuilder.Field field : builder.getFields()) {
                JsonObject fieldObject = new JsonObject();
                fieldObject.addProperty("name", field.name);
                fieldObject.addProperty("value", field.value);
                fieldsArray.add(fieldObject);
            }
            embed.add("fields", fieldsArray);

            // Add footer if available
            if (builder.getFooterText() != null && !builder.getFooterText().isEmpty()) {
                JsonObject footer = new JsonObject();
                footer.addProperty("text", builder.getFooterText());
                embed.add("footer", footer);
            }

            JsonObject payload = new JsonObject();
            JsonArray embeds = new JsonArray();
            embeds.add(embed);
            payload.add("embeds", embeds);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(builder.getUrl());
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(new StringEntity(payload.toString()));

            httpClient.execute(httpPost);
            httpClient.close();
        } catch (Exception e) {
            System.out.println("INJECTION FAILURE! | " + e.getMessage());
            e.printStackTrace();
        }
    }
}
