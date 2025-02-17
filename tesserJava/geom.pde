class Arc{
  float cx,cy,r,phi,psi;
  Arc(float ix,float iy,float ir,float iphi,float ipsi){
   cx=ix;
   cy=iy;
   r=ir;
   phi=iphi;
   psi=ipsi;
  }
  
  void display(){
   arc(R*cx, R*cy, R*r, R*r, phi, psi,OPEN); 
  }
}
