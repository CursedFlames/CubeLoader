package cursedflames.cubeloader.proxy;

public interface ISideProxy {
	public default String translateWithArgs(String string, Object... args) {
		return string;
	}

	public default String translate(String string) {
		return string;
	}

	public default boolean hasTranslationKey(String string) {
		return false;
	}
}
