package de.questor.poc.jsarch.renderer.compass;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.hardware.SensorListener;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

import de.questor.poc.jsarch.GeoUtils;
import de.questor.poc.jsarch.renderer.OnChoiceListener;



public class CompassMultiView extends View implements SensorListener /*SensorEventListener*/, LocationListener, StationView {

	private Context mContext;
	private List<OnChoiceListener> onChoiceListeners = new ArrayList<OnChoiceListener>();

	// If the distance to a poi is less than DISTANCE_TO_REACH_POI (in km), we
	// jump to the next station:
	private static final double DISTANCE_TO_REACH_POI = 0.01;
	private static final long RETAIN_GPS_MILLIS = 10000L;
	private static final String AUDIO_NAME_SCANSOUND = "scansound";
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	private String compassMode = "poiTeamConquest";

	private Paint mGridPaint;
	private Paint mTargetPaint;
	private Paint mErasePaint;
	private float mOrientation;
	private int mLastScale = -1;
	private String[] mDistanceScale = new String[4];
	private static float KM_PER_METERS = 0.001f;
	private static float METERS_PER_KM = 1000f;

	private SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	private int soundId;
	private boolean soundStarted = false;
	private String pathToSound;
	private float soundVol = (float) 1.0;

	private int colRings = 0xFF00FF00;
	private int colSweep = 0xFF33FF33;
	private int colTarget = 0xFF00FF00;
	private int colText = 0xFF00FF00;
	private GeoPoint currentLocationPoint = null;
	private Location currentBestLocation = null;
	

	/**
	 * These are the list of choices for the radius of the outer circle on the
	 * screen when using metric units. All items are in kilometers. This array
	 * is used to choose the scale of the compass display.
	 */
	private static double mMetricScaleChoices[] = { 100 * KM_PER_METERS, 200 * KM_PER_METERS, 400 * KM_PER_METERS, 1,
			2, 4, 8, 20, 40, 100, 200, 400, 1000, 2000, 4000, 10000, 20000, 40000, 80000 };

	/**
	 * Once the scale is chosen, this array is used to convert the number of
	 * kilometers on the screen to an integer. (Note that for short distances we
	 * use meters, so we multiply the distance by {@link #METERS_PER_KM}. (This
	 * array is for metric measurements.)
	 */
	private static float mMetricDisplayUnitsPerKm[] = { METERS_PER_KM, METERS_PER_KM, METERS_PER_KM, METERS_PER_KM,
			METERS_PER_KM, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f };

	/**
	 * This array holds the formatting string used to display the distance to
	 * the target. (This array is for metric measurements.)
	 */
	private static String mMetricDisplayFormats[] = { "%.0fm", "%.0fm", "%.0fm", "%.0fm", "%.0fm", "%.1fkm", "%.1fkm",
			"%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm",
			"%.0fkm", "%.0fkm" };

	/**
	 * This array holds the formatting string used to display the distance on
	 * each ring of the compass screen. (This array is for metric measurements.)
	 */
	private static String mMetricScaleFormats[] = { "%.0fm", "%.0fm", "%.0fm", "%.0fm", "%.0fm", "%.0fkm", "%.0fkm",
			"%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm",
			"%.0fkm", "%.0fkm", "%.0fkm" };

	private static float KM_PER_YARDS = 0.0009144f;
	private static float KM_PER_MILES = 1.609344f;
	private static float YARDS_PER_KM = 1093.6133f;
	private static float MILES_PER_KM = 0.621371192f;

	/**
	 * These are the list of choices for the radius of the outer circle on the
	 * screen when using standard units. All items are in kilometers. This array
	 * is used to choose the scale of the compass display.
	 */
	private static double mEnglishScaleChoices[] = { 100 * KM_PER_YARDS, 200 * KM_PER_YARDS, 400 * KM_PER_YARDS,
			1000 * KM_PER_YARDS, 1 * KM_PER_MILES, 2 * KM_PER_MILES, 4 * KM_PER_MILES, 8 * KM_PER_MILES,
			20 * KM_PER_MILES, 40 * KM_PER_MILES, 100 * KM_PER_MILES, 200 * KM_PER_MILES, 400 * KM_PER_MILES,
			1000 * KM_PER_MILES, 2000 * KM_PER_MILES, 4000 * KM_PER_MILES, 10000 * KM_PER_MILES, 20000 * KM_PER_MILES,
			40000 * KM_PER_MILES, 80000 * KM_PER_MILES };

	/**
	 * Once the scale is chosen, this array is used to convert the number of
	 * kilometers on the screen to an integer. (Note that for short distances we
	 * use meters, so we multiply the distance by {@link #YARDS_PER_KM}. (This
	 * array is for standard measurements.)
	 */
	private static float mEnglishDisplayUnitsPerKm[] = { YARDS_PER_KM, YARDS_PER_KM, YARDS_PER_KM, YARDS_PER_KM,
			MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM,
			MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM,
			MILES_PER_KM, MILES_PER_KM };

	/**
	 * This array holds the formatting string used to display the distance to
	 * the target. (This array is for standard measurements.)
	 */
	private static String mEnglishDisplayFormats[] = { "%.0fyd", "%.0fyd", "%.0fyd", "%.0fyd", "%.1fmi", "%.1fmi",
			"%.1fmi", "%.1fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi",
			"%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi" };

	/**
	 * This array holds the formatting string used to display the distance on
	 * each ring of the compass screen. (This array is for standard
	 * measurements.)
	 */
	private static String mEnglishScaleFormats[] = { "%.0fyd", "%.0fyd", "%.0fyd", "%.0fyd", "%.2fmi", "%.1fmi",
			"%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi",
			"%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi" };

	/**
	 * True when we have know our own location
	 */
	private boolean mHaveLocation = false;

	/**
	 * The view that will display the distance text
	 */
	private TextView mDistanceView;

	/**
	 * Ratio of the distance to the target to the radius of the outermost ring
	 * on the compass screen
	 */
	private float mDistanceRatio;

	/**
	 * Utility rect for calculating the ring labels
	 */
	private Rect mTextBounds = new Rect();

	/**
	 * The bitmap used to draw the target
	 */
	// private Bitmap mBlip;

	/**
	 * Used to draw the animated ring that sweeps out from the center
	 */
	private Paint mSweepPaint0;

	/**
	 * Used to draw the animated ring that sweeps out from the center
	 */
	private Paint mSweepPaint1;

	/**
	 * Used to draw the animated ring that sweeps out from the center
	 */
	private Paint mSweepPaint2;

	/**
	 * Time in millis when the most recent sweep began
	 */
	private long mSweepTime;

	/**
	 * True if the sweep has not yet intersected the blip
	 */
	private boolean mSweepBefore;

	/**
	 * Time in millis when the sweep last crossed the blip
	 */
	private long mBlipTime;

	/**
	 * True if the display should use metric units; false if the display should
	 * use standard units
	 */
	private boolean mUseMetric = true;

	/**
	 * Time in millis for the last time GPS reported a location
	 */
	private long mLastGpsFixTime = 0L;

	/**
	 * The last location reported by the network provider. Use this if we can't
	 * get a location from GPS
	 */
	private Location mNetworkLocation;

	/**
	 * True if GPS is reporting a location
	 */
	private boolean mGpsAvailable;

	/**
	 * True if the network provider is reporting a location
	 */
	private boolean mNetworkAvailable;
	
	// Counters for the SOS Cheat:
	private int cheatCountS1 = 0;
	private int cheatCountO = 0;
	private int cheatCountS2 = 0;
	
	
	private String errorMessage = "";
	
	// New class to define a target:
	private class CompassTarget {
		public int type; // 1 = poi, 2 = serverTarget
		public String id;
		public GeoPoint point;
		public int color;
		public double distance, bearing;
		public boolean isActive;
		
		public CompassTarget(int pType, String pId, GeoPoint pPoint, int pColor) {
			type = pType;
			id = pId;
			point = pPoint;
			color = pColor;
			distance = 999999;
			bearing = 0;
			isActive = true;
		}
		
		public CompassTarget(int pType, String pId, int pLatitudeE6, int pLongitudeE6, int pColor) {
			this(pType, pId, new GeoPoint(pLatitudeE6, pLongitudeE6), pColor);
		}
	}
	
	// To display more than one target, we use a list of the targets:
	private List<CompassTarget> targetList = new ArrayList<CompassTarget>();
	
	private double minDistance, minLastDistance;
	private double maxDistance, maxLastDistance;
	private boolean distanceHasChanged = false;
	
	// Functions to update the position and the color of the targets:
	public void updateTarget (int pType, String pId, GeoPoint pPoint, int pColor) {
		
		//Log.i("klaus", "in updateTarget " + pId);
		
		// We try to find the target in the list and update its values:
		boolean targetFound = false;
		for (CompassTarget target : targetList) {
			if (target.id.equals(pId)) {
				targetFound = true;
				target.point = pPoint;
				target.color = pColor;
				target.isActive = true;
				//Log.i("klaus", "FOUND, update! " + pId);
				break;
			}
		}
		// If we couldn't find the target, we add it to the list:
		if (!targetFound) {
			//Log.i("klaus", "NOT found, insert! " + pId);
			this.targetList.add(new CompassTarget(pType, pId, pPoint, pColor));
		}
	}
	
	public void updateTarget (int pType, String pId, int pLatitudeE6, int pLongitudeE6, int pColor) {
		updateTarget (pType, pId, new GeoPoint(pLatitudeE6, pLongitudeE6), pColor);
	}

	
	// A function to deactivate the target with the id pId:
	public void deactivateTarget (int pId) {
		for (CompassTarget target : targetList) {
			if (target.id.equals(pId)) {
				target.isActive = false;
				break;
			}
		}
	}
	
	public void deactivateAllTargets (int pType) {
		for (CompassTarget target : targetList) {
			if (target.type == pType) target.isActive = false;
		}
	}
	
	
	// A function to remove the target with the id pId:
	public void removeTarget (int pId) {
		for (CompassTarget target : targetList) {
			if (target.id.equals(pId)) {
				targetList.remove(target);
				break;
			}
		}
	}
	
	public void removeAllTargets () {
		targetList.clear();
	}
	
	
	// This function returns true, if all POIs have a Player nearby (used for poiTeamConquest only)
	public boolean allPoisHaveAPlayerNearby () {
		boolean thisPoiHasOne;
		for (CompassTarget poi: targetList) {
			if (poi.type == 1) {
				thisPoiHasOne = false;
				for (CompassTarget player: targetList) {
					if ((player.type == 2) && (player.isActive)) {
						if (GeoUtils.calcDistanceSimple(poi.point, player.point) < DISTANCE_TO_REACH_POI) {
							thisPoiHasOne = true;
							break;
						}
					}
				}
				// we also have to check, if eventually this player is near this poi:
				if (GeoUtils.calcDistanceSimple(poi.point, currentLocationPoint) < DISTANCE_TO_REACH_POI) 
					thisPoiHasOne = true;

				if (!thisPoiHasOne) return false;
			}
		}
		return true;
	}
	
	// This function returns true, if you are nearby one of the pois (used for poiTeamConquest only)
	public boolean youAreNearAPoi () {
		for (CompassTarget poi: targetList) {
			if (poi.type == 1) {
				if (GeoUtils.calcDistanceSimple(poi.point, currentLocationPoint) < DISTANCE_TO_REACH_POI) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	// A function, to calculate the distance and the bearing of all targets relativ to a given location.
	// It also finds the minimum and maximum distances and stores these values in minDistance and maxDistance
	public void calcDistanceAndBearingOfTargets (double pLat, double pLon) {
		
		//Log.i("klaus", "calcDistanceAndBearingOfTargets: " + ((Integer)targetList.size()).toString());
		for (CompassTarget target : targetList) {
			target.distance = GeoUtils.distanceKm(pLat, pLon, target.point.getLatitudeE6() / (double)1000000, target.point.getLongitudeE6() / (double)1000000);
			target.bearing = GeoUtils.bearing(pLat, pLon, target.point.getLatitudeE6() / (double)1000000, target.point.getLongitudeE6() / (double)1000000);
		}
		// Find the min and max distances:
		minLastDistance = minDistance;
		maxLastDistance = maxDistance;
		
		minDistance = 1000000;
		maxDistance = -1;
		for (CompassTarget target : targetList) {
			if (target.isActive) {
				// if we are in poiTeamConquest mode, we only care for pois (not for other players) when we calculate the minDistance:
				if ( ((compassMode.equals("poiTeamConquest")) && (target.type == 1)) || !(compassMode.equals("poiTeamConquest"))   ) {
					if (target.distance < minDistance) minDistance = target.distance; 
				}
				// For the maxDistance we care for everything (pois and players), because this determines the scale of our compass
				if (target.distance > maxDistance) maxDistance = target.distance;
			}
		}
		
		if ( (minDistance != minLastDistance) || (maxDistance != maxLastDistance) ) {
			distanceHasChanged = true;
		}
		
		checkForTeamConquestSuccess();		
		
	}
	
	// Same like above, but it calculates the distances to all targets relativ to "currentLocationPoint": 
	public void calcDistanceAndBearingOfTargets () {
		if (currentLocationPoint != null) {
			calcDistanceAndBearingOfTargets(currentLocationPoint.getLatitudeE6() / (double)1000000, currentLocationPoint.getLongitudeE6() / (double)1000000);
		}
	}

	
	
	// This function checks, if a poi-team-conquest may come to a success.
	// If yes, we go to the next questor-station...
	public void checkForTeamConquestSuccess () {
		 if (compassMode.equals("poiTeamConquest")) {
			//Log.i("klaus", "poiTeamConquest");
			//if (allPoisHaveAPlayerNearby()) Log.i("klaus", "allPoisHaveAPlayerNearby");
			//if (youAreNearAPoi()) Log.i("klaus", "youAreNearAPoi");
			
			if ( youAreNearAPoi() && allPoisHaveAPlayerNearby()) {
				// we take the number for the next questor-station simply from the first poi:
				/*
				Poi p = (mStation.getPois()).get(0);
				for (OnChoiceListener l : onChoiceListeners) {
					l.onChoice(new Choice(p.getTarget()));
				}
				*/
			}
		}
	}
	

	// Here is Cheat, to leave the compass to next station.
	// Tap on the compass: one long (to reset) and then 3 short, 3 long, 3 short (SOS)
	
	private void resetCheatCounter() {
		cheatCountS1 = 0;
		cheatCountO = 0;
		cheatCountS2 = 0;
	}
	
	private OnLongClickListener myCheatListenerLong = new OnLongClickListener() {
	    public boolean onLongClick(View v) {
	    	if (cheatCountS1 != 3) 
	    		resetCheatCounter();
	    	else 
	    		cheatCountO++;
	    	
	    	if (cheatCountO > 3) 
	    		resetCheatCounter();

	    	//Log.i("klaus","Cheat! Long! " + ((Integer)cheatCountS1).toString() + ((Integer)cheatCountO).toString() + ((Integer)cheatCountS2).toString());
	    	return true;
	    }
	};	
	
	private OnClickListener myCheatListenerShort = new OnClickListener() {
	    public void onClick(View v) {
	    	if (cheatCountO == 3) 
	    		cheatCountS2++;
	    	else 
	    		cheatCountS1++;
	    	
	    	if ((cheatCountS1 > 3) || (cheatCountS2 > 3) )
	    		resetCheatCounter();

	    	if ((cheatCountS1 == 3) && (cheatCountO == 3) && (cheatCountS2 == 3) ) {
		    	//Log.i("klaus","Cheat! *****************");
	    		/*
				Poi p = (mStation.getPois()).get(0);
				for (OnChoiceListener l : onChoiceListeners) {
					l.onChoice(new Choice(p.getTarget()));
				}
				*/
	    		
	    	}

	    	//Log.i("klaus","Cheat! Short! " + ((Integer)cheatCountS1).toString() + ((Integer)cheatCountO).toString() + ((Integer)cheatCountS2).toString());
	    }
	};	
	
	
	
	
	// The constructors:
	public CompassMultiView(Context context) {
		this(context, null);
	}

	public CompassMultiView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CompassMultiView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		// Paint used for the rings and the small distance test
		mGridPaint = new Paint();
		mGridPaint.setColor(colRings);
		mGridPaint.setAntiAlias(true);
		mGridPaint.setStyle(Style.STROKE);
		mGridPaint.setStrokeWidth(1.0f);
		mGridPaint.setTextSize(10.0f);
		mGridPaint.setTextAlign(Align.CENTER);

		mTargetPaint = new Paint();
		mTargetPaint.setColor(colTarget);
		mTargetPaint.setAntiAlias(true);
		mTargetPaint.setStyle(Style.FILL);

		// Paint used to erase the rectangle behind the ring text
		mErasePaint = new Paint();
		mErasePaint.setColor(0xFF191919);
		mErasePaint.setStyle(Style.FILL);

		// Outer ring of the sweep
		mSweepPaint0 = new Paint();
		// mSweepPaint0.setColor(0xFF33FF33);
		mSweepPaint0.setColor(colSweep);
		mSweepPaint0.setAntiAlias(true);
		mSweepPaint0.setStyle(Style.STROKE);
		mSweepPaint0.setStrokeWidth(2f);

		// Middle ring of the sweep
		mSweepPaint1 = new Paint();
		// mSweepPaint1.setColor(0x7733FF33);
		mSweepPaint1.setColor(colSweep);
		mSweepPaint1.setAntiAlias(true);
		mSweepPaint1.setStyle(Style.STROKE);
		mSweepPaint1.setStrokeWidth(2f);

		// Inner ring of the sweep
		mSweepPaint2 = new Paint();
		// mSweepPaint2.setColor(0x3333FF33);
		mSweepPaint2.setColor(colSweep);
		mSweepPaint2.setAntiAlias(true);
		mSweepPaint2.setStyle(Style.STROKE);
		mSweepPaint2.setStrokeWidth(2f);

		// mBlip = ((BitmapDrawable)
		// getResources().getDrawable(R.drawable.blip)).getBitmap();
		
	    // Cheat Listener:
		this.setOnLongClickListener(myCheatListenerLong);
		this.setOnClickListener(myCheatListenerShort);
		
	}
	
	
	/*
	public void initForStation(CompassStation station) {
		mStation = station;
		
		setColRings(mStation.getColRingsAsInt());
		setColSweep(mStation.getColSweepAsInt());
		setColTarget(mStation.getColTargetAsInt());
		setColText(mStation.getColTextAsInt());

		mGridPaint.setColor(colRings);
		mTargetPaint.setColor(colTarget);

		// if the Grid is hidden (alpha = 0), we should not erase the rectangle
		// behind the text on the grid:
		if ((colRings & 0xFF000000) != 0)
			mErasePaint.setColor(0xFF191919);
		else
			mErasePaint.setColor(0x00191919);

		mSweepPaint0.setColor(colSweep);
		mSweepPaint1.setColor(colSweep);
		mSweepPaint2.setColor(colSweep);
		mDistanceView.setTextColor(colText);

		// get the path to the soundfile from the station
		pathToSound = null;
		for (Media m : ((CompassStation) mStation).getMultimedia()) {
			if (AUDIO_NAME_SCANSOUND.equals(m.getName())) {
				setPathToSound(m.getSource());
				break;
			}
		}

		if (pathToSound != null) {
			try {
				soundId = soundPool.load(Questor.getCurrentGame().getCurrentQSR().getFileDescriptor(pathToSound), 0,
						Long.MAX_VALUE, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// if no sound-file is give in the story.xml, we use the standard
			// sound:
			soundId = soundPool.load(mContext, R.raw.ping, 1);
		}

		setSoundVol(mStation.getSoundVolAsFloat());

		// At last we show the intro dialog:
		if (mStation.getContent().get(0).getQhtml().length() > 2)
			new HTMLDialog(this.getContext(), mStation.getContent().get(0).getQhtml());

	}
	*/

	/**
	 * Sets the view that we will use to report distance
	 * 
	 * @param t
	 *            The text view used to report distance
	 */
	public void setDistanceView(TextView t) {
		mDistanceView = t;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (distanceHasChanged) {
			updateDistance(maxDistance);
			distanceHasChanged = false;
		}
		
		int center = getWidth() / 2;
		int radius = center - 8;
		
		// Draw the rings
		final Paint gridPaint = mGridPaint;
		canvas.drawCircle(center, center, radius, gridPaint);
		canvas.drawCircle(center, center, radius * 3 / 4, gridPaint);
		canvas.drawCircle(center, center, radius >> 1, gridPaint);
		canvas.drawCircle(center, center, radius >> 2, gridPaint);
		
		int blipRadius = (int) (mDistanceRatio * radius);

		final long now = SystemClock.uptimeMillis();
		if (mSweepTime > 0 && mHaveLocation) {
			// Draw the sweep. Radius is determined by how long ago it started
			long sweepDifference = now - mSweepTime;
			if (sweepDifference < 512L) {

				int sweepRadius = (int) (((radius + 6) * sweepDifference) >> 9);
				canvas.drawCircle(center, center, sweepRadius, mSweepPaint0);
				canvas.drawCircle(center, center, sweepRadius - 2, mSweepPaint1);
				canvas.drawCircle(center, center, sweepRadius - 4, mSweepPaint2);

				if (!soundStarted && sweepRadius > 20) {
					soundPool.play(soundId, soundVol, soundVol, 1, 0, 1);
					soundStarted = true;
				}

				// Note when the sweep has passed the blip
				boolean before = sweepRadius < blipRadius;
				if (!before && mSweepBefore) {
					mSweepBefore = false;
					mBlipTime = now;
				}
			} else {
				soundStarted = false;
				mSweepTime = now + 1000;
				mSweepBefore = true;
			}
			postInvalidate();
		}

		// Draw horizontal and vertical lines
		canvas.drawLine(center, center - (radius >> 2) + 6, center, center - radius - 6, gridPaint);
		canvas.drawLine(center, center + (radius >> 2) - 6, center, center + radius + 6, gridPaint);
		canvas.drawLine(center - (radius >> 2) + 6, center, center - radius - 6, center, gridPaint);
		canvas.drawLine(center + (radius >> 2) - 6, center, center + radius + 6, center, gridPaint);

		// Draw X in the center of the screen
		canvas.drawLine(center - 4, center - 4, center + 4, center + 4, gridPaint);
		canvas.drawLine(center - 4, center + 4, center + 4, center - 4, gridPaint);

		if (mHaveLocation) {
	
			for (CompassTarget target : targetList) {
				if (target.isActive) {

					double bearingToTarget = target.bearing - mOrientation;
					double drawingAngle = Math.toRadians(bearingToTarget) - (Math.PI / 2);
		
					float cos = (float) Math.cos(drawingAngle);
					float sin = (float) Math.sin(drawingAngle);
		
					// Draw the text for the rings
					final String[] distanceScale = mDistanceScale;
		
					addText(canvas, distanceScale[0], center, center + (radius >> 2));
					addText(canvas, distanceScale[1], center, center + (radius >> 1));
					addText(canvas, distanceScale[2], center, center + radius * 3 / 4);
					addText(canvas, distanceScale[3], center, center + radius);
		
					// Draw the blip.
					long blipDifference = now - mBlipTime;
					float circleX, circleY;
					blipRadius = (int) ((mDistanceRatio * radius * target.distance) / maxDistance);
					circleX = center + (cos * blipRadius) /*- 8*/;
					circleY = center + (sin * blipRadius) /*- 8*/;
	
					RadialGradient gradient = new android.graphics.RadialGradient(circleX - 3, circleY - 3, 16, 0xFFFFFFFF,
							target.color, android.graphics.Shader.TileMode.CLAMP);
					mTargetPaint.setShader(gradient);
					// Alpha is based on how long ago the sweep crossed the blip
					mTargetPaint.setAlpha(255 - (int) ((128 * blipDifference) >> 10));
					switch (target.type) {
					case 1: {
						canvas.drawRect(circleX - 4, circleY - 14, circleX + 4, circleY + 14, mTargetPaint);
						canvas.drawRect(circleX - 14, circleY - 4, circleX + 14, circleY + 4, mTargetPaint);
						break;
					}
					case 2: {
						canvas.drawCircle(circleX, circleY, 16, mTargetPaint);
						break;
					}
					}
					
				}
			}
		}
	}

	private void addText(Canvas canvas, String str, int x, int y) {

		mGridPaint.getTextBounds(str, 0, str.length(), mTextBounds);
		mTextBounds.offset(x - (mTextBounds.width() >> 1), y);
		mTextBounds.inset(-2, -2);
		canvas.drawRect(mTextBounds, mErasePaint);
		canvas.drawText(str, x, y, mGridPaint);
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
	}

	/**
	 * Called when we get a new value from the compass
	 * 
	 * @see android.hardware.SensorListener#onSensorChanged(int, float[])
	 */

	public void onSensorChanged(int sensor, float[] values) {

		mOrientation = values[0];
		postInvalidate();
	}


	/**
	 * Called when a location provider has a new location to report
	 * 
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	public void onLocationChanged(Location location) {
		
		if (!mHaveLocation) {
			mHaveLocation = true;
		}

		boolean useLocation = false;
		
		if (this.isBetterLocation(location, currentBestLocation )) {
			currentBestLocation = location;
			useLocation = true;
		}

		
		
		if (useLocation) {
			currentLocationPoint =  new GeoPoint((int)(location.getLatitude() * 1000000), (int)(location.getLongitude() * 1000000));  
			calcDistanceAndBearingOfTargets();
			//updateDistance(maxDistance);
			
			
			/*
			if (compassMode.equals("poi")) {
				if ((minDistance < DISTANCE_TO_REACH_POI)) {
					// at this time, we care only for the first poi:
					Poi p = (mStation.getPois()).get(0);
					if (p.getCondition() != null) {
						try {
							if (p.getCondition().eval()) {
								//Log.i("klaus", "target: " + ((Integer) (p.getTarget())).toString());
								for (OnChoiceListener l : onChoiceListeners) {
									l.onChoice(new Choice(p.getTarget()));
								}
							}
						} catch (QuestorConditionException e) {
							Log.e("MapStoryView", e.toString());
						}
					} else { // if there is no condition defined, we go on anyway:
						//Log.i("klaus", "target: " + ((Integer) (p.getTarget())).toString());

						for (OnChoiceListener l : onChoiceListeners) {
							l.onChoice(new Choice(p.getTarget()));
						}
					}
				}
			} // if (compassMode.equals("poi"))
			
			*/
		}
	}
	
	public boolean isBetterLocation(Location location, Location currentBestLocation) {
	 	// this function returns true, if the given location is better than the given currentBestLocation.
	 
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }
	
	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;
	
	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }
	
	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;
	
	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());
	
	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	} 
	
	
	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	/**
	 * Called when a location provider has changed its availability.
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
	 *      int, android.os.Bundle)
	 */
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * Called when we no longer have a valid location.
	 */
	private void handleUnknownLocation() {
		mHaveLocation = false;
		mDistanceView.setText("scanning...");
	}

	/**
	 * Update state to reflect whether we are using metric or standard units.
	 * 
	 * @param useMetric
	 *            True if the display should use metric units
	 */
	public void setUseMetric(boolean useMetric) {
		mUseMetric = useMetric;
		mLastScale = -1;
		if (mHaveLocation) {
			updateDistance(maxDistance);
		}
		invalidate();
	}

	/**
	 * Update our state to reflect a new distance to the target. This may
	 * require choosing a new scale for the compass rings.
	 * 
	 * @param distanceKm
	 *            The new distance to most far away the target
	 *  The textfield will alway show the distance to the nearest target (minDistance)    
	 */
	private void updateDistance(double distanceKm) {
		final double[] scaleChoices;
		final float[] displayUnitsPerKm;
		final String[] displayFormats;
		final String[] scaleFormats;
		String distanceStr = null;
		
		if (mUseMetric) {
			scaleChoices = mMetricScaleChoices;
			displayUnitsPerKm = mMetricDisplayUnitsPerKm;
			displayFormats = mMetricDisplayFormats;
			scaleFormats = mMetricScaleFormats;
		} else {
			scaleChoices = mEnglishScaleChoices;
			displayUnitsPerKm = mEnglishDisplayUnitsPerKm;
			displayFormats = mEnglishDisplayFormats;
			scaleFormats = mEnglishScaleFormats;
		}

		int count = scaleChoices.length;
		for (int i = 0; i < count; i++) {
			if (distanceKm < scaleChoices[i] || i == (count - 1)) {
				String format = displayFormats[i];
				double distanceDisplay = distanceKm * displayUnitsPerKm[i];
				if (mLastScale != i) {
					mLastScale = i;
					String scaleFormat = scaleFormats[i];
					float scaleDistance = (float) (scaleChoices[i] * displayUnitsPerKm[i]);
					mDistanceScale[0] = String.format(scaleFormat, (scaleDistance / 4));
					mDistanceScale[1] = String.format(scaleFormat, (scaleDistance / 2));
					mDistanceScale[2] = String.format(scaleFormat, (scaleDistance * 3 / 4));
					mDistanceScale[3] = String.format(scaleFormat, scaleDistance);
				}
				mDistanceRatio = (float) (distanceKm / scaleChoices[mLastScale]);
				//distanceStr = String.format(format, distanceDisplay);
				distanceStr = String.format(format, minDistance * displayUnitsPerKm[i]);
				break;
			}
		}

		if (errorMessage.length() > 0) distanceStr = errorMessage;
		
		mDistanceView.setText(distanceStr);
		//Log.i("klaus", "distanceStr: " + distanceStr);
	}

	/**
	 * Turn on the sweep animation starting with the next draw
	 */
	public void startSweep() {
		mSweepTime = SystemClock.uptimeMillis();
		mSweepBefore = true;
	}
	
	/**
	 * Turn off the sweep animation
	 */
	public void stopSweep() {
		mSweepTime = 0L;
	}
	

	public int getColRings() {
		return colRings;
	}

	public void setColRings(int colRings) {
		this.colRings = colRings;
	}

	public int getColSweep() {
		return colSweep;
	}

	public void setColSweep(int colSweep) {
		this.colSweep = colSweep;
	}

	public int getColTarget() {
		return colTarget;
	}

	public void setColTarget(int colTarget) {
		this.colTarget = colTarget;
	}

	public int getColText() {
		return colText;
	}

	public void setColText(int colText) {
		this.colText = colText;
	}

	public String getPathToSound() {
		return pathToSound;
	}

	public void setPathToSound(String pathToSound) {
		this.pathToSound = pathToSound;
	}

	public float getSoundVol() {
		return soundVol;
	}

	public void setSoundVol(float soundVol) {
		this.soundVol = soundVol;
	}

	@Override
	public void addOnChoiceListener(OnChoiceListener listener) {
		this.onChoiceListeners.add(listener);
	}

	@Override
	public void removeOnChoiceListener(OnChoiceListener listener) {
		this.onChoiceListeners.remove(listener);
	}

	public GeoPoint getCurrentLocationPoint() {
		return currentLocationPoint;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		distanceHasChanged = true;
	}
	

	
}