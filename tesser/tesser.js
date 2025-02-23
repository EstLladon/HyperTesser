
//Interface
let button1;
let button2;
let radio1;


//Model elements
let ModelBorderOuter=[];
let ModelBorderInner=[];
let InnerModel=[];
let BaseModel=[];
let BaseArc;

//Content elements
let gPoints=[];
let gLines=[];
let gArcs=[];


//parameters for image elements
let R=200;
let SpotR=5;
let xpt=0;
let ypt=0;
let SpotTreshold=40;
let DotThreshold=10;

//hyperplane display parameters
defmotion=[1,0,0];

//global variables
let Mode=0;
let xr,yr,a1;
let Arc1,Arc2,OArc1,OArc2;

// classes
class Point{
  constructor(cx,cy){
   this.x=cx;
   this.y=cy;    
  }
  putspot(){
    spot(this.x,this.y)
  }
}

class Arc{
  constructor(cx,cy,r,phi,psi){
   this.cx=cx;
   this.cy=cy;
   this.r=r;
   this.phi=phi;
   this.psi=psi;
  }
  display(){
   arc(R*this.cx, R*this.cy, R*this.r, R*this.r, this.phi, this.psi,OPEN); 
  }
}

//geometry classes

class gPoint{
  constructor(x,y,label){
   this.x=x;
   this.y=y;
   this.label=label;
  }
  displayInner(){
    x=getX(this.x,this,y,BaseModel);
    y=getY(this.x,this,y,BaseModel);
    size=getSpotSizeBase(this.x,this.y);
   ellipse(x, y, size, size); 
   textSize(size*2);
   text(this.label,x,y-size);
  }
  displayOuter(){
   arc(R*this.cx, R*this.cy, R*this.r, R*this.r, this.phi, this.psi,OPEN); 
  }
}

class gLine{
  constructor(phi,psi,label){
   this.phi=x;
   this.psi=y;
   this.label=label;
  }
  displayInner(){
    
  }
  displayOuter(){
   
  }
}

class gArc{
  constructor(x1,y1,x2,y2){
   this.phi=x;
   this.psi=y;
   this.colour=0;
  }
  displayInner(){
    
  }
  displayOuter(){
   
  }
}



function setup() {
createCanvas(800, 800);
xpt=width/2;
ypt=height/2;
ellipseMode(RADIUS);
  background("beige");
  //translate(width/2,height/2)
  
  button1 = createButton("+"); //, "pressed");
  
  button1.mouseClicked(button1Clicked);
  button2 = createButton("-"); //, "pressed");
 
  button2.mouseClicked(button2Clicked);
  
  radio1 = createRadio();

  //.option([value], [contentLabel])
  //If 1 param, it's both content AND
  //value. Values treated as strings.
  radio1.option(1, "Model pan");
  radio1.option(2, "Inner model view");
  radio1.option(3, "Model change");
  radio1.option(4, "Geometry");
  //radio1.option(5, "Group");
  radio1.value(1); 
  BaseArc=new Arc(0,0,1,-PI,PI);
  BaseModel[0]=BaseArc;
  ModelBorderOuter[0]=BaseArc;
  
}


function draw() {
    background("beige");
    
      translate(xpt,ypt);
    fill(255)
    strokeWeight(2)
    
    switch (radio1.value()) {
    //radio value is always a string
    case "1":
      DrawModel();
      break;
    case "2":
       DrawInnerModel();
      break;
    case "3":
      DrawModel();
      x=0;
      y=0;
      
      switch(Mode){
       case 0: 
        a1=trueatan2(mouseX-x-xpt,mouseY-y-ypt);
        xr=x+R*cos(a1);
        yr=y+R*sin(a1);
        
        ellipse(xr, yr, SpotR, SpotR);
        break;
       case 1:
        
        a2=trueatan2(mouseX-x-xpt,mouseY-y-ypt);
        an1=(a1+a2)/2;
        an2=(a1-a2)/2;
        xr2=x+R*cos(a2);
        yr2=y+R*sin(a2);
        
        
          
         
        ann1=getann1(a1,a2);
        ann2=getann2(a1,a2);
        noFill();
        
        OArc1=new Arc(x+1/cos(an2)*cos(an1),y+1/cos(an2)*sin(an1),tan(an2),ann1,ann2)
        OArc2=new Arc(x+2*cos(an2)*cos(an1)-1/cos(an2)*cos(an1),y+2*cos(an2)*sin(an1)-1/cos(an2)*sin(an1),tan(an2),ann1+PI,ann2+PI)
        strokeWeight(1)
        OArc1.display();
        OArc2.display();
        strokeWeight(5)
        Arc1=new Arc(x,y,1,ann1-HALF_PI,ann2+HALF_PI);
        Arc2=new Arc(x+2*cos(an2)*cos(an1),y+2*cos(an2)*sin(an1),1,ann1+HALF_PI,ann2+3*HALF_PI);
        strokeWeight(3)
        Arc1.display();
        Arc2.display();
        strokeWeight(1)
        fill(255);
        ellipse(x+2*R*cos(an2)*cos(an1),y+2*R*cos(an2)*sin(an1), SpotR, SpotR);
        ellipse(xr, yr, SpotR, SpotR);
        ellipse(xr2, yr2, SpotR, SpotR);
        fill(0);
        break;
        
       case 2:
       ModelBorderOuter[0]=Arc1;
       ModelBorderOuter[1]=Arc2;
       ModelBorderInner[0]=OArc1;
       ModelBorderInner[1]=OArc2;
       InnerModel[0]=OArc1;
       Mode=0;
       break;
      }
      break;
      
      
  }
  
}

//Screen behavior

function mouseDragged(){
  if (radio1.value()=="1"){
  xpt-=(pmouseX-mouseX);
  ypt-=(pmouseY-mouseY);
  }
}

function mousePressed(){
  switch (radio1.value()) {
   case "1":Mode=0;break;
   case "2":Mode=0;break;
   case "3":SwitchMode();break;
   
   case "4":Mode=0;break;
       
  }
  
}

function button1Clicked() {  
  if (radio1.value()=='1'){
  R+=10;
  }
}

function button2Clicked() {  
  if (radio1.value()=='1'){
  R-=10;
  if(R<50){R=50;}
  }
}

function SwitchMode(){
     switch(Mode){
       case 0: Mode=1;break;
       case 1: Mode=2;break;
       //case 2: Mode=3;break;
     } 
  
}

//General Geom Functions
function trueatan2(x,y){
 return HALF_PI-atan2(x,y); 
}

//Model functions

function DrawModel(){
  for(let a=0;a<ModelBorderOuter.length;a++){
     fill(255);
     ModelBorderOuter[a].display() 
     
  }
  for(let a=0;a<ModelBorderInner.length;a++){
    fill(0)
     ModelBorderInner[a].display() 
     
  }
  fill(255);
}

function DrawInnerModel(){
  fill(255)
  strokeWeight(2)
  BaseArc.display();
  stroke(204, 102, 0);
  for(let a=0;a<InnerModel.length;a++){
    
     InnerModel[a].display() 
     
  }
  stroke(0);
}

function getSpotSizeBase(x,y){
  r=sqrt(x*x+y*y);
  d=R*(1-r);
  if (d>=SpotThreshold){
    return SpotR;
  }
  if (d>=DotThreshold){
    return (SpotR*(d-DotTreshold)/(SpotTreshold-DotTreshold))
  }
  return 0;
}

function getann1(a1,a2){
      if ((a1-a2>=PI)|((a1<a2)&(a1>=a2-PI))){
           return a2+HALF_PI;
           //ann2=a1-HALF_PI;
         }else{
           return a1+HALF_PI;
           //ann2=a2-HALF_PI;
         }       
}

function getann2(a1,a2){
      if ((a1-a2>=PI)|((a1<a2)&(a1>=a2-PI))){
           //return a2+HALF_PI;
           return a1-HALF_PI;
         }else{
           //return a1+HALF_PI;
           return a2-HALF_PI;
         }       
}

//Element drawing

function spot(x, y) {
  push();
  //translate(xpt,ypt);
  translate(x, y);
  ellipse(x, y, SpotR, SpotR);
  pop();
}
