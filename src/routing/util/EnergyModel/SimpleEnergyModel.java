package routing.util.EnergyModel;

import core.DTNHost;
import core.ModuleCommunicationBus;
import core.Settings;

/**
 * Created by gustavo on 11/09/16.
 */
public class SimpleEnergyModel extends EnergyModel {
    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     *
     * @param s      The settings object
     * @param comBus
     * @param host
     */
    public SimpleEnergyModel(Settings s, ModuleCommunicationBus comBus, DTNHost host) {
        super(s, comBus, host);
    }

    @Override
    public double getBestScanTime(double min, double defaultTime, double max) {
        return (defaultTime);
    }
}
