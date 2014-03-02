/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzyRDFSEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;

import queryInterpreter.TargetCodeGenerator;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import commandParser.ParserAndExecuterEngine.InfLevel;

/**
 * 
 * @author feffo
 */
public class FuzzySystemMainMemory extends AbstractFuzzySystem {

	public FuzzySystemMainMemory(int infLevel) {
		if (infLevel == InfLevel.ALL_INF.ordinal())
			model = new GenericModel(ModelFactory.createInfModel(reasoner,
					TDBFactory.createModel()));
		else
			model = new GenericModel(TDBFactory.createModel());
	}

}
