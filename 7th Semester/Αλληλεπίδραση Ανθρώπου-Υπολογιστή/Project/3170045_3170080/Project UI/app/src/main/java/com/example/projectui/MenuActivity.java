package com.example.projectui;


import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements CustomRecyclerViewAdapter.ItemClickListener {

    CustomRecyclerViewAdapter adapter;
    Button startButton;
    Button backButton;
    Button addButton;
    Button editButton;
    ImageButton lastPressed;
    int position;
    Toast lastToast = null;
    boolean editing = false;
    static boolean initialised = false;
    static List<Menu> menus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(position);
                System.out.println(adapter.getItem(position));
                Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
                intent.putExtra("SENT_TIMER", adapter.getItem(position).getSettings().getTime());
                startActivity(intent);
            }
        });

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastToast != null) lastToast.cancel();
                finish();
            }
        });

        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //initialise settings from csv
        if(!initialised) {
            initialised = true;
            List<MenuSettings> menuSettings = initItems();
            //create menus
            Menu menu1 = new Menu(R.drawable.soup, "Soup", menuSettings.get(0));
            Menu menu2 = new Menu(R.drawable.spaghetti, "Spaghetti", menuSettings.get(1));
            Menu menu3 = new Menu(R.drawable.pizza, "Pizza", menuSettings.get(2));
            Menu menu4 = new Menu(R.drawable.beef_wellington, "Beef", menuSettings.get(3));
            Menu menu5 = new Menu(R.drawable.chicken, "Chicken", menuSettings.get(4));
            Menu menu6 = new Menu(R.drawable.potato, "Jacked Potato", menuSettings.get(6));
            Menu menu7 = new Menu(R.drawable.popcorn, "Popcorn", menuSettings.get(7));
            Menu menu8 = new Menu(R.drawable.pita, "Pita bread", menuSettings.get(8));
            menus.add(menu1);
            menus.add(menu2);
            menus.add(menu3);
            menus.add(menu4);
            menus.add(menu5);
            menus.add(menu6);
            menus.add(menu7);
            menus.add(menu8);
        }

        RecyclerView recyclerView = findViewById(R.id.menus_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomRecyclerViewAdapter(this, menus);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        GridLayoutManager rvManager = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(rvManager);
    }

    @Override
    public void onItemClick(View view, final int position) {
        if(lastToast != null){
            lastToast.cancel();
        }
        Toast toast = Toast.makeText(this, "" + adapter.getItem(position).getSettings(), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, (int)(view.getX() - view.getWidth()/2), (int)(view.getBottom() - view.getHeight()/2));
        toast.show();
        lastToast = toast;
        if (lastPressed != null) {
            lastPressed.setSelected(false);
        }
        lastPressed = (ImageButton) view;
        this.position = position;
        view.setSelected(true);
    }

    //Read a csv containing predefined menus. Csv should be placed in internal directory (instead of assets folder) to allow the user to make changes. Otherwise menu changes will not be saved
    private List<MenuSettings> initItems(){
        List<MenuSettings> menuList = new ArrayList<>();
        InputStream is = null;
        String yourFilePath = getApplicationContext().getFilesDir() + "/" + "menus.csv";
        File file = new File( yourFilePath );
        try {
            AssetManager mng = getApplicationContext().getAssets();
            is = mng.open("menus.csv");
        }catch (IOException e){
            e.printStackTrace();
        }
        CSVFormat format = CSVFormat.newFormat(',');
        try {
            Iterable<CSVRecord> records = CSVParser.parse(is, Charset.forName("UTF-8"), format.withFirstRecordAsHeader());
            for(CSVRecord record: records){
                System.out.println(record);
                MenuSettings settings = new MenuSettings(Integer.parseInt(record.get(0).trim()), record.get(1), Boolean.parseBoolean(record.get(2).trim()), Double.parseDouble(record.get(3).trim()));
                menuList.add(settings);
                System.out.println(settings);
            }
        }catch(IOException e) {
            System.err.println("Error while parsing records");
        }
        return menuList;
    }
}
