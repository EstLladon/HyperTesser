enum ArcType{
    BIG,
    SMALL,
    HYPER
  }

class Arc{  
  Complex c;
  double r,phi,psi;
  Arc(double x,double y,double ir,double iphi,double ipsi,ArcType it){   
   switch(it){
    case BIG:
      c=new Complex(x,y);
      r=ir;
      phi=getTar1(iphi,ipsi);
      psi=getTar2(iphi,ipsi); 
      break;
    case SMALL:
      c=new Complex(x,y);
      r=ir;
      phi=getTar2(iphi,ipsi);
      psi=getTar1(iphi,ipsi)+TWO_PI;
      break;
    case HYPER:
      double an1=(iphi+ipsi)/2;
      double an2=(iphi-ipsi)/2;
      double tar1=getTar1(iphi,ipsi);
      double tar2=getTar2(iphi,ipsi);   
      Complex cc=new Complex();
      cc.setPolar(1/Math.cos(an2),an1);
      c=new Complex(x,y).add(cc);
      r=Math.abs(Math.tan(an2));
      phi=tar1+HALF_PI;
      psi=tar2-HALF_PI;
      break;
   }
  }
  
  Arc(double iphi,double ipsi,ArcType it){   
   switch(it){
    case BIG:
      c=new Complex();
      r=1;
      phi=getTar1(iphi,ipsi);
      psi=getTar2(iphi,ipsi); 
      break;
    case SMALL:
      c=new Complex();
      r=1;
      phi=getTar2(iphi,ipsi);
      psi=getTar1(iphi,ipsi)+TWO_PI;
      break;
    case HYPER:
      Complex ieiphi=eip(iphi);
      Complex ieipsi=eip(ipsi);
      Complex middle=(ieiphi.add(ieipsi)).div(2);
      c=middle.inv().conj();
      r=ieipsi.sub(c).abs();
      double an1=ieipsi.sub(c).arg();
      double an2=ieiphi.sub(c).arg();
      phi=getTar2(an1,an2);
      psi=getTar1(an1,an2)+TWO_PI;
      break;
   }
  }
  
  Arc(Complex z1,Complex z2){   
   if (z1==z2){
        c=z1;
        r=0;
        phi=0;
        psi=0;
   }else{
        gMotion mp1=new gMotion(z1);
        double alpha=mp1.applyinv(z2).arg();
        double iphi=mp1.apply(alpha);
        double ipsi=mp1.apply(alpha+PI);   
        double an1=(iphi+ipsi)/2;
        double an2=(iphi-ipsi)/2;
        c=new Complex();
        c.setPolar((1/Math.cos(an2)),an1);
        //r=Math.abs(Math.tan(an2));
        r=z1.sub(c).abs();
        double iphi2=z1.sub(c).arg();
        double ipsi2=z2.sub(c).arg();
        phi=getTar2(iphi2,ipsi2);
        psi=getTar1(iphi2,ipsi2)+TWO_PI;
   }
  }
  
  Arc(Complex ic,double iphi,Complex iz){
    gMotion mp1=new gMotion(iz.sub(ic));
    double alpha=mp1.applyinv(iphi);
    double ipsi=mp1.apply(alpha+PI);   
    double an1=(iphi+ipsi)/2;
    double an2=(iphi-ipsi)/2;
    Complex eip=eip(iphi);
    Complex cc=new Complex();
    cc.setPolar(1/Math.cos(an2),an1);
    c=ic.add(cc);
    r=Math.abs(Math.tan(an2));
    double iphi2=ic.add(eip).sub(c).arg();
    double ipsi2=iz.sub(c).arg();
    phi=getTar2(iphi2,ipsi2);
    psi=getTar1(iphi2,ipsi2)+TWO_PI;     
  }
  
  Arc getHyper(){
   return new Arc(c.re,c.im,r,phi,psi,ArcType.HYPER); 
  }
  
  void move(Complex m){
   c=c.add(m); 
  }
    
  Complex eiphi(){
   Complex res=new Complex();
   res.setPolar(r,phi);
   return res;
  }
  
  Complex eipsi(){
   Complex res=new Complex();
   res.setPolar(r,psi);
   return res;
  }
  
  Complex e1(){
   Complex res=new Complex();
   res.setPolar(r,phi);
   return c.add(res);
  }
  
  Complex e2(){
   Complex res=new Complex();
   res.setPolar(r,psi);
   return c.add(res);
  }
  
  void display(double R,int[] SColor,int[] FColor){
    stroke(SColor);
     fill(FColor) ;
   arc((float)(R*c.re), (float)(R*c.im),(float)(R*r),(float)(R*r),(float)(phi),(float)(psi),OPEN); 
  }
  
  void display(double R,int[] SColor){
    stroke(SColor);
    noFill();
    arc((float)(R*c.re), (float)(R*c.im),(float)(R*r),(float)(R*r),(float)(phi),(float)(psi),OPEN); 
  }
  
  void displayline(double R){
   line((float)(R*e1().re), (float)(R*e1().im),(float)(R*e2().re), (float)(R*e2().im)); 
  }
  
  boolean isBig(){
   return psi-phi>PI; 
  }
  
  Arc flip(){
    Complex nc=c.add(eiphi()).add(eipsi());
    if (isBig()){
      return new Arc(nc.re,nc.im,r,phi+PI,psi+PI,ArcType.BIG);
    }else{
      return new Arc(nc.re,nc.im,r,phi+PI,psi+PI,ArcType.SMALL);
    }
  }
}

void Spot(Complex z,double r,Label label,double R){
  ellipse((float)(R*z.re),(float)(R*z.im),(float)r,(float)r);
  fill(textColor);
  label.display(z,(r/SpotR)*labelTSize,R);//text(label,(float)(R*z.re),(float)(R*z.im-2*r));
}

//Aux Functions
// angles to draw a big arc
public static double getTar1(double phi,double psi){
  if(phi>=psi){
         if ((phi-psi)>=PI){return psi;}
         else{return phi-TWO_PI;}
       }
  else{
         if ((psi-phi)>=PI){return phi;}
        else{return psi-TWO_PI;}
  } 
}

public static double getTar2(double phi,double psi){
  if(phi>=psi){
         if ((phi-psi)>=PI){return phi;}
         else{return psi;}}
  else{
         if ((psi-phi)>=PI){return psi;}
        else{return phi;}
  } 
}
//angle diff for small arc
public static double angdiff(double phi,double psi){
  return getTar1(phi,psi)+TWO_PI-getTar2(phi,psi);
}
