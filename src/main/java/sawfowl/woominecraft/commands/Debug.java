package sawfowl.woominecraft.commands;

import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.parameter.CommandContext;

import com.plugish.woominecraft.WooMinecraft;

import net.kyori.adventure.audience.Audience;

public class Debug extends AbstractCommand {

	public Debug(WooMinecraft plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale) {
		plugin.getConfig().setDebug(!plugin.getConfig().isDebug());
		audience.sendMessage(plugin.getLocalizedGeneralText(locale, plugin.getConfig().isDebug() ? "enable_debug" : "disable_debug"));
	}

	@Override
	public Parameterized build() {
		return builder().build();
	}

}
