package huddletable.tableapp;

import huddletable.tableapp.Listeners.MyOnVisibilityChangeListener;
import huddletable.tableapp.Listeners.AddSessionListener;
import huddletable.tableapp.Listeners.TouchImageViewListener;
import huddletable.tableapp.util.Color;
import huddletable.tableapp.util.Position;
import huddletable.tableapp.util.SystemUiHider;
import huddletable.tableapp.util.TouchImageView;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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


    //ID Strings
    public static final String mNumOfDevicesID = "numOfDevices";
    public static final String mZoomID = "zoom";
    public static String mScrollId = "scrollValues";
    public static final String mPositionID = "position";


    private final int TRANSPARENT = 0x00000;
    public static final String DELIM = "/";
    private Color mBackgroundColor;
    private String mSessionName;
    private View controlsView;
    private TouchImageView imageContentView;
    private Position currentPosition;
    private TouchImageViewListener mTouchImageViewListener;
    private PointF currentScrollPosition;
    Position mScrollPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        currentPosition = new Position(0,0);
        controlsView = findViewById(R.id.fullscreen_content_controls);
        imageContentView = (TouchImageView)findViewById(R.id.view_image);
        myFirebaseRef = ((TableApplication)getApplication()).getMyFirebaseRef();
        mSessionName = getIntent().getStringExtra(SessionActivity.SESSION_NAME);
        mTouchImageViewListener = new TouchImageViewListener(imageContentView, this);
        imageContentView.setSessionName(mSessionName);
        mScrollPosition = new Position(0,0);
        /**
         * if the session doesnt exist in firebase make a new session ...
         */
        Firebase ref = new Firebase("https://huddletableapp.firebaseio.com/"+mSessionName);
        AddSessionListener addSessionListener = new AddSessionListener(mSessionName, myFirebaseRef, this);
        ref.addListenerForSingleValueEvent(addSessionListener);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mSystemUiHider = SystemUiHider.getInstance(this, imageContentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        onVisibilityChangeListener = new MyOnVisibilityChangeListener(controlsView, shortAnimTime, mSystemUiHider);
        mSystemUiHider
                .setOnVisibilityChangeListener( onVisibilityChangeListener);


        // Set up the user interaction to manually show or hide the system UI.
        imageContentView.setOnClickListener(mToggleClickListener);

        imageContentView.setOnTouchImageViewListener(mTouchImageViewListener);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    public void setBackgroundColor(Color backgroundColor) {
//        todo: translatingTO - report!
        this.mBackgroundColor = backgroundColor;
        if(backgroundColor == Color.Green) {
            imageContentView.setImageResource(R.drawable.green);
        }
        else if (backgroundColor == Color.Red) {
            imageContentView.setImageResource(R.drawable.red);
        } else {
            imageContentView.setImageResource(R.drawable.blue);
        }
    }
    public void clearBackgroundColor() {
        Bitmap whereiswaldo = decodeSampledBitmapFromResource(getResources(), R.drawable.whereiswaldo,450,450);
        imageContentView.setImageBitmap(whereiswaldo);
        imageContentView.setBackgroundColor(TRANSPARENT);
    }

    /**
     *  Show when a device gets added to a session
     */
    public void createNewDeviceText() {
        Toast.makeText(this, SESSION + mSessionName, Toast.LENGTH_LONG).show();
    }

    /**
     *  Show when a session get created
     */
    public void createNewSessionText() {
        Toast.makeText(this, NEW_SESSION + mSessionName, Toast.LENGTH_LONG).show();
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

    private void addPositionListener() {
        Firebase myFirebaseRef = new Firebase("https://huddletableapp.firebaseio.com/"+mSessionName);
        myFirebaseRef.child(mBackgroundColor.name()).child(mPositionID).addValueEventListener(new ValueEventListener() {
            //todo:scroll!!!
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> values = (Map<String, Object>) dataSnapshot.getValue();
                if(values.get("x").equals(0l) && values.get("y").equals(0l)) {
                    //do nothing
                }else {
                    int x = (int) (long)values.get("x");
                    int y = (int)(long)values.get("y");
                    currentPosition = new Position(x, y);
                    currentScrollPosition = new PointF(x,y);
                    scroll(currentPosition);
                    clearBackgroundColor();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //todo remove
//        clearBackgroundColor();
    }

    private void scroll(Position position) {
        imageContentView.scrollTo(position.getX(), position.getY());
    }



    public void updateZoom(float currentZoom) {
        Firebase myFirebaseRef = new Firebase("https://huddletableapp.firebaseio.com/"+mSessionName);
//        if (currentZoom <= 4f) {
//            currentZoom =4f;
//        }
        String zoom = Float.toString(currentZoom);
        myFirebaseRef.child(mZoomID).setValue(zoom);
    }

    public void addListeners() {
        addPositionListener();
        addZoomListener();
        addScrollListener();
    }

    private void addScrollListener() {
        Firebase myFirebaseRef = new Firebase("https://huddletableapp.firebaseio.com/"+mSessionName);
        myFirebaseRef.child(mScrollId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> scrollValues = (Map<String, String>) dataSnapshot.getValue();
                String dxString = scrollValues.get("x");
                String dyString = scrollValues.get("y");
                int deltaX = 0;
                int deltaY = 0;
                try {
                    deltaX = Integer.parseInt(dxString);
                    deltaY = Integer.parseInt(dyString);

                } catch (Exception e) {}
                mScrollPosition = new Position(/*currentPosition.getX() + */deltaX, /*currentPosition.getY() + */deltaY);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        imageContentView.scrollToValues(mScrollPosition.getX(), mScrollPosition.getY());

    }


    private void addZoomListener() {
        Firebase myFirebaseRef = new Firebase("https://huddletableapp.firebaseio.com/"+mSessionName);
        myFirebaseRef.child(mZoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String zoomDouble = (String)dataSnapshot.getValue();
                float zoom = Float.valueOf(zoomDouble);
                imageContentView.setZoom(zoom);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
