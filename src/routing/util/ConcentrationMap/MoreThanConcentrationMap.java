package routing.util.ConcentrationMap;

import core.Coord;
import core.DTNHost;
import core.Settings;

import java.math.BigDecimal;

/**
 * Created by gustavo on 03/09/16.
 */
public class MoreThanConcentrationMap extends ConcentrationMap<BigDecimal>{

    public MoreThanConcentrationMap(DTNHost host, Settings s){
        super(host, s);
    }

    @Override
    public void mergeConcentrationMap(ConcentrationMap<BigDecimal> anotherMap) {
        BigDecimal tmpTotalOfContacts = new BigDecimal(0);

        for(Coord key : anotherMap.map.keySet()){
            //If this map contais the region...
            if(this.map.containsKey(key)){
                //and another map has more contacs registred, copy the value
                if(anotherMap.map.get(key).compareTo(this.map.get(key)) > 0){
                    //Update the information in this map
                    BigDecimal value = anotherMap.map.get(key).add(BigDecimal.ZERO);
                    this.map.replace(key, value);
                }
            }
            //else..
            else{
                //Add the entry in this map
                BigDecimal value = anotherMap.map.get(key).add(BigDecimal.ZERO);
                this.map.put(key, value);
            }

            this.updateMaxIfNeeded(key);
        }

        //Recalc the new number of contacts
        for(Coord key : this.map.keySet()){
            tmpTotalOfContacts = tmpTotalOfContacts.add(this.map.get(key));
        }

        this.totalOfContacts = tmpTotalOfContacts;
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