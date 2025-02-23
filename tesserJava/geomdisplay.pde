class Arc{
  double cx,cy,r,phi,psi;
  Arc(double ix,double iy,double ir,double iphi,double ipsi){
   cx=ix;
   cy=iy;
   r=ir;
   phi=iphi;
   psi=ipsi;
  }
  
  Complex cCenter(){
   return new Complex(cx,cy); 
  }
  
  void display(double R){
   arc((float)(R*cx), (float)(R*cy),(float)(R*r),(float)(R*r),(float)(phi),(float)(psi),OPEN); 
  }
}

class Lens{
  double psi,phi;
  gMotion Motion;
  Arc Marc,Barc,IHarc,OHarc,Larc,Darc;
  Complex shift,bb;
  Lens(double iphi,double ipsi){
    phi=iphi;
    psi=ipsi;
    Marc=smallArc(0,0,1,phi, psi);
    Barc=bigArc(0,0,1,phi, psi);
    Larc=flipBigArc(Barc);
    shift=Larc.cCenter();
    IHarc=getHyperArc(Barc);
    OHarc=getHyperArc(Larc);
    bb=IHarc.cCenter();
    bb.setAbs(bb.abs()-IHarc.r);
    Motion=(new gMotion(bb.neg())).power(2);
  }  
}

class Model{
  //Arc BaseArc;
  //radius of base circle
  double R;
  //canvas shift
  float xpt;
  float ypt;
  //Lenses 
  ArrayList<Lens> Lenses;
    //Inner shift 
  gMotion dismotion;
  //Aux variables
  int newArcMode;
  boolean OverFlag;
  Complex p1,bigp,pinput;
  Lens newLens;
  gLine newline;
  gArc newarc;
  gSemiLine newsline;
  
  Model(double iR){
    //init Lenses and content
    Lenses = new  ArrayList<Lens>();
    dismotion=new gMotion();
    //displayparameters
    R=iR;
    xpt=width/2;
    ypt=height/2;
    bigp=new Complex();
  }
  
  void DrawBaseModel(){
    stroke(0);
    background(245,245,220);
    fill(255);
    strokeWeight(2);
    ellipse(0,0,(float)R,(float)R);
  }
  //Lenses
  void DrawLensTop(Lens L){
    fill(255);
    stroke(0);
    strokeWeight(2);
    L.Larc.display(R);
  }
  void DrawLensBot(Lens L){
    stroke(190,80,60);
    fill(0);
    L.IHarc.display(R);
    stroke(0);
    L.OHarc.display(R);
  }
  void DrawLensContent(diagram D,Lens L){
    noFill();
    for(int l=0;l<D.gLines.size();l++){
      drawEl(D.gLines.get(l),L);
    }
    for(int l=0;l<D.gSemiLines.size();l++){
      drawEl(D.gSemiLines.get(l),L);
    }
    for(int l=0;l<D.gArcs.size();l++){drawEl(D.gArcs.get(l),L);}
    for(int l=0;l<D.gPoints.size();l++){
      gPoint cp=D.gPoints.get(l);
      Complex pp=L.Motion.apply(dismotion.apply(cp.p));
      float size=getSpotSizeBase(pp);
      strokeWeight(1);
      fill(255);
      Spot(L.Larc.cCenter().add(pp),size,cp.label,R);
    }  
  }
  void DrawLenses(diagram D){
    for(int a=0;a<Lenses.size();a++){
      Lens L=Lenses.get(a);
      DrawLensTop(L);
      DrawLensContent(D,L);
      DrawLensBot(L);
    }
  }
  //Inner view
  void DrawInnerModel(){
    stroke(190,80,60);
    strokeWeight(2);
    noFill();
    for(int a=0;a<Lenses.size();a++){
      noFill();
      strokeWeight(1);
      stroke(190,80,60);
      Lenses.get(a).IHarc.display(R);
    }
  }
  //Content
  void drawContent(diagram D){
    noFill();
    for(int l=0;l<D.gLines.size();l++){
      drawEl(D.gLines.get(l));
    }
    for(int l=0;l<D.gSemiLines.size();l++){
      drawEl(D.gSemiLines.get(l));
    }
    for(int l=0;l<D.gArcs.size();l++){drawEl(D.gArcs.get(l));}
    for(int l=0;l<D.gPoints.size();l++){
      gPoint cp=D.gPoints.get(l);
      Complex pp=dismotion.apply(cp.p);
      float size=getSpotSizeBase(pp);
      strokeWeight(1);
      fill(255);
      Spot(pp,size,cp.label,R);
    }  
  }
  void drawEl(gLine l){
      double phip=dismotion.apply(l.phi);
      double psip=dismotion.apply(l.psi);
      noFill();
      getHyperArc(0,0,phip,psip).display(R);
  }
  void drawEl(gSemiLine l){
      double phip=dismotion.apply(l.phi);
      Complex pp=dismotion.apply(l.p);
      noFill();
      getHyperArc(0,0,phip,pp).display(R);
  }
  void drawEl(gArc l){
      Complex pp1=dismotion.apply(l.p1);
      Complex pp2=dismotion.apply(l.p2);
      noFill();
      getHyperArc(0,0,pp1,pp2).display(R);
  }
  void drawEl(gLine l,Lens L){
      double phip=L.Motion.apply(dismotion.apply(l.phi));
      double psip=L.Motion.apply(dismotion.apply(l.psi));
      noFill();
      getHyperArc(L.shift.re,L.shift.im,phip,psip).display(R);
  }
  void drawEl(gSemiLine l,Lens L){
      double phip=L.Motion.apply(dismotion.apply(l.phi));
      Complex pp=L.Motion.apply(dismotion.apply(l.p));
      noFill();
      getHyperArc(L.shift.re,L.shift.im,phip,pp).display(R);
  }
  void drawEl(gArc l,Lens L){
      Complex pp1=L.Motion.apply(dismotion.apply(l.p1));
      Complex pp2=L.Motion.apply(dismotion.apply(l.p2));
      noFill();
      getHyperArc(L.shift.re,L.shift.im,pp1,pp2).display(R);
  }
  //functions
  //Add Lens
  void DrawAddLens0(){
    bigp=pinput;       
    bigp.setAbs(R);
    stroke(0,200,20);
    fill(255);
    ellipse((float)bigp.re, (float)bigp.im, SpotR, SpotR);
  }
  void DrawAddLens1(diagram D){
        pinput.setAbs(R);
        if (angdiff(bigp.arg(),pinput.arg())<HALF_PI){
          OverFlag=true;
          newLens=new Lens(bigp.arg(),pinput.arg());
          DrawLensTop(newLens);
          DrawLensContent(D,newLens);
          DrawLensBot(newLens);
        }else{
          OverFlag=false; 
        }
        ellipse((float)bigp.re,(float) bigp.im, SpotR, SpotR);
        ellipse((float)pinput.re, (float)pinput.im, SpotR, SpotR);
  }
  void DrawAddLens2(){
        if(OverFlag){Lenses.add(newLens);}
  }
  //add point
  void DrawAddInnerDot0(){
        if (pinput.abs()<R){
        p1=dismotion.applyinv(pinput.div(R));
        fill(255);
        stroke(0,200,20);
        ellipse((float)pinput.re, (float)pinput.im, SpotR, SpotR);
        OverFlag=true;
        }else{
          OverFlag=false;
        }
  }
  void DrawAddInnerDot1(diagram D){
    if(OverFlag){
        String pname = String.format("P%d", D.gPoints.size());
        D.gPoints.add(new gPoint(p1,pname));
    }
  }
  //draw line
  void DrawAddLine0(){
      bigp=pinput;
      if (bigp.abs()<R){
         p1=dismotion.applyinv(bigp.div(R));
         OverFlag=true;
         stroke(0,200,20);
      }else{
          bigp.setAbs(R);
          OverFlag=false;
          stroke(200,20,0);
      }      
      fill(255);
      ellipse((float)bigp.re, (float)bigp.im, SpotR, SpotR);
  }
  void DrawAddLine1(){
        if (pinput.abs()<R){
           Complex p2=dismotion.applyinv(pinput.div(R));
           if (OverFlag){
             newarc=new gArc(p1,p2);
             newArcMode=0;
           }else{
             newsline=new gSemiLine(bigp.arg(),p2);
             newArcMode=1;
           }
        }
        else{
           if (OverFlag){
             newsline=new gSemiLine(pinput.arg(),p1);
             newArcMode=1;
           }else{
             newline=new gLine(bigp.arg(),pinput.arg());
             newArcMode=2;
           }
           pinput.setAbs(R);
        }
        strokeWeight(3);        
        noFill();
        switch (newArcMode){
          case 0:
           stroke(0,200,20);
           drawEl(newarc);
           break;
          case 1:
            stroke(20,0,200);
           drawEl(newsline);
           break;
          case 2:
           stroke(200,20,0);
           drawEl(newline);
           break;
        }
        fill(255);
        ellipse((float)bigp.re, (float)bigp.im, SpotR, SpotR);
        ellipse((float)pinput.re, (float)pinput.im, SpotR, SpotR);
  }
  void DrawAddLine2(diagram D){
        switch (newArcMode){
          case 0:
           D.gArcs.add(newarc);
           break;
          case 1:
           D.gSemiLines.add(newsline);
           break;
          case 2:
           D.gLines.add(newline);
           break;
        }        
  }
 //misc
  float getSpotSizeBase(Complex p){    
    double d=R*(1-p.abs());
    if (d>=SpotTreshold){
      return SpotR;
    }
    if (d>=DotThreshold){
      return (float)(SpotR*(d-DotThreshold)/(SpotTreshold-DotThreshold));
    }
    return 0;
 }
 void DragReset(){
     dismotion=new gMotion(); 
 }
 void Reset(diagram D){
    Lenses =new ArrayList<Lens>();
    D.gPoints=new ArrayList<gPoint>();
    D.gLines=new ArrayList<gLine>();
    D.gSemiLines=new ArrayList<gSemiLine>();
    D.gArcs = new ArrayList<gArc>();
    dismotion=new gMotion();
 }
 void Pan(){
     xpt-=(pmouseX-mouseX);
     ypt-=(pmouseY-mouseY);
 }
 void Drag(){
     if (pinput.abs()<R){
       bigp=new Complex(pmouseX-xpt,pmouseY-ypt);
       dismotion.update(new gMotion(pinput.div(R),bigp.div(R)));
    }
 }
}

//Arc Display functions

Arc bigArc(double x,double y,double r,double phi,double psi){
 return new Arc(x,y,r,getTar1(phi,psi),getTar2(phi,psi));  
}

Arc bigArc(Complex pc,double r,Complex p1,Complex p2){
 double phi=p1.sub(pc).arg();
 double psi=p2.sub(pc).arg();
 return new Arc(pc.re,pc.im,r,getTar1(phi,psi),getTar2(phi,psi));  
}

Arc smallArc(double x,double y,double r,double phi,double psi){
 return new Arc(x,y,r,getTar2(phi,psi),getTar1(phi,psi)+TWO_PI);  
}

Arc smallArc(Complex pc,double r,Complex p1,Complex p2){
 double phi=p1.sub(pc).arg();
 double psi=p2.sub(pc).arg();
 return new Arc(pc.re,pc.im,r,getTar2(phi,psi),getTar1(phi,psi)+TWO_PI);  
}

Arc flipBigArc(Arc a){
  double an1=(a.phi+a.psi)/2;
  double an2=(a.phi-a.psi)/2;
  return new Arc(a.cx+2*(Math.cos(an2))*Math.cos(an1),a.cy+2*(Math.cos(an2))*Math.sin(an1),a.r,a.phi+PI,a.psi+PI);
}

Arc getHyperArc(double x,double y,double phi,double psi){
  double an1=(phi+psi)/2;
  double an2=(phi-psi)/2;
  double tar1=getTar1(phi,psi);
  double tar2=getTar2(phi,psi);        
  return new Arc((x+1/Math.cos(an2)*Math.cos(an1)),(y+1/Math.cos(an2)*Math.sin(an1)),(Math.abs(Math.tan(an2))),(tar1+HALF_PI),(tar2-HALF_PI));
}

Arc getHyperArc(double x,double y,double phi,Complex p){
  gMotion mp1=new gMotion(p);
  double alpha=mp1.applyinv(phi);
  double psi=mp1.apply(alpha+PI);   
  double an1=(phi+psi)/2;
  double an2=(phi-psi)/2;
  Complex pcc=new Complex(x,y);
  Complex pc=new Complex();
  Complex eip=new Complex();
  eip.setPolar((double)1,phi);
  pc.setPolar((1/Math.cos(an2)),an1);
  return smallArc(pcc.add(pc).re,pcc.add(pc).im,(Math.abs(Math.tan(an2))),eip.sub(pc).arg(),p.sub(pc).arg());
}

Arc getHyperArc(double x,double y,Complex p1,Complex p2){
  if (p1==p2){return new Arc(x,y,p1.abs(),p1.arg(),p1.arg());}
  gMotion mp1=new gMotion(p1);
  double alpha=mp1.applyinv(p2).arg();
  double phi=mp1.apply(alpha);
  double psi=mp1.apply(alpha+PI);   
  double an1=(phi+psi)/2;
  double an2=(phi-psi)/2;
  Complex p=new Complex();
  p.setPolar((1/Math.cos(an2)),an1);
  return smallArc((x+p.re),(y+p.im),(Math.abs(Math.tan(an2))),p1.sub(p).arg(),p2.sub(p).arg());
}

Arc getHyperArc(Arc a){        
  return getHyperArc(a.cx,a.cy,a.phi,a.psi);
}

void Spot(Complex p,double r,String label,double R){
  ellipse((float)(R*p.re),(float)(R*p.im),(float)r,(float)r);
  fill(0);
  if (r*2>5){
        textSize((float)r*2);
        text(label,(float)(R*p.re),(float)(R*p.im-2*r));
 }
}

//Aux Functions
// angles to draw a big arc
double getTar1(double phi,double psi){
  if(phi>=psi){
         if ((phi-psi)>=PI){return psi;}
         else{return phi-TWO_PI;}
       }
  else{
         if ((psi-phi)>=PI){return phi;}
        else{return psi-TWO_PI;}
  } 
}

double getTar2(double phi,double psi){
  if(phi>=psi){
         if ((phi-psi)>=PI){return phi;}
         else{return psi;}}
  else{
         if ((psi-phi)>=PI){return psi;}
        else{return phi;}
  } 
}

double angdiff(double phi,double psi){
  return getTar1(phi,psi)+TWO_PI-getTar2(phi,psi);
}
