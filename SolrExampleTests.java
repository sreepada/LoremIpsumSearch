import java.io.File;
import java.io.IOException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.SolrDocumentList;

/**
 * @author EDaniel
 */
public class SolrExampleTests {

    public static void main(String[] args) {
        try {
            //Solr cell can also index MS file (2003 version and 2007 version) types.
            String fileName = "/home/sreepada/Documents/CSCI_572/lucene_solr_4_10/solr/example/exampledocs/books.csv";
            //this will be unique Id used by Solr to index the file contents.
            String solrId = "books.csv"; 

            indexFilesSolrCell(fileName, solrId);

        } catch (Exception ex) {
//            System.out.println(ex.toString());
        }
    }

    /**
     * Method to index all types of files into Solr. 
     * @param fileName
     * @param solrId
     * @throws IOException
     * @throws SolrServerException
     */
    public static void indexFilesSolrCell(String fileName, String solrId) 
        throws IOException, SolrServerException {

        String urlString = "http://localhost:8983/solr"; 
        SolrServer solr = new HttpSolrServer(urlString);

        ContentStreamUpdateRequest up 
            = new ContentStreamUpdateRequest("/update/extract");

        up.addFile(new File(fileName), "csv");

        up.setParam("literal.id", solrId);
        up.setParam("uprefix", "attr_");
        up.setParam("fmap.content", "attr_content");

        up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);

        solr.request(up);

        QueryResponse rsp = solr.query(new SolrQuery("id:Benchmark.html"));
        SolrDocumentList results = rsp.getResults();
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        for (int i=0; i < results.size(); i++)
            System.out.println(results.get(i).getFieldNames());
    }
}
