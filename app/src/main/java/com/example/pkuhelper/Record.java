package com.example.pkuhelper;

import android.annotation.SuppressLint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Record {
    private double Distance;
    private double Pace;
    private double Frequency;
    private double Distance_Per_Loop = 0.45;

    private int duration=0;
    private String data;
    private long date;
    private int step;
    private ArrayList<ArrayList<Double>> detail = new ArrayList<ArrayList<Double>>();
    public String getDuration(){
        return ""+this.duration;
    }
    public String getData(){
        return this.data;
    }
    public long getDate_(){
        Date curDate = new Date(System.currentTimeMillis());
        return curDate.getTime();
    }
    public String getStep(){
        return ""+this.step;
    }
    public String getDetail(){
        return ""+this.detail;
    }

    public Record(double Distance,double Pace,double Frequency) throws FileNotFoundException {
        this.Distance=Distance;
        this.Pace=Pace;
        this.Frequency=Frequency;
    }

    public String getDate(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
    public double getPointDelta(){
        Random rand=new Random();
        double num = (rand.nextDouble()-0.5)*2*0.000015;
        return num;
    }

    public double getPaceDelta(){
        Random rand=new Random();
        double num = (rand.nextDouble()-0.5)*2*0.1;
        return num;
    }

    public int getFrequencyDelta(){
        Random rand=new Random();
        double num = (rand.nextDouble()-0.5)*2*15;
        return (int)num;
    }

    public void point_generator(JsonArray points_per_loop){
        int points_num_per_loop=(int)(0.4*this.Pace*60);

        if (points_num_per_loop>points_per_loop.size()){
            System.out.println("'pace' ERROR");
        }

        double total_loop=this.Distance/0.4;
        System.out.println(total_loop);
        double current_loop=0.0;

        while(current_loop<total_loop){
            System.out.println("current_Loop:"+current_loop+":total_loop:"+total_loop);
            points_num_per_loop=(int)(0.4*(this.Pace+getPaceDelta())*60);
            for(int i=0;i<points_num_per_loop;i++){
                int idx= (int) Math.floor((double)i/(double)points_num_per_loop*points_per_loop.size());
                JsonArray point=points_per_loop.get(idx).getAsJsonArray();
                ArrayList<Double> point_two = new ArrayList<Double>();
                double p0=point.get(0).getAsDouble()+getPointDelta();
                double p1=point.get(1).getAsDouble()+getPointDelta();
                point_two.add(p0);
                point_two.add(p1);
                point_two.add((double)0);
                this.detail.add(point_two);

                current_loop=current_loop+(1.0/points_num_per_loop);
                System.out.println(current_loop);
                if(current_loop>=total_loop){
                    break;
                }
            }
        }
    }

    public static JsonArray getJson(InputStreamReader inputStreamReader) throws FileNotFoundException {
        JsonParser parser=new JsonParser();
        JsonObject object=(JsonObject)parser.parse(inputStreamReader);
        //转化为数组
        return object.get("data").getAsJsonArray();
    }

    public void Build(InputStreamReader inputStreamReader) throws FileNotFoundException {
        JsonArray jsonArray=getJson(inputStreamReader);
        System.out.println("jsonarray:"+jsonArray);
        this.data=getDate();
        this.duration=(int)(this.Distance*this.Distance_Per_Loop/0.4*(this.Pace+this.getPaceDelta())*60);
        this.step=(int)((this.Frequency+getFrequencyDelta())*this.duration/60);

        point_generator(jsonArray);
    }


}
