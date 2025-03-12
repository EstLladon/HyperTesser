class gPoint{
  Complex z;
  gPoint(Complex iz){
   z=iz;
  }
}

class gLine{
  double phi,psi;
  gLine(double iphi,double ipsi){
   phi=iphi;
   psi=ipsi;
  }
  gLine(Complex z1,Complex z2){
    if (z1==z2){
     phi=z1.arg();
     psi=phi+PI;
    }else{
    gMotion mp1=new gMotion(z1);
    phi=mp1.apply(mp1.applyinv(z2).arg());
    psi=mp1.apply(mp1.applyinv(z2).arg()+PI);
    }
  }
  gLine(Complex z1,double iphi){    
    gMotion mp1=new gMotion(z1);
    phi=iphi;
    psi=mp1.apply(mp1.applyinv(phi)+PI);    
  }
}

class gSemiLine{
  double phi;
  Complex z;
  gSemiLine(double iphi,Complex iz){
   phi=iphi;
   z=iz;
  }
}

class gArc{
  Complex z1,z2;
  gArc(Complex iz1, Complex iz2){
   z1=iz1;
   z2=iz2;
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
  gMotion(double phi){
    //(exp(i*phi),t*exp(i*phi),conj(b))
    a=(new Complex(0,phi)).exp();
    b=new Complex();
    c=new Complex();
  }
  gMotion(double phi,Complex t){
    a=new Complex(1);
    b=t.neg();
    c=t.conj().neg();
    update(new gMotion(phi));
    update(new gMotion(t));
  }
  gMotion(Complex t){
   //(1,t,conj(t)) 
    a=new Complex(1);
    b=t;
    c=t.conj();
  }
  gMotion(Complex pz,Complex cz){
    a=new Complex(1);
    b=pz.neg();
    c=pz.conj().neg();
    update(new gMotion(apply(cz)));
    update(new gMotion(pz));
  }
  Complex apply(Complex z){
    return z.mul(a).add(b).div(z.mul(c).add(1));
  }
  Complex applyinv(Complex z){
    return z.neg().add(b).div(z.mul(c).sub(a));
  }
  double apply(double phi){
    return apply(eip(phi)).arg();
  }
  double applyinv(double phi){
    return applyinv(eip(phi)).arg();
  }
  gPoint apply(gPoint P){return new gPoint(apply(P.z));}
  gPoint applyinv(gPoint P){return new gPoint(applyinv(P.z));}
  gLine apply(gLine l){return new gLine(apply(l.phi),apply(l.psi));}
  gLine applyinv(gLine l){return new gLine(applyinv(l.phi),applyinv(l.psi));}
  gSemiLine apply(gSemiLine l){return new gSemiLine(apply(l.phi),apply(l.z));}
  gSemiLine applyinv(gSemiLine l){return new gSemiLine(applyinv(l.phi),applyinv(l.z));}
  gMotion inv(){return new gMotion(a.inv(),b.div(a).neg(),c.div(a).neg());}
  gArc apply(gArc l){return new gArc(apply(l.z1),apply(l.z2));}
  gArc applyinv(gArc l){return new gArc(applyinv(l.z1),applyinv(l.z2));}
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
  void preupdate(gMotion m){
    //a,b,c=motion1    
    //e,f,g=motion2
    Complex B=m.b.mul(c).add(new Complex(1));// B=b*g+1
    Complex na=m.a.mul(a).add(m.c.mul(b)).div(B);//(a*e+c*f)/B;
    Complex nb=m.b.mul(a).add(b).div(B);//(b*e+f)/B;
    Complex nc=m.a.mul(c).add(m.c).div(B);//(a*g+c)/B;
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

//different models
Complex PtoBK(Complex z){
  Complex res=new Complex();
  double r=z.abs();
  res.setPolar(2*r/(1+r*r),z.arg());
  return res;
}

Complex BKtoP(Complex z){
  Complex res=new Complex();
  double r=z.abs();
  res.setPolar((1-Math.sqrt(1-r*r))/r,z.arg());
  return res;
}

//misc functions
Complex eip(double phi){
 Complex res = new Complex();
 res.setPolar(1,phi);
 return res;
}


double distFromZero(Complex z){
  double r=z.abs();
 return Math.log((1+r)/(1-r)); 
}
