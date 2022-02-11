import java.io.Serializable;

public class ChunkFile implements Serializable {

    private byte[] fileExtract;
    private String name;

    public ChunkFile(){}

    public ChunkFile(byte[] fileExtract, String name) {
        this.fileExtract = new byte[fileExtract.length];
        this.name = name;
        System.arraycopy(fileExtract, 0, this.fileExtract, 0, fileExtract.length);
    }

    public byte[] getFileExtract() {
        return fileExtract;
    }

    public void setFileExtract(byte[] fileExtract) {
        this.fileExtract = fileExtract;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
