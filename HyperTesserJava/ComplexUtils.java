import complexnumbers.Complex;

//import static complexnumbers.Cpx.*;

public class ComplexUtils {
    //different models
    static Complex PtoBK(Complex z){
        return z.div((1+z.absq())/2);
    }

    static Complex BKtoP(Complex z){
        return z.div(1+Math.sqrt(1-z.absq()));
    }

    static Complex PtoH(Complex z){
        return z.div((1-z.absq())/2);
    }

    static Complex HtoP(Complex z){
        return z.div(1+Math.sqrt(1+z.absq()));
    }

    static Complex BKtoH(Complex z){
        return (z.div(Math.sqrt(1-z.absq())));
    }

    static Complex HtoBK(Complex z){
        return z.div(Math.sqrt(1+z.absq()));
    }
    //misc functions
    static Complex eip(double phi){
        Complex res = new Complex();
        res.setPolar(1,phi);
        return res;
    }

    static double distFromZero(Complex z){
        double r=z.abs();
        return Math.log((1+r)/(1-r));
    }
}
