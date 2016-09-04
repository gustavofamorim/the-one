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
        BigDecimal tmpTotalIncrements = BigDecimal.ZERO;
        for(Coord key : anotherMap.map.keySet()){
            //If this map contais the region
            if(this.map.containsKey(key)){
                //If another map has more contacs registred, copy the value
                if(anotherMap.map.get(key).compareTo(this.map.get(key)) < 0){
                    this.map.replace(key, this.map.get(key).add(anotherMap.map.get(key)));
                    tmpTotalIncrements = tmpTotalIncrements.add(anotherMap.map.get(key));
                }
                //If not, just increment the number of contacts
                else{
                    tmpTotalIncrements = tmpTotalIncrements.add(this.map.get(key));
                }
            }
            //If not, add the entry
            else{
                this.map.put(key, anotherMap.map.get(key));
                tmpTotalIncrements = tmpTotalIncrements.add(anotherMap.map.get(key));
            }
        }
        this.totalOfContacts = tmpTotalIncrements;

        //this.applyReductionOfValues();
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