package scn.com.sipclient.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import scn.com.sipclient.R;
import scn.com.sipclient.net.APIManager;
import scn.com.sipclient.utils.AlertDialogManager;
import scn.com.sipclient.utils.HttpStatus;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, Callback<JsonObject> {

    private TextView mTvName;
    private TextView mTvEmail;
    private TextView mTvPassword;

    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mTvName = (TextView)findViewById(R.id.tv_register_name);
        mTvEmail = (TextView)findViewById(R.id.tv_register_email);
        mTvPassword = (TextView)findViewById(R.id.tv_register_password);

        findViewById(R.id.btn_signup).setOnClickListener(this);
        findViewById(R.id.link_login).setOnClickListener(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.registering));
        pDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_signup:
                String name = mTvName.getText().toString();
                String email = mTvEmail.getText().toString();
                String password = mTvPassword.getText().toString();
                if(isValidInput()){
                    register_user(name, email,password);
                }
                break;
            case R.id.link_login:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;
        }
    }

    private void register_user(String name, String email, String password) {
        showDialog();
        Call<JsonObject> call = APIManager.getServiceAPI().signup(name,email, password);
        //asynchronous call
        call.enqueue(this);
    }

    private boolean isValidInput() {
        String name = mTvName.getText().toString();
        String email = mTvEmail.getText().toString();
        String password = mTvPassword.getText().toString();
        if (TextUtils.isEmpty(name)){
            mTvName.setError("Please Input Name.");
            mTvName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email)){
            mTvEmail.setError("Please Input Email.");
            mTvEmail.requestFocus();
            return false;
        }
        if (!isEmailValid(email)){
            mTvEmail.setError("Email is Invalid.");
            mTvEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)){
            mTvPassword.setError("Please Input Password.");
            mTvPassword.requestFocus();
            return false;
        }
        if (password.length() < 4){
            mTvPassword.setError("Password length must be over 4 letters.");
            mTvPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        hideDialog();
        String err = "";
        int status = response.code();
        if (status == HttpStatus.SC_OK) {
            //if all went fine update
            Toast.makeText(this, "Successfully Registered!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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

    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {
        hideDialog();
        AlertDialogManager.showAlertDialog(this, R.string.dialog_contact_title, R.string.action_network_error, R.string.action_confirm);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
