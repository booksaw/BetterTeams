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

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to build the embed.
 * @author Ceymikey
 */
public class EmbedBuilder {

    public static class Construct {
        private String url;
        private String title;
        private String description;
        private int color;
        private List<Field> fields = new ArrayList<>();
        private String thumbnailUrl;
        private String footerText;

        public Construct setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Construct setUrl(String url) {
            this.url = url;
            return this;
        }

        public Construct setTitle(String title) {
            this.title = title;
            return this;
        }

        public Construct setDescription(String description) {
            this.description = description;
            return this;
        }

        public Construct setColor(int color) {
            this.color = color;
            return this;
        }

        public Construct addField(String name, String value) {
            this.fields.add(new Field(name, value));
            return this;
        }

        public Construct setFooter(String footerText) {
            this.footerText = footerText;
            return this;
        }

        public EmbedBuilder build() {
            return new EmbedBuilder(this);
        }
    }

    @Getter private final String url;
    @Getter private final String title;
    @Getter private final String description;
    @Getter private final int color;
    @Getter private final List<Field> fields;
    @Getter private final String thumbnailUrl;
    @Getter private final String footerText;

    private EmbedBuilder(Construct construct) {
        this.url = construct.url;
        this.title = construct.title;
        this.description = construct.description;
        this.color = construct.color;
        this.fields = construct.fields;
        this.thumbnailUrl = construct.thumbnailUrl;
        this.footerText = construct.footerText;
    }

    public static class Field {
        public String name;
        public String value;

        public Field(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
