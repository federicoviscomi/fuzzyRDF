/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzyRDFSEngine;

import java.io.File;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import commandParser.ParserAndExecuterEngine.InfLevel;

/**
 * 
 * @author feffo
 */
public class FuzzySystemFile extends AbstractFuzzySystem {

	public FuzzySystemFile(String root, int infLevel) {
		File dir = new File(root);
		if (dir.exists() && !dir.isDirectory())
			throw new IllegalArgumentException(" file " + root
					+ " exist and is not a directory");
		dir.mkdir();
		if (infLevel == InfLevel.ALL_INF.ordinal()) {
			model = new GenericModel(ModelFactory.createInfModel(reasoner,
					TDBFactory.createModel(root)));
		} else
			model = new GenericModel(TDBFactory.createModel(root));
	}

}
