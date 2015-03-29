import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile;
import org.apache.nutch.util.NutchConfiguration;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.crawl.Inlink;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.util.*;
import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class pageRankUrl {
    private String inLink;
    private String baseUrl;
    private int weight = 1;
    public pageRankUrl (String inLink, String baseUrl) {
        this.inLink = inLink;
        this.baseUrl = baseUrl;
    }
    public String getInLink() {
        return inLink;
    }
    public String getBaseUrl() {
        return baseUrl;
    }
    public int getWeight() {
        return weight;
    }
}
public class SolrjPopulator {
    public static void main(String[] args) throws IOException, SolrServerException {
        Map<String, String> urlDidMap = new HashMap<String, String>();
        ArrayList<pageRankUrl> graph = new ArrayList<pageRankUrl>();
        try {
            Configuration confForReader = NutchConfiguration.create();
            FileSystem fs = FileSystem.get(confForReader);
            Path segmentFile = new Path(args[0]);
            SequenceFile.Reader segmentReader = new SequenceFile.Reader(fs, segmentFile, confForReader);
            Text segmentKey = new Text();
            while (segmentReader.next(segmentKey)) {
                urlDidMap.put(segmentKey.toString(), "dummyDid");
            }

            Path linkFile = new Path(args[1]);
            SequenceFile.Reader linkReader = new SequenceFile.Reader(fs, linkFile, confForReader);
            Inlinks segmentInLink = new Inlinks();
            while (linkReader.next(segmentKey, segmentInLink)) {
                Iterator<Inlink> currLink = segmentInLink.iterator();
                while(currLink.hasNext()) {
                    String somehting = (currLink.next().toString().split(" ")[1]).trim();
                    graph.add(new pageRankUrl(somehting, segmentKey.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        for (Map.Entry<String, String> entry : urlDidMap.entrySet())
//        {
//                System.out.println(entry.getKey() + "/" + entry.getValue());
//        }

        String urlString = "http://localhost:8983/solr"; 
        SolrServer solr = new HttpSolrServer(urlString);

        File dir = new File(args[2]);
        FileFilter fileFilter = new WildcardFileFilter("*.html*");
        File[] files = dir.listFiles(fileFilter);
        String currDocID;
        for (int i = 0; i < files.length; i++)
        {
            currDocID = "html-" + i;
            for(String key : urlDidMap.keySet()) {
                String urlLast = key.substring(key.lastIndexOf("/") + 1);
                String fileBasename = files[i].toString().substring(files[i].toString().lastIndexOf("/") + 1);
                if (urlLast.equals(fileBasename) || fileBasename.equals(urlLast + ".html")
                        || fileBasename.equals(urlLast + ".html")) {
                    urlDidMap.put(key, currDocID); 
                }
            }
            ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");

            up.addFile(new File(files[i].toString()), "text/html");

            Document doc = Jsoup.parse(files[i], "UTF-8", "http://example.com/");
            Elements elements;
            try {
                elements = doc.body().getElementsByClass("descriptive_row_table");
            }
            catch(NullPointerException e) {
                System.out.println("ullaasdfasdf");
                continue;
            }
            up.setParam("literal.id", currDocID);
            up.setParam("id", currDocID);
            up.setParam("literal.title", doc.title());
            Double bboxValues[] = new Double[4];

            for(Element element: elements)
            {
                String text = element.text();
                String[] key_val_pair = text.split(":", 2);
                //               System.out.println(key_val_pair.length);
                String key = key_val_pair[0].toLowerCase().replace(" ", "_");
                key = key + "_s";
                String value = key_val_pair[1].replace(" > ", ",");
                if(key.endsWith("_coordinate"))
                {
                    value = value.replaceAll("^[ ()]+", "").replaceAll("[ ()]+$", "");
                    value = value.split(" ")[0];
                }
                //                System.out.println("key=" + key + "\tvalue=" + value);
                if (key.contains("northernmost_l")) {
                    bboxValues[2] = Double.parseDouble(value.trim().split(" ")[0]);
                }
                else if (key.contains("southernmost_l"))
                    bboxValues[3] = Double.parseDouble(value.trim().split(" ")[0]);
                else if (key.contains("westernmost_l"))
                    bboxValues[0] = Double.parseDouble(value.trim().split(" ")[0]);
                else if (key.contains("easternmost_l"))
                    bboxValues[1] = Double.parseDouble(value.trim().split(" ")[0]);
                else
                    up.setParam("literal." + key, value);
            }
            if (bboxValues[0] != null) {
                up.setParam("literal.bbox", "ENVELOPE(" + bboxValues[0] + ", "
                        + bboxValues[1] + ", " + bboxValues[2] + ", " + bboxValues[3] + ")");
            }
            up.setParam("fmap.content", "attr_content");
            up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
            System.out.println(currDocID + " " + files[i]);
            try {
            solr.request(up);
            } catch (Exception e) {
                System.out.println("Exception while trying to index " + currDocID + " " + files[i]);
                e.printStackTrace();
            }
        } 

        PrintWriter writer = new PrintWriter("temp.txt", "UTF-8");
        for (pageRankUrl temp : graph) {
            if (urlDidMap.containsKey(temp.getInLink()) && urlDidMap.containsKey(temp.getBaseUrl())
                    && !urlDidMap.get(temp.getInLink()).equals("dummyDid")
                    && !urlDidMap.get(temp.getBaseUrl()).equals("dummyDid")) {
                writer.println(urlDidMap.get(temp.getInLink()) + " " + urlDidMap.get(temp.getBaseUrl()) + " " + temp.getWeight());
                    }
        }
        writer.close();

        QueryResponse rsp = solr.query(new SolrQuery("*:*"));
        SolrDocumentList results = rsp.getResults();
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        for (int k=0; k < results.size(); k++) {
            System.out.println(results.get(k).getFieldNames());
        }
    }
}
