/*Class representing an article in the document collection */
public class MyDoc implements Comparable<MyDoc>{
    private String id;
    private String title;
    private String author;
    private String date;
    private String abstr;
    private String cite;
    private String grepDate;
    private String keywords;
    private String c;

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAbstr() {
        return abstr;
    }

    public void setAbstr(String abstr) {
        this.abstr = abstr;
    }

    public String getCite() {
        return cite;
    }

    public void setCite(String cite) {
        this.cite = cite;
    }

    public String getN() {
        return grepDate;
    }

    public void setN(String grepDate) {
        this.grepDate = grepDate;
    }

    public String getKeywords(){
        return keywords;
    }

    public void setkeywords(String keywords){
        this.keywords = keywords;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public MyDoc(String id, String title, String author, String date, String abstr, String cite, String grepDate, String keywords, String c) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.date = date;
        this.abstr = abstr;
        this.cite = cite;
        this.grepDate = grepDate;
        this.keywords = keywords;
        this.c = c;
    }

    @Override
    public int compareTo(MyDoc o) {
        if(Integer.parseInt(this.id) > Integer.parseInt(o.id)){
            return 1;
        }else if(Integer.parseInt(this.id) < Integer.parseInt(o.id)){
            return -1;
        }
        return 0;
    }

    public String toString(){
        return "ID: "+this.id+"\nTitle: "+this.title+"\nAuthor(s): "+this.author;
    }
}
