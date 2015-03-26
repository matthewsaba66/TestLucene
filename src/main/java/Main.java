import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;


public class Main {
	public static void main(String[] args) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {

		/* create a standard analyzer */
		StandardAnalyzer analyzer = new StandardAnalyzer( CharArraySet.EMPTY_SET);

		/* create the index in the pathToFolder or in RAM (choose one) */
		//File file = new File("pathToFolder");
		//Path path = Paths.get("/home/matteo/test/");
		//Directory index = FSDirectory.open(path);
		Directory index =new RAMDirectory();


		/* set an index config */
		IndexWriterConfig config = new IndexWriterConfig( analyzer);
		config.setOpenMode(OpenMode.CREATE);

		/* create the writer */
		IndexWriter writer = new IndexWriter(index, config);

		/* pick a document */
		String url = "http://www.mirror.co.uk/sport/football/transfer-news/";
		String textdoc = "The captain of Liverpool football club Steven "
				+ "Gerrard announce he will leave the club elfkdjrgfojeotkejèkjog,eà5kyg";


		/* create the document adding the fields */
		Document doc = new Document();
		doc.add(new StringField("url", url, Field.Store.YES));
		doc.add(new TextField("body", textdoc, Field.Store.YES));


		/* write the document */
		writer.addDocument(doc);

		/* close the writer */
		writer.close();
		System.out.println("test");


		/*fase query*/

		/* set the maximum number of results */
		int maxHits = 10;

		/* open a directory reader and create searcher and topdocs */
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector =
				TopScoreDocCollector.create(maxHits);

		/* create the query parser */
		QueryParser qp = new QueryParser("body", analyzer);

		/* query string */
		String querystring = "The Mirror (Liverpool FC AND -Anfield)";
		Query q = qp.parse(querystring);

		/* search into the index */
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

//		/* create the query parser */
//		QueryParser qp = new QueryParser("body", analyzer);
//
//		/* create a query from 3 words */
//		String term1 = "the Mirror";
//		String term2 = "Liverpool";
//		String term3 = "Anfield";
//		Query termQuery1 = qp.parse(term1);
//		Query termQuery2 = qp.parse(term2);
//		Query termQuery3 = qp.parse(term3);
//
//		/* create inner boolean query */
//		BooleanQuery in = new BooleanQuery();
//		in.add(termQuery2, Occur.MUST);
//		in.add(termQuery3, Occur.MUST_NOT);
//
//		/* create outer boolean query */
//		BooleanQuery out = new BooleanQuery();
//		out.add(in, Occur.SHOULD);
//		out.add(termQuery1, Occur.SHOULD);
//		
		/* print results */
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println("url: " + d.get("url") + " Body: " + d.get("body"));
		}
	}


}