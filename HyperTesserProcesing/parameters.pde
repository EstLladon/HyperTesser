//tesselation parameters
int p=7;
int q=3;
int r=2;
int tessdepth=11;

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
Button  ModelViewButton=new Button(0, 250, 100, 40,"No Lens View",15,4);
//add model
Button  AddInnerPointButton=new Button(0, 350, 100, 40,"Add Point",15,5);
Button  AddInnerLineButton=new Button(110, 350, 100, 40,"Add Line",15,7);
//path buttons
Button AddToPathButton=new Button(0,430,100,40,"Add to Path",15,12);
Button DeleteFromPathButton=new Button(110,430,100,40,"Delete from Path",15,13);
// Label style Button
Button LabelStyleButton=new Button(0,480,70,40,"Labels:Full",15,15);

//Model display parameters
double toleranceToZero=0.0001;
double angleToleranceToZero=0.01;
double tolToBorder=0.02;
double groupelTolerance=0.001;

//parameters for image elements
int SpotR=5;
int SpotTreshold=40;
int DotThreshold=10;
int labelTSize=20;
//color scheme

int[] backgroundColor={245,245,220};
int[] modelColor={250,250,250};
int[] lensColor={0,8,7};
int[] textColor={0,8,7};
int[] diagramColor={250,80,30};
int[] PathColor={250,80,30};
int[] tessFillColor={90,220,255};
int[] defLineColor={200,100,0};
int[] AddPathColor={250,50,170};
int[] LensSchemeColor={190,80,60};
int[] NewBorderPointColor={200,0,0};
int[] NewSemiLineColor={0,0,200};
int[] NewArcColor={0,200,0};
int[] NewLineColor={200,0,0};


int[] RED={250,0,0};
int[] GREEN={0,250,0};
int[] BLUE={0,0,250};
