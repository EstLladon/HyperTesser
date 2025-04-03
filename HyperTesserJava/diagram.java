import complexnumbers.Complex;

import java.util.ArrayList;

//diagram
class diagram{
    ArrayList<dPoint> dPoints;
    ArrayList<gLine> gLines;
    ArrayList<gSemiLine> gSemiLines;
    ArrayList<gArc> gArcs;
    diagram(){
        dPoints= new ArrayList<>();
        gLines= new ArrayList<>();
        gSemiLines= new ArrayList<>();
        gArcs = new ArrayList<>();
    }
}

record gLine(double phi,double psi){}

record gSemiLine (double phi,Complex z){}

record gArc(Complex z1,Complex z2){}

record dPoint(Complex z,Label label){
    dPoint(Complex iz){
        this(iz,new Label());
    }
}

