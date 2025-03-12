import complexnumbers.*;
import java.lang.Math;
import java.util.*;
//gui
gui G;

//Model 
Model M;

//diagram
diagram D;

//Tesselation
tesselation T;

//Group
pslMatrixGroup Gr;

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
  G.addButton(LabelStyleButton);
  G.ButtonState=2;
  
  //initializing Model
  M=new Model(300);
  //init content
  D=new diagram();
  //init tesselation
  T=new tesselation(p,q,r,tessdepth);
  T.populateelementssector(tessdepth);
  
  Gr=new pslMatrixGroup(2,7,ga,gb);
}
  
void draw() {
  background(backgroundColor[0],backgroundColor[1],backgroundColor[2]);
  String txt = String.format("HyperTesser. Size:%d x %d Tesselation: %d %d %d FPS: %6.2fps Model: %d Lenses,%d Points, %d Lines, %d Arcs, %d Path elements", width,height,T.p,T.q,T.r,frameRate, M.Lenses.size(),D.dPoints.size(),D.gLines.size(),D.gArcs.size(),T.Path.size());
  windowTitle(txt+Message+modInverseP(3,7));
  //T.populateelementssectoriterative();
  Message=" ";
  drawModel(M,T); 
  G.display();
}

void drawModel(Model M,tesselation T){
  pushMatrix();
  translate(M.xpt,M.ypt);
  M.pinput=new Complex(mouseX-M.xpt,mouseY-M.ypt);
  fill(modelColor);
  strokeWeight(3);
  M.DrawBase();
  noFill();
  stroke(tessFillColor);
  strokeWeight(1);
  M.DrawTesselation(T,Gr,new Lens());
  stroke(PathColor);
  strokeWeight(2);
  M.DrawPath(T,new Lens());
  stroke(diagramColor);
  M.DrawDiagram(D,new Lens());
  stroke(LensSchemeColor);
  M.DrawLensScheme();
  switch(G.ButtonState){
    case 2://Model Pan
      M.DrawLenses(D,T,Gr);
      break;
    case 3://Add lens
      switch(G.Mode){
        case 0:
        M.DrawAddLens0();
        break;
        case 1:
        M.DrawAddLens1(D,T,Gr);
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
          String pname = String.format("P%d", D.dPoints.size());
          Label label=new Label(pname);
          D.dPoints.add(new dPoint(D.newpoint.z,label));
         }      
         G.Mode=0;
         break;
      }
      break;
    case 6://Model drag
        M.DrawLenses(D,T,Gr);
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
       switch(G.Mode){
        case 0:
         M.DrawAddPathLine0(T);
         break;
        case 1:
         M.DrawAddPathLine1(T);
         break;
        case 2:
         T.Path.add(T.newparc);
         G.Mode=0;
         break;         
      }
      break;
    case 13:
      strokeWeight(5);
      stroke(defLineColor);
      if(T.Path.size()>0){
        M.drawEl(T.getgArc(T.Path.get(T.upForDeletionIndex)),new Lens());
      }
      break;
  } 
  popMatrix();
}

void mousePressed() {
  int bid=G.whichButtonOver();
  switch(bid){
    case -1:
      if (G.ButtonState==AddLensButton.id||G.ButtonState==AddInnerPointButton.id||G.ButtonState==AddInnerLineButton.id||G.ButtonState==DeleteLensButton.id||G.ButtonState==AddToPathButton.id){
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
    case 14:
      switch(M.type){
        case POINCARE:
          M.type=modelType.BELTRAMKLEIN;
          ModelSwitchButton.label="BK";
          break;
        case BELTRAMKLEIN:
          M.type=modelType.POINCARE;
          ModelSwitchButton.label="P";
          break;
      }
      break;
    case 15:
      switch(M.labelstyle){
        case FULL:
          M.labelstyle=LabelStyle.IDONLY;
          LabelStyleButton.label="Labels:Id only";
          break;
        case IDONLY:
          M.labelstyle=LabelStyle.FULL;
          LabelStyleButton.label="Labels:Full";
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
  if(G.ButtonState==13){
    if (key == CODED) {
      switch (keyCode){
        //case UP:
          //T.newpathel.address+='d';
          //break;
        //case DOWN:
          //T.newpathel.address+='b';
          //break;
        case LEFT:
          T.upForDeletionIndex++;
          if (T.upForDeletionIndex==T.Path.size()){T.upForDeletionIndex=0;}
          break;
        case RIGHT:
          T.upForDeletionIndex--;
          if (T.upForDeletionIndex==-1){T.upForDeletionIndex=T.Path.size()-1;}
          break;       
      }
    } else {
      switch(key){
       case 32:
          T.Path.remove(T.upForDeletionIndex);
          if(T.Path.size()==0){
            T.upForDeletionIndex=0;
            G.ButtonState=2;
          }else{
            T.upForDeletionIndex=T.upForDeletionIndex%T.Path.size();
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
