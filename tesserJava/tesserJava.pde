import complexnumbers.*;


//Interface
Button ZoomPlusButton;
Button ZoomMinusButton;
Button ModelPanButton;
Button ModelChangeButton;
Button ClearModelButton;
Button ClearGeomButton;
boolean PanAvailable=true;
boolean ZoomAvailable=true;
//let radio1;


//Model elements
Arc[] ModelBorderOuter = new Arc[4];
Arc[] ModelBorderInner = new Arc[2];
int MSize=1;
//gArc[] InnerModel;
Arc[] BaseModel = new Arc[1];
Arc BaseArc;

//Content elements
//gPoint[] gPoints;
//gLine[] gLines;
//gArc[] gArcs;


//parameters for image elements
int R=200;
int SpotR=5;
float xpt;
float ypt;
int SpotTreshold=40;
int DotThreshold=10;

//hyperplane display parameters
Complex[] defmotion;

//global variables
int Mode=0;
float xr,yr,a1;
Arc Arc1,Arc2,OArc1,OArc2;

void setup() {
  size(1000, 800);
  textAlign(CENTER, CENTER);
  textSize(36);
  xpt=width/2;
  ypt=height/2;
  ellipseMode(RADIUS);
  background(245,245,220);
  //interface
  ZoomPlusButton=new Button(820, 10, 40, 40,"+",36,false);
  ZoomMinusButton=new Button(860, 10, 40, 40,"-",36,false);
  ModelPanButton=new RButton(820, 70, 100, 40,"Model Pan",15,false,true);
  ModelChangeButton=new RButton(820, 120, 100, 40,"Model Change",15,false,false);
  //initializing Model
  BaseArc=new Arc(0,0,1,-PI,PI);
  BaseModel[0]=BaseArc;
  ModelBorderOuter[0]=BaseArc;
}
  
void draw() {
  background(245,245,220);
  //translate(xpt,ypt);
  String txt = String.format("FPS: %6.2fps", frameRate);
  windowTitle(txt);
  updateInterface();
  pushMatrix();
  translate(xpt,ypt);
  drawModel();
  drawContent();
  popMatrix();
  drawInterface();
}

//interface classes and functions
void updateInterface(){
  ZoomPlusButton.update();
  ZoomMinusButton.update();
  ModelChangeButton.update();
  ModelPanButton.update();
}

void drawInterface(){
  ZoomPlusButton.display();
  ZoomMinusButton.display();
  ModelChangeButton.display();
  ModelPanButton.display();
}


void mousePressed() {
  if (ZoomPlusButton.over && ZoomAvailable) {
    R+=10;
  }
  if (ZoomMinusButton.over && ZoomAvailable) {
    R-=10;
    if(R<50){R=50;}
  }
}

void mouseDragged(){
  if (PanAvailable){
  xpt-=(pmouseX-mouseX);
  ypt-=(pmouseY-mouseY);
  }
}


boolean overRect(int x, int y, int iw, int ih) {
  if (mouseX >= x && mouseX <= x+iw &&
    mouseY >= y && mouseY <= y+ih) {
    return true;
  } else {
    return false;
  }
}

boolean overCircle(int x, int y, int r) {
  float disX = x - mouseX;
  float disY = y - mouseY;
  if(sq(disX) + sq(disY) < sq(r)) {
    return true;
  } else {
    return false;
  }
}

//Model functions
void drawModel(){
  fill(255);
  for(int i=0;i<MSize;i++){
    ModelBorderOuter[i].display();
  }
  fill(0);
  for(int i=0;i<2*MSize-2;i++){
    ModelBorderInner[i].display();
  }
  
}

void drawContent(){
  
}
