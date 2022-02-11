import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.w3c.dom.Text;

public class Indexer {

    public static void indexDoc(IndexWriter indexWriter, MyDoc mydocument){
        try {
            // make a new, empty document
            Document document = new Document();

            StringField id = new StringField("id", mydocument.getId(), Field.Store.YES); //enables searching on a document's unique id
            StoredField title = new StoredField("title", mydocument.getTitle());
            //Some queries contain an author field. Authors' names should be stored whole, not tokenized
            //Could be used for multi-field querying
            String[] authors = mydocument.getAuthor().split("\n");
            for(String auth: authors){
                StringField author = new StringField("author", auth, Field.Store.NO);
                document.add(author);
            }
            //concatenate fields into one to be used in single field querying
            //Fields used in queries: article title(s), article description (abstract), authors, keywords and/or date (particularly year)
            String searchableText = mydocument.getTitle() + " " + mydocument.getAbstr() + " " + mydocument.getAuthor() + " " + mydocument.getKeywords() + " " + mydocument.getDate(); //field on which search is conducted
            TextField contents = new TextField("contents", searchableText, Field.Store.NO);
            document.add(id);
            document.add(title);
            document.add(contents);
            //no searching is being done on grepdDate field. No such field is created

            indexWriter.addDocument(document);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
