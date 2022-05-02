package xyz.prateekkejriwal.branchxconfig;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import io.branch.referral.Branch;
import io.branch.referral.Branch.BranchReferralInitListener;
import io.branch.referral.BranchError;
import xyz.prateekkejriwal.branchxconfig.constants.BranchXConstants;


// Wrapper over the core.
public class BranchX {


    private static final ArrayList<Runnable> onConfigurationLoadedListeners = new ArrayList<>();
    private final Map<String, HashMap<String, Object>> eventsConfig = new HashMap<>();
    public final LinkedList<BranchEventX> eventsQ = new LinkedList<>();
    private JSONObject offlineConfiguration;
    public SharedPreferences eventsHistory;
    SharedPreferences sharedPreferences;
    private JSONObject configuration;
    private final Context context;
    Map<String, Object> listener;
    static String applicationId;
    static BranchX branchX;
    private Branch branch;

    // Returns the active configuration
    public static JSONObject getConfiguration() {
        return branchX == null ? new JSONObject() : branchX.configuration;
    }

    // Gets Events history from BranchX Core.
    public static HashMap<String, String> getEventsHistory() {
        if (branchX != null && branchX.eventsHistory != null) {
            HashMap<String, String> events = new HashMap<>();
            for (String event : branchX.eventsConfig.keySet()) {
                Long ts = branchX.eventsHistory.getLong(event, 0);
                String message = (System.currentTimeMillis() / 1000 - branchX.eventsHistory.getLong(event, 0)) + " seconds ago\n";
                if (ts.equals(0L)) {
                    message = "Event Blocked";
                }
                events.put(event, "Last triggered at: " + message);
            }
            BranchXLogger.log("Events Current State :\n" + events);
            return events;
        } else {
            return null;
        }
    }

    // Returns the Application ID
    public static String getApplicationId() {
        return applicationId;
    }


    /**
     * Loads Branch Configuration OTA, compares it with offline configuration and uses the latest one.
     *
     * @param appId
     */
    private void loadBranchConfiguration(String appId) {
        applicationId = appId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        BranchXLogger.log("Trying to fetch configuration for the App ID - " + getApplicationId());

        JsonObjectRequest appConfigRequest = new JsonObjectRequest(Request.Method.GET,
                BuildConfig.BRANCHX_SERVER + "/configurations/" + appId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject resp) {
                        JSONObject downloadedConfiguration = null;
                        JSONObject toUseConfiguration = null;
                        long lastUpdatedOnline = 0, lastUpdatedOffline = 0;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                        try {
                            // If there is an offline configuration, load the configuration and check for an updated one from the server.
                            if (offlineConfiguration != null) {
                                lastUpdatedOffline = simpleDateFormat.parse(offlineConfiguration.getString("updatedAt")).getTime();
                                toUseConfiguration = offlineConfiguration;
                                BranchXLogger.log("Found offline configuration on the device. Checking for updated configuration from Branch Systems.");
                            }


                            downloadedConfiguration = resp.getString("status").equals("success") ? resp.getJSONObject("data") : null;
                            if (downloadedConfiguration != null) {
                                BranchXLogger.log("Downloaded configuration from Branch Systems.");
                                Date date = simpleDateFormat.parse(downloadedConfiguration.getString("updatedAt"));
                                lastUpdatedOnline = date.getTime();
                                // If Downloaded configuration is latest than the offline one, overwrite the offline configuration and use the latest configuraiton.
                                if (lastUpdatedOnline > lastUpdatedOffline) {
                                    BranchXLogger.log("Downloaded configuration is found to be latest in updates. Overwriting offline configuration with latest one.");
                                    sharedPreferences.edit().putString("configuration", downloadedConfiguration.toString()).commit();
                                    toUseConfiguration = downloadedConfiguration;
                                } else if (lastUpdatedOnline == lastUpdatedOffline) {
                                    BranchXLogger.log("Downloaded configuration is found to be same as offline. Ignoring the downloaded configuration.");
                                    toUseConfiguration = downloadedConfiguration;
                                } else {

                                    BranchXLogger.log("Configuration timestamp mismatch. lastUpdatedOnline < lastUpdatedOffline. Kinda impossible");
                                    Log.e(BranchXConstants.LOGGING_TAG, "Configuration timestamp mismatch. lastUpdatedOnline < lastUpdatedOffline");
                                }
                            } else {
                                BranchXLogger.log("Invalid response from the server falling back");
                            }


                        } catch (JSONException | ParseException jsonException) {
                            Log.e(BranchXConstants.LOGGING_TAG, "Cannot parse JSON response");
                            Log.e(BranchXConstants.LOGGING_TAG, jsonException.toString());

                        }
                        BranchX.this.onConfigurationReceived(toUseConfiguration);


                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("BranchX", error.toString());
                if (offlineConfiguration != null) {
                    BranchX.this.onConfigurationReceived(offlineConfiguration);
                    BranchXLogger.log("Could not fetch configuration online, falling back to offline configuration.");
                    Toast.makeText(context, "Could not fetch configuration online, falling back to offline configuration", Toast.LENGTH_SHORT).show();
                } else {
                    BranchXLogger.log("Could not find a valid configuration");
                    Log.e("BranchX", "Could not find a valid configuration");
                    Toast.makeText(context, "Could not find a valid configuration", Toast.LENGTH_SHORT).show();
                }
            }
        });

        requestQueue.add(appConfigRequest);
    }

    private BranchX(Context context, String appId) {
        BranchXLogger.log("Starting BranchX Core.");
        this.context = context;

        // Load configurations from Shared Preferences.
        sharedPreferences = context.getSharedPreferences(BranchXConstants.SHARED_PREFS_BRANCHX, Context.MODE_PRIVATE);
        eventsHistory = context.getSharedPreferences(BranchXConstants.SHARED_PREFS_EVENTS_HISTORY, Context.MODE_PRIVATE);
        String branchConfig = sharedPreferences.getString("configuration", "{}");

        if (branchConfig != null && !branchConfig.equals("{}")) {
            try {
                JSONTokener configToken = new JSONTokener(branchConfig);
                JSONObject configuration = new JSONObject(configToken);
                Log.d(BranchXConstants.LOGGING_TAG, "Using Cached Config");
                BranchXLogger.log("Loaded Offline Configuration from the storage.");
                offlineConfiguration = configuration;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        loadBranchConfiguration(appId);
    }

    // Creates a wrapper instance with provided App Id.
    public static void initWithAppID(Context context, String appId) {
        branchX = new BranchX(context, appId);
    }

    // Adds a SDK Init Listener
    public static void addBranchSDKInitListener(Runnable runnable) {
        if (runnable != null) {
            if (branchX.branch != null) {
                runnable.run();
            }
            onConfigurationLoadedListeners.add(runnable);
        }
    }

    // The configuration has been successfully processed. Fire the callbacks.
    private void onConfigurationReceived(JSONObject configuration) {
        if (configuration != null) {
            this.configuration = configuration;
            initBranch();
            for (Runnable runnable : onConfigurationLoadedListeners) {
                runnable.run();
            }
        } else {
            Log.e("BranchX", "Server error");
        }

    }

    // Initialise Branch SDK but not session.
    protected void initBranch() {

        boolean testMode = false, loggingMode = false, sdkEnabled = false;
        String branchKey = null;
        Log.d(BranchXConstants.LOGGING_TAG, configuration.toString());
        try {
            BranchXLogger.log("Initialising Branch SDK with Configuration\n" + configuration.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            // Extract sub-confs from main configuration
            JSONObject appIdentifiers = configuration != null ? configuration.getJSONObject("appIdentifiers") : null;
            JSONObject sdkFlippers = configuration != null ? configuration.getJSONObject("flippers") : null;
            JSONObject eventsConfiguration = configuration != null ? configuration.getJSONObject("eventsConfig") : null;

            // Set Branch Key
            if (appIdentifiers != null) {
                branchKey = appIdentifiers.getString("branchKey");
            }

            //Set Flippers
            if (sdkFlippers != null) {
                sdkEnabled = sdkFlippers.getBoolean("sdk");
                testMode = sdkFlippers.getBoolean("testMode");
                loggingMode = sdkFlippers.getBoolean("logging");
            }

            // Set Events Configuration
            if (eventsConfiguration != null) {
                Iterator<String> events = eventsConfiguration.keys();
                while (events.hasNext()) {
                    String eventName = events.next();
                    JSONObject eventConfig = eventsConfiguration.getJSONObject(eventName);
                    HashMap<String, Object> eventConfigMap = new HashMap<>();
                    Integer cap = eventConfig.getInt("cap");
                    eventConfigMap.put("capping", cap);
                    try {
                        String updatedName = eventConfig.getString("updatedName");
                        eventConfigMap.put("updatedName", updatedName);
                    } catch (JSONException jsonException) {
                        Log.d(BranchXConstants.LOGGING_TAG, "No Event Update data available for Event " + eventName);
                    }
                    eventsConfig.put(eventName, eventConfigMap);
                }
            }

            BranchXLogger.log("Events Configuration\n" + eventsConfig.toString());
            Log.d(BranchXConstants.LOGGING_TAG, "Events Configuration" + eventsConfig.toString());

            // Start the Branch SDK as per the configuration.
            if (branchKey != null && sdkEnabled) {
                Branch.bypassWaitingForIntent(true);
                branch = Branch.getAutoInstance(context, branchKey);
                if (testMode) Branch.enableTestMode();
                else Branch.disableTestMode();
                if (loggingMode) Branch.enableLogging();
                else Branch.disableLogging();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // returns event configuration.
    public Map<String, HashMap<String, Object>> getEventsConfig() {
        if (eventsConfig.size() < 1) {
            return null;
        } else return eventsConfig;
    }

    public static void initBranchSession(Activity activity,
                                         BranchReferralInitListener listener,
                                         Integer delayMillis) {

        branchX.initSession(activity, listener, delayMillis, false);
    }


    public static void reInitBranchSession(Activity activity,
                                           BranchReferralInitListener listener,
                                           Integer delayMillis) {
        branchX.initSession(activity, listener, delayMillis, true);
    }


    // Initialises Branch Session
    public void initSession(Activity activity, BranchReferralInitListener listener, int delay, boolean reInit) {
        if (branch != null) {
            BranchXLogger.log("Initialising Branch Session.\nReinit= " + reInit + "\nDelay=" + delay);

            // Instantiate a Branch Session Builder
            Branch.InitSessionBuilder initSessionBuilder = Branch.sessionBuilder(activity)
                    .withData(activity.getIntent() != null ? activity.getIntent().getData() : null)
                    .withCallback((ref, err) -> {
                        listener.onInitFinished(ref, err != null ? new BranchError(err.getMessage(), err.getErrorCode()) : null);
                    });
            // If delay is greater than 0, add it to the session builder.
            if (delay > 0) {
                initSessionBuilder = initSessionBuilder.withDelay(delay);
            }
            // Init / ReInit the session.
            if (reInit) {
                initSessionBuilder.reInit();
            } else {
                initSessionBuilder.init();
            }

            BranchEventX branchEventX = null;
            while (eventsQ.size() > 0) {
                branchEventX = eventsQ.pop();
                branchEventX.logEvent(context);
            }

        } else {
            BranchXLogger.log("Branch Core not yet initialised. Storing context to init session when Branch Core is initialised.");
            this.listener = new HashMap<>();
            this.listener.put("activity", activity);
            this.listener.put("listener", listener);
            this.listener.put("delay", delay);
            Log.d("BranchX", "SDK is not initialized");
        }
        Branch.bypassWaitingForIntent(false);
    }

}

