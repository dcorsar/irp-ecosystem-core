package uk.ac.dotrural.irp.ecosystem.core.services;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import uk.ac.dotrural.irp.ecosystem.core.Constants;
import uk.ac.dotrural.irp.ecosystem.core.models.jaxb.system.EndpointInfo;
import uk.ac.dotrural.irp.ecosystem.core.models.jaxb.system.Query;
import uk.ac.dotrural.irp.ecosystem.core.models.jaxb.system.ServiceInitialiser;
import uk.ac.dotrural.irp.ecosystem.core.models.jaxb.system.SystemMessage;
import uk.ac.dotrural.irp.ecosystem.core.resources.support.reporters.ExceptionReporter;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class SPARQLEndpoint {
	private String sparql_framework;
	private String serviceURI;
	private String updateURI;
	private String queryURI;
	private UriInfo uriInfo;

	public SPARQLEndpoint() {
	}

	public String getSparql_framework() {
		return sparql_framework;
	}

	public void setSparql_framework(String sparql_framework) {
		this.sparql_framework = sparql_framework;
	}

	public String getServiceURI() {
		return serviceURI;
	}

	public void setServiceURI(String serviceURI) {
		this.serviceURI = serviceURI;
	}

	public String getUpdateURI() {
		return updateURI;
	}

	public void setUpdateURI(String updateURI) {
		this.updateURI = updateURI;
	}

	public String getQueryURI() {
		return queryURI;
	}

	public void setQueryURI(String queryURI) {
		this.queryURI = queryURI;
	}

	public UriInfo getUriInfo() {
		return uriInfo;
	}

	public void setUriInfo(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}

	public SystemMessage init(UriInfo uriInfo, ServiceInitialiser si) {
		if (uriInfo == null)
			throw new ExceptionReporter(new NullPointerException(
					"Initialising error. Context 'UriInfo' missing."));

		this.uriInfo = uriInfo;

		if (si == null)
			throw new ExceptionReporter(new NullPointerException(
					"No initialising parameters given."));

		System.out.format("   INFO: sparql_framework "
				+ si.getSparqlFramework());

		if (si.getUri() != null && !si.getUri().trim().equals("")) {
			this.serviceURI = si.getUri().trim();

			this.sparql_framework = (si.getSparqlFramework() != null && !si
					.getSparqlFramework().trim().equals("")) ? si
					.getSparqlFramework() : "joseki";
			if (sparql_framework.equals(Constants.FUSEKI)) {
				this.updateURI = serviceURI + "/update";
				this.queryURI = serviceURI + "/query";
			} else if (sparql_framework.equals(Constants.SESAME)) {
				this.updateURI = serviceURI + "/statements";
				this.queryURI = serviceURI;
			} else
				this.queryURI = serviceURI;

			System.out.format("  INFO: The sevice URI is '%s'\n", serviceURI);
			System.out.format("  INFO: The query URI is '%s'\n", queryURI);
			if (sparql_framework.equals(Constants.FUSEKI)
					|| sparql_framework.equals(Constants.SESAME))
				System.out
						.format("  INFO: The update URI is '%s'\n", updateURI);

			return new SystemMessage("success", String.format(
					"Service is created with SPARQL endpoint '%s'", serviceURI));
		} else if (si.getServicename() != null
				&& !si.getServicename().trim().equals("")) {
			this.sparql_framework = (si.getSparqlFramework() != null && !si
					.getSparqlFramework().trim().equals("")) ? si
					.getSparqlFramework() : "joseki";

			String[] elems = this.uriInfo.getBaseUri().toString().split("/");
			if (elems.length >= 3) {
				this.serviceURI = elems[0]
						+ "//"
						+ elems[2]
						+ (sparql_framework.equals(Constants.JOSEKI) ? "/joseki/"
								: "/") + si.getServicename();
				if (this.sparql_framework.equals(Constants.FUSEKI)) {
					this.updateURI = serviceURI + "/update";
					this.queryURI = serviceURI + "/query";
				} else if (sparql_framework.equals(Constants.SESAME)) {
					this.updateURI = serviceURI + "/statements";
					this.queryURI = serviceURI;
				} else
					this.queryURI = serviceURI;

				System.out.format("  INFO: The sevice URI is '%s'\n",
						serviceURI);
				System.out.format("  INFO: The query URI is '%s'\n", queryURI);
				if (sparql_framework.equals(Constants.FUSEKI)
						|| sparql_framework.equals(Constants.SESAME))
					System.out.format("  INFO: The update URI is '%s'\n",
							updateURI);

				return new SystemMessage("success", String.format(
						"Service is created with SPARQL endpoint '%s'",
						serviceURI));
			} else
				throw new ExceptionReporter(new NullPointerException(
						String.format("Peculiar base URI '%s'", this.uriInfo
								.getBaseUri().toString())));
		}

		throw new ExceptionReporter(new NullPointerException(
				SPARQLEndpoint.class.getSimpleName()
						+ " -> SPARQL endpoint is not given."));
	}

	public void update(Query query) {
		if (sparql_framework.equals(Constants.JOSEKI))
			throw new ExceptionReporter(
					new Exception(
							String.format(
									"This SPARQL endpoint %s is based on Joseki.\n"
											+ "It does not support SPARQL updates such as INSERT/DELETE/UPDATE/",
									serviceURI)));

		if (updateURI == null)
			throw new ExceptionReporter(new NullPointerException(
					"Service MUST be initialised with a SPARQL endpoint."));
		if (query == null)
			throw new ExceptionReporter(new NullPointerException(
					"No SPARQL query given."));
		if (query.getQuery() == null)
			throw new ExceptionReporter(new NullPointerException(
					"SPARQL query is not given."));
		if (query.getQuery().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"Empty SPARQL query."));

		String sQuery = query.getQuery();

		System.out.format("  INFO: %s => %s\n", sQuery, updateURI);

		UpdateRequest ur = new UpdateRequest();
		ur.add(sQuery);

		if (sparql_framework.equals(Constants.SESAME)) {
			UpdateProcessor up = UpdateExecutionFactory.createRemoteForm(ur,
					updateURI);
			up.execute();
		} else {
			UpdateProcessor up = UpdateExecutionFactory.createRemote(ur,
					updateURI);
			up.execute();
//			UpdateRemote.execute(ur, updateURI);
		}
	}

	public void update(Query... query) {
		if (sparql_framework.equals(Constants.JOSEKI))
			throw new ExceptionReporter(
					new Exception(
							String.format(
									"This SPARQL endpoint %s is based on Joseki.\n"
											+ "It does not support SPARQL updates such as INSERT/DELETE/UPDATE/",
									serviceURI)));

		if (updateURI == null)
			throw new ExceptionReporter(new NullPointerException(
					"Service MUST be initialised with a SPARQL endpoint."));
		if (query == null)
			throw new ExceptionReporter(new NullPointerException(
					"No SPARQL query given."));
		if (query.length == 0) {
			throw new ExceptionReporter(new IllegalArgumentException(
					"No update queries provided"));
		}
		for (Query q : query) {
			if (q == null)
				throw new ExceptionReporter(new NullPointerException(
						"SPARQL query is not given."));

			if (q.equals(""))
				throw new ExceptionReporter(new NullPointerException(
						"Empty SPARQL query."));
		}

		UpdateRequest ur = new UpdateRequest();
		for (Query q : query) {
			String sQuery = q.getQuery();

			System.out.format("  INFO: %s => %s\n", sQuery, updateURI);

			ur.add(sQuery);
		}
		if (sparql_framework.equals(Constants.SESAME)) {
			UpdateProcessor up = UpdateExecutionFactory.createRemoteForm(ur,
					updateURI);
			up.execute();
		} else {
			UpdateProcessor up = UpdateExecutionFactory.createRemote(ur,
					updateURI);
			up.execute();
//			UpdateRemote.createR(ur, updateURI);
		}
//		UpdateRemote.execute(ur, updateURI);
	}

	public void update(List<Query> query) {
		if (sparql_framework.equals(Constants.JOSEKI))
			throw new ExceptionReporter(
					new Exception(
							String.format(
									"This SPARQL endpoint %s is based on Joseki.\n"
											+ "It does not support SPARQL updates such as INSERT/DELETE/UPDATE/",
									serviceURI)));

		if (updateURI == null)
			throw new ExceptionReporter(new NullPointerException(
					"Service MUST be initialised with a SPARQL endpoint."));
		if (query == null)
			throw new ExceptionReporter(new NullPointerException(
					"No SPARQL query given."));
		if (query.size() == 0) {
			throw new ExceptionReporter(new IllegalArgumentException(
					"No update queries provided"));
		}
		for (Query q : query) {
			if (q == null)
				throw new ExceptionReporter(new NullPointerException(
						"SPARQL query is not given."));

			if (q.equals(""))
				throw new ExceptionReporter(new NullPointerException(
						"Empty SPARQL query."));
		}

		UpdateRequest ur = new UpdateRequest();
		for (Query q : query) {
			String sQuery = q.getQuery();

			System.out.format("  INFO: %s => %s\n", sQuery, updateURI);

			ur.add(sQuery);
		}
		if (sparql_framework.equals(Constants.SESAME)) {
			UpdateProcessor up = UpdateExecutionFactory.createRemoteForm(ur,
					updateURI);
			up.execute();
		} else {
			UpdateProcessor up = UpdateExecutionFactory.createRemote(ur,
					updateURI);
			up.execute();
//			UpdateRemote.execute(ur, updateURI);
		}
	}

	public ResultSet query(Query query) {
		if (queryURI == null)
			throw new ExceptionReporter(new NullPointerException(
					"Service MUST be initialised with a SPARQL endpoint."));
		if (query == null)
			throw new ExceptionReporter(new NullPointerException(
					"No SPARQL query given."));
		if (query.getQuery() == null)
			throw new ExceptionReporter(new NullPointerException(
					"SPARQL query is not given."));
		if (query.getQuery().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"Empty SPARQL query."));

		String sQuery = query.getQuery();

		System.out.format("  INFO: %s => %s\n", sQuery, queryURI);

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				queryURI, sQuery);
		ResultSet results = queryExecution.execSelect();

		return results;
	}

	public boolean ask(Query query) {
		System.out.println(queryURI);
		if (queryURI == null)
			throw new ExceptionReporter(new NullPointerException(
					"Service MUST be initialised with a SPARQL endpoint."));
		if (query == null)
			throw new ExceptionReporter(new NullPointerException(
					"No SPARQL query given."));
		if (query.getQuery() == null)
			throw new ExceptionReporter(new NullPointerException(
					"SPARQL query is not given."));
		if (query.getQuery().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"Empty SPARQL query."));

		String sQuery = query.getQuery();

		System.out.format("  INFO: %s => %s\n", sQuery, queryURI);

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				queryURI, sQuery);
		return queryExecution.execAsk();
	}

	public EndpointInfo info() {
		EndpointInfo endpointInfo = new EndpointInfo();
		endpointInfo.setServiceURI(this.getServiceURI());
		endpointInfo.setSparql_framework(this.getSparql_framework());
		endpointInfo.setQueryURI(this.getQueryURI());
		endpointInfo.setUpdateURI(this.getUpdateURI());

		return endpointInfo;
	}
}