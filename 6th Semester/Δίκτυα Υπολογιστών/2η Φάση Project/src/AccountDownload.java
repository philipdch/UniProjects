public class AccountDownload {
    private  Account account;
    private  int count_download;
    private int count_failures;

    public AccountDownload(){}
    public AccountDownload(Account account, int count_download, int count_failures){
        this.account=account;
        this.count_download=count_download;
        this.count_failures=count_failures;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setCount_download(int count_download) {
        this.count_download = count_download;
    }

    public void setCount_failures(int count_failures) {
        this.count_failures = count_failures;
    }

    public Account getAccount() {
        return account;
    }

    public int getCount_download() {
        return count_download;
    }

    public int getCount_failures() {
        return count_failures;
    }
}
