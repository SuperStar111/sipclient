package scn.com.sipclient.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;

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
        String serverUrl = "http://192.168.1.113:81";
        String hubName = "CallHub";

        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        mConnection = new HubConnection(serverUrl);
        mHub = mConnection.createHubProxy(hubName);
    }

    private void prepareGetMessage() {
        mHub.on("sendRoom", new SubscriptionHandler3<String, String, String>() {

            @Override
            public void run(final String sipaddress, final String call_from,final String call_to) {

                broadcastIntent = new Intent("INIT_CALL");
                broadcastIntent.putExtra("SIP_ADDRESS", sipaddress);
                broadcastIntent.putExtra("CALL_FROM", call_from);
                broadcastIntent.putExtra("CALL_TO", call_to);
                //sendBroadcast(broadcastIntent);
                Log.e("SignalR", "Message received in service");
                Log.e("SignalR", "From:" + call_from + ", To:" + call_to);
                sendBroadcast(broadcastIntent, null);
//                PendingIntent.getBroadcast()
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
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
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
