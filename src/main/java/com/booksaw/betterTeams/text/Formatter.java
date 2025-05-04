package com.booksaw.betterTeams.text;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMap;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

public abstract class Formatter {

	private static final Formatter ABSOLUTE = new AbsoluteFormatter();

	public static Formatter absolute() {
		return ABSOLUTE;
	}

	private static final Formatter LEGACY = new LegacyFormatter();

	public static Formatter legacy() {
		return LEGACY;
	}

	public static Formatter player(@NotNull Player player) {
		return new PermissiveFormatter(requireNonNull(player, "player"));
	}

	public abstract Component process(String input);

	private static final class PermissiveFormatter extends Formatter {

		private static final String ALL_PERMISSION = "betterteams.chat.*";
		private static final String MOJANG_COLOR_PERMISSION = "betterteams.chat.legacy.color";
		private static final String BUNGEE_HEX_PERMISSION = "betterteams.chat.legacy.bungeehex";
		private static final String STANDARD_HEX_PERMISSION = "betterteams.chat.legacy.hex";

		private static final Map<String, TagResolver> PERMISSIVE_TAG_RESOLVERS = new ImmutableMap.Builder<String, TagResolver>()
				.put("betterteams.chat.color.*", StandardTags.color())
				.put("betterteams.chat.color.black", TextColorTagResolver.of(NamedTextColor.BLACK))
				.put("betterteams.chat.color.dark_blue", TextColorTagResolver.of(NamedTextColor.DARK_BLUE))
				.put("betterteams.chat.color.dark_green", TextColorTagResolver.of(NamedTextColor.DARK_GREEN))
				.put("betterteams.chat.color.dark_aqua", TextColorTagResolver.of(NamedTextColor.DARK_AQUA))
				.put("betterteams.chat.color.dark_red", TextColorTagResolver.of(NamedTextColor.DARK_RED))
				.put("betterteams.chat.color.dark_purple", TextColorTagResolver.of(NamedTextColor.DARK_PURPLE))
				.put("betterteams.chat.color.gold", TextColorTagResolver.of(NamedTextColor.GOLD))
				.put("betterteams.chat.color.gray", TextColorTagResolver.of(NamedTextColor.GRAY))
				.put("betterteams.chat.color.dark_gray", TextColorTagResolver.of(NamedTextColor.DARK_GRAY))
				.put("betterteams.chat.color.blue", TextColorTagResolver.of(NamedTextColor.BLUE))
				.put("betterteams.chat.color.green", TextColorTagResolver.of(NamedTextColor.GREEN))
				.put("betterteams.chat.color.aqua", TextColorTagResolver.of(NamedTextColor.AQUA))
				.put("betterteams.chat.color.red", TextColorTagResolver.of(NamedTextColor.RED))
				.put("betterteams.chat.color.light_purple", TextColorTagResolver.of(NamedTextColor.LIGHT_PURPLE))
				.put("betterteams.chat.color.yellow", TextColorTagResolver.of(NamedTextColor.YELLOW))
				.put("betterteams.chat.color.white", TextColorTagResolver.of(NamedTextColor.WHITE))
				.put("betterteams.chat.style.*", StandardTags.decorations())
				.put("betterteams.chat.style.bold", StandardTags.decorations(TextDecoration.BOLD))
				.put("betterteams.chat.style.italic", StandardTags.decorations(TextDecoration.ITALIC))
				.put("betterteams.chat.style.underlined", StandardTags.decorations(TextDecoration.UNDERLINED))
				.put("betterteams.chat.style.strikethrough", StandardTags.decorations(TextDecoration.STRIKETHROUGH))
				.put("betterteams.chat.style.obfuscated", StandardTags.decorations(TextDecoration.OBFUSCATED))
				.put("betterteams.chat.reset", StandardTags.reset())
				.put("betterteams.chat.gradient", StandardTags.gradient())
				.put("betterteams.chat.hover", StandardTags.hoverEvent())
				.put("betterteams.chat.click", StandardTags.clickEvent())
				.put("betterteams.chat.insertion", StandardTags.insertion())
				.put("betterteams.chat.font", StandardTags.font())
				.put("betterteams.chat.transition", StandardTags.transition())
				.put("betterteams.chat.translatable", StandardTags.translatable())
				.put("betterteams.chat.selector", StandardTags.selector())
				.put("betterteams.chat.keybind", StandardTags.keybind())
				.put("betterteams.chat.newline", StandardTags.newline())
				.put("betterteams.chat.rainbow", StandardTags.rainbow())
				.build();

		private static final MiniMessage EMPTY_MINIMESSAGE = MiniMessage.builder()
				.tags(TagResolver.empty())
				.preProcessor(UnaryOperator.identity())
				.postProcessor(Component::compact)
				.build();

		private static TagResolver provideTagResolver(Permissible permissible) {
			List<TagResolver> resolvers = new ArrayList<>();
			if (permissible.hasPermission(ALL_PERMISSION)) {
				return TagResolver.standard();
			} else
				for (Map.Entry<String, TagResolver> entry : PERMISSIVE_TAG_RESOLVERS.entrySet()) {
					if (permissible.hasPermission(entry.getKey())) {
						resolvers.add(entry.getValue());
					}
				}
			return TagResolver.resolver(resolvers);
		}

		private final boolean convertMojangColor;
		private final boolean convertStandardHex;
		private final boolean convertBungeeHex;

		private final TagResolver tagResolver;

		PermissiveFormatter(Permissible permissible) {
			tagResolver = provideTagResolver(permissible);
			convertMojangColor = permissible.hasPermission(MOJANG_COLOR_PERMISSION);
			convertStandardHex = permissible.hasPermission(STANDARD_HEX_PERMISSION);
			convertBungeeHex = permissible.hasPermission(BUNGEE_HEX_PERMISSION);
		}

		@Override
		public Component process(String input) {
			if (input == null || input.isEmpty()) return Component.empty();
			String output = Legacy.sectionToAmpersand(input);
			if (convertBungeeHex) output = Legacy.bungeeHexToAdventure(output); // Must go first
			if (convertStandardHex) output = Legacy.standardHexToAdventure(output);
			if (convertMojangColor) output = Legacy.colorToAdventure(output);
			return EMPTY_MINIMESSAGE.deserialize(output, tagResolver);
		}
	}

	private static final class AbsoluteFormatter extends Formatter {

		private static final MiniMessage ABSOLUTE_MINIMESSAGE = MiniMessage.builder()
				.tags(TagResolver.standard())
				.preProcessor(new LegacyTextPreProcessor())
				.postProcessor(new LegacyTextPostProcessor())
				.build();
		
		AbsoluteFormatter() {}

		@Override
		public Component process(String input) {
			return ABSOLUTE_MINIMESSAGE.deserializeOr(input, Component.empty());
		}
	}

	private static final class LegacyFormatter extends Formatter {

		private static final MiniMessage LEGACY_MINIMESSAGE = MiniMessage.builder()
				.tags(TagResolver.resolver(
						StandardTags.color(),
						StandardTags.decorations(),
						StandardTags.gradient(),
						StandardTags.rainbow(),
						StandardTags.reset()))
				.preProcessor(new LegacyTextPreProcessor())
				.postProcessor(new LegacyTextPostProcessor())
				.build();

		LegacyFormatter() {}

		@Override
		public Component process(String input) {
			return LEGACY_MINIMESSAGE.deserializeOr(input, Component.empty());
		}
	}
}
