package scn.com.sipclient;

import android.app.Application;

import org.doubango.ngn.NgnApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import scn.com.sipclient.realm.RealmController;

/**
 * Created by pakjs on 9/3/2016.
 */
public class SIPApplication extends NgnApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        RealmController.with(this);
    }
}
