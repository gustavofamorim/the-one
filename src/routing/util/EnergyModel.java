/*
 * Copyright 2011 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package routing.util;

import java.util.Random;

import core.*;

/**
 * Energy model for routing modules. Handles power use from scanning (device
 * discovery), scan responses, and data transmission. If scanning is done more
 * often than 1/s, constant scanning is assumed (and power consumption does not
 * increase from {@link #scanEnergy} value).
 */
public class EnergyModel implements ModuleCommunicationListener {
	/** Initial units of energy -setting id ({@value}). Can be either a
	 * single value, or a range of two values. In the latter case, the used
	 * value is a uniformly distributed random value between the two values. */
	public static final String INIT_ENERGY_S = "initialEnergy";

	/** Energy usage per scanning (device discovery) -setting id ({@value}). */
	public static final String SCAN_ENERGY_S = "scanEnergy";

	/** Energy usage per scanning (device discovery) response -setting id
	 * ({@value}). */
	public static final String SCAN_RSP_ENERGY_S = "scanResponseEnergy";

	/** Energy usage per second when transferring data 
	 * -setting id ({@value}). */
	public static final String TRANSMIT_ENERGY_S = "transmitEnergy";

	/** Energy update warmup period -setting id ({@value}). Defines the
	 * simulation time after which the energy level starts to decrease due to
	 * scanning, transmissions, etc. Default value = 0. If value of "-1" is
	 * defined, uses the value from the report warmup setting
	 * {@link report.Report#WARMUP_S} from the namespace
	 * {@value report.Report#REPORT_NS}. */
	public static final String WARMUP_S = "energyWarmup";

	/** Energy recharge period. If the value is > 0 then will recharge the
	 *  battery with it. If it's = 0 then never will recharge the battery.
	 *  If the time is < 0, then a randon time will be used to determine
	 *  the next rechage time.
	 * */
	public static final String RECHARGE_TIME_S = "rechargeTime";

	/** Energy value to recharge. If < 0 will recharge a random value of
	 * energy. Something else indicates the exact value of recharge.
	 */
	public static final String RECHARGE_CAPACITY_S = "rechargeCapacity";

	/** Energy usage per GPS scanning (location discovery) -setting id ({@value}). */
	public static final String GPS_SCAN_ENERGY_S = "gpsScanEnergy";

	/** {@link ModuleCommunicationBus} identifier for the "current amount of
	 * energy left" variable. Value type: double */
	public static final String ENERGY_VALUE_ID = "Energy.value";

	/** Initial energy levels from the settings */
	private final double[] initEnergy;
	private double warmupTime;
	/** current energy level */
	private double currentEnergy;
	/** energy usage per scan */
	private double scanEnergy;
	/** energy usage per transmitted byte */
	private double transmitEnergy;
	/** energy usage per device discovery response */
	private double scanResponseEnergy;
	/** Recharge period */
	private double rechargeTime;
	/** Amount of energy to increase on recharges */
	private double rechargeCapacity;
	/** Last recharge time */
	private double lastRecharge;
	/** energy usage per GPS scan */
	private double gpsScanEnergy;
	/** sim time of the last energy updated */
	private double lastUpdate;
	private ModuleCommunicationBus comBus;
	private static Random rng = null;

	/**
	 * Constructor. Creates a new message router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public EnergyModel(Settings s, ModuleCommunicationBus comBus) {
		this.initEnergy = s.getCsvDoubles(INIT_ENERGY_S);

		if (this.initEnergy.length != 1 && this.initEnergy.length != 2) {
			throw new SettingsError(INIT_ENERGY_S + " setting must have " +
					"either a single value or two comma separated values");
		}

		this.comBus = comBus;
		setEnergy(this.initEnergy);
		this.scanEnergy = s.getDouble(SCAN_ENERGY_S);
		this.transmitEnergy = s.getDouble(TRANSMIT_ENERGY_S);
		this.scanResponseEnergy = s.getDouble(SCAN_RSP_ENERGY_S);

		this.rechargeTime = this.processRechargeTime(s.getDouble(RECHARGE_TIME_S));
		this.rechargeCapacity = s.getDouble(RECHARGE_CAPACITY_S);
		this.lastRecharge = 0;

		this.gpsScanEnergy = s.contains(GPS_SCAN_ENERGY_S) ? s.getDouble(GPS_SCAN_ENERGY_S) : 0;

		if (s.contains(WARMUP_S)) {
			this.warmupTime = s.getInt(WARMUP_S);
			if (this.warmupTime == -1) {
				this.warmupTime = new Settings(report.Report.REPORT_NS).
					getInt(report.Report.WARMUP_S);
			}
		}
		else {
			this.warmupTime = 0;
		}
	}

	/**
	 * Copy constructor.
	 * @param proto The model prototype where setting values are copied from
	 */
	protected EnergyModel(EnergyModel proto) {
		this.initEnergy = proto.initEnergy;
		setEnergy(this.initEnergy);
		this.scanEnergy = proto.scanEnergy;
		this.transmitEnergy = proto.transmitEnergy;
		this.warmupTime  = proto.warmupTime;
		this.scanResponseEnergy = proto.scanResponseEnergy;
		this.comBus = null;
		this.lastUpdate = 0;
		this.rechargeTime = proto.rechargeTime;
		this.rechargeCapacity = proto.rechargeCapacity;
		this.lastRecharge = proto.lastRecharge;
		this.gpsScanEnergy = proto.gpsScanEnergy;
	}

	public EnergyModel replicate() {
		return new EnergyModel(this);
	}

	/** Determine the rechage time period.
	 * @param rechargeTime The value loaded from settings file.
	 */
	protected double processRechargeTime(double rechargeTime){
		if(rechargeTime < 0){
			return (new Random().nextDouble());
		}
		else{
			return (rechargeTime);
		}
	}

	/** Determine the if it's recharge time.
	 * @return true if it's recharge time and false otherwise.
	 */
	public boolean isRechargeTime(){
		if(rechargeTime == 0){
			return (false);
		}
		else if(SimClock.getTime() < (this.lastRecharge + this.rechargeTime)){
			return (false);
		}
		return (true);
	}

	/** Recharge the battery just if necessary.
	 * @return true if recharged and false otherwise.
	 */
	public void rechargeIfNecessary(){
		if(isRechargeTime()){
			this.lastRecharge = SimClock.getTime();
			this.currentEnergy += this.rechargeCapacity;
			comBus.updateProperty(ENERGY_VALUE_ID, this.currentEnergy);
		}
	}

	/**
	 * Sets the current energy level into the given range using uniform
	 * random distribution.
	 * @param range The min and max values of the range, or if only one value
	 * is given, that is used as the energy level
	 */
	protected void setEnergy(double range[]) {
		if (range.length == 1) {
			this.currentEnergy = range[0];
		}
		else {
			if (rng == null) {
				rng = new Random((int)(range[0] + range[1]));
			}
			this.currentEnergy = range[0] +
				rng.nextDouble() * (range[1] - range[0]);
		}
		comBus.addProperty(ENERGY_VALUE_ID, this.currentEnergy);
		this.comBus.subscribe(ENERGY_VALUE_ID, this);
	}

	/**
	 * Returns the current energy level
	 * @return the current energy level
	 */
	public double getEnergy() {
		return this.currentEnergy;
	}

	public boolean hasEnergy(){
		return (this.currentEnergy > 0 ? true : false);
	}

	/**
	 * Updates the current energy so that the given amount is reduced from it.
	 * If the energy level goes below zero, sets the level to zero.
	 * Does nothing if the warmup time has not passed.
	 * @param amount The amount of energy to reduce
	 */
	protected void reduceEnergy(double amount) {
		if (SimClock.getTime() < this.warmupTime) {
			return;
		}

		if (comBus == null) {
			return; /* model not initialized (via update) yet */
		}
		this.rechargeIfNecessary();
		if (amount >= this.currentEnergy) {
			comBus.updateProperty(ENERGY_VALUE_ID, 0.0);
		} else {
			comBus.updateDouble(ENERGY_VALUE_ID, -amount);
		}

	}

	/**
	 * Reduces the energy reserve for the amount that is used when another
	 * host connects (does device discovery)
	 */
	public void reduceDiscoveryEnergy() {
		reduceEnergy(this.scanResponseEnergy);
	}

	/**
	 * Reduces the energy reserve for the amount that is used when the host
	 * start a position discovery
	 */
	public void reduceGpsDiscoveryEnergy(){
		reduceEnergy(this.gpsScanEnergy);
	}

	/**
	 * Reduces the energy reserve for the amount that is used by sending data
	 * and scanning for the other nodes.
	 */
	public void update(NetworkInterface iface, ModuleCommunicationBus comBus) {
		double simTime = SimClock.getTime();
		double delta = simTime - this.lastUpdate;

		if (this.comBus == null) {
			this.comBus = comBus;
			this.comBus.addProperty(ENERGY_VALUE_ID, this.currentEnergy);
			this.comBus.subscribe(ENERGY_VALUE_ID, this);
		}

		if (simTime > this.lastUpdate && iface.isTransferring()) {
			/* sending or receiving data */
			reduceEnergy(delta * this.transmitEnergy);
		}
		this.lastUpdate = simTime;

		if (iface.isScanning()) {
			/* scanning at this update round */
			if (iface.getTransmitRange() > 0) {
				if (delta < 1) {
					reduceEnergy(this.scanEnergy * delta);
				} else {
					reduceEnergy(this.scanEnergy);
				}
			}
		}
	}

	/**
	 * Called by the combus if the energy value is changed
	 * @param key The energy ID
	 * @param newValue The new energy value
	 */
	public void moduleValueChanged(String key, Object newValue) {
		this.currentEnergy = (Double)newValue;
	}

}
