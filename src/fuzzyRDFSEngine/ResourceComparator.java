package fuzzyRDFSEngine;

import java.util.Comparator;

import com.hp.hpl.jena.rdf.model.Resource;

class ResourceComparator implements Comparator<Resource> {
	static final Comparator<? super Resource> resourceComparator = new ResourceComparator();

	@Override
	public int compare(Resource o1, Resource o2) {
		return o1.toString().compareTo(o2.toString());
	}
}
