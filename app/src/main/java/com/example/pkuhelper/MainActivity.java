package com.example.pkuhelper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private CustomVideoView welcome_video;
    private Button welcome_btn;
    private TextView Policy;


    private String AppID;
    private String AppSecret;
    private String Cache_AccessToken;
    private int Token_Expired;
    private Map<String, String> mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        welcome_video = (CustomVideoView) findViewById(R.id.welcome_video);
        final String url = "android.resource://" + getPackageName() + "/" + R.raw.welcome_start;
        System.out.println(url);
        welcome_video.setVideoURI(Uri.parse(url));
        welcome_video.start();
        welcome_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                welcome_video.start();
            }
        });

        Policy=(TextView)findViewById(R.id.POLICY);
        Policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("使用须知")
                        .setMessage("1.该移动应用不会搜集任何个人身份识别资料,使用IAAA只为RUNNER鉴权。\n\n"+
                                    "2.该移动应用只为学习研究PKURUNNER传输原理，无其他任何用途。\n\n"+
                                    "3.目前为测试版本。\n\n"+
                                    "4.制作人员：周周  李缺酒\n\n"+
                                    "5.出现任何问题请联系邮箱1247905338@qq.com")
                        .setPositiveButton("已阅", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

        welcome_btn = (Button) findViewById(R.id.welcome_btn);
        welcome_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!welcome_video.isPlaying()) {
                    welcome_video.start();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_login, null);
                final EditText username = (EditText) view.findViewById(R.id.username);
                final EditText password = (EditText) view.findViewById(R.id.password);

                AppID = "PKU_Runner";
                AppSecret = "7696baa1fa4ed9679441764a271e556e"; // 或者说 salt
                Cache_AccessToken = "PKURunner_AccessToken.json";
                Token_Expired = 3600 * 3;// token 缓存 3 小时

                mp = new TreeMap<>(new Comparator<String>() {
                    @Override
                    public int compare(String str1, String str2) {
                        return str1.compareTo(str2);
                    }
                });


                Button button = (Button) view.findViewById(R.id.login);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //发起登录
                        mp.put("appId", AppID);
                        mp.put("randCode", "");
                        mp.put("smsCode", "SMS");
                        mp.put("otpCode", "");
                        mp.put("userName", username.getText().toString());
                        mp.put("password", password.getText().toString());

                        RunThread runT = new RunThread(mp);
                        runT.start();
                        try {
                            runT.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mp.clear();

                        String token=runT.getRunResult();
                        if (!token.equals("POSTERR")){
                            Toast.makeText(MainActivity.this, "鉴权成功！", Toast.LENGTH_SHORT).show();

                            //跳转主页面
                            Intent intent=new Intent(MainActivity.this,DoActivity.class);
                            intent.putExtra("username", username.getText().toString());
                            intent.putExtra("token",token);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(MainActivity.this, "IAAA鉴权失败！", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                builder.setView(view).show();
            }
        });
    }



}
class RunThread extends Thread {
    private String token=null;
    private Map<String,String> mp;
    private String mpMD5;

    public RunThread(Map<String,String> mp) {
        this.mp = mp;
    }
    public void run() {
        try {
            this.token=Login(this.mp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getAppSecret() {
        return "7696baa1fa4ed9679441764a271e556e";
    }

    private String md5(Map<String, String> mp) {
        //MD5("&".join("=".join(item) for item in sorted(payload.items())) + self.AppSecret)  # TreeMap 按 keys 升序遍历
        //String content=null;
        StringBuffer str = new StringBuffer();
        for (Map.Entry<String, String> entry : mp.entrySet()) {
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();

            str.append(mapKey);
            str.append("=");
            str.append(mapValue);
            str.append("&");
        }
        str.deleteCharAt(str.length()-1);
        String content = "" + str + getAppSecret();
        System.out.println("content:"+content);


        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append(0);
            }
            hex.append(Integer.toHexString(b & 0xff));
        }

        return hex.toString();
    }
    public String Login(Map<String, String> mp) {
        mpMD5 = md5(mp);
        System.out.println("msgAbs"+mpMD5);
        mp.put("msgAbs", mpMD5);


        Connection con = Jsoup.connect("https://iaaa.pku.edu.cn/iaaa/svc/authen/login.do");
        //遍历生成参数
        for (Map.Entry<String, String> entry : mp.entrySet()) {
            //添加参数
            con.data(entry.getKey(), entry.getValue());
            con.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 8.0.0; MI 5 MIUI/V10.0.1.0.OAACNFH)");
        }
        Document doc = null;
        try {
            doc = con.post();
        } catch (IOException e) {
            return "POSTERR";
        }
        String token=doc.body().text();
        System.out.println(token);
        token=token.substring(25,token.length()-2);
        System.out.println(token);
        return token;
    }
    public String getRunResult() {
        if (this.token.contains("错误") || this.token.contains("err")){
            return "POSTERR";
        }
        return this.token;
    }
}