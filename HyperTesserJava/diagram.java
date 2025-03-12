import complexnumbers.Complex;

import java.util.ArrayList;

//diagram
class diagram{
    ArrayList<dPoint> dPoints;
    ArrayList<HyperUtils.gLine> gLines;
    ArrayList<HyperUtils.gSemiLine> gSemiLines;
    ArrayList<HyperUtils.gArc> gArcs;
    diagram(){
        dPoints= new ArrayList<>();
        gLines= new ArrayList<>();
        gSemiLines= new ArrayList<>();
        gArcs = new ArrayList<>();
    }
}

class dPoint extends HyperUtils.gPoint {
    Label label;
    dPoint(Complex z){
        super(z);
        label=new Label();
    }
    dPoint(Complex z,Label ilabel){
        super(z);
        label=ilabel;
    }
}

//hdiagram
class hDiagram{
    ArrayList<dPoint> dPoints;
    ArrayList<HyperUtils.gLine> gLines;
    ArrayList<HyperUtils.gSemiLine> gSemiLines;
    ArrayList<HyperUtils.gArc> gArcs;
    hDiagram(){
        dPoints= new ArrayList<>();
        gLines= new ArrayList<>();
        gSemiLines= new ArrayList<>();
        gArcs = new ArrayList<>();
    }
}

class hdPoint extends HyperUtils.hPoint {
    Label label;
    hdPoint(Complex z){
        super(z);
        label=new Label();
    }
    hdPoint(Complex z,Label ilabel){
        super(z);
        label=ilabel;
    }
}