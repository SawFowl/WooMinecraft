package sawfowl.woominecraft.commands;

import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.parameter.CommandContext;

import com.plugish.woominecraft.WooMinecraft;

import net.kyori.adventure.audience.Audience;

public class Help extends AbstractCommand {

	public Help(WooMinecraft plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale) {
		audience.sendMessage(toText("&d/woo help &f- &aShows this Helpsite\n&d/woo check &f- &aCheck for donations/orders\n&d/woo ping &f- &aTest server connection\n&d/woo debug &f - &aEnable/disable debugging"));
	}

	@Override
	public Parameterized build() {
		return builder().build();
	}

}
