package com.johntcompas.androidtaggame;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class NonUserData {
	
	private static ArrayList<Player> players ; 

	public static ArrayList<Player> getPlayers() {
		return players;
	}

	public static void setPlayers(ArrayList<Player> playersa) {
		players = playersa;
	}
	
	
	

}
