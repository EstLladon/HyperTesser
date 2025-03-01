class Model{
  //radius of base circle
  double R;
  //canvas shift
  float xpt;
  float ypt;
  //Lenses 
  ArrayList<Lens> Lenses;
  //Inner shift 
  gMotion dismotion;
  //type of Model
  int type=0;
  //Aux variables
  int newArcMode;
  boolean OverFlag;
  Complex p1,bigp,pinput;
  double newphi;
  Lens newLens;
  
  Model(double iR){
    //init Lenses and Motion
    Lenses = new  ArrayList<Lens>();
    dismotion=new gMotion();
    //displayparameters
    R=iR;
    xpt=width/2;
    ypt=height/2;
  }
  //Draw Main Model
  void DrawBase(){
    ellipse(0,0,(float)R,(float)R);
  }
  void DrawTesselation(tesselation T){
    gMotion tm;
    Complex tt=dismotion.applyinv(new Complex(T.bp));
    trgroupel te=T.getNearestGroupel(tt,(int)(2*distFromZero(tt)));
    for (gMotion wm:T.elements){          
          for(int i=0;i<T.p;i++){    
            tm=wm.combine(te.Motion);
            drawMotion(tm,T.aP,T.bP);
            wm.update(T.MA);
          }      
    }  
    drawEl(new gPoint(new Complex(),""));
  }
  void DrawPath(tesselation T){
    for (pathel pe:T.Path){
      drawEl(T.getgArc(pe));
    }
  }
  void DrawDiagram(diagram D){
    noFill();
    for(gLine l:D.gLines){drawEl(l);}
    for(gSemiLine s:D.gSemiLines){drawEl(s);}
    for(gArc a:D.gArcs){drawEl(a);}
    for(gPoint g:D.gPoints){drawEl(g);} 
  }
  //Lenses
  void DrawLenses(diagram D,tesselation T){
    for(Lens L:Lenses){
      DrawLens(D,T,L);
    }
  }
  
  void DrawLens(diagram D,tesselation T,Lens L){      
      stroke(colorScheme.lensColor);
      fill(colorScheme.modelColor);
      DrawLensTop(L);
      noFill();
      stroke(colorScheme.tessFillColor);
      strokeWeight(1);
      DrawTesselation(T,L);
      stroke(colorScheme.PathColor);
      strokeWeight(2);
      DrawPath(T,L);
      stroke(colorScheme.diagramColor);
      DrawDiagram(D,L);
      fill(colorScheme.diagramColor);
      drawEl(new gPoint(new Complex(),""),L);
      stroke(colorScheme.lensColor);
      fill(colorScheme.lensColor);
      DrawLensBot(L);
  }
  
  void DrawLensTop(Lens L){
    L.Larc.display(R);
  }
  
  void DrawLensBot(Lens L){
    switch(type){
    case 0:
      L.IHarc.display(R);
      L.OHarc.display(R);
      break;
    case 1:
      L.IHarc.displayline(R);
      L.OHarc.displayline(R);
      break;
    }
  }
  
  void DrawDiagram(diagram D,Lens L){
    noFill();
    for(gLine l:D.gLines){drawEl(l,L);}
    for(gSemiLine s:D.gSemiLines){drawEl(s,L);}
    for(gArc a:D.gArcs){drawEl(a,L);}
    for(gPoint g:D.gPoints){drawEl(g,L);}     
  }
  
  void DrawTesselation(tesselation T,Lens L){
      gMotion tm;
      Complex tt=dismotion.applyinv(L.Motion.applyinv(new Complex(T.bp)));
      trgroupel te=T.getNearestGroupel(tt,(int)(2*distFromZero(tt)/T.side));
      for (gMotion wm:T.elements){          
          for(int i=0;i<p;i++){    
            tm=wm.combine(te.Motion);            
            drawMotion(tm,T.aP,T.bP,L);
            wm.update(T.MA);
          }      
      }   
  }
  
  void DrawPath(tesselation T,Lens L){
    for (pathel pe:T.Path){
      drawEl(T.getgArc(pe),L);
    }
  }
  
  void DrawLensScheme(){
    noFill();
    switch(type){
    case 0:
      for(Lens L:Lenses){
        L.IHarc.display(R);
      }
      break;
    case 1:
      for(Lens L:Lenses){
        L.IHarc.displayline(R);
      }
      break;
    }
  }
  
  //Draw elements
  void drawEl(gPoint cp){
      Complex pp=new Complex();
      switch(type){
        case 0:
          pp=dismotion.apply(cp.p);
          break;
        case 1:
          pp=dismotion.apply(cp.p);
          pp=PtoBK(pp);
          break;     
      }
      if(pp.abs()<1-tolToBorder){
        float size=getSpotSizeBase(pp);
        strokeWeight(1);
        Spot(pp,size,cp.label,R);
      }
  }
  void drawEl(gLine l){
      double phip=dismotion.apply(l.phi);
      double psip=dismotion.apply(l.psi);
      if(Math.abs(phip-psip)>tolToBorder){
        noFill();
        switch(type){
        case 0:
          getHyperArc(0,0,phip,psip).display(R);
          break;
        case 1:
          getHyperArc(0,0,phip,psip).displayline(R);
          break;
        }
      }     
  }
  void drawEl(gSemiLine l){
      double phip=dismotion.apply(l.phi);
      Complex pp=dismotion.apply(l.p);
      noFill();      
      switch(type){
        case 0:
          getHyperArc(0,0,phip,pp).display(R);
          break;
        case 1:
          pp=PtoBK(pp);
          getHyperArc(0,0,phip,pp).displayline(R);
          break;
        }
  }
  void drawEl(gArc l){
      Complex pp1=dismotion.apply(l.p1);
      Complex pp2=dismotion.apply(l.p2);
      if (type==1){
       pp1=PtoBK(pp1);
       pp2=PtoBK(pp2);
      }
      if((pp1.abs()<1-tolToBorder)||(pp2.abs()<1-tolToBorder)){
        noFill();
        if ((pp1.abs()<toleranceToZero)||(pp2.abs()<toleranceToZero)||Math.abs(pp1.arg()-pp2.arg())<angleToleranceToZero){
          line((float)(pp1.re*R),(float)(pp1.im*R),(float)(pp2.re*R),(float)(pp2.im*R));
        }else{        
          switch(type){
          case 0:
            getHyperArc(0,0,pp1,pp2).display(R);
            break;
          case 1:
            getHyperArc(0,0,pp1,pp2).displayline(R);
            break;
          }
        }
      }
  }
  void drawEl(gPoint cp,Lens L){
      Complex pp=L.Motion.apply(dismotion.apply(cp.p));
      if (type==1){
       pp=PtoBK(pp);
      }
      float size=getSpotSizeBase(pp);
      strokeWeight(1);
      Spot(pp.add(L.shift),size,cp.label,R);
  }
  void drawEl(gLine l,Lens L){
      double phip=L.Motion.apply(dismotion.apply(l.phi));
      double psip=L.Motion.apply(dismotion.apply(l.psi));
      noFill();      
      switch(type){
        case 0:
          getHyperArc(L.shift.re,L.shift.im,phip,psip).display(R);
          break;
        case 1:
          //getHyperArc(L.shift.re,L.shift.im,phip,psip).displayline(R);
          Complex eip=new Complex();
          eip.setPolar(1,phip);
          Complex eips=new Complex();
          eips.setPolar(1,psip);
          gArc temp=L.trimLine(eip.add(L.shift),eips.add(L.shift));
          if (temp.p1.re>0)line((float)temp.p1.re,(float)temp.p1.im,(float)temp.p2.re,(float)temp.p2.im);
          break;
      }
  }
  void drawEl(gSemiLine l,Lens L){
      double phip=L.Motion.apply(dismotion.apply(l.phi));
      Complex pp=L.Motion.apply(dismotion.apply(l.p));
      noFill();
      switch(type){
        case 0:
          getHyperArc(L.shift.re,L.shift.im,phip,pp).display(R);
          break;
        case 1:
          pp=PtoBK(pp);
          //getHyperArc(L.shift.re,L.shift.im,phip,pp).displayline(R);
          Complex eip=new Complex();
          eip.setPolar(1,phip);
          gArc temp=L.trimLine(eip.add(L.shift),pp.add(L.shift));
          if (temp.p1.re>0)line((float)temp.p1.re,(float)temp.p1.im,(float)temp.p2.re,(float)temp.p2.im);
          break;
      }
  }
  void drawEl(gArc l,Lens L){
      Complex pp1=L.Motion.apply(dismotion.apply(l.p1));
      Complex pp2=L.Motion.apply(dismotion.apply(l.p2));
      noFill();      
      switch(type){
          case 0:
            getHyperArc(L.shift.re,L.shift.im,pp1,pp2).display(R);
            break;
          case 1:
            pp1=PtoBK(pp1);
            pp2=PtoBK(pp2);
            gArc temp=L.trimLine(pp1.add(L.shift),pp2.add(L.shift));
            if (temp.p1.re>0)line((float)temp.p1.re,(float)temp.p1.im,(float)temp.p2.re,(float)temp.p2.im);
            break;
      }
  }
  
  void drawMotion(gMotion m,Complex p1,Complex p2){
      Complex pp=dismotion.apply(m.b);
      if (type==1){
       pp=PtoBK(pp);
      }
      if(pp.abs()<1-tolToBorder){
        Complex pp1=dismotion.apply(m.apply(p1));
        Complex pp2=dismotion.apply(m.apply(p2));
        if (type==1){
           pp1=PtoBK(pp1);
           pp2=PtoBK(pp2);
        }
        noFill();
        if ((pp.abs()<toleranceToZero)||(pp1.abs()<toleranceToZero)){
          line((float)(pp.re*R),(float)(pp.im*R),(float)(pp1.re*R),(float)(pp1.im*R));
        }else{
          switch(type){
          case 0:
            getHyperArc(0,0,pp,pp1).display(R);
            break;
          case 1:
            getHyperArc(0,0,pp,pp1).displayline(R);
            break;
          }
        }
        if ((pp.abs()<toleranceToZero)||(pp2.abs()<toleranceToZero)){
          line((float)(pp.re*R),(float)(pp.im*R),(float)(pp2.re*R),(float)(pp2.im*R));
        }else{
          switch(type){
          case 0:
            getHyperArc(0,0,pp,pp2).display(R);
            break;
          case 1:
            getHyperArc(0,0,pp,pp2).displayline(R);
            break;
          }
        }
      }
  }
  
  void drawMotion(gMotion m,Complex p1,Complex p2,Lens L){
      Complex pp=L.Motion.apply(dismotion.apply(m.b));
      if (type==1){
       pp=PtoBK(pp);
      }
      if(pp.abs()<1-tolToBorder){
        strokeWeight(1);
        Complex pp1=L.Motion.apply(dismotion.apply(m.apply(p1)));
        Complex pp2=L.Motion.apply(dismotion.apply(m.apply(p2)));
        if (type==1){
           pp1=PtoBK(pp1);
           pp2=PtoBK(pp2);
        }
        noFill();
        if ((pp.abs()<toleranceToZero)||(pp1.abs()<toleranceToZero)){
          line((float)(pp.re*R),(float)(pp.im*R),(float)(pp1.re*R),(float)(pp1.im*R));
        }else{          
          switch(type){
          case 0:
            getHyperArc(L.shift.re,L.shift.im,pp,pp1).display(R);
            break;
          case 1:
            //getHyperArc(L.shift.re,L.shift.im,pp,pp1).displayline(R);
            gArc temp=L.trimLine(pp.add(L.shift),pp1.add(L.shift));
            if (temp.p1.re>0)line((float)temp.p1.re,(float)temp.p1.im,(float)temp.p2.re,(float)temp.p2.im);
            break;
          }
        }
        if ((pp.abs()<toleranceToZero)||(pp2.abs()<toleranceToZero)){
          line((float)(pp.re*R),(float)(pp.im*R),(float)(pp2.re*R),(float)(pp2.im*R));
        }else{
          switch(type){
          case 0:
            getHyperArc(L.shift.re,L.shift.im,pp,pp2).display(R);
            break;
          case 1:
            //getHyperArc(L.shift.re,L.shift.im,pp,pp2).displayline(R);
            gArc temp=L.trimLine(pp.add(L.shift),pp1.add(L.shift));
            if (temp.p1.re>0)line((float)temp.p1.re,(float)temp.p1.im,(float)temp.p2.re,(float)temp.p2.im);
            break;
          }
        }
      }
  }
  
  //functions
  //Add Lens
  void DrawAddLens0(){
    bigp=pinput;       
    bigp.setAbs(R);
    ellipse((float)bigp.re, (float)bigp.im, SpotR, SpotR);
  }
  
  void DrawAddLens1(diagram D,tesselation T){
        pinput.setAbs(R);
        if (angdiff(bigp.arg(),pinput.arg())<HALF_PI){
          OverFlag=true;
          newLens=new Lens(bigp.arg(),pinput.arg());
          DrawLens(D,T,newLens);
          //stroke(colorScheme.NewBorderPointColor);
          //strokeWeight(3);
          //DrawLensTop(newLens);
          //stroke(colorScheme.diagramColor);
          //strokeWeight(2);
          //DrawDiagram(D,newLens);
          //stroke(colorScheme.tessFillColor);
          //strokeWeight(1);
          //DrawTesselation(T,newLens);
          //stroke(colorScheme.PathColor);
          //DrawPath(T,newLens);
          //stroke(colorScheme.NewBorderPointColor);
          //fill(colorScheme.NewBorderPointColor);
          //DrawLensBot(newLens);
        }else{
          OverFlag=false; 
        }
        stroke(colorScheme.NewBorderPointColor);
        ellipse((float)bigp.re,(float) bigp.im, SpotR, SpotR);
        ellipse((float)pinput.re, (float)pinput.im, SpotR, SpotR);
  }
  //Select Lens
  void DrawSelectLens(){
    for(Lens L:Lenses){
      if(pinput.div(R).sub(L.shift).abs()<1){
        newLens=L;
        stroke(colorScheme.NewBorderPointColor);
        strokeWeight(3);
        DrawLensTop(L);
        DrawLensBot(L);
      }
    }
  }
  //Add Point
  void DrawAddPoint(diagram D){
        if (pinput.abs()<R){
          switch(type){
            case 0:
              p1=dismotion.applyinv(pinput.div(R));
              break;
            case 1:
              p1=dismotion.applyinv(BKtoP(pinput.div(R)));
              break;
          }
        D.newpoint=new gPoint(p1);
        fill(255);
        stroke(colorScheme.NewArcColor);
        fill(colorScheme.NewArcColor);
        ellipse((float)pinput.re, (float)pinput.im, SpotR, SpotR);
        OverFlag=true;
        }else{
          OverFlag=false;
        }
  }
  //Add Line
  void DrawAddLine0(){
      bigp=pinput;
      if (bigp.abs()<R){
         switch(type){
            case 0:
              p1=dismotion.applyinv(pinput.div(R));
              break;
            case 1:
              p1=dismotion.applyinv(BKtoP(pinput.div(R)));
              break;
          }
         OverFlag=true;
         stroke(colorScheme.NewArcColor);
      }else{
          bigp.setAbs(R);
          newphi=dismotion.applyinv(bigp.arg());
          OverFlag=false;
          stroke(colorScheme.NewBorderPointColor);
      }      
      fill(255);
      ellipse((float)bigp.re, (float)bigp.im, SpotR, SpotR);
  }
  void DrawAddLine1(diagram D){
        if (pinput.abs()<R){
           Complex p2=new Complex();
           switch(type){
            case 0:
              p2=dismotion.applyinv(pinput.div(R));
              break;
            case 1:
              p2=dismotion.applyinv(BKtoP(pinput.div(R)));
              break;
          }
           if (OverFlag){
             D.newarc=new gArc(p1,p2);
             newArcMode=0;
           }else{
             D.newsline=new gSemiLine(newphi,p2);
             newArcMode=1;
           }
        }
        else{
           if (OverFlag){
             D.newsline=new gSemiLine(dismotion.applyinv(pinput.arg()),p1);
             newArcMode=1;
           }else{
             D.newline=new gLine(newphi,dismotion.applyinv(pinput.arg()));
             newArcMode=2;
           }
           pinput.setAbs(R);
        }
        strokeWeight(3);        
        noFill();
        switch (newArcMode){
          case 0:
           stroke(colorScheme.NewArcColor);;
           drawEl(D.newarc);
           break;
          case 1:
            stroke(colorScheme.NewSemiLineColor);
           drawEl(D.newsline);
           break;
          case 2:
           stroke(colorScheme.NewLineColor);
           drawEl(D.newline);
           break;
        }
        fill(255);
        ellipse((float)bigp.re, (float)bigp.im, SpotR, SpotR);
        ellipse((float)pinput.re, (float)pinput.im, SpotR, SpotR);
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
  void Reset(){
    Lenses =new ArrayList<Lens>();
    dismotion=new gMotion();
  }
  void Pan(){
     xpt+=(mouseX-pmouseX);
     ypt+=(mouseY-pmouseY);
  }
  void Drag(){
     boolean flag=false;
     if (pinput.abs()<0.9*R){flag=true;}
     if(flag){
       bigp=new Complex(pmouseX-xpt,pmouseY-ypt);
       Complex p2=new Complex();
       switch(type){
            case 0:
              p1=dismotion.applyinv(pinput.div(R));
              p2=dismotion.applyinv(bigp.div(R));
              break;
            case 1:
              p1=dismotion.applyinv(BKtoP(pinput.div(R)));
              p2=dismotion.applyinv(BKtoP(bigp.div(R)));
              break;
          }
       dismotion.preupdate(new gMotion(p1,p2));
     }
  }
}

class Lens{
  double psi,phi;
  gMotion Motion;
  Arc Marc,Barc,IHarc,OHarc,Larc,Darc;
  Complex shift,bb,point1,point2;
  Lens(double iphi,double ipsi){
    phi=iphi;
    psi=ipsi;
    Marc=smallArc(0,0,1,phi, psi);
    Barc=bigArc(0,0,1,phi, psi);
    point1=new Complex();
    point1.setPolar(1,phi);
    point2=new Complex();
    point2.setPolar(1,psi);
    Larc=flipBigArc(Barc);
    shift=Larc.cCenter();
    IHarc=getHyperArc(Barc);
    OHarc=getHyperArc(Larc);
    Darc=getHyperArc(bigArc(shift.re,shift.im,1,phi, psi));
    bb=IHarc.cCenter();
    bb.setAbs(bb.abs()-IHarc.r);
    Motion=(new gMotion(bb.neg())).power(2);
  }  
  
  gArc trimLine(Complex p1,Complex p2){
    Complex eip=new Complex();
    gArc res;
    eip.setPolar(1,-Larc.cCenter().arg());
    double b=point1.mul(eip).re;
    if (p1.mul(eip).re>=b){
      if (p2.mul(eip).re>=b){
        res = new gArc(p1,p2);
      }else{
        Complex pp2=p1.add(p2.sub(p1).mul((b-p1.mul(eip).re)/(p2.mul(eip).re-p1.mul(eip).re)));
        res = new gArc(p1,pp2.div(eip));
      }
    }else{
      if (p2.mul(eip).re>b){
        Complex pp1=p1.add(p2.sub(p1).mul((b-p1.mul(eip).re)/(p2.mul(eip).re-p1.mul(eip).re)));
        res = new gArc(pp1.div(eip),p2);
      }else{
        res = new gArc(p1,p2);
      }
    }
    return res;
  }
  boolean inView(Complex p1){
    if (p1.sub(Larc.cCenter()).abs()>=1){return false;}
    Complex eip=new Complex();
    eip.setPolar(1,-Larc.cCenter().arg());
    return p1.mul(eip).re>point1.mul(eip).re;
  }
}

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
  
  void displayline(double R){
   line((float)(R*cx+R*r*Math.cos(phi)), (float)(R*cy+R*r*Math.sin(phi)),(float)(R*cx+R*r*Math.cos(psi)), (float)(R*cy+R*r*Math.sin(psi))); 
  }
}


  //Arc functions
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
