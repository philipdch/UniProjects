import utils.IO;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Class to process formatted queries from queries.txt
    Concatenates query body, authors and keywords to create a
    complete query using all those fields
 */
public class RawQueryParser {

    public static List<MyDoc> parse(String file){
        //Map to store the query and its corresponding id

        try {
            //read whole queries.txt file as string
            String txt_file = IO.ReadEntireFileIntoAString(file);


            String[] queries = txt_file.split("((?<=((?m)^\\.[A-Z]))|(?=((?m)^\\.[A-Z])))");
            List<MyDoc> parsedDocs = new ArrayList<>();
            MyDoc newDoc;
            String query = "";
            String id = "";
            String author = "";
            String queryBody = "";
            String keywords = "";
            int i = 0;
            while(i<queries.length) {
                String token = queries[i].trim();
                switch (token) {
                    case ".I":
                        id = String.format("%02d", Integer.parseInt(queries[++i].trim()));
                        break;
                    case ".A":
                        author = queries[++i].trim();
                        break;
                    case ".W":
                        queryBody = queries[++i].trim();
                        break;
                    case ".N":
                        Pattern regex = Pattern.compile("[0-9]*\\.(.*)");
                        String line = queries[++i].trim();
                        Matcher matcher = regex.matcher(line);
                        while(matcher.find()){
                            keywords = matcher.group(1);
                        }
                        query = queryBody + " " + author + " " + keywords;


                        newDoc = new MyDoc(id, "", author,"", query, "", "", keywords, "");
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
                        author = "";
                        queryBody = "";
                    default:
                        i++;
                }
            }

            return parsedDocs;
        } catch (FileNotFoundException err) {
            err.printStackTrace();
            return null;
        }
    }
}
