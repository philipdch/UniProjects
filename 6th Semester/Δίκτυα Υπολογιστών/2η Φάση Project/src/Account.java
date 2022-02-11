public class Account {

    private String username;
    private String password;
    private  int countDownloads;
    private int countFailures;

    public  Account(){}
    public Account(String username,String password){
        this.username=username;
        this.password=password;
    }

    public Account(String username,String password, int countDownloads, int countFailures){
        this.username=username;
        this.password=password;
        this.countDownloads = countDownloads;
        this.countFailures = countFailures;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setCountDownloads(int count_download) {
        this.countDownloads = count_download;
    }

    public void setCountFailures(int count_failures) {
        this.countFailures = count_failures;
    }


    public int getCountDownloads() {
        return countDownloads;
    }

    public int getCountFailures() {
        return countFailures;
    }

    public void incrementDownloads(){
        countDownloads++;
    }

    public void incrementFailures(){
        countFailures++;
    }
}
