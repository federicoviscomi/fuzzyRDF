package fuzzyRDFSEngine;

/**
 * Contiene informazioni di degug
 * 
 * @author feffo
 * 
 */
class DebugInfo {

	static enum DebugLevels {
		REWRITE, DATABASE, ALLWRAPPEROPERATION, INTERNALSTATEMENTSTORE, BUILTIN2, MONETDBWRAPPER_ADDALL, AT_LAST
	}

	private static boolean[] debugInfo;

	static {
		debugInfo = new boolean[DebugLevels.AT_LAST.ordinal()];
		for (int i = 0; i < debugInfo.length; i++)
			debugInfo[i] = false;
		// setDebugLevel(DebugLevels.REWRITE, true);
	}

	public static boolean isDebugLevelSet(DebugLevels level) {
		return debugInfo[level.ordinal()];
	}

	public static void setDebugLevel(DebugLevels level, boolean value) {
		debugInfo[level.ordinal()] = value;
	}
}
