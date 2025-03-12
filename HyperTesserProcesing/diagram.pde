//diagram
class diagram{
  ArrayList<dPoint> dPoints;
  ArrayList<gLine> gLines;
  ArrayList<gSemiLine> gSemiLines;
  ArrayList<gArc> gArcs;
  gPoint newpoint;
  gSemiLine newsline;
  gLine newline;
  gArc newarc;
  diagram(){
    dPoints=new ArrayList<dPoint>();
    gLines=new ArrayList<gLine>();
    gSemiLines=new ArrayList<gSemiLine>();
    gArcs = new ArrayList<gArc>();
  }
}

class dPoint extends gPoint{
  Label label;
  dPoint(Complex z){
    super(z);
    label=new Label();
  }
  dPoint(Complex z,Label ilabel){
    super(z);
    label=ilabel;
  }
}

class hLine{
  double phi,psi;
  hLine(double iphi,double ipsi){
   phi=iphi;
   psi=ipsi;
  }
  hLine(Complex z1,Complex z2){
    if (z1==z2){
     phi=z1.arg();
     psi=phi+PI;
    }else{
    gMotion mp1=new gMotion(z1);
    phi=mp1.apply(mp1.applyinv(z2).arg());
    psi=mp1.apply(mp1.applyinv(z2).arg()+PI);
    }
  }
  hLine(Complex z1,double iphi){    
    gMotion mp1=new gMotion(z1);
    phi=iphi;
    psi=mp1.apply(mp1.applyinv(phi)+PI);    
  }
}

class hSemiLine{
  double phi;
  Complex z;
  hSemiLine(double iphi,Complex iz){
   phi=iphi;
   z=iz;
  }
}

class hArc{
  Complex z1,z2;
  hArc(Complex iz1, Complex iz2){
   z1=iz1;
   z2=iz2;
  }
}
