package fuzzyRDFSEngine;

import java.io.InputStream;
import java.io.PrintWriter;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class GenericModel {

	private final boolean isInferencer;
	private InfModel infModel;
	private Model model;

	public GenericModel(InfModel infModel) {
		this.infModel = infModel;
		isInferencer = true;
	}

	public GenericModel(Model model) {
		this.model = model;
		isInferencer = false;
	}

	public void read(InputStream in, String lang) {
		if (isInferencer)
			infModel.read(in, lang);
		else
			model.read(in, lang);
	}

	public void removeAll() {
		if (isInferencer)
			infModel.removeAll();
		else
			model.removeAll();
	}

	public void close() {
		if (isInferencer)
			infModel.close();
		else
			model.close();
	}

	public void write(PrintWriter out, String language) {
		if (isInferencer)
			infModel.write(out, language);
		else
			model.write(out, language);
	}

	public StmtIterator listStatements() {
		if (isInferencer)
			return infModel.listStatements();
		return model.listStatements();
	}

	public Resource createResource(String a) {
		if (isInferencer)
			return infModel.createResource(a);
		return model.createResource(a);
	}

	public void createStatement(Resource subject, Property predicate,
			RDFNode object) {
		if (isInferencer)
			infModel.createStatement(subject, predicate, object);
		else
			model.createStatement(subject, predicate, object);
	}

	public double getTotalStatementsCount() {
		if (isInferencer)
			return infModel.getGraph().size();
		return model.getGraph().size();
	}

	public void add(Statement reificationPart) {
		if (isInferencer)
			infModel.add(reificationPart);
		else
			model.add(reificationPart);
	}

	public void createStatement(Resource subject, Property predicate,
			String object) {
		if (isInferencer)
			infModel.createStatement(subject, predicate, object);
		else
			model.createStatement(subject, predicate, object);
	}

	public Graph getGraph() {
		if (isInferencer)
			return infModel.getGraph();
		return model.getGraph();
	}

	public long size() {
		if (isInferencer)
			return infModel.size();
		return model.size();
	}

}
