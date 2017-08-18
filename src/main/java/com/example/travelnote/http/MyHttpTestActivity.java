package com.example.travelnote.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.travelnote.R;

public class MyHttpTestActivity extends AppCompatActivity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_http_test);
        button = (Button)findViewById(R.id.button);
        final String url = "http://api.lvxingx.com/note/get";
        final RequestBody requestBody = new RequestBody();
        requestBody.setPageNo("1");
        requestBody.setOrderType("1");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyHttp.sendGetRequest(url, requestBody, new MyHttp.HttpCallBack() {
                    //这里是子线程
                    @Override
                    public void onSuccess(byte[] response) {
                        Log.e("TAG", "onSuccess: "+ response.toString());
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
        });
    }
}
