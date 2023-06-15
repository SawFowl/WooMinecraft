package sawfowl.woominecraft.commands;

import java.util.Locale;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

import com.plugish.woominecraft.WooMinecraft;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class Check extends AbstractCommand {

	public Check(WooMinecraft plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale) throws CommandException {
		Sponge.asyncScheduler().executor(getContainer()).execute(() -> {
			try {
				if (plugin.check()) {
					audience.sendMessage(getTextWhithPrefix(locale, "general" + "processed"));
				} else {
					audience.sendMessage(getTextWhithPrefix(locale, "general" + "none_avail"));
				}
			} catch (Exception e) {
				// send feedback for the sender
				if(e.getMessage().contains( "Expected BEGIN_OBJECT but was STRING" )) audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "rest_error")).hoverEvent(HoverEvent.showText(Component.text(e.getLocalizedMessage()).color(NamedTextColor.RED))));
				e.printStackTrace();
			}
		});
	}

	@Override
	public Parameterized build() {
		return builder().build();
	}

}
