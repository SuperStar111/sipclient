package scn.com.sipclient.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import scn.com.sipclient.R;
import scn.com.sipclient.account.LoginActivity;
import scn.com.sipclient.adapter.ContactAdapter;
import scn.com.sipclient.model.ContactItem;
import scn.com.sipclient.model.User;
import scn.com.sipclient.net.APIManager;
import scn.com.sipclient.realm.ContactModel;
import scn.com.sipclient.realm.RealmController;
import scn.com.sipclient.service.SIPService;
import scn.com.sipclient.tab.MyTabHostProvider;
import scn.com.sipclient.tab.TabHostProvider;
import scn.com.sipclient.tab.TabView;
import scn.com.sipclient.utils.AlertDialogManager;
import scn.com.sipclient.utils.HttpStatus;
import scn.com.sipclient.utils.SessionManager;

public class ContactList extends AppCompatActivity implements View.OnClickListener, Callback<JsonObject> {

    private TabHostProvider tabProvider;
    private TabView tabView;

    private ImageButton btn_add_contact;
    private RecyclerView list_contact;

    private EditText edit_search;

    private ArrayList<ContactItem> contact_list;
    private RecyclerView.Adapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabProvider = new MyTabHostProvider(this);
        tabView = tabProvider.getTabHost("contact");

        View currentView = LayoutInflater.from(this).inflate(R.layout.activity_contact_list, null);
        btn_add_contact = (ImageButton) currentView.findViewById(R.id.btn_add_contact);
        list_contact = (RecyclerView) currentView.findViewById(R.id.list_contact);
        btn_add_contact.setOnClickListener(this);

        edit_search = (EditText)currentView.findViewById(R.id.edit_search);

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                contact_search(s.toString());
            }
        });

        contact_list = new ArrayList<>();

        list_contact.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(contact_list , this);
        list_contact.setAdapter(contactAdapter);

        contact_search("");  //All search


        tabView.setCurrentView(currentView);
        setContentView(tabView.render(2));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager session = new SessionManager(ContactList.this);
                session.logoutUser();
                startActivity(new Intent(ContactList.this, LoginActivity.class));
            }
        });

        Intent intent = new Intent(ContactList.this, SIPService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_contact:
                startActivity(new Intent(ContactList.this, AddContactActivity.class));
                break;
        }
    }

    private void contact_search(String name){
        RealmResults<ContactItem> results;
        if (TextUtils.isEmpty(name)){
            results = RealmController.getInstance().getAllContacts();
        }else {
            results= RealmController.getInstance().query(name);
        }

        contact_list.clear();
        for (ContactItem result : results){
            contact_list.add(result);
        }
        contactAdapter.notifyDataSetChanged();
    }

    private void createDummyItems(){
        for (int i = 0; i < 3; i++){
            contact_list.add(new ContactItem(i, "test" + i));
        }
        list_contact.setAdapter(contactAdapter);
//        contactAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        String err = "";
        int status = response.code();
        if (status == HttpStatus.SC_OK) {
            //if all went fine update
            JsonObject jsonObject = response.body();
            JsonArray user_array = jsonObject.get("contacts").getAsJsonArray();
            contact_list.clear();
            for (int i = 0; i < user_array.size(); i++) {
                ContactItem item = new ContactItem();
                JsonObject userObject = user_array.get(i).getAsJsonObject();
                item.setId(userObject.get("ContactId").getAsInt());
                item.setUsername(userObject.get("Name").getAsString());
                item.setEmail(userObject.get("Email").getAsString());
                contact_list.add(item);
            }
            contactAdapter.notifyDataSetChanged();
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
        AlertDialogManager.showAlertDialog(this, R.string.dialog_contact_title, R.string.action_network_error, R.string.action_confirm);
    }

}
