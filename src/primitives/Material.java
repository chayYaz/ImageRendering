package primitives;

public class Material {
    //for local effects
    public Double3 _kD = Double3.ZERO;
    public Double3 _kS = Double3.ZERO;
    public int nShininess = 0;


    public Double3 kT;  //transparency
    public Double3 kR; //reflective - mirror

    //max values to stop recursion of functions calling each other
    //values were chosen according to the given instructions
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;

    /***
     * setters in similar form to builder pattern - they return the current object
     */
    public Material setkD(Double3 kD) {
        _kD = kD;
        return this;
    }

    public Material setkS(Double3 kS) {
        _kS = kS;
        return this;
    }

    public Material setkD(double kD) {
        _kD = new Double3(kD);
        return this;
    }

    public Material setkS(double kS) {
        _kS = new Double3(kS);
        return this;
    }

    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }

    public Material setkT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    public Material setkR(Double3 kR) {
        this.kR = kR;
        return this;
    }
}
