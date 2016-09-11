package routing.util.EnergyModel;

import core.DTNHost;
import core.ModuleCommunicationBus;
import core.Settings;
import routing.util.ConcentrationMap.ConcentrationMap;

/**
 * Created by gustavo on 11/09/16.
 */
public class LinearScanAjustmentModel extends EnergyModel {
    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     *
     * @param s      The settings object
     * @param comBus
     * @param host
     */
    public LinearScanAjustmentModel(Settings s, ModuleCommunicationBus comBus, DTNHost host) {
        super(s, comBus, host);
    }

    @Override
    public double getBestScanTime(double min, double defaultTime, double max) {
        double best;
        ConcentrationMap map = this.host.getConcentrationMap();

        double deltaI = min - max;
        double concentration = map.getConcentration(map.convertMapLocationToRegionKey(host.getLocation())).doubleValue();
        best = (deltaI * concentration) + max;
        return (best);
    }
}
