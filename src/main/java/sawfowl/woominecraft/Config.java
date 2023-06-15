package sawfowl.woominecraft;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class Config {

	public Config() {}

	@Setting("UpdateInterval")
	@Comment("This is how often, in seconds, the server will contact your WordPress installation\nto see if there are donations that need made.")
	private int updateInterval = 1500;
	@Setting("URL")
	@Comment("You must set this to your WordPress site URL.  If you installed WordPress in a\nsubdirectory, it should point there.")
	private String url = "http://playground.dev";
	@Setting("PrettyPermalinks")
	@Comment("If you are having issues with REST, or have disabled pretty permalinks. Set this to false.\nDoing so will use the old /index.php?rest_route=/wmc/v1/server/ base\nSetting this to false will also allow you to set the restBasePath value if you have altered your\ninstallation in any way.")
	private boolean prettyPermalinks = true;
	@Setting("RestBasePath")
	@Comment("If your REST API has a custom path base, input it here.\nNOTE: This is only loaded if prettyPermalinks is set to false.\nKnown good URL bases.\n- /wp-json/wmc/v1/server/\n- /index.php?rest_route=/wmc/v1/server/")
	private String restBasePath = "";
	@Setting("Key")
	@Comment("This must match the WordPress key in your admin panel for WooMinecraft\nThis is a key that YOU set, both needing to be identical in the admin panel\nand in this config file\nFor security purposes, you MUST NOT leave this empty.")
	private String key = "";
	@Setting("WhitelistWorlds")
	@Comment("Allowed worlds the player needs to be in to run the commands.\nDisabled by default!")
	private WhitelistWorlds whitelistWorlds = new WhitelistWorlds();
	@Setting("Debug")
	@Comment("Set to true in order to toggle debug information")
	private boolean debug = false;
	@Setting("MojangURL")
	private String mojangURL = "https://api.mojang.com/users/profiles/minecraft/";

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

	public String getMojangURL() {
		return mojangURL;
	}

}
