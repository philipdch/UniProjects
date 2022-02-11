import utils.IO;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Class to process formatted queries from queries.txt
    Concatenates query body, authors and keywords to create a
    complete query using all those fields
 */
public class RawQueryParser {

    public static Map<String, String> parse(String file){
        //Map to store the query and its corresponding id
        Map<String, String> processedQueries = new HashMap<>();
        try {
            //read whole queries.txt file as string
            String txt_file = IO.ReadEntireFileIntoAString(file);

            //Split txt before and after each field
            String[] queries = txt_file.split("((?<=((?m)^\\.[A-Z]))|(?=((?m)^\\.[A-Z])))");

            //Note: .N is the last field in every query.
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
                        if(!processedQueries.containsKey(id)){
                            processedQueries.put(id, query.replaceAll("[^[a-zA-Z0-9]]"," "));
                        }
                        id = "";
                        author = "";
                        queryBody = "";
                    default:
                        i++;
                }
            }

            return processedQueries;
        } catch (FileNotFoundException err) {
            err.printStackTrace();
            return null;
        }
    }
}
