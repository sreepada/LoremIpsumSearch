import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.File;
import java.io.FileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;


public class SolrjUpdater {
  public static void main(String[] args) throws IOException, SolrServerException {
    //HttpSolrServer server = new HttpSolrServer("http://localhost:8983/solr");
    //for(int i=0;i<1000;++i) {
    //  SolrInputDocument doc = new SolrInputDocument();
    //  doc.addField("cat", "book");
    //  doc.addField("id", "book-" + i);
    //  doc.addField("name", "The Legend of the Hobbit part " + i);
    //  server.add(doc);
    //  if(i%100==0) server.commit();  // periodically flush
    //}
    HttpSolrServer server = new HttpSolrServer("http://localhost:8983/solr");

    File dir = new File(args[0]);
    FileFilter fileFilter = new WildcardFileFilter("*.html");
    File[] files = dir.listFiles(fileFilter);
    for (int i = 0; i < files.length; i++)
    {
        SolrInputDocument solr_doc = new SolrInputDocument();
        solr_doc.addField("id" , "html-" + i);
        Map<String, Object> fieldModifier = new HashMap<>(1);
        fieldModifier.put("set", "old_value");
        solr_doc.addField("new_field_s", fieldModifier);
        server.add(solr_doc);
        server.commit();
    } 
  }
}
