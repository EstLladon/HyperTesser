class tessgroup{
  int p,q,r;
  
  tessgroup(int ip,int iq,int ir){
    p=ip;
    q=iq;
    r=ir;
  }
  
}


//cosp=mp.cospi(mp.fraction(1,p))
//        cosq=mp.cospi(mp.fraction(1,q))
//        cosr=mp.cospi(mp.fraction(1,r))
//        sinp=mp.sinpi(mp.fraction(1,p))
//        sinq=mp.sinpi(mp.fraction(1,q))
//        E=cosr+cosp*cosq
//        sh2A=(E*E-sinp*sinp*sinq*sinq)/(2*E+sinp*sinp+sinq*sinq)
//        shRp=mp.sqrt(sh2A)/sinp
//        shRq=mp.sqrt(sh2A)/sinq
//        coshRp=mp.sqrt(sh2A/(sinp*sinp)+1)
//        coshRq=mp.sqrt(sh2A/(sinq*sinq)+1)
//        bp=shRp/(coshRp+1)
//        bq=shRq/(coshRq+1)
//        return bp,bq