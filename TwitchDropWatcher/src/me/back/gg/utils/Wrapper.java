package me.back.gg.utils;

import java.util.Optional;

import io.joshworks.restclient.http.Unirest;
import me.back.gg.GameType;
import me.back.gg.Main;
import me.back.gg.models.SearchResponse;
import me.back.gg.models.Streamer;


public class Wrapper {
	
	static StringBuffer response = new StringBuffer();
	
	public static void getStreamerInfo(String name) {
		String response = Unirest.get("https://api.twitch.tv/helix/search/channels?query="+name)
			.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")
			.header("Content-Type", "application/json")
			.header("Accept", "JSON")
			.header("client-id"," your client id here") /*your client id*/
			.header("Authorization", "Bearer your token here") /*your bearer token*/
			.asString()
			.getBody();
			SearchResponse sResponse = Main.getGson().fromJson(response, SearchResponse.class);
			Optional<Streamer> opt = sResponse.getStreamers()
					.stream()
					.filter(e->e.getGameID() == GameType.RUST.getGameID())
					.findFirst();
			
			if(opt.isPresent()) {
				Streamer streamer = opt.get();
				if(streamer.isLive()) 
					Main.onlineList.add(streamer.getDisplayName());
			}
	}


}
