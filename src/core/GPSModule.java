package core;

/**
 * Class for GPS module simulation.
 */
public class GPSModule implements Cloneable{

    /** Reference for the associated DTN Host */
    private DTNHost host;
    /** Actual position of the device */
    private Coord position = new Coord(0,0);

    public GPSModule(DTNHost associatedHost, Coord position) {
        this.host = associatedHost;
        this.position = position;
    }

    /** Returns the actual position of the node with energy consumption.
     *  @return the actual position of the node with energy consumption.
     */
    public Coord getPosition() {
        this.host.getEnergy().reduceGpsDiscoveryEnergy();
        return position;
    }

    /** Returns the actual position of the node without energy consumption.
     *  @return the actual position of the node without energy consumption.
     */
    public Coord getPositionWithouConsumption(){
        return position;
    }

    /** Set the actual position of the node with energy consumption.
     *  @param x The x position of the host.
     *  @param y The y position of the host.
     */
    public void setPosition(double x, double y) {
        this.position = new Coord(x, y);
    }

    /** Set the actual position of the node with energy consumption.
     *  @param coord The position of the host.
     */
    public void setPosition(Coord coord) {
        this.setPosition(coord.getX(), coord.getY());
    }

    /**
     * Clone this GPS module.
     * @return A clone of this GPS module.
     */
    @Override
    public GPSModule clone(){
        try {
            return ((GPSModule) super.clone());
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return (null);
    }
}
