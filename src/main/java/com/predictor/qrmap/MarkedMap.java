package com.predictor.qrmap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: predictor
 * Date: 07.02.13
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */
class MarkedMap implements Serializable {
    private final BufferedImage map;

    private final String mapName;
    private final Set<Marker> markers;
    private int index=0;
    public final int height;
    public final int width;
    public final int markerRadius;
    public static final int MARKER_SCALE = 120;
    public MarkedMap(BufferedImage map, String mapName) {
        this.map = map;
        this.mapName = mapName;
        markers = new HashSet<Marker>();
        height=map.getHeight();
        width=map.getWidth();
        markerRadius = (width+height)/ MARKER_SCALE;
    }
    public MarkedMap(File mapFile) throws IOException {
        this(ImageIO.read(mapFile), mapFile.getName());
    }
    public Set<Marker> getMarkers(){
        return markers;
    }
    public void addMarker(Marker marker){
        markers.add(marker);
    }
    public void removeMarker(Point marker){
        markers.remove(marker);
    }
    public BufferedImage getMap() {
        return map;
    }
    public boolean pointInMarker(int pointX, int pointY, int markerX, int markerY){
        return (pointX-markerX)*(pointX-markerX)+(pointY-markerY)*(pointY-markerY) <= markerRadius*markerRadius;
    }
    public String getIndex(){
        return String.valueOf(index++);
    }
    public String getName() {
        return mapName;
    }
    static class Marker extends Point{
        public final String name;

        public Marker(int x, int y, String name) {
            super(x,y);
            this.name=name;
        }
    }
}
