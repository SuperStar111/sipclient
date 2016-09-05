package scn.com.sipclient.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnUriUtils;
import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import scn.com.sipclient.R;
import scn.com.sipclient.service.SIPService;
import scn.com.sipclient.utils.Constants;
import scn.com.sipclient.utils.SessionManager;

public class CallingActivity extends AppCompatActivity {

    private final static String TAG = CallingActivity.class.getSimpleName();
    //SignalR for push notification
    HubConnection mConnection;
    HubProxy mHub;

    private String me;
    private String peer;

    //SIP DOMAIN
    private final static String SIP_DOMAIN = "192.168.1.122";
    private String SIP_USERNAME = "1001";
    private final static String SIP_PASSWORD = "1234";
    private final static String SIP_SERVER_HOST = "192.168.1.122";
    private final static int SIP_SERVER_PORT = 5060;

    //SIP Engine
    private final NgnEngine mEngine;
    private final INgnConfigurationService mConfigurationService;
    private final INgnSipService mSipService;

    //SIP receiver
    private BroadcastReceiver mSipBroadCastRecv;
    private BroadcastReceiver mSipCallRecv;

    private NgnAVSession mSession;

    private boolean call_connected;

    //For count time
    private TimerTask mTask;
    private Timer mTimer;
    private int mCount = 0;

    private TextView tvCount;

    public CallingActivity() {
        mEngine = NgnEngine.getInstance();
        mConfigurationService = mEngine.getConfigurationService();
        mSipService = mEngine.getSipService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        peer = getIntent().getStringExtra("CALL_TO");
        final SessionManager session = new SessionManager(this);
        me = session.getUserDetails().getUserEmail();

        initialize();
        connectToServer();
        initCall( me, peer );

        tvCount = (TextView)findViewById(R.id.tv_count_time);
        findViewById(R.id.btn_hangup_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall(me, peer);
                exitCall();
            }
        });

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
        super.onResume();
        IntentFilter filter = new IntentFilter();
        // Register for Orders Service Hub broadcasts
        filter.addAction("INIT_SIP");
        filter.addAction("END_CALL");
        filter.addAction("START_CALL");
        registerReceiver(broadcastsReceiver, filter);
    }

    @Override
    protected void onPause(){
        if (broadcastsReceiver != null){
            unregisterReceiver(broadcastsReceiver);
        }
        super.onPause();
    }


    @Override
    public void onDestroy() {
        mConnection.stop();
        if(mSession != null){
            mSession.setContext(null);
            mSession.decRef();
        }
        if(mSipBroadCastRecv != null){
            unregisterReceiver(mSipBroadCastRecv);
            mSipBroadCastRecv = null;
        }
        if(mSipCallRecv != null){
            unregisterReceiver(mSipCallRecv);
            mSipCallRecv = null;
        }

        mTimer.cancel();
        super.onDestroy();
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

    private void startHistoryActivity(){
        if(call_connected) {
            Intent historyIntent = new Intent(CallingActivity.this, CallHistoryActivity.class);
            historyIntent.putExtra("CALLER_NAME", peer);
            historyIntent.putExtra("CALL_STATUS", Constants.CALL_MADE);
            startActivity(historyIntent);
        }
        finish();
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
            Log.e("SignalR", "sucess to connect to server");
        } catch(Exception e) {
            Log.e("SignalR", "Failed to connect to server");
        }
    }

    private void initCall(String email_from, String email_to) {
        try {
            mHub.invoke("Call",email_from, email_to).get();
            Log.e("SignalR", "Init Call");
            Log.e("SignalR", "From:" + email_from + ", To:" + email_to);

        } catch( Exception e ){
            Log.e("SignalR", "Fail to Init Call");
        }
    }

    private void endCall(String email_from, String email_to) {
        try {
            mHub.invoke("Call_End",email_from, email_to).get();
            Log.e("SignalR", "End Call by Caller");
            Log.e("SignalR", "From:" + email_from + ", To:" + email_to);
        } catch( Exception e ){
            Log.e("SignalR", "Fail to end call");
        }
    }


    private void exitCall(){
        if (mSession != null)
            mSession.hangUpCall();
        startHistoryActivity();
        mEngine.getSoundService().stopRingBackTone();
        mEngine.getSoundService().stopRingTone();
    }



    boolean makeVoiceCall(String sip_username, String sip_domain) {

        final String validUri = NgnUriUtils.makeValidSipUri(String.format("sip:%s@%s", sip_username, sip_domain));
        if (validUri == null) {
            Log.e("SignalR","failed to normalize sip uri '" + sip_username + "'");
            return false;
        }
        mSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.Audio);
        return mSession.makeCall(validUri);
    }

    private void initializeManager () {
        Log.e("pak", "initializeManager");
        // Listen for registration events
        mSipBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                // Registration Event
                if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {
                    NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                    if (args == null) {
                        Log.e("SignalR","Invalid event args");
                        return;
                    }
                    switch (args.getEventType()) {
                        case REGISTRATION_NOK:
                            Log.e("SignalR","REGISTRATION_NOK");
                            break;
                        case UNREGISTRATION_OK:
                            Log.e("SignalR","UNREGISTRATION_OK");
                            break;
                        case REGISTRATION_OK:
                            Log.e("SignalR","REGISTRATION_OK");
                            break;
                        case REGISTRATION_INPROGRESS:
                            Log.e("SignalR","REGISTRATION_INPROGRESS");
                            break;
                        case UNREGISTRATION_INPROGRESS:
                            Log.e("SignalR","UNREGISTRATION_INPROGRESS");
                            break;
                        case UNREGISTRATION_NOK:
                            Log.e("SignalR","UNREGISTRATION_NOK");
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
                    Log.i("DEBUG", "Incoming call");
                    // Ring
                    break;
                case INCALL:
                    Log.i("DEBUG", "Call connected");
                    mEngine.getSoundService().stopRingBackTone();
                    call_connected = true;
                    mTimer.schedule(mTask, 1000, 1000);
                    break;
                case TERMINATED:
                    Log.i("DEBUG", "Call terminated");
                    mEngine.getSoundService().stopRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    break;
            }
        }

    }


    private void startEngine(){
        if (!mEngine.isStarted()) {
            if (mEngine.start()) {
                Log.e("SignalR","Engine started :)");
            } else {
                Log.e("SignalR","Failed to start the engine :(");
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


    private final BroadcastReceiver broadcastsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("INIT_SIP")){
                Log.e(TAG, "Init SIP");
//                SIP_USERNAME = intent.getStringExtra("SIP_ADDRESS");
                String call_from = intent.getStringExtra("CALL_FROM");
                String call_to = intent.getStringExtra("CALL_TO");
                Log.e(TAG, "From:" + call_from + ", To:" + call_to);

                // if caller is same to me, register sip user , else nothing
                if (me.equals(call_from)){
                    SIP_USERNAME = "1001";
                    initializeManager();
                    startEngine();
                    Log.e(TAG, "Start Ring tone!");
                    mEngine.getSoundService().startRingBackTone();
                }else {

                }
            }

            if(action.equals("END_CALL")){
                String call_from = intent.getStringExtra("CALL_FROM");
                String call_to = intent.getStringExtra("CALL_TO");
                if (me.equals(call_from)){

                }else if (me.equals(call_to)){
                    Log.e("SignalR", "END CALL");
                    Log.e("SignalR", "From:" + call_from + ", To:" + call_to);
                    exitCall();
                }else{

                }
            }

            if(action.equals("START_CALL")){
                String call_from = intent.getStringExtra("CALL_FROM");
                String call_to = intent.getStringExtra("CALL_TO");
                Log.e("SignalR", "START CALL");
                Log.e("SignalR", "From:" + call_from + ", To:" + call_to);
                if (me.equals(call_from)){

                }else if (me.equals(call_to)){

                    Log.e("SignalR", "Start CALL");

                    makeVoiceCall("1002" , SIP_DOMAIN);

                    mSession.incRef();
                    mSession.setContext(context);
                }else{

                }
            }
        }
    };
}
