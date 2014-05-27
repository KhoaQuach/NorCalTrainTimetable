package com.khoa.quach.norcalcaltraintimetable;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
 
 
public class RouteDetailInfoAdapter extends ArrayAdapter<RouteDetail> {
 
    int resource;
    String response;
    Context context;
    int highlight_position = -1;
    
    public RouteDetailInfoAdapter(Context context, int resource, List<RouteDetail> items) {
        super(context, resource, items);
        this.resource=resource;
 
    }
     
    public void setHighlightPosition(int position) {
    	highlight_position = position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout routeDetailView;
        String route_name = "";
        
        RouteDetail routeDetail = getItem(position);
        
        //Inflate the view
        if( convertView == null ) {
            routeDetailView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource, routeDetailView, true);
        }
        else {
            routeDetailView = (LinearLayout) convertView;
        }
        
        TextView routeNumberText = (TextView) routeDetailView.findViewById(R.id.route_number);
        TextView routeDepartText = (TextView) routeDetailView.findViewById(R.id.route_depart);
        TextView routeArriveText = (TextView) routeDetailView.findViewById(R.id.route_arrive);
        TextView routeDurationText = (TextView) routeDetailView.findViewById(R.id.route_duration);
         
        routeNumberText.setText(routeDetail.getRouteNumber());
        routeDepartText.setText(routeDetail.getRouteDepart());
        routeArriveText.setText(routeDetail.getRouteArrive());
        routeDurationText.setText(routeDetail.getRouteDuration());
         
        route_name = routeDetail.getRouteName();
        
        // Set different background color
        if (highlight_position == position) {
        	routeDetailView.setBackgroundColor(android.graphics.Color.MAGENTA);
        }
        else if (route_name.equals("Bullet")) {
        	routeDetailView.setBackgroundColor(android.graphics.Color.GREEN);
        } else if (route_name.equals("Limited")) {
        	routeDetailView.setBackgroundColor(android.graphics.Color.CYAN);
        } else {
        	routeDetailView.setBackgroundColor(android.graphics.Color.WHITE);
        }
        
        return routeDetailView;
    }
 
}