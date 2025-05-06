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

	private Formatter() {}

	private static final TagResolver ALL_TAG_RESOLVERS = TagResolver.resolver(
			TagResolver.standard(),
			LegacyTextTags.RESET);

	private static final Formatter ABSOLUTE = new Formatter() {

		private final MiniMessage ABSOLUTE_MINIMESSAGE = MiniMessage.builder()
				.tags(ALL_TAG_RESOLVERS)
				.preProcessor(new LegacyTextPreProcessor())
				.postProcessor(new LegacyTextPostProcessor())
				.build();

		@Override
		public Component process(String input) {
			return ABSOLUTE_MINIMESSAGE.deserializeOr(input, Component.empty());
		}
	};

	public static Formatter absolute() {
		return ABSOLUTE;
	}

	private static final Formatter LEGACY = new Formatter() {

		private final MiniMessage LEGACY_MINIMESSAGE = MiniMessage.builder()
				.tags(TagResolver.resolver(
						StandardTags.color(),
						StandardTags.decorations(),
						StandardTags.gradient(),
						StandardTags.rainbow(),
						StandardTags.reset(),
						LegacyTextTags.RESET))
				.preProcessor(new LegacyTextPreProcessor())
				.postProcessor(new LegacyTextPostProcessor())
				.build();

		@Override
		public Component process(String input) {
			return LEGACY_MINIMESSAGE.deserializeOr(input, Component.empty());
		}
	};

	public static Formatter legacy() {
		return LEGACY;
	}

	public static Formatter player(@NotNull Player player) {
		return new PermissiveFormatter(requireNonNull(player, "player"));
	}

	public abstract Component process(String input);

	private static final class PermissiveFormatter extends Formatter {

		private static final String ALL_PERMISSION = "betterteams.chat.format.*";
		private static final String MOJANG_COLOR_PERMISSION = "betterteams.chat.format.legacycolor";
		private static final String BUNGEE_HEX_PERMISSION = "betterteams.chat.format.bungeehex";
		private static final String STANDARD_HEX_PERMISSION = "betterteams.chat.format.standardhex";

		private static final Map<String, TagResolver> PERMISSIVE_TAG_RESOLVERS = new ImmutableMap.Builder<String, TagResolver>()
				.put("betterteams.chat.format.color.*", StandardTags.color())
				.put("betterteams.chat.format.color.black", TextColorTagResolver.of(NamedTextColor.BLACK))
				.put("betterteams.chat.format.color.dark_blue", TextColorTagResolver.of(NamedTextColor.DARK_BLUE))
				.put("betterteams.chat.format.color.dark_green", TextColorTagResolver.of(NamedTextColor.DARK_GREEN))
				.put("betterteams.chat.format.color.dark_aqua", TextColorTagResolver.of(NamedTextColor.DARK_AQUA))
				.put("betterteams.chat.format.color.dark_red", TextColorTagResolver.of(NamedTextColor.DARK_RED))
				.put("betterteams.chat.format.color.dark_purple", TextColorTagResolver.of(NamedTextColor.DARK_PURPLE))
				.put("betterteams.chat.format.color.gold", TextColorTagResolver.of(NamedTextColor.GOLD))
				.put("betterteams.chat.format.color.gray", TextColorTagResolver.of(NamedTextColor.GRAY))
				.put("betterteams.chat.format.color.dark_gray", TextColorTagResolver.of(NamedTextColor.DARK_GRAY))
				.put("betterteams.chat.format.color.blue", TextColorTagResolver.of(NamedTextColor.BLUE))
				.put("betterteams.chat.format.color.green", TextColorTagResolver.of(NamedTextColor.GREEN))
				.put("betterteams.chat.format.color.aqua", TextColorTagResolver.of(NamedTextColor.AQUA))
				.put("betterteams.chat.format.color.red", TextColorTagResolver.of(NamedTextColor.RED))
				.put("betterteams.chat.format.color.light_purple", TextColorTagResolver.of(NamedTextColor.LIGHT_PURPLE))
				.put("betterteams.chat.format.color.yellow", TextColorTagResolver.of(NamedTextColor.YELLOW))
				.put("betterteams.chat.format.color.white", TextColorTagResolver.of(NamedTextColor.WHITE))
				.put("betterteams.chat.format.style.*", StandardTags.decorations())
				.put("betterteams.chat.format.style.bold", StandardTags.decorations(TextDecoration.BOLD))
				.put("betterteams.chat.format.style.italic", StandardTags.decorations(TextDecoration.ITALIC))
				.put("betterteams.chat.format.style.underlined", StandardTags.decorations(TextDecoration.UNDERLINED))
				.put("betterteams.chat.format.style.strikethrough", StandardTags.decorations(TextDecoration.STRIKETHROUGH))
				.put("betterteams.chat.format.style.obfuscated", StandardTags.decorations(TextDecoration.OBFUSCATED))
				.put("betterteams.chat.format.reset", StandardTags.reset())
				.put("betterteams.chat.format.legacyreset", LegacyTextTags.RESET)
				.put("betterteams.chat.format.gradient", StandardTags.gradient())
				.put("betterteams.chat.format.hover", StandardTags.hoverEvent())
				.put("betterteams.chat.format.click", StandardTags.clickEvent())
				.put("betterteams.chat.format.insertion", StandardTags.insertion())
				.put("betterteams.chat.format.font", StandardTags.font())
				.put("betterteams.chat.format.transition", StandardTags.transition())
				.put("betterteams.chat.format.translatable", StandardTags.translatable())
				.put("betterteams.chat.format.selector", StandardTags.selector())
				.put("betterteams.chat.format.keybind", StandardTags.keybind())
				.put("betterteams.chat.format.newline", StandardTags.newline())
				.put("betterteams.chat.format.rainbow", StandardTags.rainbow())
				.build();

		private static final MiniMessage EMPTY_MINIMESSAGE = MiniMessage.builder()
				.tags(TagResolver.empty())
				.preProcessor(UnaryOperator.identity())
				.postProcessor(Component::compact)
				.build();

		private static TagResolver provideTagResolver(Permissible permissible) {
			List<TagResolver> resolvers = new ArrayList<>();
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
			if (permissible.hasPermission(ALL_PERMISSION)) {
				tagResolver = ALL_TAG_RESOLVERS;
				convertMojangColor = true;
				convertStandardHex = true;
				convertBungeeHex = true;
			} else {
				tagResolver = provideTagResolver(permissible);
				convertMojangColor = permissible.hasPermission(MOJANG_COLOR_PERMISSION);
				convertStandardHex = permissible.hasPermission(STANDARD_HEX_PERMISSION);
				convertBungeeHex = permissible.hasPermission(BUNGEE_HEX_PERMISSION);
			}
		}

		@Override
		public Component process(String input) {
			if (input == null || input.isEmpty()) return Component.empty();
			String output = LegacyTextUtils.toAdventure(input, true, convertBungeeHex, convertStandardHex, convertMojangColor);
			return EMPTY_MINIMESSAGE.deserialize(output, tagResolver);
		}
	}
}
