import utils.IO;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/* class to parse formatted documents from the
    collection given and store them as individual
    documents in the list to be returned
 */
public class DocParser {

    //fields of the document
    private static final String ID = ".I";
    private static final String TITLE = ".T";
    private static final String AUTHOR = ".A";
    private static final String ABSTR = ".W";
    private static final String KEYWORDS = ".K";
    private static final String DATE = ".B";
    private static final String SAVEDATE = ".N";
    private static final String CITATIONS = ".X";
    private static final String C = ".C";

    public static List<MyDoc> parse(String file){
        try {
            //read whole cacm.all file as string
            String txt_file = IO.ReadEntireFileIntoAString(file);
            /*split string on each field.
              A field is denoted with a period (.) followed by one of the following capital letters: A, B, C, I, K, N, T, W, or X.
              Here we assume that a field can be denoted using ANY capital letter (A-Z).
              The regex uses Lookahead and Lookbehind to split the string on a single character (typically \s, \t or \n)
              before and after a field, thus keeping the name of the field and its value. The field MUST appear at the start of a line
              Explanation: (?m)^ - Assume position at start of each line
                           \\.[A-Z] - Find strings that contain .A or .B or .C etc
                           ?<= and ?= - Positive LookBehind and Lookahead
            */
            String[] docs = txt_file.split("((?<=((?m)^\\.[A-Z]))|(?=((?m)^\\.[A-Z])))");
            //Create list to store parsed documents
            List<MyDoc> parsedDocs = new ArrayList<>();
            //Initialize new document and fields to be stored
            //Note: .X is the last field in every document.
            MyDoc newDoc;
            String id = "";
            String title = "";
            String author = "";
            String date = "";
            String cite = "";
            String abstr = "";
            String grepDate = "";
            String keywords = "";
            String c = "";
            int i = 0;
            while(i<docs.length) {
                String token = docs[i].trim();
                switch (token) {
                    case ".I":
                        id = docs[++i].trim();
                        break;
                    case ".T":
                        title = docs[++i].trim();
                        break;
                    case ".A":
                        author = docs[++i].trim();
                        break;
                    case ".B":
                        date = docs[++i].trim();
                        break;
                    case ".K":
                        keywords = docs[++i].trim();
                        break;
                    case ".W":
                        abstr = docs[++i].trim();
                        break;
                    case".C":
                        c = docs[++i].trim();
                        break;
                    case ".N":
                        grepDate = docs[++i].trim();
                        break;
                    case ".X":
                        cite = docs[++i].trim();
                        newDoc = new MyDoc(id, title, author, date, abstr, cite, grepDate, keywords, c);
                        boolean exists = false;
                        for (MyDoc doc : parsedDocs) {
                            if (doc.compareTo(newDoc) == 0) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            parsedDocs.add(newDoc);
                        }
                        id = "";
                        title = "";
                        author = "";
                        date = "";
                        cite = "";
                        abstr = "";
                        grepDate = "";
                        keywords = "";
                        c = "";
                    default:
                        i++;
                }
            }
            System.out.println(parsedDocs.size()+" documents parsed");
            return parsedDocs;
        } catch (FileNotFoundException err) {
            err.printStackTrace();
            return null;
        }
    }
}
