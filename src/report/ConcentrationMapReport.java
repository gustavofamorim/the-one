package report;

import core.DTNHost;
import core.SimError;
import core.UpdateListener;
import routing.util.EnergyModel.EnergyModel;

/**
 * Created by gustavo on 27/08/16.
 */
public class ConcentrationMapReport extends SnapshotReport
        implements UpdateListener {

    @Override
    protected void writeSnapshot(DTNHost host) {
        Double value = (Double)host.getComBus().
                getProperty(EnergyModel.ENERGY_VALUE_ID);
        if (value == null) {
            throw new SimError("Host " + host +
                    " don't have concentration map enabled");
        }
        write(host.toString() + " " + host.getConcentrationMap().toString());
    }
}
