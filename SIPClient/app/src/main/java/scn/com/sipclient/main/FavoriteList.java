package scn.com.sipclient.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import scn.com.sipclient.R;
import scn.com.sipclient.account.LoginActivity;
import scn.com.sipclient.adapter.ContactAdapter;
import scn.com.sipclient.adapter.FavoriteAdapter;
import scn.com.sipclient.model.ContactItem;
import scn.com.sipclient.model.User;
import scn.com.sipclient.net.APIManager;
import scn.com.sipclient.realm.FavoriteModel;
import scn.com.sipclient.realm.RealmController;
import scn.com.sipclient.tab.MyTabHostProvider;
import scn.com.sipclient.tab.TabHostProvider;
import scn.com.sipclient.tab.TabView;
import scn.com.sipclient.utils.AlertDialogManager;
import scn.com.sipclient.utils.HttpStatus;
import scn.com.sipclient.utils.SessionManager;

public class FavoriteList extends AppCompatActivity implements Callback<JsonObject> {
    private TabHostProvider tabProvider;
    private TabView tabView;

    private RecyclerView list_favorite;

    private ArrayList<FavoriteModel> favorite_list;
    private RecyclerView.Adapter favorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabProvider = new MyTabHostProvider(this);
        tabView = tabProvider.getTabHost("favorite");
        View currentView = LayoutInflater.from(this).inflate(R.layout.activity_favorite_list, null);
        list_favorite = (RecyclerView) currentView.findViewById(R.id.list_favorite);

        favorite_list = new ArrayList<>();
        list_favorite.setLayoutManager(new LinearLayoutManager(this));
        favorAdapter = new FavoriteAdapter(favorite_list , this);
        list_favorite.setAdapter(favorAdapter);
        tabView.setCurrentView(currentView);
        setContentView(tabView.render(0));
        favorite_search();

        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager session = new SessionManager(FavoriteList.this);
                session.logoutUser();
                startActivity(new Intent(FavoriteList.this, LoginActivity.class));
            }
        });
    }

    private void favorite_search(){
        RealmResults<FavoriteModel> results;
        results = RealmController.getInstance().getAllFavorites();
        favorite_list.clear();
        for (FavoriteModel result : results){
            favorite_list.add(result);
        }
        favorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        String err = "";
        int status = response.code();
        if (status == HttpStatus.SC_OK) {
            //if all went fine update
            JsonObject jsonObject = response.body();
            JsonArray user_array = jsonObject.get("favors").getAsJsonArray();
            favorite_list.clear();
            for (int i = 0; i < user_array.size(); i++) {
                FavoriteModel item = new FavoriteModel();
                JsonObject userObject = user_array.get(i).getAsJsonObject();
                item.setFavor_id(userObject.get("FavorId").getAsInt());
                item.setName(userObject.get("Name").getAsString());
                item.setEmail(userObject.get("Email").getAsString());
                favorite_list.add(item);
            }
            if (favorite_list.isEmpty()){
                findViewById(R.id.tv_favor_status).setVisibility(View.VISIBLE);
            }else {
                findViewById(R.id.tv_favor_status).setVisibility(View.GONE);
            }
            favorAdapter.notifyDataSetChanged();
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
