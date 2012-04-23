package uk.ac.dotrural.irp.ecosystem.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.resultset.JSONOutput;

public class Util {
	public static String resultsetToString(ResultSet results) {
		JSONOutput json = new JSONOutput();
		String jsonResults = json.asString(results);

		return jsonResults;
	}

	/**
	 * Creates a string consisting of params.length repetitions of sparqlQuery,
	 * each with one value of param inserted
	 * 
	 * @param sparqlQuery
	 *            SPARQL query with one %s to be replaced
	 * @param params
	 *            looped through to replace the %s in sparqlQuery
	 * @return A string containing params occurances of sparqlQuery, each with a
	 *         different param inserted for the %s in sparqlQuery
	 */
	public static String buildRepetativeQuery(String sparqlQuery,
			String... params) {
		StringBuilder sparql = new StringBuilder();
		for (String param : params) {
			sparql.append(String.format(sparqlQuery, param));
		}
		return sparql.toString();
	}

	public static String getNodeValue(RDFNode rdfNode) {
		if (rdfNode == null)
			return "";
		else if (rdfNode.isLiteral())
			return rdfNode.asLiteral().getLexicalForm();
		else if (rdfNode.isResource() || rdfNode.isURIResource())
			return rdfNode.asResource().getURI();
		else
			return rdfNode.toString();
	}

	public static Double getNodeDoubleValue(RDFNode rdfNode) {
		if (rdfNode == null) {
			return null;
		}
		if (rdfNode.isLiteral()) {
			Literal l = rdfNode.asLiteral();
			return new Double(l.getDouble());
		}
		return null;

	}
	public static Long getNodeLongValue(RDFNode rdfNode) {
		if (rdfNode == null) {
			return null;
		}
		if (rdfNode.isLiteral()) {
			Literal l = rdfNode.asLiteral();
			return new Long(l.getLong());
		}
		return null;
		
	}

	public static String getMD5(String str) throws NoSuchAlgorithmException {
		MessageDigest algorithm = MessageDigest.getInstance("MD5");
		algorithm.reset();
		algorithm.update(str.getBytes());

		StringBuffer hexString = new StringBuffer();
		for (byte b : algorithm.digest())
			hexString.append(Integer.toHexString(0xFF & b));

		return hexString.toString();
	}

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss");

	public static String timeNow() {
		return dateFormat.format(new Date(System.currentTimeMillis()));

	}
}
