package scn.com.sipclient.call;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import scn.com.sipclient.R;
import scn.com.sipclient.main.CallHistory;
import scn.com.sipclient.main.ContactList;
import scn.com.sipclient.model.ContactItem;
import scn.com.sipclient.model.User;
import scn.com.sipclient.net.APIManager;
import scn.com.sipclient.realm.HistoryModel;
import scn.com.sipclient.realm.RealmController;
import scn.com.sipclient.utils.AlertDialogManager;
import scn.com.sipclient.utils.Constants;
import scn.com.sipclient.utils.HttpStatus;
import scn.com.sipclient.utils.SessionManager;

public class CallHistoryActivity extends AppCompatActivity implements View.OnClickListener, Callback<JsonObject> {

    private TextView tvCallDate;
    private TextView editCallSummary;
    private TextView tvCallName;
    private ImageView ivCallStatus;
    private int call_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history2);

        tvCallDate = (TextView)findViewById(R.id.tv_call_date);
        editCallSummary = (TextView) findViewById(R.id.edit_call_summary);
        tvCallName = (TextView)findViewById(R.id.tv_caller_name);
        ivCallStatus = (ImageView)findViewById(R.id.iv_call_status);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        tvCallDate.setText(currentDateandTime);

        String peer = getIntent().getStringExtra("CALLER_NAME");
        call_status = getIntent().getIntExtra("CALL_STATUS" , 0);

        tvCallName.setText(peer);
        if (call_status == Constants.CALL_MADE){
            ivCallStatus.setImageResource(R.drawable.call_out);
        }

        if (call_status == Constants.CALL_RECEIVED){
            ivCallStatus.setImageResource(R.drawable.call_in);
        }

        findViewById(R.id.btn_call_save).setOnClickListener(this);
        findViewById(R.id.btn_call_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_call_save:
                addHistory();
                break;
            case R.id.btn_call_cancel:
                finish();
                break;
        }
    }

    private void addHistory(){
        String call_date = tvCallDate.getText().toString();
        String call_summary = editCallSummary.getText().toString();

        SessionManager session = new SessionManager(this);
        User user = session.getUserDetails();
        Call<JsonObject> call = APIManager.getServiceAPI().add_history(user.getUserId() , tvCallName.getText().toString(),call_status,
                editCallSummary.getText().toString(),tvCallDate.getText().toString() );
        call.enqueue(this);
    }
    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        String err = "";
        int status = response.code();
        if (status == HttpStatus.SC_OK) {
            JsonObject jsonObject = response.body();
            HistoryModel history = new HistoryModel();
            history.setHistoryId(jsonObject.get("HistoryId").getAsInt());
            history.setCallDate(jsonObject.get("CallDate").getAsString());
            history.setCallerEmail(jsonObject.get("CallerEmail").getAsString());
            history.setCallStatus(jsonObject.get("CallStatus").getAsInt());
            history.setCallSummary(jsonObject.get("CallSummary").getAsString());
            RealmController.getInstance().addHistory(history);
            startActivity(new Intent(this, CallHistory.class));
            finish();
        }

        if (status == HttpStatus.SC_BAD_REQUEST) {
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response.errorBody().string(), JsonObject.class);
                err = jsonObject.get("Message").getAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        }

        if (status == HttpStatus.SC_NOT_FOUND) {
            Toast.makeText(this, response.errorBody().toString(), Toast.LENGTH_LONG).show();
        }
        if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            AlertDialogManager.showAlertDialog(this, R.string.dialog_contact_title, R.string.action_network_error, R.string.action_confirm);
        }

    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {
        AlertDialogManager.showAlertDialog(this, R.string.dialog_contact_title, R.string.action_network_error, R.string.action_confirm);
    }
}
