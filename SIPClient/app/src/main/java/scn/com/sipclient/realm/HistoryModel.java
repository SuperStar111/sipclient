package scn.com.sipclient.realm;

import io.realm.RealmObject;

/**
 * Created by pakjs on 9/4/2016.
 */
public class HistoryModel extends RealmObject {
    private int HistoryId;
    private String CallerEmail;
    private int CallStatus;
    private String CallDate;
    private String CallSummary;

    public String getCallDate() {
        return CallDate;
    }

    public void setCallDate(String callDate) {
        CallDate = callDate;
    }

    public String getCallerEmail() {
        return CallerEmail;
    }

    public void setCallerEmail(String callerEmail) {
        CallerEmail = callerEmail;
    }

    public int getCallStatus() {
        return CallStatus;
    }

    public void setCallStatus(int callStatus) {
        CallStatus = callStatus;
    }

    public String getCallSummary() {
        return CallSummary;
    }

    public void setCallSummary(String callSummary) {
        CallSummary = callSummary;
    }

    public int getHistoryId() {
        return HistoryId;
    }

    public void setHistoryId(int historyId) {
        HistoryId = historyId;
    }
}
