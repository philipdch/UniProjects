import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import sharedResources.MusicFile;
import sharedResources.Value;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class HandleClients extends Thread {
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private Publisher caller;


    public HandleClients(ObjectOutputStream out, ObjectInputStream in, Socket connection, Publisher caller) {
        this.output = out;
        this.input = in;
        this.connection = connection;
        this.caller = caller;
    }

    public void run() {
        //TODO Determine whether client is broker or Publisher's user
        try {
            Object received = input.readObject();
            if (received instanceof HashMap) { //check if received object is regular request or identification from user
                //TODO identify connected user
                //Terminate publisher
                output.writeObject("Are you sure you want to close this server?");
                received = input.readObject(); //read user's answer
                if (((String) received).equalsIgnoreCase("yes")) {
                    caller.closeServer(); //terminate server
                }
            } else{
                System.out.println("Receiving song name from Broker");
                String songName = (String) received;
                String artistName = (String) input.readObject();
                push(songName, artistName);
            }
        } catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }finally{
            try {
                System.out.println("Closing this connection.");
                connection.close();
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//finally
    }
    public void push(String songName,String artistName) throws IOException {
        List<MusicFile> chunks=new ArrayList<>();
        findSong(caller.getSearchDirectory(), songName,artistName,chunks);
        int chunks_length=chunks.size();
        output.writeObject(chunks_length);
        for(int i=0; i<chunks_length; i++  ){
            Value v=new Value(chunks.get(i));
            output.writeObject(v);
        }
        System.out.println(chunks_length);
    }

    public static void findSong(File d, String songName, String artistName, List<MusicFile> chunks) {
        String song=null ;
        String singer = null;
        //estw oti arxika mpainei se ena fakelo
        File[] fileList = d.listFiles(); //get list of files in directory
        for (int i = 0; i < fileList.length; i++) {
            File file = fileList[i];
            String artist = null;
            if (file.isDirectory()) {
                findSong(file, songName, artistName,chunks); //search additional folders
            }else if (file.isFile()) {
                String ext = FilenameUtils.getExtension(file.getPath()); //get file extension
                if (!ext.equals("mp3") || file.getName().contains("._")) {
                    continue;
                }
                try {
                    song =FilenameUtils.getBaseName(file.getPath());
                    if (song.equals(songName)){
                        Mp3File mp3File = new Mp3File(file.getPath());
                        if (mp3File.hasId3v1Tag()) {
                            ID3v1 id3v1tag = mp3File.getId3v1Tag();
                            singer = id3v1tag.getArtist();
                        }
                        if (mp3File.hasId3v2Tag()) {
                            ID3v2 id3v2tag = mp3File.getId3v2Tag();
                            singer = (id3v2tag.getArtist() != null)? id3v2tag.getArtist() : id3v2tag.getComposer();
                        }
                        if (singer != null && singer.equals(artistName)) {
                            System.out.println("I found the song");
                            convertMp3toArrayBytes(file,chunks);
                            break;
                        }
                    }
                } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                    System.err.println("please check your actions");
                }
            }
        }
    }

    public static  void convertMp3toArrayBytes(File file, List<MusicFile> chunks) throws IOException {
        byte[] bytesArray=new byte[(int)file.length()];
        FileInputStream fis=new FileInputStream(file);
        fis.read(bytesArray);
        fis.close();
        if(bytesArray!=null){
            System.out.println("convert the file to array of bytes" );
            System.out.println( "there are "+bytesArray.length + " bytes in file");
            creatChunks(file,bytesArray,chunks);
        }
    }
    public static void creatChunks(File file,byte[] bytesArray,List<MusicFile> chunks)  {
        int array_byte_length = 512000;
        int genre=0;
        String artist=null;
        String album=null;
        try {
            Mp3File mp3File = new Mp3File(file.getPath());
            String  song = FilenameUtils.getBaseName(file.getPath());
            if (mp3File.hasId3v1Tag()) {
                ID3v1 id3v1tag = mp3File.getId3v1Tag();
                artist=id3v1tag.getArtist();
                album=id3v1tag.getAlbum();
                genre=id3v1tag.getGenre();

            }
            if (mp3File.hasId3v2Tag()) {
                ID3v1 id3v2tag = mp3File.getId3v2Tag();
                artist=id3v2tag.getArtist();
                album=id3v2tag.getAlbum();
                genre=id3v2tag.getGenre();

            }
            System.out.println("TOTAL BYTES = "+ bytesArray.length);
            int counter = 1;
            int numberOfChunks = bytesArray.length /array_byte_length;
            int last_smaller_chunks=bytesArray.length-(numberOfChunks*array_byte_length);
            if(bytesArray.length<array_byte_length) {
                chunks.add(new MusicFile(song , artist, album, genre, bytesArray));
            }
            else {
                System.out.println("Sending " + numberOfChunks + " chunks");
                byte[] array_byte;
                for (int i = 0; i < bytesArray.length - array_byte_length; i += array_byte_length) {
                    array_byte = Arrays.copyOfRange(bytesArray, i, i + array_byte_length);
                    chunks.add(new MusicFile(song + "_" + counter++, artist, album, genre, array_byte));
                    System.out.println(i);
                }

                System.out.println(bytesArray.length - array_byte_length);
                array_byte = Arrays.copyOfRange(bytesArray, bytesArray.length -last_smaller_chunks, bytesArray.length);
                chunks.add(new MusicFile(song + "_" + counter, artist, album, genre, array_byte));
            }

        }
        catch (IOException | InvalidDataException | UnsupportedTagException e) {
            System.err.println("Error while reading mp3");
        }
    }
}
