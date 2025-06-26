package com.hhz.bms.bms.signal;

class VehicleSignal {
    private int carId;
    private Integer warnId;
    private Signal signal;

    public VehicleSignal(int carId, Integer warnId, Signal signal) {
        this.carId = carId;
        this.warnId = warnId;
        this.signal = signal;
    }

    @Override
    public String toString() {
        if (warnId != null) {
            return String.format("{\"carId\":%d,\"warnId\":%d,\"signal\":%s}", carId, warnId, signal);
        } else {
            return String.format("{\"carId\":%d,\"signal\":%s}", carId, signal);
        }
    }

}