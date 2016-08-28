package routing.util.ConcentrationMap;

import core.Coord;
import core.DTNHost;
import core.Settings;

/**
 * Created by gustavo on 28/08/16.
 */
public class SumConcentrationMap extends ConcentrationMap<Long>{

    public SumConcentrationMap(DTNHost host, Settings s){
        super(host, s);
    }

    @Override
    public void mergeConcentrationMap(ConcentrationMap<Long> anotherMap) {
        for(Coord key : anotherMap.map.keySet()){
            //If this map contais the region, sum the numbers contacts in the region
            if(this.map.containsKey(key)){
                this.map.replace(key, this.map.get(key) + anotherMap.map.get(key));
            }
            else{
                this.map.put(key, anotherMap.map.get(key));
            }
        }
        this.totalOfContacts += anotherMap.totalOfContacts;
    }

    @Override
    public Long getRegionNrOfContacts(Coord region) {
        if(this.map.containsKey(region)){
            return (this.map.get(region));
        }
        return (new Long(0));
    }

    @Override
    public void setRegionNrOfContacts(Coord region, Long contacts) {
        if(!this.map.containsKey(region)){
            this.map.put(region, contacts);
        }
        else{
            this.map.replace(region, contacts);
        }
    }
}
