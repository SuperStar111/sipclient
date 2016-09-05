package scn.com.sipclient.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import scn.com.sipclient.R;
import scn.com.sipclient.adapter.ContactAdapter;
import scn.com.sipclient.adapter.SearchAdapter;
import scn.com.sipclient.model.ContactItem;
import scn.com.sipclient.model.User;
import scn.com.sipclient.net.APIManager;
import scn.com.sipclient.utils.AlertDialogManager;
import scn.com.sipclient.utils.HttpStatus;
import scn.com.sipclient.utils.SessionManager;

public class AddContactActivity extends AppCompatActivity implements Callback<JsonObject> {

    private RecyclerView list_search;

    private EditText edit_add_search;

    private ArrayList<ContactItem> search_list;
    private RecyclerView.Adapter searchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        list_search = (RecyclerView) findViewById(R.id.list_search_result);
        edit_add_search = (EditText)findViewById(R.id.edit_add_search);
        edit_add_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    search("@ALL@");
                }else{
                    search(s.toString());
                }
            }
        });

        search_list = new ArrayList<>();
        list_search.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(search_list , this);
        list_search.setAdapter(searchAdapter);
    }

    private void search(String email){

        SessionManager session = new SessionManager(this);
        User user = session.getUserDetails();

        Call<JsonObject> call = APIManager.getServiceAPI().user_search(email);
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
            JsonArray user_array = jsonObject.get("users").getAsJsonArray();
            search_list.clear();
            for (int i = 0; i < user_array.size(); i++) {
                ContactItem item = new ContactItem();
                JsonObject userObject = user_array.get(i).getAsJsonObject();
                item.setUsername(userObject.get("Name").getAsString());
                item.setEmail(userObject.get("Email").getAsString());
                search_list.add(item);
            }
            searchAdapter.notifyDataSetChanged();
        }

        if (status == HttpStatus.SC_BAD_REQUEST) {
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response.errorBody().string(), JsonObject.class);
                err = jsonObject.get("Message").getAsString();
            } catch (IOException e) {
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
