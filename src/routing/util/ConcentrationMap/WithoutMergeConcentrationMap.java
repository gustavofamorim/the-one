package routing.util.ConcentrationMap;

import core.Coord;
import core.DTNHost;
import core.Settings;

/**
 * Created by gustavo on 28/08/16.
 */
public class WithoutMergeConcentrationMap extends ConcentrationMap<Long> {

    public WithoutMergeConcentrationMap(DTNHost host, Settings s){
        super(host, s);
    }

    @Override
    public void mergeConcentrationMap(ConcentrationMap<Long> anotherMap) {
        //This map not support merge. Just return.
        return;
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
