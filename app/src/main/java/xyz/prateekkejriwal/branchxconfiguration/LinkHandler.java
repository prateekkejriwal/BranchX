package xyz.prateekkejriwal.branchxconfiguration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import xyz.prateekkejriwal.branchxconfig.BranchEventX;
import xyz.prateekkejriwal.branchxconfig.BranchX;

public class LinkHandler extends Activity implements Branch.BranchReferralInitListener {
    TextView linkView;

    // Handle updated incoming data.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // Reinit Branch Session on new Intent calls
        BranchX.reInitBranchSession(this, this, 0);

    }

    // Initialise Branch SDK and create a session.
    @Override
    protected void onStart() {
        super.onStart();

        // Listen to SDK getting initialised and start a new session.
        BranchX.addBranchSDKInitListener(() -> {
            // Initialise Branch Session
            BranchX.initBranchSession(this, this, 0);
        });

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_handler);
        linkView = findViewById(R.id.link);

        // Initialise the SDK with the Branch App ID.
        BranchX.initWithAppID(this, BuildConfig.BRANCHX_APP_ID);

    }

    // Read the incoming Branch Response.
    @Override
    public void onInitFinished(@Nullable JSONObject referringParams, @Nullable BranchError error) {
        if (referringParams != null) {
            try {
                linkView.setText(getIntent() != null ? getIntent().getData().toString() + '\n' + "Link Data:" + referringParams.toString(2) : "");
                Log.d("BranchX", referringParams.toString());
                String deeplinkPath = referringParams.getString("$deeplink_path");
                handleDeeplinkPath(deeplinkPath);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Redirect the user based on path.
    void handleDeeplinkPath(String path) {
        Toast.makeText(this, "Deeplink Path detected... Redirecting the user in 5 seconds", Toast.LENGTH_SHORT).show();
        List<String> pathSegments = new ArrayList<>(Arrays.asList(path.split("/")));
        if (pathSegments.size() > 0 && pathSegments.get(0).equals("")) {
            pathSegments.remove(pathSegments.get(0));
        }
        switch (pathSegments.get(0)) {
            case "home":
                new Handler().postDelayed(() -> {
                    runOnUiThread(() -> {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    });
                }, 5000);
            case "second":
                break;
            default:
                Intent intent = new Intent(this, SplashActivity.class);
                intent.putExtra("create_toast", true);
                intent.putExtra("toast_content", "You asked for an activity that is not available :" + pathSegments.get(0));
                startActivity(intent);
                finish();
        }


    }
}
