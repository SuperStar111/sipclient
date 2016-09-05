package scn.com.sipclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import scn.com.sipclient.R;
import scn.com.sipclient.call.CallingActivity;
import scn.com.sipclient.main.EditContactActivity;
import scn.com.sipclient.model.ContactItem;
import scn.com.sipclient.model.User;
import scn.com.sipclient.net.APIManager;
import scn.com.sipclient.realm.FavoriteModel;
import scn.com.sipclient.realm.RealmController;
import scn.com.sipclient.utils.AlertDialogManager;
import scn.com.sipclient.utils.HttpStatus;
import scn.com.sipclient.utils.SessionManager;

/**
 * Created by  star on 6/6/2016.
 */
public class FavoriteAdapter extends
        RecyclerView.Adapter{

    private List<FavoriteModel> favorite_list;
    private Context context;

    private ArrayList<String> items;

    public FavoriteAdapter(List<FavoriteModel> favorite_list , Context context) {
        this.favorite_list = favorite_list;
        this.context = context;
        items = new ArrayList<>();
        items.add("Delete");
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.contact_row, parent, false);
            return  new SimpleViewHolder(itemLayoutView, context);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int pos = position;
        final SimpleViewHolder VHitem = (SimpleViewHolder) holder;
        VHitem.tvUserName.setText(favorite_list.get(pos).getName());

        VHitem.contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CallingActivity.class);
                intent.putExtra("CALL_TO", favorite_list.get(pos).getEmail());
                context.startActivity(intent);
            }
        });

        VHitem.contactLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialDialog.Builder(context)
                        .items(items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which){
                                    case 0:
                                        delete_favorite(favorite_list.get(pos).getFavor_id());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
                return false;
            }
        });

    }

    private void delete_favorite(int favorId){
        SessionManager session = new SessionManager(context);
        User user = session.getUserDetails();

        Call<JsonObject> call = APIManager.getServiceAPI().delete_favorite(user.getUserId() , favorId);
        //asynchronous call
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String err = "";
                int status = response.code();
                if (status == HttpStatus.SC_OK) {
                    //if all went fine update
                    JsonObject jsonObject = response.body();
                    int id = jsonObject.get("FavorId").getAsInt();

                    for (int i = 0; i < favorite_list.size(); i++){
                        if (favorite_list.get(i).getFavor_id() == id){
                            favorite_list.remove(i);
                        }
                    }
                    RealmController.getInstance().deleteFavorite(id);
                    Toast.makeText(context, "Deleted successfuly", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }

                if (status == HttpStatus.SC_BAD_REQUEST) {
                    try {
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(response.errorBody().string(), JsonObject.class);
                        err = jsonObject.get("Message").getAsString();
                    } catch (Exception e) {

                    }
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
                }

                if (status == HttpStatus.SC_NOT_FOUND) {
                    Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                AlertDialogManager.showAlertDialog(context, R.string.dialog_contact_title, R.string.action_network_error, R.string.action_confirm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorite_list.size();
    }



//  ViewHolder Class

    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
        LinearLayout contactLayout;

        TextView tvUserName;

        ImageView ivPeerImage;

        Context m_context;
        private static final int MENU_EDIT = 0;
        private static final int MENU_DELETE = 1;
        private static final int MENU_FAVORITE = 2;
        public SimpleViewHolder(View itemView, Context context) {
            super(itemView);
            this.m_context = context;
            contactLayout = (LinearLayout)itemView.findViewById(R.id.contactLayout);
            tvUserName = (TextView) itemView.findViewById(R.id.contact_username);
            ivPeerImage = (ImageView) itemView.findViewById(R.id.contact_avartar);
        }
    }

}
