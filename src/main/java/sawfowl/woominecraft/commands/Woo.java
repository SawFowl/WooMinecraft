package sawfowl.woominecraft.commands;

import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.parameter.CommandContext;

import com.plugish.woominecraft.WooMinecraft;

import net.kyori.adventure.audience.Audience;

public class Woo extends AbstractCommand {

	public Woo(WooMinecraft plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale) {
		audience.sendMessage(getTextWhithPrefix(locale, "general", "avail_commands"));
		audience.sendMessage(getTextWhithPrefix(locale, "general", "command_check"));
		audience.sendMessage(getTextWhithPrefix(locale, "general", "command_ping"));
		audience.sendMessage(getTextWhithPrefix(locale, "general", "command_debug"));
	}

	@Override
	public Parameterized build() {
		return builder()
				.addChild(new Check(plugin).build(), "check")
				.addChild(new Ping(plugin).build(), "ping")
				.addChild(new Debug(plugin).build(), "debug")
				.build();
	}

}
