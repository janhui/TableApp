package huddletable.tableapp.Listeners;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

/**
 * Created by josekalladanthyil on 28/05/15.
 */
public class PositionListener implements ChildEventListener {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        int x = 0;
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        int x = 0;

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        int x = 0;

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        int x = 0;

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
