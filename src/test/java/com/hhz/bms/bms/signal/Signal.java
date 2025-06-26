package com.hhz.bms.bms.signal;

class Signal {
    private double Mx;
    private double Mi;
    private double Ix;
    private double Ii;

    public Signal(double Mx, double Mi, double Ix, double Ii) {
        this.Mx = Mx;
        this.Mi = Mi;
        this.Ix = Ix;
        this.Ii = Ii;
    }

    @Override
    public String toString() {
        return String.format("{\"Mx\":%.1f,\"Mi\":%.1f,\"Ix\":%.1f,\"Ii\":%.1f}", Mx, Mi, Ix, Ii);
    }
}