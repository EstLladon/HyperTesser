enum LabelStyle{
 FULL,
 IDONLY
}

class Label{
 String N,S,E,W,NE,NW,SE,SW,O;
 Label(){
   O="";
   N="";
   S="";
   E="";
   W="";
   NE="";
   NW="";
   SE="";
   SW="";
 }
 Label(String ilabel){
   O=ilabel;
   N="";
   S="";
   E="";
   W="";
   NE="";
   NW="";
   SE="";
   SW="";
 }
 Label(String ilabeln,String ilabels){
   N=ilabeln;
   O="";
   S=ilabels;
   E="";
   W="";
   NE="";
   NW="";
   SE="";
   SW="";
 }
 
 void display(Complex z,double tsize,double R){
   if (tsize>5){
    textSize((float)tsize);
    text(O,(float)(z.mul(R).re),(float)(z.mul(R).im)); 
    text(N,(float)(z.mul(R).re),(float)(z.mul(R).im-tsize)); 
    text(NW,(float)(z.mul(R).re-0.7*tsize),(float)(z.mul(R).im-0.7*tsize)); 
    text(NE,(float)(z.mul(R).re+0.7*tsize),(float)(z.mul(R).im-0.7*tsize)); 
    text(E,(float)(z.mul(R).re+tsize),(float)(z.mul(R).im)); 
    text(W,(float)(z.mul(R).re-tsize),(float)(z.mul(R).im)); 
    text(SE,(float)(z.mul(R).re+0.7*tsize),(float)(z.mul(R).im+0.7*tsize)); 
    text(SW,(float)(z.mul(R).re-0.7*tsize),(float)(z.mul(R).im+0.7*tsize)); 
    text(S,(float)(z.mul(R).re),(float)(z.mul(R).im+tsize)); 
   }
 }
}
