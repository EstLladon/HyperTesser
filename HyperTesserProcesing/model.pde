enum modelType{
  POINCARE,
  BELTRAMKLEIN
}

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
  modelType type=modelType.POINCARE;
  //Label style
  LabelStyle labelstyle=LabelStyle.FULL;
  //Aux variables
  int newArcMode;
  boolean OverFlag;
  Complex z1,bigp,pinput;
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
  
  //Lenses
  void DrawLenses(diagram D,tesselation T,Group G){
    for(Lens L:Lenses){
      DrawLens(D,T,G,L);
    }
  }
  
  void DrawLens(diagram D,tesselation T,Group G,Lens L){      
      stroke(lensColor);
      DrawLensTop(L);
      noFill();
      stroke(tessFillColor);
      strokeWeight(1);
      DrawTesselation(T,G,L);
      stroke(PathColor);
      strokeWeight(2);
      DrawPath(T,L);
      stroke(diagramColor);
      DrawDiagram(D,L);
      drawEl(new dPoint(new Complex()),L);
      stroke(lensColor);    
      DrawLensBot(L);
  }
  
  void DrawLensTop(Lens L){
    L.Larc.display(R,lensColor,modelColor);
  }
  
  void DrawLensBot(Lens L){
      switch(type){
       case POINCARE:
         L.IHarc.display(R,lensColor,lensColor);
         L.OHarc.display(R,lensColor,lensColor);
         break;
       case BELTRAMKLEIN:
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
    for(dPoint g:D.dPoints){drawEl(g,L);}     
  }
  
  void DrawTesselation(tesselation T,Group G,Lens L){
      gMotion tm;
      Complex tt=dismotion.applyinv(L.Motion.applyinv(new Complex(T.bp)));
      trgroupel te=T.getNearestGroupel(tt,(int)(2*distFromZero(tt)/T.side));
      for (trgroupel tr:T.elements){ 
          gMotion wm=tr.Motion;
          String ta=tr.address;
          for(int i=0;i<p;i++){    
            tm=wm.combine(te.Motion); 
            Label label = G.getlabel(G.getelement(te.address+ta),labelstyle);            
            drawMotion(tm,T.aP,T.bP,L);
            Complex z = L.Motion.apply(dismotion.apply(tm.b));
            float size=getSpotSizeBase(z);
            z=z.add(L.shift);
            if (type== modelType.BELTRAMKLEIN){z=PtoBK(z);}
            fill(textColor);
            label.display(z,(size/SpotR)*labelTSize,R);
            wm.update(T.MA);
            ta='a'+ta;
          }      
      }   
  }
  
  void DrawPath(tesselation T,Lens L){
    for (pArc pe:T.Path){
      drawEl(T.getgArc(pe),L);
    }
  }
  
  void DrawLensScheme(){
    noFill();
    switch(type){
    case POINCARE:
      for(Lens L:Lenses){
        L.IHarc.display(R,LensSchemeColor);
      }
      break;
    case BELTRAMKLEIN:
      for(Lens L:Lenses){
        L.IHarc.displayline(R);
      }
      break;
    }
  }
  
  //Draw elements  
  void drawEl(dPoint P,Lens L){
      Complex z=L.Motion.apply(dismotion.apply(P.z));
      if (type== modelType.BELTRAMKLEIN){z=PtoBK(z);}
      if (L.inView(z)){
        float size=getSpotSizeBase(z);
        Spot(z.add(L.shift),size,P.label,R);
      }
  }
  
  void drawEl(gLine l,Lens L){
      double phip=L.Motion.apply(dismotion.apply(l.phi));
      double psip=L.Motion.apply(dismotion.apply(l.psi));
      noFill();      
      switch(type){
        case POINCARE:
          new Arc(L.shift.re,L.shift.im,(double)1,phip,psip,ArcType.HYPER).display(R,diagramColor);
          break;
        case BELTRAMKLEIN:
          gArc temp=L.trimLine(eip(phip),eip(psip));
          stroke(diagramColor);
          line((float)(temp.z1.add(L.shift).re*R),(float)(temp.z1.add(L.shift).im*R),(float)(temp.z2.add(L.shift).re*R),(float)(temp.z2.add(L.shift).im*R));
          break;
      }
  }
  void drawEl(gSemiLine l,Lens L){
      double phip=L.Motion.apply(dismotion.apply(l.phi));
      Complex z=L.Motion.apply(dismotion.apply(l.z));
      noFill();
      switch(type){
        case POINCARE:
          new Arc(L.shift,phip,z.add(L.shift)).display(R,diagramColor);
          break;
        case BELTRAMKLEIN:
          z=PtoBK(z);
          gArc temp=L.trimLine(eip(phip),z);
          line((float)(temp.z1.add(L.shift).re*R),(float)(temp.z1.add(L.shift).im*R),(float)(temp.z2.add(L.shift).re*R),(float)(temp.z2.add(L.shift).im*R));
          break;
      }
  }
  void drawEl(gArc l,Lens L){
      Complex z1=L.Motion.apply(dismotion.apply(l.z1));
      Complex z2=L.Motion.apply(dismotion.apply(l.z2));
      noFill();      
      switch(type){
          case POINCARE:
            Arc temparc = new Arc(z1,z2);
            temparc.move(L.shift);
            temparc.display(R,diagramColor);
            break;
          case BELTRAMKLEIN:
            z1=PtoBK(z1);
            z2=PtoBK(z2);
            gArc temp=L.trimLine(z1,z2);
            line((float)(temp.z1.add(L.shift).re*R),(float)(temp.z1.add(L.shift).im*R),(float)(temp.z2.add(L.shift).re*R),(float)(temp.z2.add(L.shift).im*R));
            break;
      }
  }
   
  void drawMotion(gMotion m,Complex z1,Complex z2,Lens L){
      Complex z=L.Motion.apply(dismotion.apply(m.b));
      Arc temparc;
      if (type== modelType.BELTRAMKLEIN){
       z=PtoBK(z);
      }
      if(z.abs()<1-tolToBorder){
        Complex zz1=L.Motion.apply(dismotion.apply(m.apply(z1)));
        Complex zz2=L.Motion.apply(dismotion.apply(m.apply(z2)));
        if (type== modelType.BELTRAMKLEIN){
           zz1=PtoBK(zz1);
           zz2=PtoBK(zz2);
        }
        noFill();
        if ((z.abs()<toleranceToZero)||(zz1.abs()<toleranceToZero)){
          line((float)(z.re*R+L.shift.re*R),(float)(z.im*R+L.shift.im*R),(float)(zz1.re*R+L.shift.re*R),(float)(zz1.im*R+L.shift.im*R));
        }else{          
          switch(type){
          case POINCARE:
            temparc = new Arc(z,zz1);
            temparc.move(L.shift);
            temparc.display(R,tessFillColor);
            break;
          case BELTRAMKLEIN:
            gArc temp=L.trimLine(z,zz1);
            line((float)((temp.z1.add(L.shift).re)*R),(float)((temp.z1.add(L.shift).im)*R),(float)((temp.z2.add(L.shift).re)*R),(float)((temp.z2.add(L.shift).im)*R));
            break;
          }
        }
        if ((z.abs()<toleranceToZero)||(zz2.abs()<toleranceToZero)){
          line((float)(z.add(L.shift).re*R),(float)(z.add(L.shift).im*R),(float)(zz2.add(L.shift).re*R),(float)(zz2.add(L.shift).im*R));
        }else{
          switch(type){
          case POINCARE:
            temparc = new Arc(z,zz2);
            temparc.move(L.shift);
            temparc.display(R,tessFillColor);
            break;
          case BELTRAMKLEIN:
            gArc temp=L.trimLine(z,zz2);
            line((float)((temp.z1.add(L.shift).re)*R),(float)((temp.z1.add(L.shift).im)*R),(float)((temp.z2.add(L.shift).re)*R),(float)((temp.z2.add(L.shift).im)*R));            
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
  
  void DrawAddLens1(diagram D,tesselation T,Group G){
        pinput.setAbs(R);
        double diff=angdiff(bigp.arg(),pinput.arg());
        if (diff>0.01 && diff<HALF_PI){
          OverFlag=true;
          newLens=new Lens(bigp.arg(),pinput.arg());
          DrawLens(D,T,G,newLens);
          Message+=" "+(float)(newLens.IHarc.c.abs());
        }else{
          OverFlag=false; 
        }
        stroke(NewBorderPointColor);
        ellipse((float)bigp.re,(float) bigp.im, SpotR, SpotR);
        ellipse((float)pinput.re, (float)pinput.im, SpotR, SpotR);
  }
  //Select Lens
  void DrawSelectLens(){
    for(Lens L:Lenses){
      if(pinput.div(R).sub(L.shift).abs()<1){
        newLens=L;
        stroke(NewBorderPointColor);
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
            case POINCARE:
              z1=dismotion.applyinv(pinput.div(R));
              break;
            case BELTRAMKLEIN:
              z1=dismotion.applyinv(BKtoP(pinput.div(R)));
              break;
          }
          D.newpoint=new dPoint(z1);
          stroke(NewArcColor);
          //drawEl(D.newpoint,new Lens());
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
            case POINCARE:
              z1=dismotion.applyinv(pinput.div(R));
              break;
            case BELTRAMKLEIN:
              z1=dismotion.applyinv(BKtoP(pinput.div(R)));
              break;
          }
         OverFlag=true;
         stroke(NewArcColor);
      }else{
          bigp.setAbs(R);
          newphi=dismotion.applyinv(bigp.arg());
          OverFlag=false;
          stroke(NewBorderPointColor);
      }      
      fill(255);
      ellipse((float)bigp.re, (float)bigp.im, SpotR, SpotR);
  }
  void DrawAddLine1(diagram D){
        if (pinput.abs()<R){
           Complex z2=new Complex();
           switch(type){
            case POINCARE:
              z2=dismotion.applyinv(pinput.div(R));
              break;
            case BELTRAMKLEIN:
              z2=dismotion.applyinv(BKtoP(pinput.div(R)));
              break;
          }
           if (OverFlag){
             D.newarc=new gArc(z1,z2);
             newArcMode=0;
           }else{
             D.newsline=new gSemiLine(newphi,z2);
             newArcMode=1;
           }
        }
        else{
           if (OverFlag){
             D.newsline=new gSemiLine(dismotion.applyinv(pinput.arg()),z1);
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
           stroke(NewArcColor);
           drawEl(D.newarc, new Lens());
           drawEl(new dPoint(D.newarc.z1),new Lens());
           drawEl(new dPoint(D.newarc.z2),new Lens());
           break;
          case 1:
            stroke(NewSemiLineColor);
           drawEl(D.newsline,new Lens());
           drawEl(new dPoint(D.newsline.z),new Lens());
           break;
          case 2:
           stroke(NewLineColor);
           drawEl(D.newline,new Lens());
           Message+=" "+(float)(new Arc(0,0,(double)1,newphi,dismotion.applyinv(pinput.arg()),ArcType.HYPER).c.abs());
           break;
        }
        fill(255);
        ellipse((float)bigp.re, (float)bigp.im, SpotR, SpotR);
        ellipse((float)pinput.re, (float)pinput.im, SpotR, SpotR);
  }
  
  void DrawAddPathLine0(tesselation T){
          if (pinput.abs()<R){
          switch(type){
            case POINCARE:
              z1=dismotion.applyinv(pinput.div(R));
              break;
            case BELTRAMKLEIN:
              z1=dismotion.applyinv(BKtoP(pinput.div(R)));
              break;
          }
          T.newpathel=T.getNearestPathel(z1,(int)(2*distFromZero(z1)/T.side));
          stroke(NewArcColor);
          //fill(NewArcColor);
          drawEl(new dPoint(T.getPoint(T.newpathel)),new Lens());
          OverFlag=true;
        }else{
          OverFlag=false;
        }
  }
  
  void DrawAddPathLine1(tesselation T){
    if (pinput.abs()<R){
           Complex z2=new Complex();
           switch(type){
            case POINCARE:
              z2=dismotion.applyinv(pinput.div(R));
              break;
            case BELTRAMKLEIN:
              z2=dismotion.applyinv(BKtoP(pinput.div(R)));
              break;
          }
          if (OverFlag){
             pathel pathel2=T.getNearestPathel(z2,(int)(2*distFromZero(z2)/T.side));
             T.newparc=new pArc(T.newpathel,pathel2);
             drawEl(T.getgArc(T.newparc),new Lens());
          }else{
           G.Mode=0; 
          }
        }
  }
 
 //misc
  float getSpotSizeBase(Complex z){    
    double d=R*(1-z.abs());
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
     if(pinput.abs()<0.9*R){
       bigp=new Complex(pmouseX-xpt,pmouseY-ypt);
       Complex z2=new Complex();
       switch(type){
            case POINCARE:
              z1=dismotion.applyinv(pinput.div(R));
              z2=dismotion.applyinv(bigp.div(R));
              break;
            case BELTRAMKLEIN:
              z1=dismotion.applyinv(BKtoP(pinput.div(R)));
              z2=dismotion.applyinv(BKtoP(bigp.div(R)));
              break;
          }
       dismotion.preupdate(new gMotion(z1,z2));
       dismotion.preupdate(new gMotion(z1,z2));
     }
  }
}
