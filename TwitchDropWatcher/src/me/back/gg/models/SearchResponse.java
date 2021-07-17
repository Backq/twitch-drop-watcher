package me.back.gg.models;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class SearchResponse {
	
	@SerializedName(value = "data")
	private ArrayList<Streamer> streamers;
	
	public ArrayList<Streamer> getStreamers(){
		return streamers;
	}
}
