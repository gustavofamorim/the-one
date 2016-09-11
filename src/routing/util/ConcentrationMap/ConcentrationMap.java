package routing.util.ConcentrationMap;

import core.Coord;
import core.DTNHost;
import core.Settings;
import routing.util.RoutingInfo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.TreeMap;

/** The concentration gradient map of the simulation map.
 */
public abstract class ConcentrationMap<StoreType> implements Cloneable{

    public static final String PACKAGE_S = "routing.util.ConcentrationMap.";

    /** The granularity -setting id ({@value}). Double valued.
     * Defines the side of all square regions mapped.
     */
    public static final String GRANULARITY_MAP_S = "regionLength";

    /** The model to be used when is merging the map. Double valued.
     * Defines the side of all square regions mapped.
     */
    public static final String CONCENTRATION_MERGE_MODEL_S = "concentrationMergeModel";

    /** The region side length. */
    protected static double regionLength;

    /** Concentration gradient map.
     * The key {@code Coord} indicate the left botton point of the
     * region.
     * The value {@code BigDecimal} indicate the registred number of contacts in the region
     * identified by the key.
     */
    protected TreeMap<Coord, StoreType> map;

    /** The node reference */
    protected DTNHost host;

    /** The world size */
    //TODO: Study a best way to do this...
    protected int worldSize[] = new Settings(movement.MovementModel.MOVEMENT_MODEL_NS).getCsvInts(movement.MovementModel.WORLD_SIZE);

    /** The total number of contacts registered. */
    protected BigDecimal totalOfContacts = new BigDecimal(0);

    public ConcentrationMap(DTNHost host, Settings s){
        this.host = host;
        this.map = new TreeMap<>();
        this.regionLength = s.getDouble(GRANULARITY_MAP_S);
    }

    /**
     * Calculate the region tuple of an coordinate.
     * @param mapLocation The actual location of the node.
     * @return The tuble of the region wich includes de coordinate.
     */
    public Coord convertMapLocationToRegionKey(Coord mapLocation){
        if(!this.map.containsKey(mapLocation)) {
            //Calculate the region coordinate
            double x = Math.floor(mapLocation.getX() / this.regionLength);
            double y = Math.floor(mapLocation.getY() / this.regionLength);
            return (new Coord(x, y));
        }
        return (mapLocation);
    }

    /**
     * Returns the concentration of a region.
     * @param region The region coordinate.
     * @return The concentration of a region.
     */
    public BigDecimal getConcentration(Coord region){
        //Coord regionBase = this.convertMapLocationToRegionKey(coord);
        if(this.map.containsKey(region) && !this.getRegionNrOfContacts(region).equals(BigDecimal.ZERO)){
            BigDecimal result = this.getRegionNrOfContacts(region).divide(this.totalOfContacts, MathContext.DECIMAL128);
            return (result);
        }
        //Region not mapped yet
        return (new BigDecimal(0));
    }

    /**
     * Record a contact event on the region were the node is.
     * @param nodeLocation The actual location of the node.
     */
    public void recordContact(Coord nodeLocation){
        Coord regionOfNode = this.convertMapLocationToRegionKey(nodeLocation);

        this.totalOfContacts = this.totalOfContacts.add(new BigDecimal(1));

        if(!this.map.containsKey(regionOfNode)){
            this.setRegionNrOfContacts(regionOfNode, new BigDecimal(1));
        }
        else{
            this.setRegionNrOfContacts(regionOfNode, this.getRegionNrOfContacts(regionOfNode).add(new BigDecimal(1)));
        }
    }

    /**
     * Merge this map with another map received from another node.
     * @param anotherMap The map received from another node.
     */
    public abstract void mergeConcentrationMap(ConcentrationMap<StoreType> anotherMap);

    public void applyReductionOfValues(){
        for (Coord key : this.map.keySet()) {
            this.setRegionNrOfContacts(key, this.getRegionNrOfContacts(key).divide(new BigDecimal(2), MathContext.DECIMAL128));
        }
        this.totalOfContacts.divide(new BigDecimal(2), MathContext.DECIMAL128);
    }

    /** Returns the routing info of this concentration map.
     *  @return The routing info of this concentration map.
     */
    public RoutingInfo makeRountingInfo(){
        RoutingInfo concentrationMap = new RoutingInfo("Regions Map:");
        concentrationMap.addMoreInfo(new RoutingInfo("Total Number of Contacts: " + this.totalOfContacts));

        RoutingInfo regionMap = new RoutingInfo("Region Map: ");
        concentrationMap.addMoreInfo(regionMap);

        for(Coord coord : this.map.keySet()){
            regionMap.addMoreInfo(new RoutingInfo("Region (" + coord.getX() + ", " + coord.getY() + "): " + this.getConcentration(coord)));
        }

        return (concentrationMap);
    }

    public abstract BigDecimal getRegionNrOfContacts(Coord region);

    public abstract void setRegionNrOfContacts(Coord region, BigDecimal contacts);

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

    /**
     * Parse this concentration map to a string.
     * @return A string parsed of this map.
     */
    @Override
    public String toString(){
        Coord tmpCoord = new Coord(0,0);
        String str = "";
        str += this.totalOfContacts + "\n";
        for(double i = 0; i < (Math.ceil(this.worldSize[0] / this.regionLength) + 1); i+=1){
            tmpCoord.setX(i);
            for(double j = 0; j < (Math.ceil(this.worldSize[1] / this.regionLength) + 1); j+=1){
                tmpCoord.setY(j);
                str += "\t" + i + "\t" + j + "\t" + /*this.getRegionNrOfContacts(tmpCoord) +*/ "\t" + this.getConcentration(tmpCoord) + "\n";
            }
        }

        return (str);
    }
}
