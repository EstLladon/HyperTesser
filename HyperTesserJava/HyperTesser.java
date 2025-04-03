import org.jetbrains.annotations.NotNull;
import processing.core.*;
import complexnumbers.*;

public class HyperTesser extends PApplet {

//gui
gui G;

//Model 
hModel M;

//diagram
diagram D;
//Tesselation
htesselation T;
//Group
myGroup Gr;

String Message="";

public void setup() {
  /* size commented out by preprocessor */
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
  hMotiondispenser gdisp=new ggMotionDispenser();
  hMotiondispenser hdisp=new hhMotionDispenser();
  hMotiondispenser disp=hdisp;
  M=new hModel(300, (float) width /2, (float) height /2,gdisp);
  //init diagram
  D=new diagram();
  //init tesselation
  T=new htesselation(p,q,r,gdisp);
  //T=new tesselation(p,q,r);
  T.populateeltrie(tessdepth);
  pslMatrixDispenser psldisp=new pslMatrixDispenser(7);
  Gr=new newpsl2PMatrixGroup(new intMatrix2by2(ga,7),new intMatrix2by2(gb,7),7,psldisp);
}
  
public void draw() {
  int res;
  background(backgroundColor[0],backgroundColor[1],backgroundColor[2]);
  Message=" ";
  pushMatrix();
  M.pinput=new Complex(mouseX-M.xpt,mouseY-M.ypt);
  M.ppinput=new Complex(pmouseX-M.xpt,pmouseY-M.ypt);
  translate(M.xpt,M.ypt);
  res=M.drawModel(D,T,Gr,this,G.ButtonState,G.Mode);
  //res=M2.drawModel(D,T2,Gr,this,G.ButtonState,G.Mode);
  popMatrix();
  G.ButtonState=res/10;
  G.Mode=res%10;
  G.display(this);
  String txt=String.format(" fps:%6.2f newz:%6.2f %6.2f Ps:%d",frameRate,M.newz.re,M.newz.im,D.dPoints.size());
  windowTitle(txt+Message);
}

public void mousePressed() {
  int bid=G.whichButtonOver(mouseX,mouseY,width);
  switch(bid){
    case -1:
      if (G.ButtonState==AddLensButton.id||G.ButtonState==AddInnerPointButton.id||G.ButtonState==AddInnerLineButton.id||G.ButtonState==DeleteLensButton.id||G.ButtonState==AddToPathButton.id){
        G.Mode++;
      }
      break;
    case 0:
      M.zoomIncrease();
      break;
    case 1:
      M.zoomDecrease();
      break;
    case 8:
      M.dragReset();
      break;
    case 9:
      D=new diagram();
      M.dragReset();
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
    G.Switches[0]= G.ButtonState != AddLensButton.id && G.ButtonState != ModelDragButton.id;
}

public void mouseDragged(){
  if (G.Switches[0]){
    M.Pan(mouseX,mouseY,pmouseX,pmouseY);
  }
  else if(G.ButtonState==ModelDragButton.id) {
    M.Drag(mouseX,mouseY,pmouseX,pmouseY);
  }
}

public void keyPressed() {
  if(G.ButtonState==13){
    if (key == CODED) {
      switch (keyCode){
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
        if (key == 32) {
            T.Path.remove(T.upForDeletionIndex);
            if (T.Path.isEmpty()) {
                T.upForDeletionIndex = 0;
                G.ButtonState = 2;
            } else {
                T.upForDeletionIndex = T.upForDeletionIndex % T.Path.size();
            }
        }
    }
  }
}

public void stroke(int @NotNull [] Color){
  stroke(Color[0],Color[1],Color[2]);
}

public void fill(int @NotNull [] Color){
  fill(Color[0],Color[1],Color[2]);
}

public float getSpotSizeBase(@org.jetbrains.annotations.NotNull Complex z, double R){
        double d=R*(1-z.abs());
        if (d>=SpotTreshold){
            return SpotR;
        }
        if (d>=DotThreshold){
            return (float)(SpotR*(d-DotThreshold)/(SpotTreshold-DotThreshold));
        }
        return 0;
}

//tesselation parameters
int p=7;
int q=3;
int r=2;
int tessdepth=15;

//Group parameters

int[][] ga={ { 1, 6 }, 
             { 0, 1 } };
int[][] gb={ { 1, 6 }, 
             { 1, 0 } };


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
Button  ModelSwitchButton=new Button(90, 10, 60, 40,"P",36,14);
//Lens buttons
Button  AddLensButton=new Button(0, 200, 100, 40,"Add Lens",15,3);
Button  DeleteLensButton=new Button(110, 200, 100, 40,"Delete Lens",15,11);
Button  ModelViewButton=new Button(0, 250, 100, 40,"Lens Scheme View",15,4);
//add model
Button  AddInnerPointButton=new Button(0, 350, 100, 40,"Add Point",15,5);
Button  AddInnerLineButton=new Button(110, 350, 100, 40,"Add Line",15,7);
//path buttons
Button AddToPathButton=new Button(0,430,100,40,"Add to Path",15,12);
Button DeleteFromPathButton=new Button(110,430,100,40,"Delete from Path",15,13);
// Label style Button
Button LabelStyleButton=new Button(0,480,100,40,"Labels:Id's only",15,15);

//Model display parameters
public double toleranceToZero=0.0001f;
public static double angleToleranceToZero=0.01f;
public double tolToBorder=0.02f;
public static double groupelTolerance=0.001f;

//parameters for image elements
public int SpotR=5;
public int SpotTreshold=40;
public int DotThreshold=10;
public int labelTSize=20;
//color scheme

public int[] backgroundColor={245,245,220};
public int[] modelColor={250,250,250};
public int[] lensColor={0,8,7};
public int[] textColor={0,8,7};
public int[] diagramColor={250,80,30};
public int[] PathColor={250,80,30};
public int[] tessFillColor={90,220,255};
public int[] defLineColor={200,100,0};
public int[] AddPathColor={250,50,170};
public int[] LensSchemeColor={190,80,60};
public int[] NewBorderPointColor={200,0,0};
public int[] NewSemiLineColor={0,0,200};
public int[] NewArcColor={0,200,0};
public int[] NewLineColor={200,0,0};

public int[] RED={250,0,0};
public int[] GREEN={0,250,0};
public int[] BLUE={0,0,250};


public void settings() { size(1500, 900); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "HyperTesser" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }

  }
}
