import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.net.MalformedURLException;

public class SolrjSearcher {
  public static void main(String[] args) throws MalformedURLException, SolrServerException {
    HttpSolrServer solr = new HttpSolrServer("http://localhost:8983/solr");

    SolrQuery query = new SolrQuery();
    query.setQuery(args[0]);
    query.setFields("id","*");
    query.setStart(0);    
    query.set("defType", "edismax");

    QueryResponse response = solr.query(query);
    SolrDocumentList results = response.getResults();
    for (int i = 0; i < results.size(); ++i) {
      System.out.println( (i+1) + ":" + results.get(i).get("title"));
    }
  }
}
