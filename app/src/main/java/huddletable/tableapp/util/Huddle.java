package huddletable.tableapp.util;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huddletable.tableapp.TableActivity;

/**
 * Created by josekalladanthyil on 19/06/15.
 */
public class Huddle {
    float zoom;
    int numOfDevices;
    Position currentPosition;
    String mSessionName;
    Color mBackgroundColor;


    private Huddle(){

    }
    float getZoom() {
        Firebase myFirebaseRef = new Firebase("https://huddletableapp.firebaseio.com/"+mSessionName);
        myFirebaseRef.child(TableActivity.mZoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String zoomDouble = (String)dataSnapshot.getValue();
                zoom = Float.valueOf(zoomDouble);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return zoom;
    }


    int getNumberOfDevices(){
        Firebase ref = new Firebase("https://huddletableapp.firebaseio.com/"+mSessionName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                float zoom = 4f;
                if (value == null) {
                    numOfDevices = 0;
                } else {
                    Map<String, Long> numOfDevicesMap = (Map<String, Long>)dataSnapshot.getValue();
                    numOfDevices = (int)(long)numOfDevicesMap.get(TableActivity.mNumOfDevicesID);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return numOfDevices;
    }

    Position getPosition() {

        Firebase myFirebaseRef = new Firebase("https://huddletableapp.firebaseio.com/"+mSessionName);
        myFirebaseRef.child(mBackgroundColor.name()).child(TableActivity.mPositionID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> values = (Map<String, Object>) dataSnapshot.getValue();
                if(values.get("x").equals(0l) && values.get("y").equals(0l)) {
                    //do nothing
                }else {
                    int x = (int) (long)values.get("x");
                    int y = (int)(long)values.get("y");
                    currentPosition = new Position(x, y);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return currentPosition;
    }
//    todo:
    List<Position> getAllPosition() {
        return null;
    }

    void setZoom(float zoomValue) {
        Firebase myFirebaseRef = new Firebase("https://huddletableapp.firebaseio.com/"+mSessionName);
        String zoom = Float.toString(zoomValue);
        myFirebaseRef.child(TableActivity.mZoomID).setValue(zoom);
    }
    void setPosition(final Position position) {
        final Firebase myFirebaseRef = new Firebase("https://huddletableapp.firebaseio.com/"+mSessionName);;
        Firebase ref = myFirebaseRef.child(mBackgroundColor.toString());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Position> dataPosition = new HashMap<>();
                dataPosition.put(TableActivity.mPositionID, position);
                myFirebaseRef.child(mSessionName).child(mBackgroundColor.toString()).setValue(dataPosition);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
