package com.example.ergasia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import sharedResources.ArtistName;
import sharedResources.BrokerInfo;



public class Search_Activity extends AppCompatActivity {
    String name;
    String song;
    ListView songList;
    TextView unavailableArt;
    private Socket socketClient;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    final static String SERVER_IP = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("tag", "On start");

        Button searchButton = (Button) findViewById(R.id.searchButton);
        unavailableArt = (TextView) findViewById(R.id.unavailableArt);
        songList = (ListView) findViewById(R.id.List_view);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socketClient != null) {
                    Log.e("onClick", "Closing this connection : " + socketClient);
                    disconnect();
                }
                EditText artist = (EditText) findViewById(R.id.searchArtist);
                name = artist.getText().toString();
                Log.e("EditText", name);
                //if we have already searched an unavailable artist set the TextView blank again
                if(unavailableArt.getText().toString().equals("Unavailable Artist. Try Again :)")) unavailableArt.setText("");
                SearchAsyncTask task = new SearchAsyncTask();
                task.execute(name);
            }
        });

        songList.setClickable(true);

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                song = songList.getItemAtPosition(position).toString();

                Toast.makeText(Search_Activity.this, song, Toast.LENGTH_SHORT).show();

                SocketHandler.setSocket(socketClient);
                SocketHandler.setInput(input);
                SocketHandler.setOutput(output);

                Intent nextIntent= new Intent(Search_Activity.this,Fetch_Activity.class);
                nextIntent.putExtra("songName",song);
                startActivityForResult(nextIntent,0);


            }
        });

    }

    public void disconnect() {
        try {
            socketClient.close();
            output.close();
            input.close();
            Log.e("onClick", "Connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class SearchAsyncTask extends AsyncTask<String, String, List<String>> {
        List<String> songsToChoose = new ArrayList<String>();
        List<BrokerInfo> brokers = new ArrayList<BrokerInfo>();
        ProgressDialog progressDialog;


        @Override
        protected List<String> doInBackground(String... name) {
            Log.e("SearchAsyncTask", "In doInBackground");
            try {

                try {
                    int brokerPort = 1057;
                    connect(brokerPort, SERVER_IP);

                    while (true) {
                        boolean wrongBroker = requestArtist(name[0]);

                        /*
                        we have NOT reached the correct Broker
                        */
                        if (wrongBroker) {//brokerAnswer.equals("Incorrect broker!")){
                            Log.d("BrokerAnswer", (String) input.readObject());//artist
                            brokers = (List<BrokerInfo>) input.readObject();//BrokerInfo list
                            String newIp = "";
                            int newPort = 0;
                            for (BrokerInfo b : brokers) {
                                for (ArtistName n : b.getArtistList()) {
                                    if (n.getArtistName().equals(name[0])) {
                                        Log.d("NewBroker", String.valueOf(b.getPort()));
                                        newIp = b.getIpAddress();
                                        newPort = b.getPort();
                                        break;
                                    }
                                }
                            }

                            if (newIp.equals("")) {
                                Log.d("tag", "Unavailable Artist. Try Again :)");
                                //print message to the app user
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                       unavailableArt.setText(R.string.unavailable);
                                    }
                                });
                                break;
                            } else {
                                connect(newPort, SERVER_IP);//newIp);
                            }

                        }
                         /*
                            We HAVE reached the correct Broker
                         */
                        else {
                            //Correct Broker: Searching for songs
                            songsToChoose = (ArrayList<String>) input.readObject();

                            break;
                        }
                    }
                    Thread.sleep(1000);
                    publishProgress("Fetching song list");

                } catch (InterruptedException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return songsToChoose;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Search_Activity.this, "Please wait", "We're searching for all the available songs");
        }

        @Override
        protected void onPostExecute(List<String> list) {
            progressDialog.dismiss();
            ArrayAdapter<String> arrayAdapter;
            arrayAdapter = new ArrayAdapter<String>(Search_Activity.this, android.R.layout.simple_list_item_1, songsToChoose);
            songList.setAdapter(arrayAdapter);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        public void connect(int i, String ip) {
            try {
                Log.e("init", "Attempting Connection...");
                socketClient = new Socket(InetAddress.getByName(ip), i);
                System.out.println("Connected to:" + ip + " " + i);
                //streams
                output = new ObjectOutputStream(socketClient.getOutputStream());
                output.flush();
                input = new ObjectInputStream(socketClient.getInputStream());
                Log.e("init", "Got I/O streams");
                //inform Server for Consumer connection
                output.writeObject("Consumer");
                output.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }


        public boolean requestArtist(String name) throws IOException, ClassNotFoundException {
            Log.d("BrokerAnswer", (String) input.readObject()); //awaiting for artist name input
            Log.e("Broker Answer", name);
            output.writeObject(name);
            output.flush();
            String brokerAnswer = (String) input.readObject();
            Log.d("BrokerAnswer", brokerAnswer); //searching for songs from artist OR incorrect broker
            if (brokerAnswer.equals("Incorrect broker!")) return true;
            else return false;//brokerAnswer.equals("Searching for songs from artist: ....")
        }

    }
}