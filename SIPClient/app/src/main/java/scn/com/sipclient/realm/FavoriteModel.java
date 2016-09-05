package scn.com.sipclient.realm;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by star on 9/4/2016.
 */
public class FavoriteModel extends RealmObject {
    private int favor_id;
    private String Name;
    private String Email;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getFavor_id() {
        return favor_id;
    }

    public void setFavor_id(int favor_id) {
        this.favor_id = favor_id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
