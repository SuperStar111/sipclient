package scn.com.sipclient.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import scn.com.sipclient.R;
import scn.com.sipclient.model.ContactItem;
import scn.com.sipclient.model.User;
import scn.com.sipclient.net.APIManager;
import scn.com.sipclient.realm.RealmController;
import scn.com.sipclient.utils.HttpStatus;
import scn.com.sipclient.utils.SessionManager;

public class EditContactActivity extends AppCompatActivity implements View.OnClickListener, Callback<JsonObject> {

    private TextView tv_email;
    private EditText tv_name;

    private int contactId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_detail);

        tv_name = (EditText)findViewById(R.id.edit_contact_name);
        tv_email = (TextView)findViewById(R.id.edit_contact_email);


        contactId = getIntent().getIntExtra("CID", 0);
        String name = getIntent().getStringExtra("NAME");
        String email = getIntent().getStringExtra("EMAIL");
        tv_name.setText(name);
        tv_email.setText(email);
        findViewById(R.id.btn_edit_save).setOnClickListener(this);
        findViewById(R.id.btn_edit_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit_save:
                update_contact(contactId);
                break;
            case R.id.btn_edit_cancel:
                finish();
                break;
        }
    }

    private void update_contact( int contactId){
        SessionManager session = new SessionManager(this);
        User user = session.getUserDetails();

        Call<JsonObject> call = APIManager.getServiceAPI().update_contact(user.getUserId(), contactId, tv_email.getText().toString(), tv_name.getText().toString());
        //asynchronous call
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        String err = "";
        int status = response.code();
        if (status == HttpStatus.SC_OK) {
            //if all went fine update
            JsonObject jsonObject = response.body();
            ContactItem item = new ContactItem();
            item.setId(jsonObject.get("ContactId").getAsInt());
            item.setUsername(jsonObject.get("Name").getAsString());
            item.setEmail(jsonObject.get("Email").getAsString());

            RealmController.getInstance().updateContact(item);
            startActivity(new Intent(this, ContactList.class));
            finish();
        }

        if (status == HttpStatus.SC_BAD_REQUEST) {
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response.errorBody().string(), JsonObject.class);
                err = jsonObject.get("Message").getAsString();
            } catch (Exception e) {

            }
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        }

        if (status == HttpStatus.SC_NOT_FOUND) {
            Toast.makeText(this, response.errorBody().toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {

    }
}
