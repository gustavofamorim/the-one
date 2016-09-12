package routing.util.ConcentrationMap;

import core.Coord;
import core.DTNHost;
import core.Settings;
import sun.awt.image.PixelConverter;

import java.math.BigDecimal;

/**
 * Created by gustavo on 28/08/16.
 */
public class IncrementalConcentrationMap extends ConcentrationMap<BigDecimal>{

    public IncrementalConcentrationMap(DTNHost host, Settings s){
        super(host, s);
    }

    @Override
    public void mergeConcentrationMap(ConcentrationMap<BigDecimal> anotherMap) {
        BigDecimal tmpTotalIncrements = BigDecimal.ZERO;

        for(Coord key : anotherMap.map.keySet()){
            //If this map contais the region, increment the number of contacts in the region
            if(this.map.containsKey(key)){
                this.map.replace(key, this.map.get(key).add(BigDecimal.ONE));
                tmpTotalIncrements = tmpTotalIncrements.add(BigDecimal.ONE);
            }
            else{
                this.map.put(key, anotherMap.map.get(key));
                tmpTotalIncrements = tmpTotalIncrements.add(anotherMap.map.get(key));
            }

            this.updateMaxIfNeeded(key);
        }
        this.totalOfContacts = this.totalOfContacts.add(tmpTotalIncrements);
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
