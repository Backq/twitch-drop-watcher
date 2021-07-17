package me.back.gg;

public enum GameType {
	
	RUST(263490);
	
	int gameID;
	
	GameType(int gameID){
		this.gameID = gameID;
	}
	
	public int getGameID() {
		return gameID;
	}
}
