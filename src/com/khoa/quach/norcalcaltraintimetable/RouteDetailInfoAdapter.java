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
    
    public RouteDetailInfoAdapter(Context context, int resource, List<RouteDetail> items) {
        super(context, resource, items);
        this.resource=resource;
 
    }
     
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout routeDetailView;
        
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
        TextView routeTypeText = (TextView) routeDetailView.findViewById(R.id.route_type);
         
        routeNumberText.setText(routeDetail.getRouteNumber());
        routeDepartText.setText(routeDetail.getRouteDepart());
        routeArriveText.setText(routeDetail.getRouteArrive());
        routeDurationText.setText(routeDetail.getRouteDuration());
        routeTypeText.setText(routeDetail.getRouteType());
         
        return routeDetailView;
    }
 
}