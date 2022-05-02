package xyz.prateekkejriwal.branchxconfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import xyz.prateekkejriwal.branchxconfig.BranchEventX;
import xyz.prateekkejriwal.branchxconfig.BranchLogsReceiver;
import xyz.prateekkejriwal.branchxconfig.BranchX;
import xyz.prateekkejriwal.branchxconfig.BranchXLogger;

public class MainActivity extends AppCompatActivity {

    Button event1, event2;
    RecyclerView logs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Buttons for events.
        event1 = findViewById(R.id.event1);
        event2 = findViewById(R.id.event2);

        //Logs on the screen.
        logs = findViewById(R.id.logs_recycler);
        LogsList adapter = new LogsList();
        logs.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        logs.setLayoutManager(linearLayoutManager);

        // Listen for updated logs from the SDK.
        // Need to update the logic, the current one can get heavy on systems.
        BranchXLogger.addLogsReceiver(new BranchLogsReceiver() {
            @Override
            public void newLog(BranchXLogger.BranchXLog log) {
                adapter.newLog(log);
            }

            @Override
            public void firstPayload(ArrayList<BranchXLogger.BranchXLog> firstSetOfLogs) {
                adapter.firstSetOfLogs(firstSetOfLogs);
            }
        });
        JSONObject config = BranchX.getConfiguration();

        // Print configuration on screen.
        TextView configuration = findViewById(R.id.configuration);
        try {
            configuration.setText(config.toString(2));
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        setEventListeners();
        updateEventsLog();

    }


    class LogsList extends RecyclerView.Adapter<LogViewHolder> {
        List<BranchXLogger.BranchXLog> logs;

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.logs_view, parent, false);
            return new LogViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            BranchXLogger.BranchXLog log = logs.get(position);
            holder.timestamp.setText(Instant.ofEpochSecond(log.getTimestamp()).toString());
            holder.message.setText(log.getMessage());
        }

        @Override
        public int getItemCount() {
            return logs == null ? 0 : logs.size();
        }

        public void newLog(BranchXLogger.BranchXLog log){
            int oldSize=logs.size();
            logs.add(log);
            notifyItemInserted(oldSize);
        }

        public void firstSetOfLogs(ArrayList<BranchXLogger.BranchXLog> firstSetOfLogs) {
            logs= new ArrayList<>(firstSetOfLogs);
            notifyDataSetChanged();
        }
    }

    class LogViewHolder extends RecyclerView.ViewHolder {
        TextView timestamp;
        TextView message;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.timestamp);
            message = itemView.findViewById(R.id.log);

        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    private void setEventListeners() {

        event1.setOnClickListener((view) -> {
            BranchEventX.createEvent("event1").logEvent(this);
            updateEventsLog();
        });

        event2.setOnClickListener((view) -> {
            BranchEventX.createEvent("event2").logEvent(this);
            updateEventsLog();

        });
    }

    void updateEventsLog() {
        BranchX.getEventsHistory();

    }
}