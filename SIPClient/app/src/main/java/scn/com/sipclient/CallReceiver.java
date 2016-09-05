package scn.com.sipclient;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import scn.com.sipclient.call.CallingActivity;
import scn.com.sipclient.call.ReceiveCallActivity;
import scn.com.sipclient.utils.SessionManager;

public class CallReceiver extends BroadcastReceiver {
    public CallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e("SignalR", "Receiver received");
        String action = intent.getAction();

        SessionManager session = new SessionManager(context);
        String me = session.getUserDetails().getUserEmail();
        if(action.equals("INIT_CALL")){
            String call_from = intent.getStringExtra("CALL_FROM");
            String call_to = intent.getStringExtra("CALL_TO");
            if (me.equals(call_from)){
                Intent initIntent = new Intent("INIT_SIP");
//                initIntent.putExtra("SIP_ADDRESS", sipaddress);
                initIntent.putExtras(intent.getExtras());
                //sendBroadcast(broadcastIntent);
                context.sendBroadcast(initIntent);
            }else if (me.equals(call_to)){
                Intent callIntent = new Intent(context, ReceiveCallActivity.class);
                callIntent.putExtras(intent.getExtras());
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(callIntent);
            }else{

            }
        }
    }
}
