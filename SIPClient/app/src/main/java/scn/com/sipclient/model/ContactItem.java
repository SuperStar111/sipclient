package scn.com.sipclient.model;

import io.realm.RealmObject;

/**
 * Created by pakjs on 8/19/2016.
 */
public class ContactItem extends RealmObject {
    private int id;
    private String username;
    private String avartar;
    private String email;

    public ContactItem(){

    }


    public ContactItem(int id, String username){
        this.id = id;
        this.username = username;
    }

    public String getAvartar() {
        return avartar;
    }

    public void setAvartar(String avartar) {
        this.avartar = avartar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
