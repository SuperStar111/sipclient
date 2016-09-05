package scn.com.sipclient.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import io.realm.RealmResults;
import scn.com.sipclient.R;
import scn.com.sipclient.adapter.FavoriteAdapter;
import scn.com.sipclient.adapter.HistoryAdapter;
import scn.com.sipclient.realm.FavoriteModel;
import scn.com.sipclient.realm.HistoryModel;
import scn.com.sipclient.realm.RealmController;
import scn.com.sipclient.tab.MyTabHostProvider;
import scn.com.sipclient.tab.TabHostProvider;
import scn.com.sipclient.tab.TabView;

public class CallHistory extends AppCompatActivity {

    private TabHostProvider tabProvider;
    private TabView tabView;

    private RecyclerView list_history;

    private ArrayList<HistoryModel> history_list;
    private RecyclerView.Adapter historyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabProvider = new MyTabHostProvider(this);
        tabView = tabProvider.getTabHost("history");

        View currentView = LayoutInflater.from(this).inflate(R.layout.activity_call_history, null);
        list_history = (RecyclerView) currentView.findViewById(R.id.list_history);

        history_list = new ArrayList<>();
        list_history.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(history_list , this);
        list_history.setAdapter(historyAdapter);
        tabView.setCurrentView(currentView);
        setContentView(tabView.render(1));
        history_search();
    }

    private void history_search(){
        RealmResults<HistoryModel> results;
        results = RealmController.getInstance().getAllHitory();
        history_list.clear();
        for (HistoryModel result : results){
            history_list.add(result);
        }
        historyAdapter.notifyDataSetChanged();
    }

}
