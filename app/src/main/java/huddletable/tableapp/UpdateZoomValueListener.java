package huddletable.tableapp;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by josekalladanthyil on 28/05/15.
 */
public class UpdateZoomValueListener implements ValueEventListener {
    private final TableActivity mTableActivity;
    private Firebase myFirebaseRef;
    private String mSessionName;

    public UpdateZoomValueListener(String mSessionName, Firebase myFirebaseRef, TableActivity tableActivity) {
        this.mSessionName = mSessionName;
        this.myFirebaseRef = myFirebaseRef;
        this.mTableActivity = tableActivity;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
