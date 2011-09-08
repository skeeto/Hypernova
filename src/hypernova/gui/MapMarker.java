package hypernova.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.awt.BasicStroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics;


public class MapMarker
{
    private static final double SEG_LENGTH = 6;
    private static final int SEG_OPACITY= 125;
    final static BasicStroke wideStroke = new BasicStroke(4.0f);  

    private boolean isVisible = true;
    private double mx = 0;
    private double my = 0;
    private Color c = null;
  
    private static ArrayList<MapMarker> markers = new ArrayList<MapMarker>();

    public static void add(MapMarker marker) {markers.add(marker);}
    public static void clear() {markers.clear();}

    public static void drawAll(double focusX, double focusY, Graphics2D g)
    {
      Iterator it = markers.iterator();
      while(it.hasNext()) ((MapMarker) it.next()).draw(focusX, focusY, g);
    }

    public void setValues(double x, double y, Color col, boolean visible)
    { 
        mx = x;
        my = y;
        c = col; 
        isVisible = visible;
    }

    public MapMarker(double x, double y, Color col, boolean visible) {setValues(x,y,col,visible);}
    public MapMarker(double x, double y, int r, int g, int b) {setValues(x,y,new Color(r,g,b,SEG_OPACITY),true);}
    public MapMarker(double x, double y) {setValues(x,y,new Color(255,255,255),true);}
  
    public void setColor(Color col) {c = col;}
    public void setColor(int r, int g, int b) {c = new Color(r,g,b,SEG_OPACITY);}
    public void setPosition(double x, double y) {mx = x; my = y;}
    public void setVisible(boolean visible) {isVisible = visible;}

    public void draw(double focusX, double focusY, Graphics2D g)
    {
        if( isVisible == false ) return;
        // TODO: if close enough, draw icon

        double dx = mx - focusX;
        double dy = focusY - my;
        double theta = Math.atan(dy / dx);
        if(dx < 0 && dy >= 0) theta += Math.PI;
        else if(dx < 0 && dy < 0) theta -= Math.PI;
        else if(dx == 0 && dy >= 0) theta = Math.PI / 2; 
        else if(dx == 0 && dy < 0) theta = -Math.PI / 2; 
        else if(dx == 0 && dy == 0) theta = 0;
        
        g.setPaint(c);
        g.setStroke(wideStroke);
        g.draw(new Arc2D.Double(1, 1, Viewer.MM_SIZE - 2, Viewer.MM_SIZE - 2,  57.2957795*theta - SEG_LENGTH / 2, SEG_LENGTH, Arc2D.OPEN));
    }

    
}
