package xyz.prateekkejriwal.branchxconfiguration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import xyz.prateekkejriwal.branchxconfig.BranchX;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView appId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.loader);
        progressBar.setVisibility(View.VISIBLE);
        appId = findViewById(R.id.appId);


        BranchX.initWithAppID(this,BuildConfig.BRANCHX_APP_ID);
        appId.setText("App ID: " + BranchX.getApplicationId());


    }

    @Override
    protected void onStart() {
        super.onStart();
        BranchX.addBranchSDKInitListener(new Runnable() {
            @Override
            public void run() {
                JSONObject config = BranchX.getConfiguration();
                try {
                    appId.setText("Configuration:" + config.toString(2));
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                BranchX.initBranchSession(SplashActivity.this, (ref, err) -> {
                    progressBar.setVisibility(View.GONE);
                    if (ref != null) {
                        Log.d("AppAtBranch", ref.toString());
                        Toast.makeText(getApplicationContext(), "Branch Session Initialized.\nSending the user to Next Screen in 5 seconds", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(() -> {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                });
                            }
                        }, 4000);
                    }
                }, 0);


            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Log.d("create_toast", String.valueOf(intent.getBooleanExtra("create_toast", false)));
        if (intent.getBooleanExtra("create_toast", false)) {
            String toastMessage = intent.getStringExtra("toast_content");
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        }
    }
}