import complexnumbers.Complex;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static java.util.Arrays.deepToString;
import static processing.core.PConstants.TWO_PI;
import static processing.core.PApplet.println;

class HyperUtils {

    static class gMotion {
        Complex a;
        Complex b;
        Complex c;

        gMotion(Complex ia, Complex ib, Complex ic) {
            a = ia;
            b = ib;
            c = ic;
        }

        gMotion() {
            a = new Complex(1);
            b = new Complex();
            c = b;
        }

        gMotion(double phi) {
            //(exp(i*phi),t*exp(i*phi),conj(b))
            a = (new Complex(0, phi)).exp();
            b = new Complex();
            c = new Complex();
        }

        gMotion(double phi, @NotNull Complex t) {
            a = new Complex(1);
            b = t.neg();
            c = t.conj().neg();
            update(new gMotion(phi));
            update(new gMotion(t));
        }

        gMotion(@NotNull Complex t) {
            //(1,t,conj(t))
            a = new Complex(1);
            b = t;
            c = t.conj();
        }

        gMotion(@NotNull Complex pz, Complex cz) {
            a = new Complex(1);
            b = pz.neg();
            c = pz.conj().neg();
            update(new gMotion(apply(cz)));
            update(new gMotion(pz));
        }

        Complex apply(@NotNull Complex z) {
            return z.mul(a).add(b).div(z.mul(c).add(1));
        }

        Complex applyinv(@NotNull Complex z) {
            return z.neg().add(b).div(z.mul(c).sub(a));
        }

        double apply(double phi) {
            return apply(ComplexUtils.eip(phi)).arg();
        }

        double applyinv(double phi) {
            return applyinv(ComplexUtils.eip(phi)).arg();
        }

        gMotion inv() {
            return new gMotion(a.inv(), b.div(a).neg(), c.div(a).neg());
        }

        gMotion combine(@NotNull gMotion m) {
            //a,b,c=motion1
            //e,f,g=motion2
            // B=b*g+1
            Complex B = b.mul(m.c).add(1);
            Complex na = a.mul(m.a).add(c.mul(m.b)).div(B);//(a*e+c*f)/B;
            Complex nb = b.mul(m.a).add(m.b).div(B);//(b*e+f)/B;
            Complex nc = a.mul(m.c).add(c).div(B);//(a*g+c)/B;
            return new gMotion(na, nb, nc);
        }

        void update(@NotNull gMotion m) {
            //a,b,c=motion1
            //e,f,g=motion2
            Complex B = b.mul(m.c).add(new Complex(1));// B=b*g+1
            Complex na = a.mul(m.a).add(c.mul(m.b)).div(B);//(a*e+c*f)/B;
            Complex nb = b.mul(m.a).add(m.b).div(B);//(b*e+f)/B;
            Complex nc = a.mul(m.c).add(c).div(B);//(a*g+c)/B;
            a = na;
            b = nb;
            c = nc;
        }

        void preupdate(@NotNull gMotion m) {
            //a,b,c=motion1
            //e,f,g=motion2
            Complex B = m.b.mul(c).add(new Complex(1));// B=b*g+1
            Complex na = m.a.mul(a).add(m.c.mul(b)).div(B);//(a*e+c*f)/B;
            Complex nb = m.b.mul(a).add(b).div(B);//(b*e+f)/B;
            Complex nc = m.a.mul(c).add(m.c).div(B);//(a*g+c)/B;
            a = na;
            b = nb;
            c = nc;
        }

        gMotion power(int n) {
            gMotion res = new gMotion();
            for (int i = 0; i < n; i++) {
                res = combine(res);
            }
            return res;
        }

        public String toString(){
            return a.toString()+" "+b.toString()+" "+c.toString();
        }
    }
    static gMotion checkgMotion(myMotion m,motionDispenser disp){
        Complex b=disp.toP(m.getz());
        if (b.equals(0,0)){return new gMotion(m.apply(0));}
        Complex a=ComplexUtils.eip(m.apply(b.arg())).div(ComplexUtils.eip(b.arg()));
        Complex c=b.conj().neg().div(a);
        return new HyperUtils.gMotion(a,b,c);
    }
}

class hMotion{
    //T body;
    hMotion(){}
    //hMotion(T a){body =a;}
    hMotion(hMotion b){}
    hMotion(Complex z){}
    hMotion(double phi){}
    hMotion(double phi,Complex z){}
    hMotion(Complex pz,Complex cz){}
    hMotion inv(){return new hMotion();}
    Complex apply(Complex z){return new Complex();}
    Complex applyinv(Complex z){return inv().apply(z);}
    double apply(double phi){return phi;}
    double applyinv(double phi){
        return inv().apply(phi);
    }
    hMotion combine(hMotion b){return new hMotion();}
    void update (hMotion ia){}
    void preupdate (hMotion ia){}
    Complex getz(){return new Complex();}
    Complex geteiphi(){return new Complex();}
}

abstract class hMotiondispenser{
    abstract hMotion id();
    abstract hMotion getmotion(Complex z);
    abstract hMotion getmotion(double phi);
    abstract hMotion getmotion(double phi,Complex z);
    abstract hMotion getmotion(Complex pz,Complex cz);
    abstract Complex toP(Complex z);
    abstract Complex toBK(Complex z);
    abstract Complex fromP(Complex z);
    abstract Complex fromBK(Complex z);
}

class ggMotion extends hMotion{
    Complex a;
    Complex b;
    //Complex c;
    ggMotion(){a=new Complex(1);b=new Complex();}
    ggMotion(hMotion m){a=m.geteiphi();b=m.getz();}
    ggMotion(gArc a){this.a=a.z1();b=a.z2();}
    ggMotion(Complex z){a=new Complex(1);b=z;};
    ggMotion(double phi){a=new Complex(0, phi).exp();b=new Complex();};
    ggMotion(double phi,Complex z){
        a=new Complex(1);
        b=z.neg();
        update(new ggMotion(phi));
        update(new ggMotion(z));
    };
    ggMotion(Complex pz,Complex cz){
        a=new Complex(1);
        b=pz.neg();
        update(new ggMotion(apply(cz)));
        update(new ggMotion(pz));
    };
    @Override
    hMotion inv() {
        return new ggMotion(new gArc(a.inv(), b.div(a).neg()));
    }
    @Override
    Complex apply(Complex z) {
        return z.mul(a).add(b).div(z.mul(b.conj().mul(a)).add(1));
    }
    @Override
    Complex applyinv(Complex z){return z.neg().add(b).div(z.mul(b.conj().mul(a)).sub(a));}
    @Override
    double apply(double phi) {return apply(ComplexUtils.eip(phi)).arg();}
    @Override
    double applyinv(double phi) {return applyinv(ComplexUtils.eip(phi)).arg();}
    @Override
    ggMotion combine(hMotion m) {
        //a,b,c=motion1
        //e,f,g=motion2
        // B=b*g+1
        Complex c=b.conj().mul(a);
        Complex ma=m.geteiphi();
        Complex mb=m.getz();
        Complex mc=mb.conj().mul(ma);
        Complex B = b.mul(mc).add(1);
        Complex na = a.mul(ma).add(c.mul(mb)).div(B);//(a*e+c*f)/B;
        Complex nb = b.mul(ma).add(mb).div(B);//(b*e+f)/B;
        return new ggMotion(new gArc(na, nb));
    }
    @Override
    void update(hMotion ia) {
        ggMotion res=combine(ia);
        a=res.a;
        b=res.b;
    }
    @Override
    void preupdate (hMotion ia){
        hMotion res=ia.combine(this);
        a=res.geteiphi();
        b=res.getz();
    }
    @Override
    Complex getz() {return b;}
    @Override
    Complex geteiphi() {return a;}
}

class ggMotionDispenser extends hMotiondispenser{
    ggMotion id() {
        return new ggMotion();
    }

    ggMotion getmotion(Complex z) {return new ggMotion(z);}

    ggMotion getmotion(double phi) {return new ggMotion(phi);}

    ggMotion getmotion(double phi, Complex z) {return new ggMotion(phi,z);}

    ggMotion getmotion(Complex pz, Complex cz) {return new ggMotion(pz,cz);}

    Complex toP(Complex z) {return z;}

    Complex toBK(Complex z) {return ComplexUtils.PtoBK(z);}

    Complex fromP(Complex z) {return z;}

    Complex fromBK(Complex z) {return ComplexUtils.BKtoP(z);}
}

class hhMotion extends hMotion{
    double [][] a;
    hhMotion(double[][] a){
        this.a=a;
    }

    hhMotion(){
        this.a=new double[][]{{1,0,0},{0,1,0},{0,0,1}};
    }

    hhMotion(Complex z) {
        double x=z.re;
        double y=z.im;
        double t=Math.sqrt(x*x+y*y+1);
        a= new double[][]{{t,           x,              y},
                          {x, x*x/(t+1)+1,  x * y / (t+1)},
                          {y,   x*y/(t+1),   y*y/(t+1)+1}};
    }

    hhMotion (double phi) {
        a=new double[][]{{1,             0,              0},
                         {0, Math.cos(phi), -Math.sin(phi)},
                         {0, Math.sin(phi), Math.cos(phi)}};
    }

    hhMotion(double phi,Complex z){
        double x=-z.re;
        double y=-z.im;
        double t=Math.sqrt(x*x+y*y+1);
        a= new double[][]{{t,           x,              y},
                          {x, x*x/(t+1)+1,  x * y / (t+1)},
                          {y,   x*y/(t+1),   y*y/(t+1)+1}};
        update(new hhMotion(phi));
        update(new hhMotion(z));
    };

    hhMotion(Complex pz,Complex cz){
        double x=-pz.re;
        double y=-pz.im;
        double t=Math.sqrt(x*x+y*y+1);
        a= new double[][]{{t,           x,              y},
                          {x, x*x/(t+1)+1,  x * y / (t+1)},
                          {y,   x*y/(t+1),   y*y/(t+1)+1}};
        update(new hhMotion(apply(cz)));
        update(new hhMotion(pz));
    };

    public String toString() {
        return deepToString(a);
    }

    hhMotion inv() {
        double[][] adj = new double[3][3];

        adj[0][0] =  (a[1][1] * a[2][2] - a[1][2] * a[2][1]);
        adj[0][1] = -(a[0][1] * a[2][2] - a[0][2] * a[2][1]);
        adj[0][2] =  (a[0][1] * a[1][2] - a[0][2] * a[1][1]);

        adj[1][0] = -(a[1][0] * a[2][2] - a[1][2] * a[2][0]);
        adj[1][1] =  (a[0][0] * a[2][2] - a[0][2] * a[2][0]);
        adj[1][2] = -(a[0][0] * a[1][2] - a[0][2] * a[1][0]);

        adj[2][0] =  (a[1][0] * a[2][1] - a[1][1] * a[2][0]);
        adj[2][1] = -(a[0][0] * a[2][1] - a[0][1] * a[2][0]);
        adj[2][2] =  (a[0][0] * a[1][1] - a[0][1] * a[1][0]);

        return new hhMotion(adj);
    }

    Complex apply(Complex z) {
        double x=z.re;
        double y=z.im;
        double t=Math.sqrt(1+ z.absq());
        return new Complex(a[1][0]*t+a[1][1]*x+a[1][2]*y,a[2][0]*t+a[2][1]*x+a[2][2]*y);
    }

    double apply(double phi) {
        return new Complex(a[1][0]+a[1][1]*Math.cos(phi)+a[1][2]*Math.sin(phi),a[2][0]+a[2][1]*Math.cos(phi)+a[2][2]*Math.sin(phi)).arg();
    }

    hhMotion combine(hMotion m) {
        double[][] result=new double[3][3];
        double[][] B=new double[3][3];
        if (m instanceof hhMotion res) {
            B=res.a.clone();
        }else {
            Complex z=ComplexUtils.PtoH(m.getz().div(m.geteiphi()));
            double x=z.re;
            double y=z.im;
            double t=Math.sqrt(x*x+y*y+1);
            double[][] move= new double[][]{{t,           x,              y},
                                            {x, x*x/(t+1)+1,  x * y / (t+1)},
                                            {y,   x*y/(t+1),   y*y/(t+1)+1}};
            Complex eiphi=m.geteiphi();
            double [][] turn =new double[][]{{1,             0,              0},
                                             {0, eiphi.re, -eiphi.im},
                                             {0, eiphi.im, eiphi.re}};
            B[0][0]=turn[0][0]*move[0][0]+turn[0][1]*move[1][0]+turn[0][2]*move[2][0];
            B[0][1]=turn[0][0]*move[0][1]+turn[0][1]*move[1][1]+turn[0][2]*move[2][1];
            B[0][2]=turn[0][0]*move[0][2]+turn[0][1]*move[1][2]+turn[0][2]*move[2][2];
            B[1][0]=turn[1][0]*move[0][0]+turn[1][1]*move[1][0]+turn[1][2]*move[2][0];
            B[1][1]=turn[1][0]*move[0][1]+turn[1][1]*move[1][1]+turn[1][2]*move[2][1];
            B[1][2]=turn[1][0]*move[0][2]+turn[1][1]*move[1][2]+turn[1][2]*move[2][2];
            B[2][0]=turn[2][0]*move[0][0]+turn[2][1]*move[1][0]+turn[2][2]*move[2][0];
            B[2][1]=turn[2][0]*move[0][1]+turn[2][1]*move[1][1]+turn[2][2]*move[2][1];
            B[2][2]=turn[2][0]*move[0][2]+turn[2][1]*move[1][2]+turn[2][2]*move[2][2];
            //println("boop");
        }
        result[0][0]=a[0][0]*B[0][0]+a[0][1]*B[1][0]+a[0][2]*B[2][0];
        result[0][1]=a[0][0]*B[0][1]+a[0][1]*B[1][1]+a[0][2]*B[2][1];
        result[0][2]=a[0][0]*B[0][2]+a[0][1]*B[1][2]+a[0][2]*B[2][2];
        result[1][0]=a[1][0]*B[0][0]+a[1][1]*B[1][0]+a[1][2]*B[2][0];
        result[1][1]=a[1][0]*B[0][1]+a[1][1]*B[1][1]+a[1][2]*B[2][1];
        result[1][2]=a[1][0]*B[0][2]+a[1][1]*B[1][2]+a[1][2]*B[2][2];
        result[2][0]=a[2][0]*B[0][0]+a[2][1]*B[1][0]+a[2][2]*B[2][0];
        result[2][1]=a[2][0]*B[0][1]+a[2][1]*B[1][1]+a[2][2]*B[2][1];
        result[2][2]=a[2][0]*B[0][2]+a[2][1]*B[1][2]+a[2][2]*B[2][2];
        return new hhMotion(result);
    }

    void update(hMotion m) {
        if (m instanceof hhMotion res) {
            a=combine(res).a.clone();
        }else {
            hhMotion move = new hhMotion(ComplexUtils.PtoH(m.getz().div(m.geteiphi())));
            hhMotion turn = new hhMotion(m.geteiphi().arg());
            hhMotion ia = turn.combine(move);
            hhMotion res = combine(ia);
            a = res.a.clone();
            //println("boopupdate");
        }
    }

    void preupdate(hMotion mm) {
        if (mm instanceof hhMotion res) {
            a=res.combine(this).a.clone();
        }else {
            hMotion m = mm.combine(this);
            hhMotion move = new hhMotion(ComplexUtils.PtoH(m.getz().div(m.geteiphi())));
            hhMotion turn = new hhMotion(m.geteiphi().arg());
            hhMotion ia = turn.combine(move);
            a = ia.combine(this).a.clone();
            //println("booppreupdate");
        }
    }

    public Complex getz(){
        return ComplexUtils.HtoP(new Complex(a[1][0],a[2][0]));
    }

    Complex geteiphi() {
        return new Complex(a[1][0],a[2][0]).div(new Complex(a[0][1],a[0][2]));
    }
}

class hhMotionDispenser extends hMotiondispenser{
    hhMotion id() {
        return new hhMotion();
    }

    hhMotion getmotion(Complex z) {return new hhMotion(z);}

    hhMotion getmotion(double phi) {return new hhMotion(phi);}

    hhMotion getmotion(double phi, Complex z) {return new hhMotion(phi,z);}

    hhMotion getmotion(Complex pz, Complex cz) {return new hhMotion(pz,cz);}

    Complex toP(Complex z) {return ComplexUtils.HtoP(z);}

    Complex toBK(Complex z) {return ComplexUtils.HtoBK(z);}

    Complex fromP(Complex z) {return ComplexUtils.PtoH(z);}

    Complex fromBK(Complex z) {return ComplexUtils.BKtoH(z);}
}