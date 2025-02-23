class gui{
  ArrayList<Button> Buttons;
  boolean[] Switches;
  int defButton=-1;
  int ButtonState;
  int Mode;
  int interfaceWidth;
  gui(int iwidth,int switchnum){
   Buttons=new  ArrayList<Button>();
   ButtonState=-1;
   interfaceWidth=iwidth;
   Switches=new boolean[switchnum];
   Mode=0;
  }
  void addButton(int ix, int iy, int iw, int ih,String ilabel,int itsize,int iid){
    Button B=new Button(ix, iy, iw, ih,ilabel,itsize,iid);
    Buttons.add(B);
  }
  void addButton(Button B){
    Buttons.add(B);
  }
  void addDefButton(Button B){
    Buttons.add(B);
    defButton=B.id;
  }
  void display(){
    stroke(0);
    for(int i=0;i<Buttons.size();i++){
     Buttons.get(i).display(interfaceWidth,ButtonState); 
    }
  }
  int whichButtonOver(){ 
   for(int i=0;i<Buttons.size();i++){
    if (overButton(i)){return Buttons.get(i).id;}    
   }
   return -1;
  }
  boolean overButton(int i){
  float a=width - interfaceWidth+Buttons.get(i).x;
  float b=a+Buttons.get(i).boxx;
  float c=Buttons.get(i).y;
  float d = c+Buttons.get(i).boxy;
  if (i>Buttons.size()) {return false; } 
  else if ((mouseX>=a)&&(mouseX<=b)&&(mouseY>=c)&&(mouseY<=d)){
       return true;
     } else {
      return false;
    }   
  }  
}

class Button {
  int x, y;
  int boxx, boxy;
  boolean over;
  boolean press;
  String label="";
  int tSize;
  int id;
  Button(int ix, int iy, int iw, int ih,String ilabel,int itsize,int iid) {
    x = ix;
    y = iy;
    boxx = iw;
    boxy = ih;
    over=false;
    press=false;
    label=ilabel;
    tSize=itsize;
    id=iid;
  }
  
  void display(int interfaceWidth,int State) {
    if (State==id)
    {
    fill(255);
    rect(width - interfaceWidth+x, y,boxx,boxy);
    fill(0);
    textSize(tSize);
    text(label,width - interfaceWidth+x,y,boxx,boxy); 
    }else{
      fill(200);
      rect(width - interfaceWidth+x,y,boxx,boxy);
      fill(0);
      textSize(tSize);
      text(label,width - interfaceWidth+x,y,boxx,boxy);       
    }
  }
}
