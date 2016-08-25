package routing.util;

import core.Coord;
import core.DTNHost;
import core.Settings;

import java.util.TreeMap;

/** The concentration gradient map of the simulation map.
 */
public class ConcentrationMap implements Cloneable{

    /** The granularity -setting id ({@value}). Double valued.
     * Defines the side of all square regions mapped.
     */
    public static final String GRANULARITY_MAP_S = "granularity";

    /** The region side length. */
    private static double granularity;

    /** Concentration gradient map.
     * The key {@code Coord} indicate the left botton point of the
     * region.
     * The value {@code Double} indicate the registred number of contacts in the region
     * identified by the key.
     */
    private TreeMap<Coord, Long> map;

    public ConcentrationMap(DTNHost host, Settings s){
        this.granularity = s.getDouble(GRANULARITY_MAP_S);
        this.map = new TreeMap<>();
    }

    /**
     * Calculate the region tuple of an coordinate.
     * @param mapLocation The actual location of the node.
     * @return The tuble of the region wich includes de coordinate.
     */
    private Coord convertMapLocationToRegionKey(Coord mapLocation){
        Double x = Math.floor(mapLocation.getX() / this.granularity);
        Double y = Math.floor(mapLocation.getY() / this.granularity);
        return (new Coord(x,y));
    }

    /**
     * Returns the concentration of a region.
     * @param coord The region coordinate.
     * @return The concentration of a region.
     */
    public double getConcentration(Coord coord){
        Coord regionBase = this.convertMapLocationToRegionKey(coord);
        if (this.map.containsKey(regionBase)){
            return (this.map.get(regionBase) / (this.granularity * this.granularity));
        }
        //Region not mapped yet
        return (0);
    }

    /**
     * Record a contact event on the region were the node is.
     * @param nodeLocation The actual location of the node.
     */
    public void recordContact(Coord nodeLocation){
        Coord regionOfNode = this.convertMapLocationToRegionKey(nodeLocation);
        if(!this.map.containsKey(regionOfNode)){
            this.map.put(regionOfNode, new Long(1));
        }
        else{
            this.map.replace(regionOfNode, new Long(this.map.get(regionOfNode) + 1));
        }
    }

    /**
     * Merge this map with another map received from another node.
     * @param anotherMap The map received from another node.
     */
    public void mergeConcentrationMap(ConcentrationMap anotherMap){
        for(Coord key : anotherMap.map.keySet()){
            //If this map contais the region, sum the numbers contacts in the region
            if(this.map.containsKey(key)){
                this.map.replace(key, this.map.get(key) + anotherMap.map.get(key));
            }
            else{
                this.map.put(key, anotherMap.map.get(key));
            }
        }
    }

    /** Returns the routing info of this concentration map.
     *  @return The routing info of this concentration map.
     */
    public RoutingInfo makeRountingInfo(){
        RoutingInfo concentrationMap = new RoutingInfo("Concentration Map:");

        for(Coord coord : this.map.keySet()){
            RoutingInfo info = new RoutingInfo("Region (" + coord.getX() + ", " + coord.getY() + "): " + this.map.get(coord));
            concentrationMap.addMoreInfo(info);
        }

        return (concentrationMap);
    }

    /**
     * Clone this concentration map.
     * @return A clone of this map.
     */
    @Override
    public ConcentrationMap clone(){
        try {
            return ((ConcentrationMap)super.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return (null);
    }
}
