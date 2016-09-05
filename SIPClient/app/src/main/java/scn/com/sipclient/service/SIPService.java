package scn.com.sipclient.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import microsoft.aspnet.signalr.client.Constants;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler5;

public class SIPService extends Service {

    HubConnection mConnection;
    HubProxy mHub;
    private Intent broadcastIntent;
    public SIPService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        prepareGetMessage();
        connectToServer();
    }


    private void initialize() {
        String serverUrl = scn.com.sipclient.utils.Constants.SERVER_URL;
        String hubName = "CallHub";

        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        mConnection = new HubConnection(serverUrl);
        mHub = mConnection.createHubProxy(hubName);
    }

    private void prepareGetMessage() {
        mHub.on("sendRoom", new SubscriptionHandler3<String, String, String>(){

            @Override
            public void run(final String sipAddress, final String call_from,final String call_to) {

                broadcastIntent = new Intent("INIT_CALL");
                String [] sipAddr = sipAddress.split(":");
                broadcastIntent.putExtra("SIPNAME_FROM", sipAddr[0]);
                broadcastIntent.putExtra("SIPNAME_TO", sipAddr[1]);
                broadcastIntent.putExtra("SIP_DOMAIN", sipAddr[2]);
                broadcastIntent.putExtra("CALL_FROM", call_from);
                broadcastIntent.putExtra("CALL_TO", call_to);
                Log.e("SignalR", "Message received in service");
                Log.e("SignalR", "From:" + call_from + ", To:" + call_to);
                sendBroadcast(broadcastIntent, null);
            }
        }, String.class , String.class, String.class);


        mHub.on("endCall", new SubscriptionHandler2<String, String>() {

            @Override
            public void run(final String call_from,final String call_to) {

                broadcastIntent = new Intent("END_CALL");
                broadcastIntent.putExtra("CALL_FROM", call_from);
                broadcastIntent.putExtra("CALL_TO", call_to);
                //sendBroadcast(broadcastIntent);
                sendBroadcast(broadcastIntent, null);
//                PendingIntent.getBroadcast()
            }
        }, String.class , String.class);

        mHub.on("startCall", new SubscriptionHandler2<String, String>() {

            @Override
            public void run(final String call_from,final String call_to) {

                broadcastIntent = new Intent("START_CALL");
                broadcastIntent.putExtra("CALL_FROM", call_from);
                broadcastIntent.putExtra("CALL_TO", call_to);
                //sendBroadcast(broadcastIntent);
                sendBroadcast(broadcastIntent, null);
//                PendingIntent.getBroadcast()
            }
        }, String.class , String.class);

    }

    private void connectToServer() {
        try {
            SignalRFuture<Void> awaitConnection = mConnection.start();
            awaitConnection.get();
//            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Log.e("SignalR", "Failed to connect to server");
        }
    }



    @Override
    public void onDestroy() {
        mConnection.stop();
        super.onDestroy();
    }

}
