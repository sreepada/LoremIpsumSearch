import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;

import java.net.MalformedURLException;

public class SolrjSearcher {
    public static void main(String[] args) throws MalformedURLException, SolrServerException {
        HttpSolrServer solr = new HttpSolrServer("http://localhost:8983/solr");

        SolrQuery query = new SolrQuery();
        query.setQuery("artic");
        //query.setQuery("text:\"cat\"");
        query.addFilterQuery("(text:\"oil extraction\"~10) AND (text:\"iron\")");
        //query.addFilterQuery("cat:book");
        //query.addFilterQuery("id:book-2");
        //query.addFilterQuery("science_keywords_s:Paleoclimate");
        query.setFields("id","*");
        query.setStart(0);    
        query.setRows(500);    
        query.set("defType", "edismax");

        //QueryResponse response = solr.query(new SolrQuery("text:arctic and ((text:\"oil exploration\"~10) or (text:\"iron\") or (text:oil))"));
        QueryResponse response = solr.query(query);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            System.out.println("==============================================================================================================");
            SolrDocument thisDoc = results.get(i);
            System.out.println("id\t:" + thisDoc.getFieldValue("id"));
            System.out.println("\ttitle\t\t:" + thisDoc.getFieldValue("title"));
            System.out.println("\tdescription\t:" + thisDoc.getFieldValue("description"));
            System.out.println("\tattr_content\t:" + thisDoc.getFieldValue("description"));
        }
    }
}
