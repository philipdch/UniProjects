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
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.classification.utils.DocToDoubleVectorUtils;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.*;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.MatchAllDocsQuery;
import java.io.File;
import java.io.IOException;

public class DocRetrieval {

    private static final String TREC_EVAL_PATH = "C:\\Users\\Philip\\Downloads\\trec_eval\\"; //path to the trec_eval folder
    private static final String CACM_PATH = "C:\\Users\\Philip\\Downloads\\cacm\\"; //path to collection folder

    public static void main(String[] args) {
        Directory dir = null;
        List<MyDoc> parsedDocs = DocParser.parse(CACM_PATH + "cacm.all");
        List<MyDoc> parsedQueries=RawQueryParser.parse(CACM_PATH + "query.text");

        String indexDirectory = ".\\index\\"; //path to index 
        try {
            dir = FSDirectory.open(Paths.get(indexDirectory)); //get index from specified File System Directory

            Analyzer analyzer = new EnglishAnalyzer();
            Similarity similarity = new ClassicSimilarity();
            // configure IndexWriter
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity);

            // Create a new index in the directory, deleting any previous index that may have been created
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // Create the IndexWriter with the configuration as above
            IndexWriter indexWriter = new IndexWriter(dir, iwc);

            //Index documents
            FieldType type = new FieldType();
            type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
            type.setTokenized(true);
            type.setStored(true);
            type.setStoreTermVectors(true);
            for(MyDoc doc: parsedDocs){
                Indexer.indexDoc(indexWriter, doc,type);
            }
            for(MyDoc doc: parsedQueries){
               Indexer.indexDoc(indexWriter, doc,type);
            }
            indexWriter.close();

            IndexReader indexReader = DirectoryReader.open(dir);

            testSparseFreqDoubleArrayConversion(indexReader,parsedDocs.size(),parsedQueries.size());

        }catch(IOException | ParseException e){

            System.err.println("Directory not found!");
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private static void   testSparseFreqDoubleArrayConversion(IndexReader reader,int size ,int sizeq) throws Exception {
        Terms fieldTerms = MultiFields.getTerms(reader, "content");   //the number of terms in the vocabulary
        System.out.println("Terms:" + fieldTerms.size());

        double[][] txq=new  double[(int) fieldTerms.size()][sizeq];
        double[][] txd=new  double[(int) fieldTerms.size()][size];
        //iterate through the terms of the vocabulary
        System.out.println();
        System.out.println();
        int j=0;
        if (fieldTerms != null && fieldTerms.size() != -1) {
            IndexSearcher indexSearcher = new IndexSearcher(reader);
            for (ScoreDoc scoreDoc : indexSearcher.search(new MatchAllDocsQuery(), Integer.MAX_VALUE).scoreDocs) {   //retrieves all documents

                Terms docTerms = reader.getTermVector(scoreDoc.doc, "content");

                Double[] vector = DocToDoubleVectorUtils.toSparseLocalFreqDoubleArray(docTerms, fieldTerms); //creates document's vector
                for(int i = 0; i<=vector.length-1; i++ ) {
                    if(j<size) {
                        txd[i][j] = vector[i];
                    }
                    else{
                        txq[i][j-size] = vector[i];
                    }
                }
                j++;"S:\Downloads\7\model.txt"
            }
        }
        writeTX(txd,"txd.text");
        writeTX(txq,"txq.text");
    }


    private static void writeTX(double[][] txd,String file){

        BufferedWriter bw = null;
        try{

            bw = new BufferedWriter((new FileWriter(TREC_EVAL_PATH +file )));
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            for (int row = 0; row < txd.length; row++) {
                if(row!=0){
                    bw.write("\n");
                }
                for (int col = 0; col < txd[row].length; col++) {
                    if(col!=0) {
                        bw.write(",");
                    }
                    bw.write(txd[row][col]+"");
                    bw.flush();
                }
            }
        }catch (IOException e){
            System.err.println("Error reading file");
        }
        try{

            bw.close();
        }catch (IOException e){
            System.err.println("Error closing reader or writer");
        }
    }
}












