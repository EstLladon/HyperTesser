class tesselation{
  int p,q,r;
  double bp,bq,br,side;
  double shA,shRp,shRq,shRr;
  ArrayList<gMotion> elements;
  gMotion MA,MB,MAb,MBb,bpshift,bqshift;
  Complex aP,bP,cP,cQ,cR,cRb;
  ArrayList<pathel> Path;
  pathel newpathel;
  tesselation(int ip,int iq,int ir){
    p=ip;
    q=iq;
    r=ir;
    
    double bp,bq,br;
    double cosp=Math.cos(PI/p);
    double cosq=Math.cos(PI/q);
    double cosr=Math.cos(PI/r);
    double cosr2=Math.cos(PI/(2*r));
    double sinp=Math.sin(PI/p);
    double sinq=Math.sin(PI/q);
    double sinr=Math.sin(PI/r);
    double sinr2=Math.sin(PI/(2*r));
    double E=cosr+cosp*cosq;
    double sh2A=(E*E-sinp*sinp*sinq*sinq)/(2*E+sinp*sinp+sinq*sinq);
    shA=Math.sqrt(sh2A);
    shRp=shA/sinp;
    shRq=shA/sinq;
    shRr=shA/sinr2;
    double coshRp=Math.sqrt(sh2A/(sinp*sinp)+1);
    double coshRq=Math.sqrt(sh2A/(sinq*sinq)+1);
    double coshRr=Math.sqrt(sh2A/(sinr2*sinr2)+1);
    double coshA=Math.sqrt(sh2A+1);
    bp=shRp/(coshRp+1);
    bq=shRq/(coshRq+1);
    br=shRr/(coshRr+1);
    double ba=shA/(coshA+1);
    side=Math.log((1+ba)/(1-ba)); 
    double coshc=(cosq+cosp*cosr)/(sinp*sinr);
    double cosC=-cosp*cosr2+sinp*sinr2*coshc;
    cP=new Complex(bp);
    MA=new gMotion(TWO_PI/p,cP);
    aP=MA.b;
    MAb=MA.inv();
    cQ=new Complex(-bq);
    MB=new gMotion(TWO_PI/q,cQ);
    bP=MB.b;
    MBb=MB.inv();
    bpshift=new gMotion(cP.neg());
    bqshift=new gMotion(cQ.neg());
    cR=new Complex();
    cR.setPolar(br,Math.acos(cosC));
    cRb=cR.conj();
    Path =new ArrayList<pathel>();
  }
  
  void checkprint(){
    // for testing
  }
  //tesselation base
  void populateelementssector(int i){
    elements=new ArrayList<gMotion>();
    String curword="";
    String ta;
    trgroupel tg;
    while(curword.length()<=i){
      tg=lazytrueaddress(curword);
      ta=tg.address;
      if ((curword.equals(ta))&&((ta.length()==0)||(ta.charAt(0)=='b')||(ta.charAt(0)=='d'))){elements.add(tg.Motion);}
      curword=getNextWord(curword);
    }
    println(elements.size());
  }
  
  
  //triange group functions
  trgroupel lazytrueaddress(String v){
    String res="";
    Complex goal=calculatemotion(v).b;
    gMotion resm=new gMotion();
    Complex ga,gb,gab,gbb;
    int j;
    Complex best=new Complex();
    int i=v.length();
    while((goal.abs()>shA/10)&&(i>0)){
      ga=MA.applyinv(goal);
      gb=MB.applyinv(goal);
      gab=MAb.applyinv(goal);
      gbb=MBb.applyinv(goal);
      double tol=(1-goal.abs())/1000000;
      j=min4(goal.abs(),ga.abs(),gb.abs(),gab.abs(),gbb.abs(),tol);
      switch(j){
        case 0:
          i=0;
        case 1:
          best=ga;
          res+='a';
          resm.preupdate(MA);
          break;
        case 2:
          best=gb;
          res+='b';
          resm.preupdate(MB);
          break;
        case 3:
          best=gab;
          res+='c';
          resm.preupdate(MAb);
          break;
        case 4:
          best=gbb;
          res+='d';
          resm.preupdate(MBb);
          break;
      }
      goal=best;
      i--;
    }
    return new trgroupel(res, resm);
  }
  
  trgroupel getNearestGroupel(Complex goal,int limit){
    Complex ga,gb,gab,gbb;
    int j;
    int i=0;
    trgroupel res=new trgroupel("",new gMotion());
    Complex best=new Complex();
    while((goal.abs()>aP.abs()*0.8)&&i<limit){
      ga=MA.applyinv(goal);
      gb=MB.applyinv(goal);
      gab=MAb.applyinv(goal);
      gbb=MBb.applyinv(goal);
      double tol=1/1000;
      j=min4(goal.abs(),ga.abs(),gb.abs(),gab.abs(),gbb.abs(),tol);
      switch(j){
        case 0:
          i=limit;
          break;
        case 1:
          best=ga;
          res.address+='a';
          res.Motion.preupdate(MA);
          break;
        case 2:
          best=gb;
          res.address+='b';
          res.Motion.preupdate(MB);
          break;
        case 3:
          best=gab;
          res.address+='c';
          res.Motion.preupdate(MAb);
          break;
        case 4:
          best=gbb;
          res.address+='d';
          res.Motion.preupdate(MBb);
          break;
      }     
      goal=best;
      i++;
    }
    return res;
  }
  
  gMotion calculatemotion(String s){
    gMotion resM=new gMotion();
    for (int i=0;i<s.length();i++) {
      switch (s.charAt(i)){
        case 'a':
        resM.preupdate(MA);
        break;
        case 'b':
        resM.preupdate(MB);
        break;
        case 'c':
        resM.preupdate(MAb);
        break;
        case 'd':
        resM.preupdate(MBb);
        break;
      }
    }
    return resM;
  }  
  //Path functions
  gArc getgArc(pathel pe){
    gMotion pem=calculatemotion(pe.address);
    switch(pe.type){
      case 0:
        return new gArc(pem.apply(cP),pem.apply(cQ)); 
      case 1:
        return new gArc(pem.apply(cP),pem.apply(cR)); 
      case 2:
        return new gArc(pem.apply(cP),pem.apply(cRb)); 
      case 3:
        return new gArc(pem.apply(cQ),pem.apply(cR)); 
      case 4:
        return new gArc(pem.apply(cQ),pem.apply(cRb)); 
    }
    return new gArc(pem.apply(cP),pem.apply(cQ)); 
  }
  
  Complex getPoint(pathel pe){
    gMotion pem=calculatemotion(pe.address);
    switch(pe.type){
      case 0:
        return pem.b; 
      case 1:
        return pem.apply(cP); 
      case 2:
        return pem.apply(cQ); 
      case 3:
        return pem.apply(cR); 
      case 4:
        return pem.apply(cRb); 
    }
    return pem.b;
  }
  
  //pathel getNearestPathel(Complex goal,int limit){
    
  //}
 
  void putPathToDiagram(diagram D){
    for (pathel pe:Path){
      D.gArcs.add(getgArc(pe));
    }
  }
  
  
  
}

class trgroupel{
  String address;
  gMotion Motion;
  trgroupel(String iadd,gMotion imotion){
    address=iadd;
    Motion=imotion;
  } 
}

class pathel{
  String address;
  int type;
  pathel(String iadd,int itype){
    address=iadd;
    type=itype;
  }   
}



public static int min4(double o,double a,double b,double c,double d,double tolerance){
  double best;
  int res;
  if (a<=b-tolerance){
    best=a;
    res=1;
  }
  else{
    best=b;
    res=2;
  }
  if(c<best-tolerance){
    best=c;
    res=3;
  }
  if(d<best-tolerance){
    res=4;
  }
  if (o<best-tolerance){
   res=0; 
  }
  return res;
}



public static String getNextWord(String word) {
        char[] chars = word.toCharArray();
        int i = chars.length - 1;        
        while (i >= 0 && chars[i] == 'd'){
            chars[i] = 'a';
            i--;
        }        
        if (i >= 1) {
            chars[i]++;
            return new String(chars);
        }else if(i==0){
         if ((chars[0]=='a')){chars[0]='b';}
         else{chars[0]='d';} 
         return new String(chars);
        }
       return new String(chars)+'a';
}
