package xyz.prateekkejriwal.branchxconfig;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import io.branch.referral.util.BRANCH_STANDARD_EVENT;
import io.branch.referral.util.BranchEvent;
import xyz.prateekkejriwal.branchxconfig.constants.BranchXConstants;

/**
 * Wrapper over the existing BranchEvent
 * Adds capabilities like changing of event name and event capping.
 */
public class BranchEventX extends BranchEvent {
    private String eventOriginalName;
    static BranchX branchXInstance;
    static Map<String, HashMap<String, Object>> eventsConfig;

    private BranchEventX(String eventName) {
        super(eventName);
    }

    /**
     * Static method to create a Branch Event with the updated name (if present in the configuration)
     * @param eventName - event name from the application
     * @returns branchEventX - An instance of original Branch Event.
     */
    public static BranchEventX createEvent(String eventName) {
        branchXInstance = BranchX.branchX;
        if(branchXInstance==null){
            Log.d(BranchXConstants.LOGGING_TAG,"BranchX Core not yet initialised. Please wait before making an event.");
        return null;
        }
        // Get events configuration from BranchX.
        eventsConfig = branchXInstance.getEventsConfig();

        // Get Specific event configuration from BranchX
        HashMap<String, Object> eventConfig = eventsConfig.get(eventName);

        // Use the updated event name.
        String updatedName = (String) eventConfig.get("updatedName");
        BranchEventX eventX;
        if (updatedName != null) {
            eventX = new BranchEventX(updatedName);
        } else {
            eventX = new BranchEventX(eventName);
        }

        // Store Original event name in a different property.
        eventX.eventOriginalName = eventName;


        return eventX;
    }


    /**
     * Fires a call to Branch with the event payload.
     * Checks for capping and then fires the event if passes.
     * @param context
     * @return
     */
    @Override
    synchronized public boolean logEvent(Context context) {
        String eventName = this.getEventName();
        boolean fireEvent = true;

        if (branchXInstance != null) {
            Map<String, HashMap<String, Object>> eventsConfig = branchXInstance.getEventsConfig();

            if (eventsConfig != null) {
                HashMap<String, Object> eventConfig = eventsConfig.get(eventOriginalName);

                if (eventConfig != null) {

                    // Get the capping details
                    Integer capping = eventConfig.get("capping") != null ? (Integer) eventConfig.get("capping") : 0;

                    // If the event is capped to less than 1, do not fire the event, event is blocked.
                    if (capping < 1) {
                        fireEvent = false;
                        BranchXLogger.log("Blocked Event " + eventName);
                        Log.e(BranchXConstants.LOGGING_TAG, "Blocked Event " + eventName);
                    } else {
                        // Get the last time stamp of the event fired.
                        long lastTs = getCapFromSharedPrefs(branchXInstance, this.eventOriginalName);
                        long now = System.currentTimeMillis() / 1000;

                        // If the last event was fired within the capping timeframe, reject the event.
                        if ((now - lastTs) <= capping) {
                            String message = "Event Capping Reached for event:" + eventName + " : tried sending more instances than allowed limit in " + capping + " seconds";
                            BranchXLogger.log(message);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            Log.e(BranchXConstants.LOGGING_TAG, message);
                            fireEvent = false;
                        }
                        // If the last event was fired outside the capping timeframe, accept the event.
                        if ((now - lastTs) > capping) {
                            Log.d(BranchXConstants.LOGGING_TAG, "capping: " + lastTs + "    " + now);
                            BranchXLogger.log("Event fired  : " + eventName);
                            Toast.makeText(context, "Event fired : " + eventName, Toast.LENGTH_SHORT).show();
                            lastTs = now;
                        }

                        // Updates the capping detail on Shared Preferences.
                        if (fireEvent) {
                            updateCapOnSharedPrefs(branchXInstance, this.eventOriginalName, lastTs);
                        }
                    }
                } else {
                    // If the configuration is not yet loaded, put the event to Queue.
                    Log.e(BranchXConstants.LOGGING_TAG, "Event config not available.Adding to queue for later.");
                    if(branchXInstance.eventsQ!=null ){branchXInstance.eventsQ.add(this);}
                    fireEvent = false;
                }
            }
        } else {
            // BranchX SDK has not yet initialised.
            Log.d(BranchXConstants.LOGGING_TAG, "Branch SDK not yet ready");
            fireEvent = false;
        }

        if (fireEvent) {
            super.logEvent(context);
        }

        return fireEvent;
    }

    // Update capping for the event on shared preferences.
    void updateCapOnSharedPrefs(BranchX branchX, String eventName, long ts) {
        branchX.eventsHistory.edit().putLong(eventName, ts).commit();
    }

    // Gets capping details from the shared preferences.
    long getCapFromSharedPrefs(BranchX branchX, String eventName) {
        return branchX.eventsHistory.getLong(eventName, 0);


    }
}
