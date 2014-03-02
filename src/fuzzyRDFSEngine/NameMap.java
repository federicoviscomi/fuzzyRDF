package fuzzyRDFSEngine;

import java.util.HashMap;
import java.util.Map;

public class NameMap {

	private final Map<String, String> map;

	public NameMap() {
		map = new HashMap<String, String>();
		map.put("type", "rdftype");
	}

	public String get(String predicateName) {
		String tableName;
		predicateName = predicateName.toLowerCase();
		if ((tableName = map.get(predicateName)) == null)
			return predicateName;
		return tableName;
	}

}
