package com.locomode.sqlite.model;

public class LocationSet {
	int _id;
	String _buildingName;
	double _latitude;
	double _longitude;
	int _mode;
	int _endMode;
	String _BSSID;
	int _radius;
	//constructors
	public LocationSet(){
		
	}
	
	public LocationSet(int _id, String _buildingName, double _latitude, double _longitude, int _mode, int _endMode, String _BSSID, int _radius)
	{
		this._id = _id;
		this._buildingName = _buildingName;
		this._latitude = _latitude;
		this._longitude = _longitude;
		this._mode = _mode;
		this._endMode = _endMode;
		this._BSSID = _BSSID;
		this._radius = _radius;
	}
	//setters
	public void setId(int id)
	{
		this._id = id;
	}
	
	public void setBuildingName(String buildingName)
	{
		this._buildingName = buildingName;
	}
	
	public void setLatitude(double latitude)
	{
		this._latitude= latitude;
	}
	
	public void setLongitude(double longitude)
	{
		this._longitude= longitude;
	}
	
	public void setMode(int mode)
	{
		this._mode = mode;
	}
	
	public void setEndMode(int endMode)
	{
		this._endMode = endMode;
	}
	
	public void setBSSID(String BSSID)
	{
		this._BSSID = BSSID;
	}
	
	public void setRadius(int radius)
	{
		this._radius = radius;
	}
	
	
	//getters
	
	public int getId() {
		return this._id;
	}
	
	public String getBuildingName(){
		return this._buildingName;
	}
	
	public double getLatitude() {
		return this._latitude;
	}
	
	public double getLongitude() {
		return this._longitude;
	}
	
	public int getMode() {
		return this._mode;
	}
	
	public int getEndMode() {
		return this._endMode;
	}
	
	public String getBSSID(){
		return this._BSSID;
	}
	
	public int getRadius(){
		return this._radius;
	}
}
