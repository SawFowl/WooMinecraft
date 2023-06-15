package sawfowl.woominecraft;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class WhitelistWorlds {

	public WhitelistWorlds(){}

	@Setting("Enable")
	private boolean enable = false;
	@Setting("List")
	private List<String> list = Arrays.asList("minecraft:overworld");

	public boolean isEnable() {
		return enable;
	}

	public List<String> getList() {
		return list;
	}

}
