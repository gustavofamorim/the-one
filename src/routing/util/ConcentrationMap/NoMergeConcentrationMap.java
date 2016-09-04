package routing.util.ConcentrationMap;

import core.Coord;
import core.DTNHost;
import core.Settings;

import java.math.BigDecimal;

/**
 * Created by gustavo on 28/08/16.
 */
public class NoMergeConcentrationMap extends ConcentrationMap<BigDecimal> {

    public NoMergeConcentrationMap(DTNHost host, Settings s){
        super(host, s);
    }

    @Override
    public void mergeConcentrationMap(ConcentrationMap<BigDecimal> anotherMap) {
        //This map not support merge. Just return.
        return;
    }

    @Override
    public BigDecimal getRegionNrOfContacts(Coord region) {
        if(this.map.containsKey(region)){
            return (this.map.get(region));
        }
        return (BigDecimal.ZERO);
    }

    @Override
    public void setRegionNrOfContacts(Coord region, BigDecimal contacts) {
        if(!this.map.containsKey(region)){
            this.map.put(region, contacts);
        }
        else{
            this.map.replace(region, contacts);
        }
    }
}
