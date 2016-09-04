package routing.util.ConcentrationMap;

import core.Coord;
import core.DTNHost;
import core.Settings;
import util.Tuple;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by gustavo on 28/08/16.
 */
public class AverageConcentrationMap extends ConcentrationMap<Tuple<BigDecimal, BigDecimal>> {

    public AverageConcentrationMap(DTNHost host, Settings s){
        super(host, s);
    }

    @Override
    public void mergeConcentrationMap(ConcentrationMap<Tuple<BigDecimal, BigDecimal>> anotherMap) {
        for(Coord key : anotherMap.map.keySet()){
            //If this map contais the region, sum the numbers contacts in the region
            if(this.map.containsKey(key)){
                //Calculate the average of registered contacts
                Tuple<BigDecimal, BigDecimal> thisMapTuple = this.map.get(key);
                Tuple<BigDecimal, BigDecimal> anotherMapTuple = anotherMap.map.get(key);

                BigDecimal totalContacts = thisMapTuple.getValue().add(anotherMapTuple.getValue());

                BigDecimal concentration = thisMapTuple.getKey().multiply(thisMapTuple.getValue()).add(anotherMapTuple.getKey().multiply(anotherMapTuple.getValue()));
                concentration = concentration.divide(totalContacts, MathContext.DECIMAL128);

                this.map.replace(key, new Tuple<>(concentration, totalContacts));
            }
            else{
                this.map.put(key, anotherMap.map.get(key));
            }
        }
        this.totalOfContacts = this.totalOfContacts.add(anotherMap.totalOfContacts);

        this.applyReductionOfValues();
    }

    @Override
    public void applyReductionOfValues(){
        for(Coord key : this.map.keySet()){
            Tuple<BigDecimal, BigDecimal> tuple = this.map.get(key);

            tuple.setKey(tuple.getKey().divide(new BigDecimal(2), MathContext.DECIMAL128));
            tuple.setValue(tuple.getValue().divide(new BigDecimal(2), MathContext.DECIMAL128));
        }
        this.totalOfContacts = this.totalOfContacts.divide(new BigDecimal(2), MathContext.DECIMAL128);
    }

    @Override
    public BigDecimal getRegionNrOfContacts(Coord region) {
        if(this.map.containsKey(region)){
            return (this.map.get(region).getValue());
        }
        return (new BigDecimal(0));
    }

    @Override
    public void setRegionNrOfContacts(Coord region, BigDecimal contacts) {
        if(!this.map.containsKey(region)){
            this.map.put(region, new Tuple<BigDecimal, BigDecimal>(contacts, new BigDecimal(1)));
        }
        else{
            this.map.replace(region, new Tuple<BigDecimal, BigDecimal>(contacts, new BigDecimal(1)));
        }
    }
}
