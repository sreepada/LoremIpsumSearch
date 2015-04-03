public class GeoData {
	String geoName;
	double latitude;
	double longitude;
	
	public GeoData(String name, double latitude, double longitude)
	{
		this.geoName = name;
		this.latitude= latitude;
		this.longitude= longitude;
	}
	public String getGeoName() {
		return this.geoName;
	}
	
	public double getLatitide() {
		return this.latitude;
	}
	
	public double getLongitutde() {
		return this.longitude;
	}

}
