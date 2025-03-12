import complexnumbers.Complex;

class Lens{
    double psi,phi;
    HyperUtils.gMotion Motion;
    Arc Marc,Barc,IHarc,OHarc,Larc,Darc;
    Complex shift,bb,lenscenter,point1,point2,lenscenterflat,lensdir;
    double dir,dist;

    Lens(double iphi,double ipsi){
        phi=iphi;
        psi=ipsi;
        Marc=new Arc(phi, psi, ArcType.SMALL);
        Barc=new Arc(phi, psi, ArcType.BIG);
        point1=Barc.eiphi();
        point2=Barc.eipsi();
        Larc=Barc.flip();
        shift=Larc.c;
        dir=Larc.c.arg();
        lensdir=new Complex();
        lensdir.setPolar(1,dir);
        IHarc=new Arc(0,0,1,phi,psi,ArcType.HYPER);
        Darc=new Arc(0,0,1,phi,psi,ArcType.HYPER);
        Darc.move(shift);
        OHarc=Larc.getHyper();
        lenscenterflat=(point1.add(point2)).div(2);
        lenscenter= ComplexUtils.BKtoP(lenscenterflat);
        dist=lenscenterflat.abs();
        Motion=(new HyperUtils.gMotion(lenscenter.neg())).power(2);
    }

    Lens(){
        phi=0;
        psi=0;
        shift=new Complex();
        dir=0;
        lensdir=new Complex(1);
        Motion=(new HyperUtils.gMotion());
    }

    boolean isNullLens(){
        return phi==0&&psi==0;
    }

    HyperUtils.gArc trimLine(Complex z1, Complex z2){
        if (isNullLens()){return new HyperUtils.gArc(z1,z2);}
        Complex zz1=z1,zz2=z2;
        double z1eipre=z1.div(lensdir).re;
        double z2eipre=z2.div(lensdir).re;
        if ((z1eipre<-dist)&&(z2eipre<-dist)){
            return new HyperUtils.gArc(lenscenterflat,lenscenterflat);
        }
        if ((z1eipre>=-dist)&&(z2eipre>=-dist)){
            return new HyperUtils.gArc(z1,z2);
        }
        if (z1eipre<-dist){
            zz1=z1.mul((z2eipre+dist)/(z2eipre-z1eipre)).add(z2.mul((-z1eipre-dist)/(z2eipre-z1eipre)));
        }else{
            zz2=z1.mul((-z2eipre-dist)/(z1eipre-z2eipre)).add(z2.mul((z1eipre+dist)/(z1eipre-z2eipre)));
        }
        return new HyperUtils.gArc(zz1,zz2);
    }

    boolean inView(Complex z){
        return isNullLens()||z.div(lensdir).re>=-dist;
    }
}

