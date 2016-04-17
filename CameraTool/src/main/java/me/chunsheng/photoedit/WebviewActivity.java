package me.chunsheng.photoedit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class WebviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);


        if (getIntent().getStringExtra("WebUrl") != null) {
            Log.e("Url", getIntent().getStringExtra("WebUrl"));
        }
    }
}
