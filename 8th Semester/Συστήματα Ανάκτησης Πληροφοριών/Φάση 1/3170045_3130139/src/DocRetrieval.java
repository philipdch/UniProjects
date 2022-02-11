import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import utils.IO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.*;

public class DocRetrieval {

    private static final int TOP_RESULTS = 30; //number of results to be recorded
    private static final String TREC_EVAL_PATH = "."; //path to the trec_eval folder
    private static final String QUERIES_PATH = ".";

    public static void main(String[] args) {
        /*First argument: location of the file containing the documents
          Second argument: location from which to store and read the index
          Third argument: Location of the queries.txt file
         */
        Directory dir = null;
        if (args.length < 3) {
            System.out.println("Insufficient number of arguments given. Expected 3\n(Collection directory location, Index location, Queries file location");
            return;
        }

        formatQrels(QUERIES_PATH);
        //read the documents from the file and store them in a list
        List<MyDoc> parsedDocs = DocParser.parse(args[0]);
        if(parsedDocs == null){
            System.err.println("Document collection could not be loaded");
            return;
        }
        //read the queries from the txt and store them in a map
        Map<String, String> parsedQueries = RawQueryParser.parse(args[2]);
        if(parsedQueries == null){
            System.err.println("Error processing queries");
            return;
        }

        String indexDirectory = args[1];
        try {
            dir = FSDirectory.open(Paths.get(indexDirectory)); //get index from specified File System Directory
            // define which analyzer to use for the normalization of documents
            Analyzer analyzer = new EnglishAnalyzer();
            // define retrieval model
            Similarity similarity = new ClassicSimilarity();
            // configure IndexWriter
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity);

            // Create a new index in the directory, deleting any previous index that may have been created
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // Create the IndexWriter with the configuration as above
            IndexWriter indexWriter = new IndexWriter(dir, iwc);

            //Index documents
            for(MyDoc doc: parsedDocs){
                Indexer.indexDoc(indexWriter, doc);
            }
            indexWriter.close();

            IndexReader indexReader = DirectoryReader.open(dir);
            for (int i=0; i<indexReader.maxDoc(); i++) {
                Document doc = indexReader.document(i);
            }

            IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir)); //Creates a searcher searching the provided index.
            indexSearcher.setSimilarity(similarity);
            // create a query parser on the field "contents"
            String field = "contents";
            QueryParser parser = new QueryParser(field, analyzer);

            PrintWriter writer = new PrintWriter(TREC_EVAL_PATH + "\\results.text", StandardCharsets.UTF_8);
            SortedSet<String> qids = new TreeSet<>(parsedQueries.keySet());
            for(String qid: qids){
                // parse the query according to QueryParser
                Query query = parser.parse(QueryParser.escape(parsedQueries.get(qid)));
                System.out.println("\nSearching for: " + query.toString(field));

                // search the index using the indexSearcher
                TopDocs results = indexSearcher.search(query, TOP_RESULTS);
                ScoreDoc[] hits = results.scoreDocs;
                long numTotalHits = results.totalHits;
                System.out.println("***** " + numTotalHits + " matching documents *****");
                //display results
                int counter = 1;
                for (ScoreDoc hit : hits) {
                    Document hitDoc = indexSearcher.doc(hit.doc);
                    writer.println(qid + " 0 " + hitDoc.get("id") + " 0 " + hit.score + " 1"); //write search results to results.txt
                    System.out.println(counter++ + ")\tScore " + hit.score + "\tid=" + hitDoc.get("id") + "\ttitle:" + hitDoc.get("title"));
                }
            }
            writer.close();
        }catch(IOException | ParseException e){
            System.err.println("Directory not found!");
            e.printStackTrace();
        }
    }

    private static void formatQrels(String filepath){
        File file = new File(filepath);
        BufferedReader br = null;
        BufferedWriter bw = null;
        try{
            br = new BufferedReader(new FileReader(file));
            bw = new BufferedWriter((new FileWriter(TREC_EVAL_PATH + "\\qrels.text")));
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            String line = br.readLine();
            while(line != null){
                String[] tokens = line.split("\\s+");
                bw.write(tokens[0] + " " + tokens[2] + " " + tokens[1] + " " + "1" + "\r\n"); //write fields: qid iteration docno relevance
                bw.flush();
                line = br.readLine();
            }
        }catch (IOException e){
            System.err.println("Error reading file");
        }
        try{
            br.close();
            bw.close();
        }catch (IOException e){
            System.err.println("Error closing reader or writer");
        }
    }
}