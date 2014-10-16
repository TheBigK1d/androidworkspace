package com.johntcompas.androidtaggame;



import java.util.UUID;

import com.google.android.gms.maps.model.LatLng;

public class Player implements Comparable<Player>{

	private UUID uuid ; 
	private LatLng nonUserPosition;
	private String nonUserStatus ; 
	private Double nonUserAccuracy;
	
	
	public Player(String uuid , LatLng nonUserPosition, String nonUserStatus,
			Double nonUserAccuracy) {
		this.nonUserPosition = nonUserPosition;
		this.nonUserStatus = nonUserStatus;
		this.nonUserAccuracy = nonUserAccuracy;
		this.setUuid(uuid) ; 
	}


	public LatLng getNonUserPosition() {
		return nonUserPosition;
	}


	public void setNonUserPositions(LatLng nonUserPosition) {
		this.nonUserPosition = nonUserPosition;
	}


	public String getNonUserStatus() {
		return nonUserStatus;
	}


	public void setNonUserStatus(String nonUserStatus) {
		this.nonUserStatus = nonUserStatus;
	}


	public Double getNonUserAccuracy() {
		return nonUserAccuracy;
	}


	public void setNonUserAccuracy(Double nonUserAccuracy) {
		this.nonUserAccuracy = nonUserAccuracy;
	}


	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = UUID.fromString(uuid);
	}
	
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public int compareTo(Player another) {
		return uuid.compareTo(another.getUuid()) ; 
	}
}
