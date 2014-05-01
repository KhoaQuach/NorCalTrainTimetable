package com.khoa.quach.norcalcaltraintimetable;

public class RouteDetail {
	
    String route_number;
    String route_depart;
    String route_arrive;
    String route_duration;
    String route_type;
    
    // Empty constructor
    public RouteDetail(){}
    
    public RouteDetail(String _route_number, 
    		    String _route_depart, 
    		    String _route_arrive,
    		    String _route_duration,
    		    String _route_type){
    	this.route_number = _route_number;
        this.route_depart = _route_depart;
        this.route_arrive = _route_arrive;
        this.route_duration = _route_duration;
        this.route_type = _route_type;
    }
     
    public String getRouteNumber(){
        return this.route_number;
    }
     
    public void setRouteNumber(String _route_number){
        this.route_number = _route_number;
    }
     
    public String getRouteDepart(){
        return this.route_depart;
    }
     
    public void setRouteDepart(String _route_depart){
        this.route_depart = _route_depart;
    }
     
    public String getRouteArrive(){
        return this.route_arrive;
    }
     
    public void setRouteArrive(String _route_arrive){
        this.route_arrive = _route_arrive;
    }
    
    public String getRouteDuration(){
        return this.route_duration;
    }
     
    public void setRouteDuration(String _route_duration){
        this.route_duration = _route_duration;
    }
    
    public String getRouteType(){
        return this.route_type;
    }
     
    public void setRouteType(String _route_type){
        this.route_type = _route_type;
    }
    
}
