package me.back.gg.models;

import com.google.gson.annotations.SerializedName;

public class Streamer {

	
	@SerializedName(value = "is_live")
	private boolean isLive;
	
	@SerializedName(value = "display_name")
	private String displayName;
	
	@SerializedName(value = "game_id")
	private int gameID;
	
	public String getDisplayName() {
		return displayName;
	}
	
	public boolean isLive() {
		return isLive;
	}
	
	public int getGameID() {
		return gameID;
	}
	
}
