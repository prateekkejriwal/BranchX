package xyz.prateekkejriwal.branchxconfig;

import java.util.ArrayList;
import java.util.Stack;

// BranchX Logs Receiver
// Implement BranchLogsReceiver as per your requirement.
public interface BranchLogsReceiver {
   public void newLog(BranchXLogger.BranchXLog log);
   public void firstPayload(ArrayList<BranchXLogger.BranchXLog> firstSetofLogs);
}
