package routing.util.ConcentrationMap;

import core.Coord;
import core.DTNHost;
import core.Settings;
import util.Tuple;

import java.math.BigDecimal;

/**
 * Created by gustavo on 28/08/16.
 */
public class AverageConcentrationMap extends ConcentrationMap<Tuple<BigDecimal, Long>> {

    public AverageConcentrationMap(DTNHost host, Settings s){
        super(host, s);
    }

    @Override
    public void mergeConcentrationMap(ConcentrationMap<Tuple<BigDecimal, Long>> anotherMap) {
        for(Coord key : anotherMap.map.keySet()){
            //If this map contais the region, sum the numbers contacts in the region
            if(this.map.containsKey(key)){
                //Calculate the average of registered contacts
                Tuple<BigDecimal, Long> thisMapTuple = this.map.get(key);
                Tuple<BigDecimal, Long> anotherMapTuple = anotherMap.map.get(key);

                Long totalContacts = thisMapTuple.getValue() + anotherMapTuple.getValue();

                BigDecimal concentration = thisMapTuple.getKey().multiply(new BigDecimal(thisMapTuple.getValue())).add(anotherMapTuple.getKey().multiply(new BigDecimal(anotherMapTuple.getValue())));
                concentration = concentration.divide(new BigDecimal(totalContacts));

                this.map.replace(key, new Tuple<>(concentration, totalContacts));
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
            return (this.map.get(region).getValue());
        }
        return (new Long(0));
    }

    @Override
    public void setRegionNrOfContacts(Coord region, Long contacts) {
        if(!this.map.containsKey(region)){
            this.map.put(region, new Tuple<BigDecimal, Long>(new BigDecimal(contacts), new Long(1)));
        }
        else{
            this.map.replace(region, new Tuple<BigDecimal, Long>(new BigDecimal(contacts), new Long(1)));
        }
    }
}
