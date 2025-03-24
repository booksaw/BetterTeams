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

import com.booksaw.betterTeams.Main;
import dev.ceymikey.exceptions.FailedEndpointException;
import dev.ceymikey.exceptions.InjectionFailureException;
import dev.ceymikey.json.JsonArray;
import dev.ceymikey.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordPayload {
	private DiscordPayload() {
	}

	public static void inject(@NotNull EmbedBuilder builder) {
		if (builder.getUrl() == null || builder.getUrl().isEmpty()) {
			throw new FailedEndpointException();
		}

		if ((builder.getTitle() == null || builder.getTitle().isEmpty())
				&& (builder.getDescription() == null || builder.getDescription().isEmpty())
				&& (builder.getFields() == null || builder.getFields().isEmpty())) {
			throw new InjectionFailureException();
		}

		HttpURLConnection connection = null;

		try {
			// Build the JSON payload
			JsonObject payload = getPayload(builder);

			// Set up HTTP connection
			URL url = new URL(builder.getUrl());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			// Send the payload
			byte[] payloadBytes = payload.toString().getBytes(StandardCharsets.UTF_8);
			connection.setFixedLengthStreamingMode(payloadBytes.length);

			try (OutputStream os = connection.getOutputStream()) {
				os.write(payloadBytes);
				os.flush();
			}

			// Get response code to ensure the request is complete
			int responseCode = connection.getResponseCode();
			if (responseCode < 200 || responseCode >= 300) {
				Main.plugin.getLogger().severe("Could not send webhook. HTTP Error: " + responseCode);
			}

		} catch (Exception e) {
			Main.plugin.getLogger().severe("Could not send webhook. INJECTION FAILURE! | " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private static @NotNull JsonObject getPayload(@NotNull EmbedBuilder builder) {
		JsonObject embed = new JsonObject();
		embed.put("title", builder.getTitle());
		embed.put("description", builder.getDescription());
		embed.put("color", builder.getColor());

		if (builder.getThumbnailUrl() != null && !builder.getThumbnailUrl().isEmpty()) {
			JsonObject thumbnail = new JsonObject();
			thumbnail.put("url", builder.getThumbnailUrl());
			embed.put("thumbnail", thumbnail);
		}

		JsonArray fieldsArray = new JsonArray();
		for (EmbedBuilder.Field field : builder.getFields()) {
			JsonObject fieldObject = new JsonObject();
			fieldObject.put("name", field.name);
			fieldObject.put("value", field.value);
			fieldsArray.put(fieldObject);
		}
		embed.put("fields", fieldsArray);

		// Add footer if available
		if (builder.getFooterText() != null && !builder.getFooterText().isEmpty()) {
			JsonObject footer = new JsonObject();
			footer.put("text", builder.getFooterText());
			embed.put("footer", footer);
		}

		JsonObject payload = new JsonObject();
		payload.put("embeds", new JsonArray(embed));
		return payload;
	}
}