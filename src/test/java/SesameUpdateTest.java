import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class SesameUpdateTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		
		UpdateRequest ur = new UpdateRequest();
		String sQuery = "insert data { <http://www.example.com/c> a <http://www.example.com/b>}";
//		ur.add(sQuery);
////		UpdateRemote.execute(ur, "http://dtp-24.sncs.abdn.ac.uk:8080/openrdf-sesame/repositories/journeys/statements");
//		UpdateProcessor up = UpdateExecutionFactory
//				.createRemoteForm(
//						ur,
//						"http://dtp-24.sncs.abdn.ac.uk:8080/openrdf-sesame/repositories/journeys/statements");
//		up.execute();

		sQuery = "ask { ?s ?p ?o}";
		QueryExecution queryExecution = QueryExecutionFactory
				.sparqlService(
						"http://dtp-24.sncs.abdn.ac.uk:8080/openrdf-sesame/repositories/journeys",
						sQuery);
		boolean results = queryExecution.execAsk();
		System.out.println(results);
//		System.out.println(ResultSetFormatter.asText(results));
	}

}
