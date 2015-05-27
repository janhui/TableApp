package huddletable.tableapp;

import huddletable.tableapp.Listeners.MyOnVisibilityChangeListener;
import huddletable.tableapp.util.Color;
import huddletable.tableapp.util.Position;
import huddletable.tableapp.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class TableActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    public static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    public static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    /**
     * onVisibilitychange listener to hide the ui bar to give full screen view!
     */
    private MyOnVisibilityChangeListener onVisibilityChangeListener;

    private Firebase myFirebaseRef;
    private final String NEW_SESSION = "You have joined a new session called ";
    private final String SESSION = "You have joined the session called ";
    private final String mNumOfDevicesID = "num_of_devices";
    private final String mSessionNameID = "session_name";
    private Color mBackgroundColor;
    private String mSessionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        myFirebaseRef = ((TableApplication)getApplication()).getMyFirebaseRef();
        contentView.setBackgroundColor(Color.Blue.getColor());
        mSessionName = getIntent().getStringExtra(SessionActivity.SESSION_NAME);

        //TODO: if the session doesnt exist in firebase make a new session ...
        myFirebaseRef.child(mSessionNameID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    myFirebaseRef.child(mSessionNameID).setValue(mSessionName);
                    registerDevice();
                    createNewSessionText();
                } else {
                    registerDevice();
                    createNewDeviceText();
                }
            }

            @Override
            public void onCancelled(FirebaseError arg0) {
            }
        });


        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        onVisibilityChangeListener = new MyOnVisibilityChangeListener(controlsView, shortAnimTime, mSystemUiHider);
        mSystemUiHider
                .setOnVisibilityChangeListener( onVisibilityChangeListener);


        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(mToggleClickListener);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        onVisibilityChangeListener.delayedHide(100);
    }

    /**
     *  finds the number of devices already registered in this session.
     *  if its null then
     *      set the num_of_devices = 0; and get the correct color.
     *  else
     *      gets the current number and sets the position of this device to be 0,0 in
     *      firebase with the next background color
     *      i.e if there are 1 device(red) already next color is green this will be green a
     *          nd have an initial value and position = 0,0
     */
    private void registerDevice() {
        myFirebaseRef.child(mNumOfDevicesID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer numOfDevices = (Integer)dataSnapshot.getValue();
                if(numOfDevices == null) {
                    numOfDevices = 0;
                    myFirebaseRef.child(mNumOfDevicesID).setValue(numOfDevices);
                } else {
                    // if this is not the first device then sets its background color
                    numOfDevices = numOfDevices++ % Color.values().length;
                }
                Map<String, Position> myPosition = new HashMap<String, Position>();
                mBackgroundColor = Color.values()[numOfDevices];
                myPosition.put(mBackgroundColor.name(), new Position(0,0));
                myFirebaseRef.child(mSessionName).setValue(myPosition);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     *  Show when a device gets added to a session
     */
    private void createNewDeviceText() {
        Toast.makeText(this,SESSION+mSessionName, Toast.LENGTH_LONG);
    }

    /**
     *  Show when a session get created
     */
    private void createNewSessionText() {
        Toast.makeText(this,NEW_SESSION+mSessionName, Toast.LENGTH_LONG);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                onVisibilityChangeListener.delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    View.OnClickListener mToggleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (TOGGLE_ON_CLICK) {
                mSystemUiHider.toggle();
            } else {
                mSystemUiHider.show();
            }
        }
    };




}
