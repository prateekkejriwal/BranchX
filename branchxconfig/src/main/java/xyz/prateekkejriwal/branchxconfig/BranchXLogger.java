package xyz.prateekkejriwal.branchxconfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import io.branch.referral.BranchUtil;

public class BranchXLogger {
    HashSet<BranchLogsReceiver> logReceivers = new HashSet<>();
    ArrayList<BranchXLog> logs = new ArrayList<>();

    private static final BranchXLogger logger;

    static {
        logger = new BranchXLogger();
    }
    private BranchXLogger(){}

    static synchronized void log(String message) {
        logger.logMessage(message);
    }

    private void logMessage(String message) {
        BranchXLog branchXLog = new BranchXLog(System.currentTimeMillis() / 1000, message);
        logs.add(branchXLog);
        for (BranchLogsReceiver branchLogsReceiver : logReceivers) {
            branchLogsReceiver.newLog(branchXLog);
        }
    }

    public static void addLogsReceiver(BranchLogsReceiver branchLogsReceiver) {
        branchLogsReceiver.firstPayload(logger.logs);
        logger.logReceivers.add(branchLogsReceiver);
    }


    public static class BranchXLog {
        private long timestamp;

        @Override
        public String toString() {
            return "BranchXLog{" +
                    "timestamp=" + timestamp +
                    ", message='" + message + '\'' +
                    '}';
        }

        public BranchXLog(long timestamp, String message) {
            this.timestamp = timestamp;
            this.message = message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        private String message;
    }


}
