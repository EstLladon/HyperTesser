import complexnumbers.Complex;
import org.jetbrains.annotations.NotNull;

class hLens{
    double psi,phi;
    hMotion Motion;
    Arc Barc;
    Arc Larc;
    Complex shift;
    Complex lenscenter;
    Complex point1;
    Complex point2;
    Complex lenscenterflat;
    Complex lensdir;
    double dir;
    double dist;

    hLens(double iphi,double ipsi,hMotiondispenser disp){
        phi=iphi;
        psi=ipsi;
        Barc=new Arc(phi, psi, ArcType.BIG);
        point1=ComplexUtils.eip(phi);
        point2=ComplexUtils.eip(psi);
        Larc=Barc.flip();
        shift=Larc.c;
        dir=shift.arg();
        lensdir=new Complex();
        lensdir.setPolar(1,dir);
        lenscenterflat=(point1.add(point2)).div(2);
        lenscenter= ComplexUtils.BKtoP(lenscenterflat);
        dist=lenscenterflat.abs();
        hMotion m=disp.getmotion(disp.fromBK(lenscenterflat).neg());
        Motion=m.combine(m);
    }

    hLens(double iphi, double ipsi, @NotNull hLens P,hMotiondispenser disp){
        phi=P.Motion.apply(iphi);
        psi=P.Motion.apply(ipsi);
        Barc=new Arc(phi, psi, ArcType.BIG);
        point1=ComplexUtils.eip(phi);
        point2=ComplexUtils.eip(psi);
        Larc=Barc.flip();
        shift=Larc.c;
        dir=shift.arg();
        lensdir=new Complex();
        lensdir.setPolar(1,dir);
        lenscenterflat=(point1.add(point2)).div(2);
        lenscenter= ComplexUtils.BKtoP(lenscenterflat);
        dist=lenscenterflat.abs();
        hMotion m=disp.getmotion(disp.fromBK(lenscenterflat).neg());
        Motion=P.Motion.combine(m.combine(m));
        Larc.move(P.shift);
        shift=shift.add(P.shift);
    }

    hLens(hMotiondispenser disp){
        phi=0;
        psi=0;
        shift=new Complex();
        dir=0;
        lensdir=new Complex(1);
        Motion=disp.id();
    }

    boolean isNullLens(){
        return phi==0&&psi==0;
    }

    gArc trimLine(Complex z1, Complex z2){
        if (isNullLens()){return new gArc(z1,z2);}
        Complex zz1=z1,zz2=z2;
        double z1eipre=z1.div(lensdir).re;
        double z2eipre=z2.div(lensdir).re;
        if ((z1eipre<-dist)&&(z2eipre<-dist)){
            return new gArc(lenscenterflat,lenscenterflat);
        }
        if ((z1eipre>=-dist)&&(z2eipre>=-dist)){
            return new gArc(z1,z2);
        }
        if (z1eipre<-dist){
            zz1=z1.mul((z2eipre+dist)/(z2eipre-z1eipre)).add(z2.mul((-z1eipre-dist)/(z2eipre-z1eipre)));
        }else{
            zz2=z1.mul((-z2eipre-dist)/(z1eipre-z2eipre)).add(z2.mul((z1eipre+dist)/(z1eipre-z2eipre)));
        }
        return new gArc(zz1,zz2);
    }

    gArc trimLine(Complex z1, Complex z2,hLens L){
        if (isNullLens()){return new gArc(z1,z2);}
        Complex zz1=z1,zz2=z2;
        double z1eipre=z1.div(lensdir).re;
        double z2eipre=z2.div(lensdir).re;
        if ((z1eipre<-dist)&&(z2eipre<-dist)){
            return new gArc(lenscenterflat,lenscenterflat);
        }
        if ((z1eipre>=-dist)&&(z2eipre>=-dist)){
            return new gArc(z1,z2);
        }
        if (z1eipre<-dist){
            zz1=z1.mul((z2eipre+dist)/(z2eipre-z1eipre)).add(z2.mul((-z1eipre-dist)/(z2eipre-z1eipre)));
        }else{
            zz2=z1.mul((-z2eipre-dist)/(z1eipre-z2eipre)).add(z2.mul((z1eipre+dist)/(z1eipre-z2eipre)));
        }
        return new gArc(zz1,zz2);
    }

    boolean inView(Complex z){
        return isNullLens()||z.div(lensdir).re>=-dist;
    }

    boolean inView(double iphi){
        return inView(ComplexUtils.eip(iphi));
    }
}

