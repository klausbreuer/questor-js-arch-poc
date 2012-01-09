package de.questor.poc.jsarch.renderer.compass;

import android.content.Context;
import android.content.Intent;

public class CompassDelegate {
	
	private Context context;
	
	public CompassDelegate(Context ctx) {
		context = ctx;
	}

	public void show() {
		Intent i = new Intent(context, CompassActivity.class);
		context.startActivity(i);
	}
	
	public void updatePlayerPosition(String playerId, int lonE6, int latE6, int color) {
		Intent i = new Intent(CompassActivity.UPDATE_PLAYER_POSITION_INTENT);
		i.putExtra("id", playerId);
		i.putExtra("lonE6", lonE6);
		i.putExtra("latE6", latE6);
		i.putExtra("color", color);
		
		context.sendBroadcast(i);
	}

	public void updatePoiPosition(String poiId, int lonE6, int latE6, int color) {
		Intent i = new Intent(CompassActivity.UPDATE_POI_POSITION_INTENT);
		i.putExtra("id", poiId);
		i.putExtra("lonE6", lonE6);
		i.putExtra("latE6", latE6);
		i.putExtra("color", color);
		
		context.sendBroadcast(i);
	}

}
