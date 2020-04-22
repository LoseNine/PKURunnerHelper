package com.example.pkuhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class DoActivity extends AppCompatActivity {
    private DrawerLayout mdrawerLayout;
    private TextView seekText,paceText,frequencyText,marque;
    private SeekBar seekBar,seekBarPace,seekBarFrequency;
    private Button doit;


    private double Distence=5;
    private double Pace=5;
    private double Stride_frequncy=160;

    public String username;
    public String token;

    public String getUsername(){
        return this.username;
    }
    public String getToken(){
        return this.token;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mdrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView=(NavigationView)findViewById(R.id.nav_view) ;
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.all);


        navView.setCheckedItem(R.id.nav_home);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        Toast.makeText(getApplicationContext(),"暂时没有开发页面跳转",Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case R.id.nav_weather:
                        WeatherThread weatherThread=new WeatherThread();
                        weatherThread.start();
                        try {
                            weatherThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String log=weatherThread.weather();
                        Toast.makeText(getApplicationContext(),log,Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case R.id.nav_flag:
                        UserThread userThread = new UserThread(getUsername(),getToken());
                        userThread.start();
                        try {
                            userThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String log2=userThread.getRes();
                        Toast.makeText(getApplicationContext(),log2,Toast.LENGTH_SHORT)
                                .show();
                        break;
                    default:
                        break;
                }

                mdrawerLayout.closeDrawers();
                return true;
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String str1 = bundle.getString("username");
        String str2 = bundle.getString("token");
        this.username=str1;
        this.token=str2;

        marque=(TextView)findViewById(R.id.marque);
        marque.setSelected(true);

        Toast.makeText(DoActivity.this, "欢迎使用！", Toast.LENGTH_SHORT).show();

        seekText = (TextView) findViewById(R.id.seekText);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        /* 设置SeekBar 监听setOnSeekBarChangeListener */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*拖动条停止拖动时调用 */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("SeekBarActivity", "拖动停止");
                Distence=seekBar.getProgress();
            }
            /*拖动条开始拖动时调用*/
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("SeekBarActivity", "开始拖动");
            }
            /* 拖动条进度改变时调用*/
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekText.setText("设置跑步距离: " + progress + " km");
            }
        });

        paceText = (TextView) findViewById(R.id.paceText);
        seekBarPace = (SeekBar) findViewById(R.id.seekBarPace);
        /* 设置SeekBar 监听setOnSeekBarChangeListener */
        seekBarPace.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*拖动条停止拖动时调用 */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("SeekBarActivity", "拖动停止");
                Pace=seekBarPace.getProgress();
            }
            /*拖动条开始拖动时调用*/
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("SeekBarActivity", "开始拖动");
            }
            /* 拖动条进度改变时调用*/
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paceText.setText("设置跑步速度: " + progress + " min/km");
            }
        });

        frequencyText = (TextView) findViewById(R.id.frequencyText);
        seekBarFrequency = (SeekBar) findViewById(R.id.seekBarFrequency);
        /* 设置SeekBar 监听setOnSeekBarChangeListener */
        seekBarFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*拖动条停止拖动时调用 */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("SeekBarActivity", "拖动停止");
                Stride_frequncy=seekBarFrequency.getProgress();
            }
            /*拖动条开始拖动时调用*/
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("SeekBarActivity", "开始拖动");
            }
            /* 拖动条进度改变时调用*/
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequencyText.setText("迈步频率 " + progress + " step/min");
            }
        });


        doit=(Button)findViewById(R.id.doit);
        doit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DoActivity.this)
                        .setTitle("选择数据")
                        .setMessage("请确认要提交的里程数后再提交！\n"+
                                "跑步距离："+Distence+'\n'+
                                "跑步速度："+Pace+"\n"+
                                "迈步频率："+Stride_frequncy)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(DoActivity.this,"提交成功！",Toast.LENGTH_SHORT).show();
                                try {
                                    //获取Record
                                    InputStream inputStrean= getResources().getAssets().open("pace.json");
                                    InputStreamReader inputStreamReader=new InputStreamReader(inputStrean);
                                    Record record=new Record(Distence,Pace,Stride_frequncy);
                                    record.Build(inputStreamReader);

                                    String resultRecord=record.getDetail();
                                    System.out.println("Result:"+resultRecord);
                                    //新线程Upload
                                    UploadThread runUpload = new UploadThread(record,getUsername(),getToken());
                                    runUpload.start();


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(DoActivity.this,"重新选择！",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        System.out.println(menu);
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mdrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

}

class UploadThread extends Thread {
    private Record resultRecord;
    private String username;
    private String token;

    public UploadThread(Record resultRecord,String username,String token) {
        this.username=username;
        this.resultRecord=resultRecord;
        this.token=token;
    }
    public void run() {
        try {
            upload(this.resultRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(Record resultRecord) {
        Connection con = Jsoup.connect("https://pkunewyouth.pku.edu.cn/record/"+this.username);
        //遍历生成参数
        con.data("userId",this.username);
        con.data("duration",resultRecord.getDuration());
        con.data("data",resultRecord.getData());
        con.data("detail", resultRecord.getDetail());
        con.data("misc","{\"agent\": \"Android v1.2+\"}");
        con.data("step",resultRecord.getStep());

        con.header("User-Agent", "okhttp/3.10.0");
        con.header("Authorization",this.token);

        Document doc = null;
        try {
            doc = con.post();
        } catch (IOException e) {
            System.out.println("upload error");
        }
        String htmlResult=doc.html();
        System.out.println(htmlResult);
    }
//    public String getRunResult() {
//        if (this.token.contains("错误") || this.token.contains("err")){
//            return "POSTERR";
//        }
//        return this.token;
//    }
}

class WeatherThread extends Thread {
    public String res;
    public void run() {
        try {
            getWeather();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public String weather(){
        return res;
    }

    public void getWeather() {
        Connection con = Jsoup.connect("https://pkunewyouth.pku.edu.cn/weather/all");

        con.header("User-Agent", "okhttp/3.10.0");

        Document doc = null;
        try {
            doc = con.get();
        } catch (IOException e) {
            System.out.println("upload error");
            this.res="本学期暂未开始跑步!";
            return;
        }
        String htmlResult=doc.html();
        System.out.println(htmlResult);
        this.res=htmlResult;
    }
}

class UserThread extends Thread {
    private String username;
    private String token;
    private String res;

    public UserThread(String username,String token) {
        this.username=username;
        this.token=token;
    }
    public void run() {
        try {
            getUserData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRes(){
        return res;
    }

    public void getUserData() {
        Connection con = Jsoup.connect("https://pkunewyouth.pku.edu.cn/record/status/"+this.username);

        con.header("User-Agent", "okhttp/3.10.0");
        con.header("Authorization",this.token);

        Document doc = null;
        try {
            doc = con.get();
        } catch (IOException e) {
            System.out.println("upload error");
            this.res="本学期暂未开始跑步！";
            return;
        }
        String htmlResult=doc.html();
        System.out.println(htmlResult);
        this.res=htmlResult;
    }
}