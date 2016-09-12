package routing.util.EnergyModel;

import core.DTNHost;
import core.ModuleCommunicationBus;
import core.Settings;
import core.SimClock;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by gustavo on 11/09/16.
 */
public class AdaptativeScanAjustmentModel extends EnergyModel{
    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     *
     * @param s      The settings object
     * @param comBus
     * @param host
     */
    public AdaptativeScanAjustmentModel(Settings s, ModuleCommunicationBus comBus, DTNHost host) {
        super(s, comBus, host);
    }

    @Override
    public double getBestScanTime(double min, double defaultTime, double max) {

        if(this.adjustmentWarmup >= SimClock.getTime()){
            return (defaultTime);
        }

        BigDecimal concentration = this.host.getConcentrationMap().getConcentration(this.host.getLocation());
        BigDecimal maxConcentration = this.host.getConcentrationMap().getMaxConcentration();

        BigDecimal remappedConcentration =
                this.remapRange(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO, maxConcentration, concentration);

        //min + (1-concentration)*max
        BigDecimal result =
                BigDecimal.ONE.subtract(remappedConcentration).multiply(new BigDecimal(max)).add(new BigDecimal(min));

        return (result.doubleValue());
    }

    private BigDecimal remapRange(BigDecimal min1, BigDecimal max1, BigDecimal min2, BigDecimal max2, BigDecimal value){

        BigDecimal delta1 = max1.subtract(min1);
        BigDecimal delta2 = max2.subtract(min2);
        BigDecimal valueDif = value.subtract(min1);

        return valueDif.multiply(delta2, MathContext.DECIMAL128).divide(delta1, MathContext.DECIMAL128).add(min2);
    }
}
