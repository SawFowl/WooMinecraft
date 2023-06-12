package sawfowl.woominecraft;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class Config {

	public Config() {}

	@Setting("update_interval")
	private int updateInterval = 1500;
	@Setting("url")
	private String url = "http://playground.dev";
	@Setting("prettyPermalinks")
	private boolean prettyPermalinks = true;
	@Setting("restBasePath")
	private String restBasePath = "";
	@Setting("key")
	private String key = "";
	@Setting("whitelist-worlds")
	private WhitelistWorlds whitelistWorlds = new WhitelistWorlds();
	@Setting("debug")
	private boolean debug = false;

	public int getUpdateInterval() {
		return updateInterval;
	}

	public String getUrl() {
		return url;
	}

	public boolean isPrettyPermalinks() {
		return prettyPermalinks;
	}

	public String getRestBasePath() {
		return restBasePath;
	}

	public String getKey() {
		return key;
	}

	public WhitelistWorlds getWhitelistWorlds() {
		return whitelistWorlds;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean value) {
		debug = value;
	}

}
