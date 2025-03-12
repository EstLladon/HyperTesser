import complexnumbers.Complex;

class HyperUtils {
    static class gPoint {
        Complex z;

        gPoint(Complex iz) {
            z = iz;
        }
    }

    static class gLine {
        double phi, psi;

        gLine(double iphi, double ipsi) {
            phi = iphi;
            psi = ipsi;
        }
    }

    static class gSemiLine {
        double phi;
        Complex z;

        gSemiLine(double iphi, Complex iz) {
            phi = iphi;
            z = iz;
        }
    }

    static class gArc {
        Complex z1, z2;

        gArc(Complex iz1, Complex iz2) {
            z1 = iz1;
            z2 = iz2;
        }
    }

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

        gMotion(double phi, Complex t) {
            a = new Complex(1);
            b = t.neg();
            c = t.conj().neg();
            update(new gMotion(phi));
            update(new gMotion(t));
        }

        gMotion(Complex t) {
            //(1,t,conj(t))
            a = new Complex(1);
            b = t;
            c = t.conj();
        }

        gMotion(Complex pz, Complex cz) {
            a = new Complex(1);
            b = pz.neg();
            c = pz.conj().neg();
            update(new gMotion(apply(cz)));
            update(new gMotion(pz));
        }

        Complex apply(Complex z) {
            return z.mul(a).add(b).div(z.mul(c).add(1));
        }

        Complex applyinv(Complex z) {
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

        gArc apply(gArc l) {
            return new gArc(apply(l.z1), apply(l.z2));
        }

        gArc applyinv(gArc l) {
            return new gArc(applyinv(l.z1), applyinv(l.z2));
        }

        gMotion combine(gMotion m) {
            //a,b,c=motion1
            //e,f,g=motion2
            // B=b*g+1
            Complex B = b.mul(m.c).add(1);
            Complex na = a.mul(m.a).add(c.mul(m.b)).div(B);//(a*e+c*f)/B;
            Complex nb = b.mul(m.a).add(m.b).div(B);//(b*e+f)/B;
            Complex nc = a.mul(m.c).add(c).div(B);//(a*g+c)/B;
            return new gMotion(na, nb, nc);
        }

        void update(gMotion m) {
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

        void preupdate(gMotion m) {
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
    }

    static class hPoint {
        double[] a = new double[3];

        hPoint() {
            a[0] = 1;
            a[1] = 0;
            a[2] = 0;
        }

        hPoint(double[] ia){
            a=new double[]{ia[0],ia[1],ia[2]};
        }

        hPoint(Complex z) {
            double x = z.re;
            double y = z.im;
            a[1] = x;
            a[2] = y;
            a[0] = Math.sqrt(x * x + y * y + 1);
        }

        Complex getz() {
            return new Complex(a[1], a[2]);
        }
    }


    static class hLine {
        double phi, psi;

        hLine(double iphi, double ipsi) {
            phi = iphi;
            psi = ipsi;
        }
    }

    static class hSemiLine {
        double phi;
        hPoint P = new hPoint();

        hSemiLine(double iphi, Complex z) {
            phi = iphi;
            P=new hPoint(z);
        }
        hSemiLine(double iphi, hPoint iP) {
            phi = iphi;
            P=iP;
        }
    }

    static class hArc {
        hPoint p1;
        hPoint p2;

        hArc(Complex z1, Complex z2) {
            p1=new hPoint(z1);
            p1=new hPoint(z2);
        }

        hArc(hPoint iP1, hPoint iP2) {
            p1=iP1;
            p1=iP2;
        }
    }

    static class hMotion {
        double[][] mat=new double[3][3];

        hMotion() {
            mat= new double[][]{{1, 0, 0},
                                {0, 1, 0},
                                {0, 0, 1}};
        }

        hMotion(double[][] i){
            //mat = new double[3][3];
            mat= new double[][]{{i[0][0], i[0][1], i[0][2]},
                                {i[1][0], i[1][1], i[1][2]},
                                {i[2][0], i[2][1], i[2][2]}};
        }

        hMotion(double phi) {
            mat= new double[][]{{1,             0,              0},
                                {0, Math.cos(phi), -Math.sin(phi)},
                                {0, Math.sin(phi), Math.cos(phi)}};
        }

        hMotion(Complex z) {
            double x=z.re;
            double y=z.im;
            double t=Math.sqrt(x*x+y*y+1);
            double r=z.absq();
            mat = new double[][]{{t,           x,              y},
                                 {x, x*x/(t+1)+1,  x * y / (t+1)},
                                 {y,   x*y/(t+1),   y*y/(t+1)+1}};
        }

        hMotion(double phi, Complex z) {
            new hMotion(z.neg());
            update(new hMotion(phi));
            update(new hMotion(z));
        }

        hMotion(Complex pz, Complex cz) {
            double x=-pz.re;
            double y=-pz.im;
            double t=Math.sqrt(x*x+y*y+1);
            double r=pz.absq();
            mat = new double[][]{{t,           x,              y},
                                 {x, x*x/(t+1)+1,  x * y / (t+1)},
                                 {y,   x*y/(t+1),   y*y/(t+1)+1}};
            Complex ccz=apply(cz);
            preupdate(new hMotion(ccz));
            preupdate(new hMotion(pz));
        }

        //private double determinant() {
        //    return mat[0][0] * (mat[1][1] * mat[2][2] - mat[1][2] * mat[2][1])
        //            - mat[0][1] * (mat[1][0] * mat[2][2] - mat[1][2] * mat[2][0])
        //            + mat[0][2] * (mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0]);
        //}

        private double[][] adjugate() {
            double[][] adj = new double[3][3];

            adj[0][0] =  (mat[1][1] * mat[2][2] - mat[1][2] * mat[2][1]);
            adj[0][1] = -(mat[0][1] * mat[2][2] - mat[0][2] * mat[2][1]);
            adj[0][2] =  (mat[0][1] * mat[1][2] - mat[0][2] * mat[1][1]);

            adj[1][0] = -(mat[1][0] * mat[2][2] - mat[1][2] * mat[2][0]);
            adj[1][1] =  (mat[0][0] * mat[2][2] - mat[0][2] * mat[2][0]);
            adj[1][2] = -(mat[0][0] * mat[1][2] - mat[0][2] * mat[1][0]);

            adj[2][0] =  (mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0]);
            adj[2][1] = -(mat[0][0] * mat[2][1] - mat[0][1] * mat[2][0]);
            adj[2][2] =  (mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0]);

            return adj;
        }

        Complex getz(){
            return new Complex(mat[1][0],mat[2][0]);
        }
        double[] apply(double [] a){
            return new double[]{mat[0][0]*a[0]+mat[0][1]*a[1]+mat[0][2]*a[2],
                                mat[1][0]*a[0]+mat[1][1]*a[1]+mat[1][2]*a[2],
                                mat[2][0]*a[0]+mat[2][1]*a[1]+mat[2][2]*a[2]};

        }

        double[] applyinv(double [] a){
            double[][] matt=adjugate();
            return new double[]{matt[0][0]*a[0]+matt[0][1]*a[1]+matt[0][2]*a[2],
                    matt[1][0]*a[0]+matt[1][1]*a[1]+matt[1][2]*a[2],
                    matt[2][0]*a[0]+matt[2][1]*a[1]+matt[2][2]*a[2]};

        }

        hMotion inv(){
            return new hMotion(adjugate());
        }

        Complex apply(Complex z) {
            return new Complex(mat[1][0]*Math.sqrt(1+ z.absq())+mat[1][1]*z.re+mat[1][2]*z.im,mat[2][0]*Math.sqrt(1+ z.absq())+mat[2][1]*z.re+mat[2][2]*z.im);
        }

        Complex applyinv(Complex z) {
            double[][] matt=adjugate();
            return new Complex(matt[1][0]*Math.sqrt(1+ z.absq())+matt[1][1]*z.re+matt[1][2]*z.im,matt[2][0]*Math.sqrt(1+ z.absq())+matt[2][1]*z.re+matt[2][2]*z.im);
        }

        hPoint apply(hPoint P) {
            return new hPoint(apply(P.a));
        }

        hPoint applyinv(hPoint P) {
            return new hPoint(applyinv(P.a));
        }

        double apply(double phi) {
            return new Complex(mat[1][0]+mat[1][1]*Math.cos(phi)+mat[1][2]*Math.sin(phi),mat[2][0]+mat[2][1]*Math.cos(phi)+mat[2][2]*Math.sin(phi)).arg();
        }

        double applyinv(double phi) {
            double[][] matt=adjugate();
            return new Complex(matt[1][0]+matt[1][1]*Math.cos(phi)+matt[1][2]*Math.sin(phi),matt[2][0]+matt[2][1]*Math.cos(phi)+matt[2][2]*Math.sin(phi)).arg();
        }

        hLine apply(hLine l) {
            return new hLine(apply(l.phi), apply(l.psi));
        }

        hLine applyinv(hLine l) {
            return new hLine(applyinv(l.phi), applyinv(l.psi));
        }

        hSemiLine apply(hSemiLine l) {
            return new hSemiLine(apply(l.phi), apply(l.P));
        }

        hSemiLine applyinv(hSemiLine l) {
            return new hSemiLine(applyinv(l.phi), applyinv(l.P));
        }

        hArc apply(hArc l) {
            return new hArc(apply(l.p1), apply(l.p2));
        }

        gArc applyinv(gArc l) {
            return new gArc(applyinv(l.z1), applyinv(l.z2));
        }

        hMotion combine(hMotion m) {
            double[][] result = mult(mat,m.mat);
            return new hMotion(result);
        }

        void update(hMotion m){
            mat= mult(mat,m.mat);
            //mat[0][0]=i[0][0];
            //mat[0][1]=i[0][1];
            //mat[0][2]=i[0][2];
            //mat[1][0]=i[1][0];
            //mat[1][1]=i[1][1];
            //mat[1][2]=i[1][2];
            //mat[2][0]=i[2][0];
            //mat[2][1]=i[2][1];
            //mat[2][2]=i[2][2];
        }

        void preupdate(hMotion m){
            mat= mult(m.mat,mat);
        }
    }

    static double[][] mult(double[][] A,double[][] B){
        double[][] result=new double[3][3];
        result[0][0]=A[0][0]*B[0][0]+A[0][1]*B[1][0]+A[0][2]*B[2][0];
        result[0][1]=A[0][0]*B[0][1]+A[0][1]*B[1][1]+A[0][2]*B[2][1];
        result[0][2]=A[0][0]*B[0][2]+A[0][1]*B[1][2]+A[0][2]*B[2][2];
        result[1][0]=A[1][0]*B[0][0]+A[1][1]*B[1][0]+A[1][2]*B[2][0];
        result[1][1]=A[1][0]*B[0][1]+A[1][1]*B[1][1]+A[1][2]*B[2][1];
        result[1][2]=A[1][0]*B[0][2]+A[1][1]*B[1][2]+A[1][2]*B[2][2];
        result[2][0]=A[2][0]*B[0][0]+A[2][1]*B[1][0]+A[2][2]*B[2][0];
        result[2][1]=A[2][0]*B[0][1]+A[2][1]*B[1][1]+A[2][2]*B[2][1];
        result[2][2]=A[2][0]*B[0][2]+A[2][1]*B[1][2]+A[2][2]*B[2][2];
        return result;
    }
}