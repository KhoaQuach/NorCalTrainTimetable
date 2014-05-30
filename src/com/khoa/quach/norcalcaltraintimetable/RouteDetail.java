package com.khoa.quach.norcalcaltraintimetable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RouteDetail {
	
    String route_number;
    String route_depart;
    String route_arrive;
    String route_duration;
    String route_name;
    boolean need_transfer = true;
    
    // Empty constructor
    public RouteDetail(){}
    
    public RouteDetail(String _route_number, 
    		    String _route_depart, 
    		    String _route_arrive,
    		    String _route_duration,
    		    String _route_name){
    	this.route_number = _route_number;
        this.route_depart = _route_depart;
        this.route_arrive = _route_arrive;
        this.route_duration = _route_duration;
        this.route_name = _route_name;
    }
    
    private void calculateAndSetDuration() {
    	
    	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    	
    	Date d1 = null;
        Date d2 = null;

        if ( this.route_arrive.isEmpty() || this.route_depart.isEmpty()) {
        	this.route_duration = "0:00";
        	return;
        }
        
        try 
        {	
            d1 = format.parse(this.route_arrive);
            d2 = format.parse(this.route_depart);

            // in milliseconds
            long diff = d1.getTime() - d2.getTime();

            long diffMinutes = diff / (60 * 1000);
            if ( 60 <= diffMinutes ) {
            	long diffHours = diff / (60 * 60 * 1000) % 24;
            	diffMinutes = diff / (60 * 1000) % 60;
            	if (0 <= diffMinutes && diffMinutes <= 9) {
            		this.route_duration = Long.toString(diffHours) + ":0" + Long.toString(diffMinutes);
            	}
            	else {
            		this.route_duration = Long.toString(diffHours) + ":" + Long.toString(diffMinutes);
            	}
            } 
            else {
            	this.route_duration = "0:" + Long.toString(diffMinutes); 
            }
        }
        catch (Exception e) 
        {
            // TODO: handle exception
        } 
    }
    
    public boolean getNeedTransfer() {
    	return need_transfer;
    }
    
    public void setNeedTransfer(boolean _need_transfer) {
    	need_transfer = _need_transfer;
    }
    
    public String getRouteNumber(){
        return this.route_number;
    }
     
    public void setRouteNumber(String _route_number){
        this.route_number = _route_number;
    }
     
    public String getRouteDepart(){
    	
    	String departTime = "";
    	
    	 try {
         	// Convert to 12 hour format
         	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
             Date depart = sdf.parse(this.route_depart);
             departTime = new SimpleDateFormat("hh:mm a").format(depart);
     	} catch (java.text.ParseException e) {
 			departTime = route_depart;
 		}
    	 
        return departTime;
    }
     
    public void setRouteDepart(String _route_depart){
        this.route_depart = _route_depart;
        
        if ( (!this.route_depart.isEmpty()) && (this.route_arrive != null) && (!this.route_arrive.isEmpty())) {
        	calculateAndSetDuration();	
        }
    }
     
    public String getRouteArrive(){
    	
    	String arriveTime = "";
    	
    	try {
        	// Convert to 12 hour format
        	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date arrive = sdf.parse(this.route_arrive);
            arriveTime = new SimpleDateFormat("hh:mm a").format(arrive);
    	} catch (java.text.ParseException e) {
			arriveTime = route_arrive;
		}
    	
        return arriveTime;
    }
     
    public void setRouteArrive(String _route_arrive){
        this.route_arrive = _route_arrive;
        
        if ( (this.route_depart != null) && (!this.route_depart.isEmpty()) && (!this.route_arrive.isEmpty())) {
        	calculateAndSetDuration();
        }
    }
    
    public String getRouteDuration(){
        return this.route_duration;
    }
     
    public void setRouteDuration(String _route_duration){
        this.route_duration = _route_duration;
    }
    
    public String getRouteName(){
        return this.route_name;
    }
     
    public void setRouteName(String _route_name){
        this.route_name = _route_name;
    }
    
}
