package sawfowl.woominecraft;

import java.util.function.Consumer;

import org.spongepowered.api.scheduler.ScheduledTask;

import com.plugish.woominecraft.WooMinecraft;

public class SpongeRunner implements Consumer<ScheduledTask>  {

	public final WooMinecraft plugin;

	public SpongeRunner(WooMinecraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public void accept(ScheduledTask t) {
		try {
			plugin.check();
		} catch (Exception e) {
			plugin.getLogger().warn(e.getMessage());
			e.printStackTrace();
		}
	}

}
