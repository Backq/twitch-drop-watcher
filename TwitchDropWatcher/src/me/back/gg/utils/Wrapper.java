package me.back.gg.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import io.joshworks.restclient.http.Unirest;

import me.back.gg.GameType;
import me.back.gg.Main;
import me.back.gg.models.SearchResponse;
import me.back.gg.models.Streamer;


public class Wrapper {
	
	static StringBuffer response = new StringBuffer();

	public static void getStreamerInfo(String name) {
		try {
		String response = Unirest.get("https://api.twitch.tv/helix/search/channels?query="+name)
			.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")
			.header("Content-Type", "application/json")
			.header("Accept", "JSON")
			.header("client-id", readID( ))
			.header("Authorization", "Bearer " + readOAuth( ))
			.asString()
			.getBody();
			SearchResponse sResponse = Main.getGson().fromJson(response, SearchResponse.class);

			/**
			 * If you ever got this Exception: 
			 * java.lang.NullPointerException: Cannot invoke "java.util.ArrayList.stream()" because the return value of "me.back.gg.models.SearchResponse.getStreamers()" is null
			 * 
			 * Here is how to fix it.
			 * 
			 * You need to get new OAuth token, as you could see from the response:
			 * {"error":"Unauthorized","status":401,"message":"Invalid OAuth token"}
			 * 
			 */
			System.out.println("[DEBUG] Response: " + response);
			
			Optional<Streamer> opt = sResponse.getStreamers()
					.stream()
					.filter(e->e.getGameID() == GameType.RUST.getGameID() )
					.findFirst();
			
			if(opt.isPresent()) {
				Streamer streamer = opt.get();
				if(streamer.isLive()) 
					Main.onlineList.add(streamer.getDisplayName());
			}
		} catch (Exception aa) {System.out.println("[ERROR]: Fill 'client_id.txt' and 'OAuth.txt' with the correct token.");}
	}
	
	
	
	public static String readList(File name) throws FileNotFoundException, IOException {
		try( BufferedReader br = new BufferedReader(new FileReader(name))) {
			for(String line; (line = br.readLine()) != null; ) {
				return line;
				
			}
	    }		
		return null;
	}
	
	public static String readID( ) throws FileNotFoundException, IOException {
		Path currentPath = Paths.get("");
        String currentDir = currentPath.toAbsolutePath().toString();
        
        File client_id = new File(currentDir + "\\auths\\client_id.txt");
        
        return readList(client_id);
	}
	
	public static String readOAuth( ) throws FileNotFoundException, IOException {
		Path currentPath = Paths.get("");
        String currentDir = currentPath.toAbsolutePath().toString();
        
        File oAuth = new File(currentDir + "\\auths\\OAuth.txt");
        
        return readList(oAuth);
	}
	
	public static void init() throws IOException {
		Path currentPath = Paths.get("");
        String currentDir = currentPath.toAbsolutePath().toString();
        
        File dir = new File(currentDir + "\\auths");
        if (!dir.exists()) {
        	dir.mkdir();
            System.out.println("[+] Path Created.");
        }
        
        File oAuth = new File(dir + "\\OAuth.txt");
        File client_id = new File(dir + "\\client_id.txt");

        
        if(!oAuth.exists() || !client_id.exists()) {
            oAuth.createNewFile();
            client_id.createNewFile();
            System.out.println("[+] Auth files created, please fill them and restart the program.");
            System.out.println("OAuth Directory: " + oAuth.getAbsolutePath());
            System.out.println("client_id Directory: " + client_id.getAbsolutePath());
            System.exit(0);
        } else {
        	return;
        }
	}

}
