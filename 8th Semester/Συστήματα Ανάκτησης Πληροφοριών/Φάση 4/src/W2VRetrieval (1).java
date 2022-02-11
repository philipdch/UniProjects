import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map;
import java.util.Map.Entry;
import java.nio.file.Paths;
import java.io.FileWriter;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;

import org.nd4j.linalg.ops.transforms.Transforms;

/**
 * Ranking tests evaluating different {@link Similarity} implementations against {@link WordEmbeddingsSimilarity}
 */
public class W2VRetrieval {


    private static final String TREC_EVAL_PATH = "C:\\Users\\Philip\\Downloads\\trec_eval\\"; //path to the trec_eval folder
    private static final String CACM_PATH = "C:\\Users\\Philip\\Downloads\\cacm\\";
    private static final String INDEX_PATH = ".\\index\\"; //path to index
    private static final int TOP_RESULTS = 10;


    public void testRankingWithTFIDFAveragedWordEmbeddings() throws Exception {


        int choice = 1;
        String collectionPath = CACM_PATH + "cacm.all";
        Path path = Paths.get(INDEX_PATH);
        Path path2 = Paths.get(".\\index\\2");
        Directory directory = FSDirectory.open(path);
        Directory directory2 = FSDirectory.open(path2);


        List<MyDoc> parsedDocs = DocParser.parse(collectionPath);
        if (parsedDocs == null || parsedDocs.isEmpty()) {
            System.out.println("Error parsing documents");
            return;
        }

        try {

            IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());

            IndexWriter indexWriter = new IndexWriter(directory, config);
            FieldType type = new FieldType(TextField.TYPE_STORED);
            type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            type.setTokenized(true);
            type.setStored(true);
            type.setStoreTermVectors(true);
            type.setStoreTermVectorOffsets(true);
            type.setStoreTermVectorPositions(true);

            for (MyDoc doc : parsedDocs) {
                Indexer.indexDoc(indexWriter, doc, type);
            }


            String fieldName = "content";
            IndexReader reader = DirectoryReader.open(indexWriter);

            FieldValuesSentenceIterator fieldValuesSentenceIterator = new FieldValuesSentenceIterator(reader, fieldName);

            SentenceIterator sentenceIterator = new BasicLineIterator(collectionPath);
            Word2Vec vec;
            if (choice == 1) {
                vec = new Word2Vec.Builder()
                        .layerSize(100)
                        .windowSize(7)
                        .tokenizerFactory(new DefaultTokenizerFactory())
                        .elementsLearningAlgorithm(new SkipGram<>())
                        .iterate(fieldValuesSentenceIterator)
                        .build();
                vec.fit();
            } else {
                File gModel = new File("S:\\Downloads\\7\\model.txt");
                vec = WordVectorSerializer.readWord2VecModel(gModel);
            }

            PrintWriter writer = new PrintWriter(TREC_EVAL_PATH + "\\results.text", StandardCharsets.UTF_8);

            try {
                //SPELLCHECKER

                SpellChecker spellChecker = new SpellChecker(directory2);
                LuceneDictionary spelldict = new LuceneDictionary(reader, "content");
                IndexWriterConfig config2 = new IndexWriterConfig(new WhitespaceAnalyzer());
                spellChecker.indexDictionary(spelldict, config2, false);


                String queryString;
                String id;
                Map<String, String> queriesU = RawQueryParser.parse(CACM_PATH + "query.text");
                Map<String, String> queries = new TreeMap<String, String>(queriesU);
                IndexSearcher searcher = new IndexSearcher(reader);
                searcher.setSimilarity(new WordEmbeddingsSimilarity(vec, fieldName, WordEmbeddingsSimilarity.Smoothing.TF_IDF));


                Terms fieldTerms = MultiFields.getTerms(reader, fieldName);

                INDArray denseAverageTFIDFQueryVector = Nd4j.zeros(vec.getLayerSize());
                Map<String, Double> tfIdfs = new HashMap<>();


                Iterator<Entry<String, String>> it = queries.entrySet().iterator();
                String qid;
                while (it.hasNext()) {


                    Map.Entry<String, String> set = (Map.Entry<String, String>) it.next();
                    queryString = set.getValue();
                    qid = set.getKey();


                    queryString = queryString.toLowerCase();

                    String[] split = queryString.toLowerCase().split(" ");
                    //queryString="";
                    String space = " ";


                    String[] suggestions = {};
                    for (String queryTerm : split) {


                        if (!queryTerm.equals("")) {
                /*
                if(spellChecker.exist(queryTerm)&&queryTerm.length()>2){
                  suggestions=  spellChecker.suggestSimilar(queryTerm, 1);
                  if(suggestions!=null&&suggestions.length>0){
                    queryTerm=suggestions[0];
                  }


                }
                queryString=queryString+space+queryTerm;
                */
                /*
                TermsEnum iterator = fieldTerms.iterator();
                BytesRef term;
                while ((term = iterator.next()) != null) {
                  TermsEnum.SeekStatus seekStatus = iterator.seekCeil(term);
                  if (seekStatus.equals(TermsEnum.SeekStatus.END)) {
                    iterator = fieldTerms.iterator();
                  }
                  if (seekStatus.equals(TermsEnum.SeekStatus.FOUND)) {
                    String string = term.utf8ToString();
                    if (string.equals(queryTerm)) {
                      double tf = iterator.totalTermFreq();
                      double docFreq = iterator.docFreq();
                      double tfIdf = VectorizeUtils.tfIdf(reader.numDocs(), tf, docFreq);
                      tfIdfs.put(string, tfIdf);
                    }
                  }
                }

                Double n = tfIdfs.get(queryTerm);
                INDArray vector = vec.getLookupTable().vector(queryTerm);
                if (vector != null) {
                  INDArray f=vector.div(n);
                  denseAverageTFIDFQueryVector.addi(f);

                }

                */
                        }
                    }

                    INDArray denseAverageQueryVector = vec.getWordVectorsMean(Arrays.asList(split));

                    QueryParser parser = new QueryParser(fieldName, new WhitespaceAnalyzer());
                    System.out.println(queryString);
                    Query query = parser.parse(queryString);

                    TopDocs hits = searcher.search(query, TOP_RESULTS);
                    Map<Double, Integer> scores = new HashMap<>();
                    for (int i = 0; i < hits.scoreDocs.length; i++) {
                        ScoreDoc scoreDoc = hits.scoreDocs[i];

                        writer.println(qid + " 0 " + scoreDoc.doc + " 0 " + scoreDoc.score + " 1");
                        System.out.println(qid + " 0 " + scoreDoc.doc + " 0 " + scoreDoc.score + " 1");
                        Explanation ex = searcher.explain(query, scoreDoc.doc);
                        System.out.println(ex);
            /*
            INDArray denseAverageDocumentVector = VectorizeUtils.toDenseAverageVector(docTerms, vec);
            INDArray denseAverageTFIDFDocumentVector = VectorizeUtils.toDenseAverageTFIDFVector(docTerms, reader.numDocs(), vec);

            //double sim=Transforms.cosineSim(denseAverageQueryVector, denseAverageDocumentVector)
            double sim=Transforms.cosineSim(denseAverageTFIDFQueryVector, denseAverageTFIDFDocumentVector);

            scores.put(sim,scoreDoc.doc);
          */
                    }




/*
          TreeMap<Double,Integer>  sort = new TreeMap<Double,Integer> (scores);
          Map<Double, Integer> sorted = sort.descendingMap();
          int rep=0;
          for (Map.Entry mapElement : sorted.entrySet()) {
            double value=(double)mapElement.getKey();

            // Finding the value
            int  key = (int)mapElement.getValue();
            writer.println(qid + " 0 " +key +" 0 " + value + " 1");
            System.out.println("q: "+qid+"doc: "+key+" sim: "+value);
            if(rep>=10){
              break;

            }
            rep++;
          }
*/


                }

            } finally {
                writer.close();
                indexWriter.deleteAll();
                indexWriter.commit();
                indexWriter.close();
                reader.close();
            }

        } finally {

            directory.close();
            directory2.close();
        }
    }

    private static void formatQrels(String filepath) {
        File file = new File(filepath);
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(file));
            bw = new BufferedWriter((new FileWriter(TREC_EVAL_PATH + "\\qrels.text")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String line = br.readLine();
            while (line != null) {
                String[] tokens = line.split("\\s+");
                bw.write(tokens[0] + " " + tokens[2] + " " + tokens[1] + " " + "1" + "\r\n"); //write fields: qid iteration docno relevance
                bw.flush();
                line = br.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error reading file");
        }
        try {
            br.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("Error closing reader or writer");
        }
    }


    public static void main(String[] args) {
        W2VRetrieval t = new W2VRetrieval();
        try {
            t.testRankingWithTFIDFAveragedWordEmbeddings();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

}