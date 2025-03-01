import complexnumbers.*;
import java.lang.Math;
import java.util.*;

//Gui
int interfaceWidth=220;
//zoom buttons
Button  ZoomPlusButton=new Button(0, 10, 40, 40,"+",36,0);
Button  ZoomMinusButton=new Button(40, 10, 40, 40,"-",36,1);
//Model move buttons
Button  ModelPanButton=new Button(0, 70, 100, 40,"Model Pan",15,2);
Button  ModelDragButton=new Button(110, 70, 100, 40,"Model Drag",15,6);
Button  ModelDragResetButton=new Button(0, 120, 100, 40,"Model Return",15,8);
Button  ModelResetButton=new Button(110, 120, 100, 40,"Model Reset",15,9);
//Model Switch Button
Button  ModelSwitchButton=new Button(90, 10, 40, 40,"P",36,14);
//Lens buttons
Button  AddLensButton=new Button(0, 200, 100, 40,"Add Lens",15,3);
Button  DeleteLensButton=new Button(110, 200, 100, 40,"Delete Lens",15,11);
Button  ModelViewButton=new Button(0, 250, 100, 40,"No Lens View",15,4);
//add model
Button  AddInnerPointButton=new Button(0, 350, 100, 40,"Add Point",15,5);
Button  AddInnerLineButton=new Button(110, 350, 100, 40,"Add Line",15,7);
//path buttons
Button AddToPathButton=new Button(0,430,100,40,"Add to Path",15,12);
Button DeleteFromPathButton=new Button(110,430,100,40,"Delete from Path",15,13);
//gui
gui G;

//Model 
Model M;

//diagram
diagram D;

//Tesselation
tesselation T;

//Color Scheme
//colorScheme C;


String Message="";

void setup() {
  size(1500, 900);
  windowResizable(true);
  textAlign(CENTER, CENTER);
  textSize(36);
  ellipseMode(RADIUS);
  
  //init interface
  G=new gui(interfaceWidth,2);
  G.addButton(ZoomPlusButton);
  G.addButton(ZoomMinusButton);
  G.addDefButton(ModelPanButton);
  G.addButton(AddLensButton);
  G.addButton(ModelViewButton);
  G.addButton(AddInnerPointButton);
  G.addButton(AddInnerLineButton);
  G.addButton(ModelDragButton);
  G.addButton(ModelDragResetButton);
  G.addButton(ModelResetButton);
  G.addButton(DeleteLensButton);
  G.addButton(AddToPathButton);
  G.addButton(DeleteFromPathButton);
  G.addButton(ModelSwitchButton);
  G.ButtonState=2;
  
  //initializing Model
  M=new Model(300);
  //init content
  D=new diagram();
  //init tesselation
  T=new tesselation(p,q,r);
  T.populateelementssector(tessdepth);
}
  
void draw() {
  background(colorScheme.backgroundColor[0],colorScheme.backgroundColor[1],colorScheme.backgroundColor[2]);
  String txt = String.format("HyperTesser. Size:%d x %d FPS: %6.2fps Model: %d Lenses,%d Points, %d Lines, %d Arcs, %d Path elements", width,height,frameRate, M.Lenses.size(),D.gPoints.size(),D.gLines.size(),D.gArcs.size(),T.Path.size());
  windowTitle(txt+Message);
  drawModel(M,T); 
  G.display();
}


void drawModel(Model M,tesselation T){
  pushMatrix();
  translate(M.xpt,M.ypt);
  M.pinput=new Complex(mouseX-M.xpt,mouseY-M.ypt);
  fill(colorScheme.modelColor);
  M.DrawBase();
  noFill();
  stroke(colorScheme.tessFillColor);
  strokeWeight(1);
  M.DrawTesselation(T);
  stroke(colorScheme.PathColor);
  strokeWeight(2);
  M.DrawPath(T);
  stroke(colorScheme.diagramColor);
  M.DrawDiagram(D);
  stroke(colorScheme.LensSchemeColor);
  M.DrawLensScheme();
  switch(G.ButtonState){
    case 2://Model Pan
      M.DrawLenses(D,T);
      break;
    case 3://Add lens
      switch(G.Mode){
        case 0:
        M.DrawAddLens0();
        break;
        case 1:
        M.DrawAddLens1(D,T);
        break;
        case 2:
        if(M.OverFlag){M.Lenses.add(M.newLens);}
        G.Mode=0;
        G.ButtonState=2;
        break;
      }
      break;
    case 5://Add point
      switch(G.Mode){
        case 0:
         M.DrawAddPoint(D);
        break;
        case 1:
         if(M.OverFlag){
          String pname = String.format("P%d", D.gPoints.size());
          D.newpoint.label=pname;
          D.gPoints.add(D.newpoint);
         }      
         G.Mode=0;
         break;
      }
      break;
    case 6://Model drag
        M.DrawLenses(D,T);
        break;    
    case 7://add line      
      switch(G.Mode){
        case 0:
         M.DrawAddLine0();
         break;
        case 1:
         M.DrawAddLine1(D);
         break;
        case 2:
         switch (M.newArcMode){
          case 0:
           D.gArcs.add(D.newarc);
           break;
          case 1:
           D.gSemiLines.add(D.newsline);
           break;
          case 2:
           D.gLines.add(D.newline);
           break;
         } 
         G.Mode=0;
         break;         
      }
      break;
    case 11://delete lens      
      switch(G.Mode){
        case 0:
         M.DrawSelectLens();
         break;
        case 1:
         M.Lenses.remove(M.newLens);
         G.Mode=0;
         if (M.Lenses.size()==0){G.ButtonState=2;}
         break;
      }
      break;
      
     case 12:
       stroke(colorScheme.defLineColor);
       M.drawEl(T.getgArc(T.newpathel));
  } 
  popMatrix();
}

void mousePressed() {
  int bid=G.whichButtonOver();
  switch(bid){
    case -1:
      if (G.ButtonState==AddLensButton.id||G.ButtonState==AddInnerPointButton.id||G.ButtonState==AddInnerLineButton.id||G.ButtonState==DeleteLensButton.id){
        G.Mode++;
      }
      break;
    case 0:
      M.R*=1.2;
      break;
    case 1:
      M.R/=1.2;
      if(M.R<50){M.R=50;}
      break;
    case 8:
      M.DragReset(); 
      break;
    case 9:
      M.Reset();
      D=new diagram();
      break;
    case 12:
      G.ButtonState=bid;
      Complex tt=M.dismotion.applyinv(new Complex());
      trgroupel te=T.getNearestGroupel(tt,(int)(2*distFromZero(tt)));
      T.newpathel=new pathel(te.address,0);
      break;
    case 14:
      M.type=(M.type+1)%2;
      switch(M.type){
        case 0:
          ModelSwitchButton.label="P";
          break;
        case 1:
          ModelSwitchButton.label="BK";
          break;
      }
      break;
    default:
      G.ButtonState=bid;
      G.Mode=0;
      break;    
  }
  
  //pan
  if (G.ButtonState==AddLensButton.id||G.ButtonState==ModelDragButton.id){
    G.Switches[0]=false;
  } 
  else{G.Switches[0]=true;}
}

void mouseDragged(){
  if (G.Switches[0]){
    M.Pan();
  }
  else if(G.ButtonState==ModelDragButton.id){
    M.Drag();
  }
}

void keyPressed() {
  if(G.ButtonState==12){
    if (key == CODED) {
      switch (keyCode){
        case UP:
          T.newpathel.address+='d';
          break;
        case DOWN:
          T.newpathel.address+='b';
          break;
        case LEFT:
          T.newpathel.address+='c';
          break;
        case RIGHT:
          T.newpathel.address+='a';
          break;
        
      }
    } else {
      switch(key){
       case 'a':
         T.newpathel.type=(T.newpathel.type+1)%5;
         break;
       case 32:
          String nadd=T.lazytrueaddress(T.newpathel.address).address;
          pathel patheladd=new pathel(nadd,T.newpathel.type);
          int ind=T.Path.indexOf(patheladd);
          ind=-1;
          for(int i=T.Path.size()-1;i>=0;i--){           
           if (T.Path.get(i).address.equals(patheladd.address)) {
             ind=i;
             break;
           }
          }
          if (ind==-1){
           T.Path.add(patheladd);
           Message=" added "+patheladd.address;
          }else{
           T.Path.remove(ind); 
           Message=" removed " +patheladd.address;
          }          
          break;
        
      }
    }
  }
}

void stroke(int[] Color){
  stroke(Color[0],Color[1],Color[2]);
}

void fill(int[] Color){
  fill(Color[0],Color[1],Color[2]);
}
