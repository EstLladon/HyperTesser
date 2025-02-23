class gPoint{
  Complex p;
  String label;
  gPoint(Complex ip,String ilabel){
   p=ip;
   label=ilabel;
  }
}

class gLine{
  double phi,psi;
  String status;
  gLine(double iphi,double ipsi){
   phi=iphi;
   psi=ipsi;
  }
  gLine(Complex p1,Complex p2){
    if (p1==p2){
     phi=p1.arg();
     psi=phi+PI;
    }else{
    gMotion mp1=new gMotion(p1);
    phi=mp1.apply(mp1.applyinv(p2).arg());
    psi=mp1.apply(mp1.applyinv(p2).arg()+PI);
    }
  }
  gLine(Complex p1,double iphi){    
    gMotion mp1=new gMotion(p1);
    phi=iphi;
    psi=mp1.apply(mp1.applyinv(phi)+PI);    
  }
}

class gSemiLine{
  double phi;
  Complex p;
  String label;
  gSemiLine(double iphi,Complex ip){
   phi=iphi;
   p=ip;
  }
}

class gArc{
  Complex p1,p2;
  String status; 
  gArc(Complex ip1, Complex ip2){
   p1=ip1;
   p2=ip2;
  }
}

class gMotion{
  Complex a;
  Complex b;
  Complex c;
  gMotion(Complex ia,Complex ib,Complex ic){
    a=ia;
    b=ib;
    c=ic;
  }
  gMotion(){
    a=new Complex(1);
    b=new Complex();
    c=b;
  }
  gMotion(double phi,Complex t){
    //(exp(i*phi),t*exp(i*phi),conj(b))
    a=(new Complex(0,phi)).exp();
    b=a.mul(t);
    c=t.conj();
  }
  gMotion(Complex t){
   //(1,t,conj(t)) 
    a=new Complex(1);
    b=t;
    c=t.conj();
  }
  gMotion(Complex pp,Complex cp){
    a=new Complex(1);
    b=pp.neg();
    c=pp.conj().neg();
    update(new gMotion(apply(cp)));
    update(new gMotion(pp));
  }
  Complex apply(Complex p){
    return p.mul(a).add(b).div(p.mul(c).add(1));
  }
  Complex applyinv(Complex p){
    return p.neg().add(b).div(p.mul(c).sub(a));
  }
  double apply(double phi){
    Complex eip=new Complex();
    eip.setPolar((double)1,phi);
    return apply(eip).arg();
  }
  double applyinv(double phi){
    Complex eip=new Complex();
    eip.setPolar((double)1,phi);
    return applyinv(eip).arg();
  }
  gPoint apply(gPoint p){return new gPoint(apply(p.p),p.label);}
  gPoint applyinv(gPoint p){return new gPoint(applyinv(p.p),p.label);}
  gLine apply(gLine l){return new gLine(apply(l.phi),apply(l.psi));}
  gLine applyinv(gLine l){return new gLine(applyinv(l.phi),applyinv(l.psi));}
  gSemiLine apply(gSemiLine l){return new gSemiLine(apply(l.phi),apply(l.p));}
  gSemiLine applyinv(gSemiLine l){return new gSemiLine(applyinv(l.phi),applyinv(l.p));}
  gMotion inv(){return new gMotion(a.inv(),b.div(a).neg(),c.div(a).neg());}
  gArc apply(gArc l){return new gArc(apply(l.p1),apply(l.p2));}
  gArc applyinv(gArc l){return new gArc(applyinv(l.p1),applyinv(l.p2));}
  gMotion combine(gMotion m){
    //a,b,c=motion1    
    //e,f,g=motion2
    // B=b*g+1
    Complex B=b.mul(m.c).add(1);
    Complex na=a.mul(m.a).add(c.mul(m.b)).div(B);//(a*e+c*f)/B;
    Complex nb=b.mul(m.a).add(m.b).div(B);//(b*e+f)/B;
    Complex nc=a.mul(m.c).add(c).div(B);//(a*g+c)/B;
    return new gMotion(na,nb,nc);
  }
  void update(gMotion m){
    //a,b,c=motion1    
    //e,f,g=motion2
    Complex B=b.mul(m.c).add(new Complex(1));// B=b*g+1
    Complex na=a.mul(m.a).add(c.mul(m.b)).div(B);//(a*e+c*f)/B;
    Complex nb=b.mul(m.a).add(m.b).div(B);//(b*e+f)/B;
    Complex nc=a.mul(m.c).add(c).div(B);//(a*g+c)/B;
    a=na;
    b=nb;
    c=nc;
  }
  gMotion power(int n){
    gMotion res=new gMotion();
    for(int i=0;i<n;i++){
      res=combine(res);
    }
    return res;
  }
}

class diagram{
  ArrayList<gPoint> gPoints;
  ArrayList<gLine> gLines;
  ArrayList<gSemiLine> gSemiLines;
  ArrayList<gArc> gArcs;
  
  diagram(){
    gPoints=new ArrayList<gPoint>();
    gLines=new ArrayList<gLine>();
    gSemiLines=new ArrayList<gSemiLine>();
    gArcs = new ArrayList<gArc>();
  }
}
