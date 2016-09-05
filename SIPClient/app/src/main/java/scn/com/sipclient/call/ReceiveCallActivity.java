package scn.com.sipclient.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import java.util.Timer;
import java.util.TimerTask;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import scn.com.sipclient.R;
import scn.com.sipclient.utils.Constants;
import scn.com.sipclient.utils.SessionManager;

public class ReceiveCallActivity extends AppCompatActivity {


    private final static String TAG = ReceiveCallActivity.class.getSimpleName();
    HubConnection mConnection;
    HubProxy mHub;

    private String me;
    private String peer;

    private final static String SIP_DOMAIN = "192.168.1.122";
    private String SIP_USERNAME = "1002";
    private final static String SIP_PASSWORD = "1234";
    private final static String SIP_SERVER_HOST = "192.168.1.122";
    private final static int SIP_SERVER_PORT = 5060;

    private final NgnEngine mEngine;
    private final INgnConfigurationService mConfigurationService;
    private final INgnSipService mSipService;

    private BroadcastReceiver mSipBroadCastRecv;
    private BroadcastReceiver mSipCallRecv;

    private NgnAVSession avSession;

    private boolean call_connected = false;

    private TimerTask mTask;
    private Timer mTimer;
    private int mCount = 0;
    private TextView tvCount;

    private final BroadcastReceiver broadcastsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //If caller end call, exit call
            if(action.equals("END_CALL")){
                String call_from = intent.getStringExtra("CALL_FROM");
                String call_to = intent.getStringExtra("CALL_TO");
                if (me.equals(call_from)){

                }else if (me.equals(call_to)){
                    exitCall();
                }else{

                }
            }


            if(action.equals("START_CALL")){
                String call_from = intent.getStringExtra("CALL_FROM");
                String call_to = intent.getStringExtra("CALL_TO");
                if (me.equals(call_from)){

                }else if (me.equals(call_to)){

                }else{

                }
            }
            // abortBroadcast();
        }
    };

    public ReceiveCallActivity() {
        mEngine = NgnEngine.getInstance();
        mConfigurationService = mEngine.getConfigurationService();
        mSipService = mEngine.getSipService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_call);


        // Get Caller And Caller from intent
        final String email_to= getIntent().getStringExtra("CALL_FROM");
        final String email_from = getIntent().getStringExtra("CALL_TO");

        // My information and call peer
        SessionManager session = new SessionManager(this);
        me = session.getUserDetails().getUserEmail();
        peer = email_to;

        // Connect To SignalR hub
        initialize();
        connectToServer();

        tvCount = (TextView)findViewById(R.id.tv_count_time);

        // Hang up Call
        findViewById(R.id.btn_hangup_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall(email_from, email_to);
                exitCall();
            }
        });

        // Accept Call
        findViewById(R.id.btn_accept_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptCall(email_from, email_to);
                findViewById(R.id.btn_accept_call).setEnabled(false);
            }
        });

        //Initialize SIP
        initializeManager();
        startEngine();
        mEngine.getSoundService().startRingTone();

        mTask = new TimerTask() {
            @Override
            public void run() {
                mCount ++;
                tvCount.post(new Runnable() {
                    @Override
                    public void run() {
                        tvCount.setText(getPassedTimeAsString());
                    }
                });
            }
        };

        mTimer = new Timer();
    }

    @Override
    protected void onResume(){

        ///UserPreferencesManager.setAssignedTaxi(assignedTaxi, context);

        super.onResume();
        if(broadcastsReceiver != null) {

            //Register Receiver for SignalR push end call and start call
            IntentFilter filter = new IntentFilter();
            // Register for Orders Service Hub broadcasts
            filter.addAction("END_CALL");
            filter.addAction("START_CALL");
            // And broadcasts receiver
            registerReceiver(broadcastsReceiver, filter);
        }
    }

    @Override
    protected void onPause(){

        ///UserPreferencesManager.setAssignedTaxi(assignedTaxi, context);
        if(broadcastsReceiver != null) {
            unregisterReceiver(broadcastsReceiver);
        }
        super.onPause();
    }


    @Override
    public void onDestroy() {
        mConnection.stop();
        if(avSession != null){
            avSession.hangUpCall();
            avSession.setContext(null);
            avSession.decRef();
        }
        if(mSipBroadCastRecv != null){
            unregisterReceiver(mSipBroadCastRecv);
            mSipBroadCastRecv = null;
        }
        if(mSipCallRecv != null){
            unregisterReceiver(mSipCallRecv);
            mSipCallRecv = null;
        }
        super.onDestroy();
    }

    //When call end, this activity is finished and start History Activity if Call
    private void startHistoryActivityAndFinish(){
        if(call_connected){
            Intent historyIntent = new Intent(ReceiveCallActivity.this, CallHistoryActivity.class);
            historyIntent.putExtra("CALLER_NAME", peer );
            historyIntent.putExtra("CALL_STATUS", Constants.CALL_RECEIVED);
            startActivity(historyIntent);
        }
        finish();
    }


    private void exitCall(){
        if (avSession != null)
            avSession.hangUpCall();

        startHistoryActivityAndFinish();

        mEngine.getSoundService().stopRingBackTone();
        mEngine.getSoundService().stopRingTone();
    }


    private void initialize() {
        String serverUrl = "http://192.168.1.113:81";
        String hubName = "CallHub";

        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        mConnection = new HubConnection(serverUrl);
        mHub = mConnection.createHubProxy(hubName);
    }

    private void connectToServer() {
        try {
            SignalRFuture<Void> awaitConnection = mConnection.start();
            awaitConnection.get();
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Log.e(TAG, "Failed to connect to server");
        }
    }

    private void endCall(String email_from, String email_to) {
        try {
            mHub.invoke("Call_End",email_from, email_to).get();
            Log.e(TAG, "End Call");
        } catch( Exception e ){
            Log.e(TAG, "Fail to end call");
        }
    }

    private void acceptCall(String email_from, String email_to) {
        try {
            mHub.invoke("Call_Start",email_from, email_to).get();
            Log.e(TAG, "Accept Call");
            Log.e(TAG, "From:" + email_from + ", To:" + email_to);
        } catch( Exception e ){
            Log.e(TAG, "Fail to end call");
        }
    }


    private void initializeManager () {
        Log.e(TAG, "initializeManager");
        // Listen for registration events
        mSipBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                // Registration Event
                if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {
                    NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                    if (args == null) {
                        Log.e(TAG,"Invalid event args");
                        return;
                    }
                    switch (args.getEventType()) {
                        case REGISTRATION_NOK:
                            Log.e(TAG,"REGISTRATION_NOK");
                            break;
                        case UNREGISTRATION_OK:
                            Log.e(TAG,"UNREGISTRATION_OK");
                            break;
                        case REGISTRATION_OK:
                            Log.e(TAG,"REGISTRATION_OK");
                            mEngine.getSoundService().startRingTone();
                            break;
                        case REGISTRATION_INPROGRESS:
                            Log.e(TAG,"REGISTRATION_INPROGRESS");
                            break;
                        case UNREGISTRATION_INPROGRESS:
                            Log.e(TAG,"UNREGISTRATION_INPROGRESS");
                            break;
                        case UNREGISTRATION_NOK:
                            Log.e(TAG,"UNREGISTRATION_NOK");
                            break;
                    }
                }
            }
        };
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        registerReceiver(mSipBroadCastRecv, intentFilter);



        // listen for audio/video session state
        mSipCallRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleSipEvent(intent);
            }
        };
        IntentFilter intentRFilter = new IntentFilter();
        intentRFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
        registerReceiver(mSipCallRecv, intentRFilter);
    }

    private void handleSipEvent(Intent intent){
        final String action = intent.getAction();

        if(NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)){
            NgnInviteEventArgs args =
                    intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
            if(args == null){
                Log.d("DEBUG", "Invalid event args");
                return;
            }

            NgnAVSession avSession
                    = NgnAVSession.getSession(args.getSessionId());
            if (avSession == null) {
                return;
            }

            final NgnInviteSession.InviteState callState = avSession.getState();
            NgnEngine mEngine = NgnEngine.getInstance();

            switch(callState){
                case NONE:
                default:
                    break;
                case INCOMING:
                    Log.e("DEBUG", "Incoming call");
                    // Ring
                    avSession.acceptCall();
                    mEngine.getSoundService().stopRingTone();
//                    avSession.incRef();
//                    avSession.setContext(this);
                    break;
                case INCALL:
                    Log.e("DEBUG", "Call connected");
                    call_connected = true;
                    mTimer.schedule(mTask, 1000, 1000);
                    break;
                case TERMINATED:
                    Log.e("DEBUG", "Call terminated");
                    mEngine.getSoundService().stopRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    break;
            }
        }

    }

    private String getPassedTimeAsString(){
        int second = mCount % 60;
        int minute = mCount / 60 % 60;
        int hour = mCount / 60 / 60;
        String strTime = "00:00";
        if (hour > 0){
            strTime = String.format("%02d:%02d:%02d", hour, minute, second);
        }else{
            strTime = String.format("%02d:%02d",minute, second);
        }
        return strTime;
    }

    private void startEngine(){
        if (!mEngine.isStarted()) {
            if (mEngine.start()) {
                Log.e(TAG,"Engine started :)");
            } else {
                Log.e(TAG,"Failed to start the engine :(");
            }
        }
        // Register
        if (mEngine.isStarted()) {
            if (!mSipService.isRegistered()) {
                // Set credentials
                mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, SIP_USERNAME);
                mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU,
                        String.format("sip:%s@%s", SIP_USERNAME, SIP_DOMAIN));
                mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, SIP_PASSWORD);
                mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, SIP_SERVER_HOST);
                mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, SIP_SERVER_PORT);
                mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, SIP_DOMAIN);
                // VERY IMPORTANT: Commit changes
                mConfigurationService.commit();
                // register (log in)
                mSipService.register(this);
            }
        }
    }
}
