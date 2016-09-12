package routing.util.ConcentrationMap;

import core.Coord;
import core.DTNHost;
import core.Settings;

import java.math.BigDecimal;

/**
 * Created by gustavo on 28/08/16.
 */
public class SumConcentrationMap extends ConcentrationMap<BigDecimal>{

    public SumConcentrationMap(DTNHost host, Settings s){
        super(host, s);
    }

    @Override
    public void mergeConcentrationMap(ConcentrationMap<BigDecimal> anotherMap) {
        for(Coord key : anotherMap.map.keySet()){
            //If this map contais the region, sum the numbers contacts in the region
            if(this.map.containsKey(key)){
                this.map.replace(key, this.map.get(key).add(anotherMap.map.get(key)));
            }
            else{
                this.map.put(key, anotherMap.map.get(key));
            }

            this.updateMaxIfNeeded(key);
        }
        this.totalOfContacts = this.totalOfContacts.add(anotherMap.totalOfContacts);
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
