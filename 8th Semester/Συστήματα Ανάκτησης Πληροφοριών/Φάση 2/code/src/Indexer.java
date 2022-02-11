import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.w3c.dom.Text;

public class Indexer {

    public static void indexDoc(IndexWriter indexWriter, MyDoc mydocument, FieldType type){
        try {
            // make a new, empty document
            Document document = new Document();

            StringField id = new StringField("id", mydocument.getId(), Field.Store.YES); //enables searching on a document's unique id
            StoredField title = new StoredField("title", mydocument.getTitle());
            String[] authors = mydocument.getAuthor().split("\n");
            for(String auth: authors){
                StringField author = new StringField("author", auth, Field.Store.NO);
                document.add(author);
            }
            //concatenate fields into one to be used in single field querying
            //Fields used in queries: article title(s), article description (abstract), authors, keywords and/or date (particularly year)


            String searchableText = mydocument.getTitle() + " " + mydocument.getAbstr() + " " + mydocument.getAuthor() + " " + mydocument.getKeywords() + " " + mydocument.getDate(); //field on which search is conducted

            Field field = new Field("content", searchableText, type);
            document.add(id);
            document.add(title);
            document.add(field);


            indexWriter.addDocument(document);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
