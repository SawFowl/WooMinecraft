package sawfowl.woominecraft.commands;

import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.parameter.CommandContext;

import com.plugish.woominecraft.WooMinecraft;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Debug extends AbstractCommand {

	public Debug(WooMinecraft plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale) {
		plugin.getConfig().setDebug(!plugin.getConfig().isDebug());
		audience.sendMessage(chatPrefix.append(Component.text("Set debug to: ").append(Component.text(plugin.getConfig().isDebug()).color(plugin.getConfig().isDebug() ? NamedTextColor.GREEN : NamedTextColor.RED))));
	}

	@Override
	public Parameterized build() {
		return builder().build();
	}

}
