package com.khoa.quach.norcalcaltraintimetable;

public class GeoDistance {

	public static double difference (double source_lat, double source_lon, double destination_lat, double destination_lon) 
	{
	    double earthRadius = 3958.75;
	    double dLat = Math.toRadians(destination_lat-source_lat);
	    double dLng = Math.toRadians(destination_lon-source_lon);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(Math.toRadians(source_lat)) * Math.cos(Math.toRadians(destination_lat)) *
	    Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    return new Double(dist * meterConversion).floatValue();
	}
	
}
