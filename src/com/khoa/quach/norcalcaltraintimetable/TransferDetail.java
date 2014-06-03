package com.khoa.quach.norcalcaltraintimetable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransferDetail {

	String stop_name;
    String route_number;
    String route_arrive;
    
    // Empty constructor
    public TransferDetail(){}
    
    public TransferDetail(String _stop_name, 
    		    String _route_number, 
    		    String _route_arrive) {
    	this.stop_name = _stop_name;
        this.route_number = _route_number;
        this.route_arrive = _route_arrive;
    }
    
    public String getRouteNumber(){
        return this.route_number;
    }
     
    public void setRouteNumber(String _route_number){
        this.route_number = _route_number;
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
    }
    
    public String getStopName(){
        return this.stop_name;
    }
     
    public void setStopName(String _stop_name){
        this.stop_name = _stop_name;
    }

}



