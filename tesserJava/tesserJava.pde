import complexnumbers.*;
import java.lang.Math;

//Interface
int interfaceWidth=110;
int labelTSize=36;
Button  ZoomPlusButton=new Button(0, 10, 40, 40,"+",36,0);
Button  ZoomMinusButton=new Button(40, 10, 40, 40,"-",36,1);
Button  ModelPanButton=new Button(0, 70, 100, 40,"Model Pan",15,2);
Button  AddLensButton=new Button(0, 120, 100, 40,"Add Lens",15,3);
Button  ModelViewButton=new Button(0, 170, 100, 40,"Model Inner",15,4);
Button  AddInnerPointButton=new Button(0, 220, 100, 40,"Add Point",15,5);
Button  AddInnerLineButton=new Button(0, 270, 100, 40,"Add Line",15,7);
Button  ModelDragButton=new Button(0, 500, 100, 40,"Model Drag",15,6);
Button  ModelDragResetButton=new Button(0, 550, 100, 40,"Model Return",15,8);
Button  ModelResetButton=new Button(0, 600, 100, 40,"Model Reset",15,9);
gui Gui;

//Model 
Model MainModel;
//diagram
diagram D;

//parameters for image elements
int SpotR=5;
int SpotTreshold=40;
int DotThreshold=10;

void setup() {
  size(1000, 800);
  textAlign(CENTER, CENTER);
  textSize(36);
  ellipseMode(RADIUS);
  background(245,245,220);

  //init interface
  Gui=new gui(interfaceWidth,2);
  Gui.addButton(ZoomPlusButton);
  Gui.addButton(ZoomMinusButton);
  Gui.addDefButton(ModelPanButton);
  Gui.addButton(AddLensButton);
  Gui.addButton(ModelViewButton);
  Gui.addButton(AddInnerPointButton);
  Gui.addButton(AddInnerLineButton);
  Gui.addButton(ModelDragButton);
  Gui.addButton(ModelDragResetButton);
  Gui.addButton(ModelResetButton);
  Gui.ButtonState=2;
  //initializing Model
  MainModel=new Model(200);
  //init content
  D=new diagram();
}
  
void draw() {
  background(245,245,220);
  String txt = String.format("FPS: %6.2fps Model: %d Lenses,%d Points, %d Lines, %d Arcs", frameRate, MainModel.Lenses.size(),D.gPoints.size(),D.gLines.size(),D.gArcs.size());
  windowTitle(txt);
  drawModel();
  Gui.display();
}

void mousePressed() {
  int bid=Gui.whichButtonOver();
  if (bid==ZoomPlusButton.id) {
    MainModel.R+=10;
  }
  else if (bid==ZoomMinusButton.id) {
    MainModel.R-=10;
    if(MainModel.R<50){MainModel.R=50;}
  }
  else if(bid==ModelDragResetButton.id){
   MainModel.DragReset(); 
  }
  else if(bid==ModelResetButton.id){
   MainModel.Reset(D);
  }
  else if (bid>=0){
    Gui.ButtonState=bid;
    Gui.Mode=0;
  } 
  else if ((Gui.ButtonState==AddLensButton.id)||(Gui.ButtonState==AddInnerPointButton.id)||(Gui.ButtonState==AddInnerLineButton.id)){
     Gui.Mode++;
  }
  //pan
  if ((Gui.ButtonState==AddLensButton.id)||(Gui.ButtonState==ModelDragButton.id)){
    Gui.Switches[0]=false;
  } 
  else{Gui.Switches[0]=true;}
}

void mouseDragged(){
  if (Gui.Switches[0]){
    MainModel.Pan();
  }
  else if(Gui.ButtonState==ModelDragButton.id){
    MainModel.Drag();
  }
}

void drawModel(){
  pushMatrix();
  translate(MainModel.xpt,MainModel.ypt);
  MainModel.pinput=new Complex(mouseX-MainModel.xpt,mouseY-MainModel.ypt);
  MainModel.DrawBaseModel();
  MainModel.drawContent(D);
  MainModel.DrawInnerModel();
  switch(Gui.ButtonState){
    case 2:
        MainModel.DrawLenses(D);
        break;
    case 3://Add lens
      switch(Gui.Mode){
        case 0:
        MainModel.DrawAddLens0();
        break;
        case 1:
        MainModel.DrawAddLens1(D);
        break;
        case 2:
        MainModel.DrawAddLens2();
        Gui.Mode=0;
        Gui.ButtonState=2;
        break;
      }
      break;
    case 5://Add inner dot
      switch(Gui.Mode){
        case 0:
         MainModel.DrawAddInnerDot0();
        break;
        case 1:
         MainModel.DrawAddInnerDot1(D);        
         Gui.Mode=0;
         break;
      }
      break;
    case 6:
        MainModel.DrawLenses(D);
        break;    
    case 7://add inner line      
      switch(Gui.Mode){
        case 0:
         MainModel.DrawAddLine0();
         break;
        case 1:
        MainModel.DrawAddLine1();
         break;
        case 2:
         MainModel.DrawAddLine2(D);
         Gui.Mode=0;
         break;
      }
      break;
  } 
  popMatrix();
}
