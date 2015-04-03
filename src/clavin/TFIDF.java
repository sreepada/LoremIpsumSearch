import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class TFIDF {

	Map<String, DocumentInfo> documentDetails = new TreeMap<String, DocumentInfo>();
	Map<String, Integer> IDF = new HashMap<String, Integer>();
	int numberOfDocs=0;
	
	/**
	 * @param args
	 * @throws TikaException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
public static void main(String[] args) throws IOException, SAXException, TikaException {
		
		
		if(args.length < 1)
		{
			System.out.println("Insufficient number of arguments.\nTFIDF usage:\n" +
					"java TFIDF <directory containing files>");
		}
		else
		{
			TFIDF temp = new TFIDF();
			
			String dirName = args[0];
			if(!(dirName.endsWith("\\") || dirName.endsWith("/")))
				dirName+="/";
			
			File dir = new File(dirName);
			
			File[] files = dir.listFiles();
			
			System.out.println("Reading files under "+dir.getAbsolutePath());
			
			for(File file: files)
			{
				if(file.isFile())
					temp.parseContent(file);
			}
			temp.calcTFIDF();
			
			System.out.println("***Preprocessing complete***\n");
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter query string to get 10 docs with TF.IDF value.\nTo exit type Quit");
			
			String line = input.readLine();
			line= line.trim();
			while(!line.equalsIgnoreCase("quit"))
			{
				List<Entry<String, Double>> result = temp.getTFIDFforString(line);
				for(int i=0;i<result.size();i++)
				{
					Entry<String, Double> entry = result.get(i);
					String doc = entry.getKey();
					System.out.println(doc+" --> "+entry.getValue());
				}
				line = input.readLine();
			}
			
			System.out.println("Exiting -----> bye.");
		}
		
	}
	
	
	
	void addDocsToTFIDFCalc(List<String> files) throws IOException, SAXException, TikaException
	{
		for(String file:files)
			this.parseContent(new File(file));
		
	}
	
	void parseContent(File file) throws IOException, SAXException, TikaException
	{
		DocumentInfo doc = new DocumentInfo();
		String input = LocationExtractor.tikaReadFileContent(file);
		String lines[] = input.split("\n");
		for(String line: lines)
		{
			line = line.trim();
			String data[] = line.split(" ");
			for(String word: data)
			{
				doc.addWord(word);
			}
		}
		
		this.documentDetails.put(file.getAbsolutePath(),doc);
		int sum=0;
		for(String word:doc.wordCount.keySet())
		{
			sum+=doc.wordCount.get(word);
			if(this.IDF.containsKey(word))
				this.IDF.put(word,this.IDF.get(word)+1);
			else
				this.IDF.put(word,1);
		}
		doc.setTotalWordCount(sum);
		this.numberOfDocs+=1;
	}
	
	Map<String, DocumentInfo> calcTFIDF()
	{
		for(String key: this.documentDetails.keySet())
		{
			
			DocumentInfo doc = this.documentDetails.get(key);
			doc.computeTFIDF(this.numberOfDocs, this.IDF);
		}
		
		return this.documentDetails;
	}
	
	List<Entry<String, Double>> getTFIDFforString(String query)
	{
		String[] data = query.split(" |\n");
		TreeMap<String, Double> ranking = new TreeMap<String, Double>();
		String minname ="";
		double minval=-99990.0;
		int counter=0;
		for(String docName: documentDetails.keySet())
		{	
			
			DocumentInfo doc = documentDetails.get(docName);
			double score=0.0;
			for(String word: data)
				score+=doc.getTFIDFValue(word);
			
			if(score > minval)
			{
				if(counter>10)
					ranking.remove(minname);
				
				ranking.put(docName, score);
				minname = getMinName(ranking);
				minval = ranking.get(minname);
				counter++;
			}
			
		}
		
		List<Entry<String, Double>> entries = new ArrayList<Entry<String, Double>>(ranking.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Double>>() {
		    public int compare(Entry<String, Double> e1, Entry<String, Double> e2) {
		        if(e1.getValue()<(e2.getValue()))
		        	return 1;
		        else return -1;
		    }

		});
		
		
		return entries;
		
	}
	
	String getMinName(TreeMap<String, Double> ranking)
	{
		String minname=ranking.firstKey();
		double minval = ranking.get(minname);
		
		for(String doc: ranking.keySet())
		{
			double score = ranking.get(doc);
			if(score <minval)
				minname = doc;
		}
		
		return minname;
	}

	
}
