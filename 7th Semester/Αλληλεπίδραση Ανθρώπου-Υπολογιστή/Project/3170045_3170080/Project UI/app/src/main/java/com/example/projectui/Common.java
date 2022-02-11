package com.example.projectui;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class Common {
    final int minutes=10;
    final int seconds=6;
    final  int k=9;
    public int position_minutes=2;
    int position_seconds;
    ArrayList<Integer> arr;
    ArrayList<ArrayList<Integer>> arrayList_minutes=new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> arrayList_second=new ArrayList<>();
    String str;
    static boolean initialised = false;


    Common (){
        for (int i = 0; i <= minutes; i++) {
            for (int j = 0; j <= k; j++) {
                ArrayList<Integer> arr = new ArrayList<>();
                arr.add(i);
                arr.add(j);
                arrayList_minutes.add(arr);
                if (i == minutes) break;

            }

        }

        for (int i = 0; i < seconds; i++) {
            for (int j = 0; j <= k; j++) {
                ArrayList<Integer> arr = new ArrayList<>();
                arr.add(i);
                arr.add(j);
                arrayList_second.add(arr);
            }
        }
        position_seconds = (arrayList_second.size()) / 2;

    }


    public void  plus_minutes(TextView text_minites,Context act) {
        if(position_minutes<arrayList_minutes.size()-2){
            position_minutes++;
            str=new String();
            arr=arrayList_minutes.get(position_minutes);
            for(int i:arr){
                str+=i;
            }

            text_minites.setText(str);

        }
        else{
              /*   position_minutes=0;
                 str=new String();
                 arr=arrayList_minutes.get(position_minutes);
                 for(int i:arr){
                     str+=i;
                 }
                 text_minites.setText(str);/*/
            Context context = act.getApplicationContext();
            CharSequence text = "maximum is 99 minutes!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            System.out.println();
        }

    }

    public  void plus_minutes_5(TextView text_minites, Context act){
        if((position_minutes<arrayList_minutes.size()-2 && position_minutes+5<arrayList_minutes.size()-1)){
            position_minutes+=5;
            str=new String();
            arr=arrayList_minutes.get(position_minutes);
            for(int i:arr){
                str+=i;
            }
            text_minites.setText(str);

        }
        else{
                 /*int temp=arrayList_minutes.size()-position_minutes-1;
                 position_minutes=5-temp;
                 str=new String();
                 arr=arrayList_minutes.get(position_minutes);
                 for(int i:arr){
                     str+=i;
                 }
                 text_minites.setText(str);*/
            Context context = act.getApplicationContext();
            CharSequence text = "maximun is 99 minutes";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
    }


    public  void cos_minutes_5(TextView text_minites,Context act){
        if(position_minutes<=arrayList_minutes.size()-1 && position_minutes-5>=0){
            position_minutes-=5;
            str=new String();
            arr=arrayList_minutes.get(position_minutes);
            for(int i:arr){
                str+=i;
            }
            text_minites.setText(str);

        }
        else{
                /* int temp=5-position_minutes;
                 position_minutes=arrayList_minutes.size()-temp-1;
                 str=new String();
                 arr=arrayList_minutes.get(position_minutes);
                 for(int i:arr){
                     str+=i;
                 }
                 text_minites.setText(str);*/
            Context context =act.getApplicationContext();
            CharSequence text = "minimum is 0 minutes";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    public  void cos_minutes(TextView text_minutes,Context act) {
        if(position_minutes>0){
            position_minutes--;
            str=new String();
            arr=arrayList_minutes.get(position_minutes);
            for(int i:arr){
                str+=i;
            }
            text_minutes.setText(str);
        }
        else{
                  /*  position_minutes=arrayList_minutes.size()-1;
                    str=new String();
                    arr=arrayList_minutes.get(position_minutes);
                    for(int i:arr){
                        str+=i;
                    }
                    text_minites.setText(str);*/
            Context context =act.getApplicationContext();
            CharSequence text = "minimum is 0 minutes";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            System.out.println();
        }
    }

    public  void plus_seconds(TextView text_seconds, Context act) {
        if (position_seconds+10< arrayList_second.size()-1) {
            position_seconds+=10;
            str = new String();
            arr = arrayList_second.get(position_seconds);
            for (int i : arr) {
                str += i;
            }
            text_seconds.setText(str);
        }
               /* else if(position_minutes==arrayList_minutes.size()-1 && position_seconds>0){
                    Context context = getApplicationContext();
                    CharSequence text = "maximum is 30 minutes";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }*/
        else {
            System.out.println(position_seconds+10);
            System.out.println("else");
                   /* position_seconds=0;
                    str = new String();
                    arr = arrayList_second.get(position_seconds);
                    for (int i : arr) {
                        str += i;
                    }
                    System.out.println(position_seconds);
                    tex_seconds.setText(str);/*/
            Context context =act.getApplicationContext();
            CharSequence text = "maximum is 50 seconds";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
    }

    public  void cos_seconds(TextView text_seconds,Context act) {

        if(position_seconds>=0 && position_seconds-10>=0){
            position_seconds-=10;
            str=new String();
            arr=arrayList_second.get(position_seconds);
            for(int i:arr){
                str+=i;
            }
            System.out.println(position_seconds);
            text_seconds.setText(str);
        }
        else{
                    /*position_seconds=arrayList_second.size()-1;
                    position_seconds-=10;
                    str=new String();
                    arr=arrayList_second.get(position_seconds);
                    for(int i:arr){
                        str+=i;
                    }
                    System.out.println(position_seconds);
                    tex_seconds.setText(str);*/
            Context context =act.getApplicationContext();
            CharSequence text = "minimum is 0 seconds";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

}
