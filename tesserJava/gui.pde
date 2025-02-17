class Button {

  int x, y;
  int boxx, boxy;
  boolean over;
  boolean press;
  boolean locked;
  String label="";
  int tSize;

  Button(int ix, int iy, int iw, int ih,String ilabel,int itsize,boolean ilock) {
    x = ix;
    y = iy;
    boxx = iw;
    boxy = ih;
    over=false;
    press=false;
    label=ilabel;
    tSize=itsize;
    locked=ilock;
  }
  void update() {
     if (overRect(x, y, boxx, boxy)){
       over=true;
     } else {
      over = false;
    }   
  }
  void display() {
    if (locked){
      fill(200);
      rect(x,y,x+boxx,y+boxy);
    }else{
    fill(255);
    rect(x, y,boxx,boxy);
    fill(0);
    textSize(tSize);
    text(label,x,y,boxx,boxy); 
    }
  }
}

class RButton extends Button{

  boolean active;

  RButton(int ix, int iy, int iw, int ih,String ilabel,int itsize,boolean ilock, boolean iactive) {
    super(ix, iy, iw, ih, ilabel,itsize,ilock);
    active=iactive;
  }
  void update() {
     if (overRect(x, y, boxx, boxy)){
       over=true;
     } else {
      over = false;
    }   
  }
  void display() {
    
    if (locked){
      fill(10);
      rect(x, y, boxx,boxy);
    }else if(active){
    fill(255);
    rect(x, y, boxx,boxy);
    fill(0);
    textSize(tSize);
    text(label, x,y,boxx,boxy); 
    }else{
      fill(200);
      rect(x, y,boxx,boxy);
      fill(0);
      textSize(tSize);
      text(label, x,y,boxx,boxy); 
    }
  }
}
