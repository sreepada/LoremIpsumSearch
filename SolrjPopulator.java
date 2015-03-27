import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
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

public class SolrjPopulator {
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
        System.out.println(files[i]);
        SolrInputDocument solr_doc = new SolrInputDocument();

        //File input = new File("/home/madan/usc_notes/spring_2015/ir/nutch/runtime/local/alldata/Wei2011.html");
        //File input = new File(files[i]);
        Document doc = Jsoup.parse(files[i], "UTF-8", "http://example.com/");
        System.out.println(doc.title());
        Elements elements = doc.body().getElementsByClass("descriptive_row_table");
        solr_doc.addField("id" , "html-" + i);
        solr_doc.addField("title", doc.title());
        System.out.println("Title=" + doc.title());
        for(Element element: elements)
        {
            String text = element.text();
            String[] key_val_pair = text.split(":", 2);
            //System.out.println(key_val_pair.length);
            String key = key_val_pair[0].toLowerCase().replace(" ", "_");
            if(key.contains("most_l"))
            {
                key = key.substring(0, 1) + "_coordinate";
            }
            else
            {
                key = key + "_s";
            }
            String value = key_val_pair[1].replace(" > ", ",");
            if(key.endsWith("_coordinate"))
            {
                value = value.replaceAll("^[ ()]+", "").replaceAll("[ ()]+$", "");
                value = value.split(" ")[0];
            }
            //System.out.println("key=" + key + "\tvalue=" + value);
            solr_doc.addField(key, value);
        }
        server.add(solr_doc);
        server.commit();
    } 
  }
}
