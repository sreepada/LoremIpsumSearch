package clavin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		
		TFIDF temp = new TFIDF();
		temp.parseContent(new File("src/clavin/temp1"));
		temp.parseContent(new File("src/clavin/temp2"));
		temp.calcTFIDF();
		
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
	
}
