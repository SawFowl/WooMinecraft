package sawfowl.woominecraft.commands;

import java.util.Locale;

import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.Command.Builder;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.plugin.PluginContainer;

import com.plugish.woominecraft.WooMinecraft;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public abstract class AbstractCommand implements CommandExecutor {

	protected final WooMinecraft plugin;
	protected final Component chatPrefix = toText("&5[&fWooMinecraft&5] ");
	public AbstractCommand(WooMinecraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		if(!plugin.isEnable()) throw new CommandException(toText("&cWooMinecraft doesn't support offline mode"));
		execute(context, context.cause().audience(), getLocale(context.cause()));
		return CommandResult.success();
	}

	public Builder builder() {
		return Command.builder().permission("woo.admin").executor(this);
	}

	protected Component getText(Locale locale, Object... path) {
		return plugin.getLocalizedText(locale, path);
	}

	protected Component getTextWhithPrefix(Locale locale, Object... path) {
		return chatPrefix.append(getText(locale, path));
	}

	protected PluginContainer getContainer() {
		return plugin.getContainer();
	}

	protected Component toText(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}

	public abstract void execute(CommandContext context, Audience audience, Locale locale) throws CommandException ;

	public abstract Parameterized build();

	private Locale getLocale(CommandCause cause) {
		return cause.audience() instanceof SystemSubject ? plugin.getSystemOrDefaultLocale() : (cause.audience() instanceof LocaleSource ? ((LocaleSource) cause.audience()).locale() : org.spongepowered.api.util.locale.Locales.DEFAULT);
	}

}
