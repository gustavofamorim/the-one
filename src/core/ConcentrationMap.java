package core;

import util.Tuple;

import java.util.HashMap;

/** The concentration gradient map of the simulation map.
 */
public class ConcentrationMap implements Cloneable{

    /** Delete delivered messages -setting id ({@value}). Boolean valued.
     * If set to true and final recipient of a message rejects it because it
     * already has it, the message is deleted from buffer. Default=false. */
    public static final String CONCENTRATION_MAP_PREFIX_S = "ConcentrationMapping.";

    /** The granularity -setting id ({@value}). Double valued.
     * Defines the side of all square regions mapped.
     */
    public static final String GRANULARITY_MAP_S = "granularity";

    /** The region side length. */
    private static double granularity;

    /** Concentration gradient map.
     * The key {@code Tuple<Double, Double>} indicate the left botton point of the
     * region.
     * The value {@code Double} indicate the registred number of contacts in the region
     * identified by the key.
     */
    private HashMap<Tuple<Double, Double>, Long> map;

    public ConcentrationMap(Settings s){

    }

    /**
     * Calculate the region tuple of an coordinate.
     * @param mapLocation The actual location of the node.
     * @return The tuble of the region wich includes de coordinate.
     */
    private Tuple<Double, Double> convertMapLocationToRegionKey(Coord mapLocation){
        Double x = Math.floor(mapLocation.getX() / this.granularity);
        Double y = Math.floor(mapLocation.getY() / this.granularity);
        return (new Tuple<Double, Double>(x,y));
    }

    /**
     * Returns the concentration of a region.
     * @param coord The region coordinate.
     * @return The concentration of a region.
     */
    public double getConcentration(Tuple<Double, Double> coord){
        if (this.map.containsKey(coord)){
            return (this.map.get(coord) / (this.granularity * this.granularity));
        }
        //Region not mapped yet
        return (0);
    }

    /**
     * Returns the concentration of a region were the node is.
     * @param nodeLocation The actual location of the node.
     * @return The concentration of a region.
     */
    public double getConcentration(Coord nodeLocation){
        return (this.getConcentration(this.convertMapLocationToRegionKey(nodeLocation)));
    }

    /**
     * Record a contact event on the region were the node is.
     * @param nodeLocation The actual location of the node.
     */
    public void recordContact(Tuple<Double, Double> nodeLocation){
        if(!this.map.containsKey(nodeLocation)){
            this.map.put(nodeLocation, (long) 1);
        }
        else{
            this.map.replace(nodeLocation, this.map.get(nodeLocation) + 1);
        }
    }

    /**
     * Merge this map with another map received from another node.
     * @param anotherMap The map received from another node.
     */
    public void mergeConcentrationMap(ConcentrationMap anotherMap){
        for(Tuple<Double, Double> key : anotherMap.map.keySet()){
            //If this map contais the region, sum the numbers contacts in the region
            if(this.map.containsKey(key)){
                this.map.replace(key, this.map.get(key) + anotherMap.map.get(key));
            }
            else{
                this.map.put(key, anotherMap.map.get(key));
            }
        }
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
