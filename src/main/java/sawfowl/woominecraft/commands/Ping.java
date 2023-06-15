package sawfowl.woominecraft.commands;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

import com.plugish.woominecraft.WooMinecraft;

import net.kyori.adventure.audience.Audience;

public class Ping extends AbstractCommand {

	Parameter.Value<String> url = Parameter.string().key("Url").optional().build();
	public Ping(WooMinecraft plugin) {
		super(plugin);
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale) {
		Sponge.asyncScheduler().executor(getContainer()).execute(() -> {
			// allow ability to ping any website to test outgoing connections from the mc server
			// example /woo ping https://www.google.com/
			context.one(url);
			if (context.one(url).isPresent()) {
				try {
					URL U = new URL(context.one(url).get());
					HttpURLConnection ping = (HttpURLConnection) U.openConnection();
					ping.setConnectTimeout(1000);
					ping.setReadTimeout(1000);
					ping.setRequestMethod("HEAD");
					int Rc = ping.getResponseCode();
					String rs = ping.getResponseMessage();
					ping.disconnect();
					if (Rc < 199) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_issues").append(toText(Rc + " ")).append(toText(rs))));
					} else if (Rc >= 200 && Rc <= 299) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_good").append(toText(Rc))));
					} else if (Rc >=300 && Rc <= 399) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_issues").append(toText(Rc + " ")).append(toText(rs))));
					} else if ( Rc >= 400 && Rc <=599) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_bad").append(toText(Rc + " ")).append(toText(rs))));
					}
				} catch (IOException e) {
					audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_fail")));
				}

			} else {
				try {
					audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_server")));
					HttpURLConnection ping = (HttpURLConnection) new URL(plugin.getConfig().getUrl()).openConnection();
					ping.setConnectTimeout(700);
					ping.setReadTimeout(700);
					ping.setRequestMethod("HEAD");
					int Rc = ping.getResponseCode();
					String rs = ping.getResponseMessage();
					ping.disconnect();
					if (Rc < 199) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_issues").append(toText(Rc + " ")).append(toText(rs))));
					} else if (Rc >= 200 && Rc <= 299) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_good").append(toText(Rc))));
					} else if (Rc >= 300 && Rc <= 399) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_issues").append(toText(Rc + " ")).append(toText(rs))));
					} else if (Rc >= 400 && Rc <= 599) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_bad").append(toText(Rc + " ")).append(toText(rs))));
					}
				} catch (IOException e) {
					// send feedback for the sender
					plugin.getLogger().error(e.getMessage());
					audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_fail")));
					if (plugin.isDebug()) {
						plugin.getLogger().info(plugin.getConfig().getKey());
						plugin.getLogger().info(plugin.getConfig().getUrl());
					}
				}
				try {
					audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_rest")));
					HttpURLConnection ping = (HttpURLConnection) plugin.getSiteURL().openConnection();
					ping.setConnectTimeout(700);
					ping.setReadTimeout(700);
					ping.setRequestMethod("HEAD");
					int Rc = ping.getResponseCode();
					String rs = ping.getResponseMessage();
					ping.disconnect();
					if (Rc < 199) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_issues").append(toText(Rc + " ")).append(toText(rs))));
					} else if (Rc >= 200 && Rc <= 299) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_good").append(toText(Rc))));
					} else if (Rc >= 300 && Rc <= 399) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_issues").append(toText(Rc + " ")).append(toText(rs))));
					} else if (Rc >= 400 && Rc <= 599) {
						audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_bad").append(toText(Rc + " ")).append(toText(rs))));
					}
				} catch (IOException e) {
					plugin.getLogger().error(e.getMessage());
					audience.sendMessage(chatPrefix.append(plugin.getLocalizedGeneralText(locale, "check_fail")));
					if (plugin.isDebug()) {
						plugin.getLogger().info(plugin.getConfig().getKey());
						plugin.getLogger().info(plugin.getConfig().getUrl());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public Parameterized build() {
		return builder().addParameter(url).build();
	}

}
