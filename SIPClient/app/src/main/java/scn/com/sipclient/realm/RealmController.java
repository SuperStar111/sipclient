package scn.com.sipclient.realm;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import io.realm.Realm;
import io.realm.RealmResults;
import scn.com.sipclient.model.ContactItem;

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

//        realm.refresh();
        realm.setAutoRefresh(true);
    }

    //clear all objects from Book.class
    public void clearAll() {
        realm.beginTransaction();
//        realm.clear(Contacts.class);
        realm.delete(ContactItem.class);
        realm.commitTransaction();
    }

    //find all objects in the Book.class
    public RealmResults<HistoryModel> getAllHitory() {
        return realm.where(HistoryModel.class).findAllSorted("HistoryId");
    }
    public void addHistory(HistoryModel historyModel) {
        realm.beginTransaction();
        realm.copyToRealm(historyModel);
        realm.commitTransaction();
    }

    //query a single item with the given id
    public HistoryModel getHistory(int id) {

        return realm.where(HistoryModel.class).equalTo("HistoryId", id).findFirst();
    }

    public HistoryModel getHistory(String email) {

        return realm.where(HistoryModel.class).equalTo("CallerEmail", email).findFirst();
    }

    public void deleteHistory(HistoryModel historyModel) {
        if (historyModel != null) {
            realm.beginTransaction();
            historyModel.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    public void deleteHistory(int id) {
        HistoryModel historyModel = getHistory(id);
        deleteHistory(historyModel);
    }


    public void deleteHistory(String email) {
        HistoryModel historyModel = getHistory(email);
        deleteHistory(historyModel);
    }

    //find all objects in the Book.class
    public RealmResults<FavoriteModel> getAllFavorites() {
        return realm.where(FavoriteModel.class).findAllSorted("favor_id");
    }

    //query a single item with the given id
    public FavoriteModel getFavorite(int id) {

        return realm.where(FavoriteModel.class).equalTo("favor_id", id).findFirst();
    }

    public FavoriteModel getFavorite(String email) {
        return realm.where(FavoriteModel.class).equalTo("Email", email).findFirst();
    }

    public void deleteFavorite(FavoriteModel contactModel) {
        if (contactModel != null) {
            realm.beginTransaction();
            contactModel.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    public void deleteFavorite(int id) {
        FavoriteModel contactModel = getFavorite(id);
        deleteFavorite(contactModel);
    }


    public void deleteFavorite(String email) {
        FavoriteModel contactModel = getFavorite(email);
        deleteFavorite(contactModel);
    }

    public void addFavorite(FavoriteModel contactModel) {
        realm.beginTransaction();
        realm.copyToRealm(contactModel);
        realm.commitTransaction();
    }

    public void updateFavorite(FavoriteModel newModel) {
        FavoriteModel contactModel = getFavorite(newModel.getFavor_id());
        updateFavorite(contactModel, newModel);
    }

    public void updateFavorite(FavoriteModel originModel, FavoriteModel newModel) {
        realm.beginTransaction();
//        originModel.setEmail(newModel.getEmail());
        originModel.setName(newModel.getName());
        realm.commitTransaction();
    }

    //check if Book.class is empty
    public boolean hasFavorites() {
        return !realm.where(FavoriteModel.class).findAll().isEmpty();
    }

    //query example
    public RealmResults<FavoriteModel> queryWithFavorite(String name) {

        return realm.where(FavoriteModel.class)
                .contains("Name", name)
                .findAll();
    }


    //find all objects in the Book.class
    public RealmResults<ContactItem> getAllContacts() {
        return realm.where(ContactItem.class).findAllSorted("id");
    }

    //query a single item with the given id
    public ContactItem getContact(int id) {

        return realm.where(ContactItem.class).equalTo("id", id).findFirst();
    }

    public void deleteContact(ContactItem contactModel) {
        if (contactModel != null) {
            if (getFavorite(contactModel.getEmail()) != null){
                deleteFavorite(contactModel.getEmail());
            }
            realm.beginTransaction();
            contactModel.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    public void deleteContact(int id) {
        ContactItem contactModel = getContact(id);
        deleteContact(contactModel);
    }

    public void addContact(ContactItem contactModel) {
        realm.beginTransaction();
        realm.copyToRealm(contactModel);
        realm.commitTransaction();
    }

    public void updateContact(ContactItem newModel) {
        ContactItem contactModel = getContact(newModel.getId());
        updateContact(contactModel, newModel);
    }

    public void updateContact(ContactItem originModel, ContactItem newModel) {
        realm.beginTransaction();
//        originModel.setEmail(newModel.getEmail());
        originModel.setUsername(newModel.getUsername());
        realm.commitTransaction();
    }

    //check if Book.class is empty
    public boolean hasContacts() {
        return !realm.where(ContactItem.class).findAll().isEmpty();
    }

    //query example
    public RealmResults<ContactItem> query(String name) {

        return realm.where(ContactItem.class)
                .contains("username", name)
                .findAll();
    }
}