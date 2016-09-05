package scn.com.sipclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import scn.com.sipclient.R;
import scn.com.sipclient.db.DBHelper;
import scn.com.sipclient.main.ContactList;
import scn.com.sipclient.model.ContactItem;
import scn.com.sipclient.model.User;
import scn.com.sipclient.net.APIManager;
import scn.com.sipclient.realm.RealmController;
import scn.com.sipclient.utils.AlertDialogManager;
import scn.com.sipclient.utils.HttpStatus;
import scn.com.sipclient.utils.SessionManager;

/**
 * Created by  star on 6/6/2016.
 */
public class SearchAdapter extends
        RecyclerView.Adapter implements Callback<JsonObject> {

    private List<ContactItem> contact_list;
    private Context context;

    public SearchAdapter(List<ContactItem> contact_list , Context context) {
        this.contact_list = contact_list;
        this.context = context;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.search_row, parent, false);
            return  new SimpleViewHolder(itemLayoutView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int pos = position;
        final SimpleViewHolder VHitem = (SimpleViewHolder) holder;
        VHitem.tvUserName.setText(contact_list.get(pos).getUsername());
        VHitem.tvUserEmail.setText(contact_list.get(pos).getEmail());

        VHitem.contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title("Add Contact")
                        .content("Do you really add this user?")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ContactItem item = new ContactItem();
                                item.setEmail(contact_list.get(pos).getEmail());
                                item.setUsername(contact_list.get(pos).getUsername());
                                add_contact(item);


                            }
                        })
                        .negativeText("Cancel")
                        .show();
            }
        });
    }

    private void add_contact(ContactItem item){

        SessionManager session = new SessionManager(context);
        User user = session.getUserDetails();

        Call<JsonObject> call = APIManager.getServiceAPI().add_contact(user.getUserId(), item.getEmail(), item.getUsername());
        //asynchronous call
        call.enqueue(this);
    }


    @Override
    public int getItemCount() {
        return contact_list.size();
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        String err = "";
        int status = response.code();
        if (status == HttpStatus.SC_OK) {
            JsonObject jsonObject = response.body();
            ContactItem user = new ContactItem();
            user.setId(jsonObject.get("ContactId").getAsInt());
            user.setUsername(jsonObject.get("Name").getAsString());
            user.setEmail(jsonObject.get("Email").getAsString());
            RealmController.getInstance().addContact(user);
            context.startActivity(new Intent(context, ContactList.class));
        }

        if (status == HttpStatus.SC_BAD_REQUEST) {
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response.errorBody().string(), JsonObject.class);
                err = jsonObject.get("Message").getAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(context, err, Toast.LENGTH_LONG).show();
        }

        if (status == HttpStatus.SC_NOT_FOUND) {
            Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
        }
        if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            AlertDialogManager.showAlertDialog(context, R.string.dialog_contact_title, R.string.action_network_error, R.string.action_confirm);
        }

    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {

    }

//  ViewHolder Class

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout contactLayout;

        TextView tvUserName;
        TextView tvUserEmail;

        ImageView ivPeerImage;


        public SimpleViewHolder(View itemView) {
            super(itemView);
            contactLayout = (LinearLayout)itemView.findViewById(R.id.contactLayout);
            tvUserName = (TextView) itemView.findViewById(R.id.contact_username);
            tvUserEmail = (TextView) itemView.findViewById(R.id.contact_useremail);
            ivPeerImage = (ImageView) itemView.findViewById(R.id.contact_avartar);
        }
    }

}
