package cursedflames.cubeloader.proxy;

public interface ISideProxy {
	public default String format(String translateKey, Object... parameters) {
		// Why does Minecraft localize item names, etc. serverside anyway
		// That doesn't make any sense
		return translateKey;
	}
}
