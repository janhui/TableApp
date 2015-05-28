package huddletable.tableapp.Listeners;

import android.util.Log;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import huddletable.tableapp.TableActivity;
import huddletable.tableapp.util.Color;
import huddletable.tableapp.util.Position;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by josekalladanthyil on 28/05/15.
 *
 *
 *  finds the number of devices already registered in this session.
 *  if its null then
 *      set the num_of_devices = 0; and get the correct color.
 *  else
 *      gets the current number and sets the position of this device to be 0,0 in
 *      firebase with the next background color
 *      i.e if there are 1 device(red) already next color is green this will be green a
 *          nd have an initial value and position = 0,0
 */
public class AddSessionListener implements ValueEventListener {
    private final String mSessionName;
    private Firebase myFirebaseRef;
    private Color mBackgroundColor;
    private TableActivity mTableActivity;

    public AddSessionListener(String mSessionName, Firebase myFirebaseRef, TableActivity tableActivity) {
        this.mSessionName = mSessionName;
        this.myFirebaseRef = myFirebaseRef;
        this.mTableActivity = tableActivity;
    }
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Object value = dataSnapshot.getValue();
        int numOfDevices;
        if (value == null) {
            numOfDevices = 0;
            mBackgroundColor = Color.values()[numOfDevices];
            mTableActivity.createNewSessionText();
        } else {
            Map<String, Long> numOfDevicesMap = (Map<String, Long>)dataSnapshot.getValue();
            numOfDevices = (int)(long)numOfDevicesMap.get(TableActivity.mNumOfDevicesID);
            mBackgroundColor = Color.values()[numOfDevices++ % Color.size()];
            mTableActivity.createNewDeviceText();
        }
        Map<String, Object> initialMap = new HashMap<>();
        Map<String, Object> positionMap = new HashMap<>();
        Position initialPosition = new Position(0,0);
        positionMap.put(TableActivity.mPositionID, initialPosition);
        initialMap.put(TableActivity.mNumOfDevicesID, numOfDevices);
        initialMap.put(mBackgroundColor.name(), positionMap);
//        myFirebaseRef.child(TableActivity.mSessionNameID).child(mSessionName).setValue(initialMap);
        myFirebaseRef.child(mSessionName).setValue(initialMap);
        mTableActivity.setBackgroundColor(mBackgroundColor);
        mTableActivity.addPositionListener();
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    public Color getBackgroundColor() {
        return mBackgroundColor;
    }
}