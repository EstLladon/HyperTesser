class gPoint{
  Complex p;
  String label;
  gPoint(Complex ip,String ilabel){
   p=ip;
   label=ilabel;
  }
  void displayInner(){
    //x=getX(this.x,this,y,BaseModel);
    //y=getY(this.x,this,y,BaseModel);
    //size=getSpotSizeBase(this.x,this.y);
   //ellipse(x, y, size, size); 
   //textSize(size*2);
   //text(this.label,x,y-size);
  }
  void displayOuter(){
   //arc(R*cx, R*this.cy, R*this.r, R*this.r, this.phi, this.psi,OPEN); 
  }
}

class gLine{
  float phi,psi;
  String label;
  gLine(float iphi,float ipsi,String ilabel){
   phi=iphi;
   psi=ipsi;
   label=ilabel;
  }
  void displayInner(){
    
  }
  void displayOuter(){
   
  }
}

class gArc{
  gPoint p1,p2;
  String status; 
  gArc(Complex ip1, Complex ip2, String istatus){
   p1=new gPoint(ip1,"");
   p2=new gPoint(ip2,"");
   status=istatus;
  }
  void displayInner(){
    
  }
  void displayOuter(){
   
  }
}
