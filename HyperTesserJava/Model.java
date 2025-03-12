import complexnumbers.Complex;

import java.util.ArrayList;

import static processing.core.PConstants.*;

class Model{
    //radius of base circle
    double R;
    //canvas shift
    float xpt;
    float ypt;
    //diagram
    //diagram D;
    //Lenses
    ArrayList<Lens> Lenses;
    //Inner shift
    HyperUtils.gMotion dismotion;
    //type of Model
    modelType type=modelType.POINCARE;
    //Label style
    LabelStyle labelstyle=LabelStyle.IDONLY;
    //Aux variables
    newArcType newArcMode;
    boolean OverFlag;
    Complex savedz=new Complex(),savedz2=new Complex(),newz=new Complex(),oldz,ppinput,pinput;
    double savedphi,savedphi2,newphi;

    Lens newLens;

    Model(double iR,float ixpt,float iypt){
        //init Lenses and Motion
        Lenses = new ArrayList<>();
        dismotion=new HyperUtils.gMotion();
        //display parameters
        R=iR;
        xpt= ixpt;
        ypt= iypt;
    }
    //Draw Main Model
    int drawModel(diagram D, tesselation T,myGroup Gr, HyperTesser h,int State, int Mode){
        h.translate(xpt,ypt);
        pinput=new Complex(h.mouseX-xpt,h.mouseY-ypt);
        ppinput=new Complex(h.pmouseX-xpt,h.pmouseY-ypt);
        newz =switch (type){
            case POINCARE -> dismotion.applyinv(pinput.div(R));
            case BELTRAMKLEIN -> dismotion.applyinv(ComplexUtils.BKtoP(pinput.div(R)));
        };
        newphi= dismotion.applyinv(pinput.arg());
        h.fill(h.modelColor);
        h.strokeWeight(3);
        DrawBase(h);
        h.noFill();
        h.stroke(h.tessFillColor);
        h.strokeWeight(1);
        DrawTesselationtrie(T,Gr,new Lens(),h);
        h.stroke(h.PathColor);
        h.strokeWeight(2);
        DrawPath(T,new Lens(),h);
        h.stroke(h.diagramColor);
        DrawDiagram(D,new Lens(),h);
        h.stroke(h.LensSchemeColor);
        DrawLensScheme(h.LensSchemeColor,h);
        switch(State){
            case 2://Model Pan
                DrawLenses(D,T,Gr,h);
                return 20;
            case 3://Add lens
                switch(Mode){
                    case 0:
                        DrawAddLens0(h);
                        return 30;
                    case 1:
                        DrawAddLens1(D,T,Gr,h);
                        return 31;
                    case 2:
                        if(OverFlag){Lenses.add(new Lens(savedphi,savedphi2));}
                        return 20;
                }
                break;
            case 5://Add point
                switch(Mode){
                    case 0:
                        //DrawAddPoint(h);
                        DrawAddPoint(h);
                        return 50;
                    case 1:
                        if(OverFlag){
                            String pname = String.format("P%d", D.dPoints.size());
                            D.dPoints.add(new dPoint(savedz,new Label(pname)));
                        }
                        return 50;
                }
                break;
            case 6://Model drag
                DrawLenses(D,T,Gr,h);
                return 60;
            case 7://add line
                switch(Mode){
                    case 0:
                        DrawAddLine0(h);
                        return 70;
                    case 1:
                        DrawAddLine1(D,h);
                        return 71;
                    case 2:
                        switch (newArcMode){
                            case ARC:
                                D.gArcs.add(new HyperUtils.gArc(savedz,savedz2));
                                break;
                            case SEMILINE:
                                D.gSemiLines.add(new HyperUtils.gSemiLine(savedphi,savedz));
                                break;
                            case LINE:
                                D.gLines.add(new HyperUtils.gLine(savedphi,savedphi2));
                                break;
                        }
                        return 70;
                }
                break;
            case 11://delete lens
                switch(Mode){
                    case 0:
                        DrawSelectLens(h);
                        return 110;
                    case 1:
                        Lenses.remove(newLens);
                        if (Lenses.isEmpty()){return 20;}else{return 110;}
                }
                break;
            case 12:
                switch(Mode){
                    case 0:
                        DrawAddPathLine0(T,h);
                        return 120;
                    case 1:
                        DrawAddPathLine1(T,h);
                        return 121;
                    case 2:
                        T.Path.add(T.newparc);
                        return 120;
                }
                break;
            case 13:
                h.strokeWeight(5);
                if(!T.Path.isEmpty()){
                    HyperUtils.gArc temparc=T.getgArc(T.Path.get(T.upForDeletionIndex));
                    drawArc(temparc.z1,temparc.z2,new Lens(),h.defLineColor,h);
                }
                return 130;
        }
        return 20;
    }

    void DrawBase(HyperTesser h){
        //h.Circle(0,0,R);
        h.ellipse( 0,0,(float)R,(float)R);
    }

    //Lenses
    void DrawLenses(diagram D, tesselation T, myGroup G,HyperTesser h){
        for(Lens L:Lenses){
            DrawLens(D,T,G,L,h);
        }
    }

    void DrawLens(diagram D, tesselation T, myGroup G, Lens L,HyperTesser h){
        DrawLensTop(L,h.lensColor,h.modelColor,h);
        h.noFill();
        h.stroke(h.tessFillColor);
        h.strokeWeight(1);
        DrawTesselationtrie(T,G,L,h);
        h.stroke(h.PathColor);
        h.strokeWeight(2);
        DrawPath(T,L,h);
        h.stroke(h.diagramColor);
        DrawDiagram(D,L,h);
        drawPoint(new Complex(),new Label(),L,h.diagramColor,h.textColor,h);
        DrawLensBot(L,h.lensColor,h);
    }

    void DrawLensTop(Lens L,int[] lensColor,int[] modelColor,HyperTesser h){
        L.Larc.display(R,lensColor,modelColor,h);
    }

    void DrawLensBot(Lens L,int[] lensColor,HyperTesser h){
        switch(type){
            case POINCARE:
                L.IHarc.display(R,lensColor,lensColor,h);
                L.OHarc.display(R,lensColor,lensColor,h);
                break;
            case BELTRAMKLEIN:
                L.IHarc.displayline(R,h);
                L.OHarc.displayline(R,h);
                break;
        }
    }

    void DrawDiagram(diagram D,Lens L,HyperTesser h){
        for(HyperUtils.gLine l:D.gLines){drawLine(l.phi,l.psi,L,h.diagramColor,h);}
        for(HyperUtils.gSemiLine s:D.gSemiLines){drawSemiLine(s.phi,s.z,L,h.diagramColor,h);}
        for(HyperUtils.gArc a:D.gArcs){drawArc(a.z1,a.z2,L,h.diagramColor,h);}
        for(dPoint g:D.dPoints){drawPoint(g.z,g.label,L,h.diagramColor,h.textColor,h);}
    }


    void DrawTesselationtrie(tesselation T, myGroup G, Lens L,HyperTesser h){
        HyperUtils.gMotion tm;
        Complex tt=dismotion.applyinv(L.Motion.applyinv(new Complex(T.bp)));
        tesselation.trgroupel te=T.getNearestGroupel(tt,(int)(2* ComplexUtils.distFromZero(tt)/T.side));
        h.Message+=te.address;
        for (String word : T.trie){
            HyperUtils.gMotion wm=T.trie.getbody(word);
            StringBuilder ta= new StringBuilder(word);
            for(int i=0;i<T.p;i++){
                tm=wm.combine(te.Motion);
                drawArc(tm.b,tm.apply(T.aP),L,h.tessFillColor,h);
                drawArc(tm.b,tm.apply(T.bP),L,h.tessFillColor,h);
                Complex z = L.Motion.apply(dismotion.apply(tm.b));
                if (type== modelType.BELTRAMKLEIN){z=ComplexUtils.PtoBK(z);}
                if (L.inView(z)) {
                    float size = h.getSpotSizeBase(z, R);
                    z = z.add(L.shift);
                    Label label = G.getlabel(G.getelement(te.address + ta), labelstyle);
                    h.fill(h.textColor);
                    label.display(z.re * R, z.im * R, (size / h.SpotR) * h.labelTSize, h);
                }
                wm.update(T.MA);
                ta.insert(0, 'a');
            }
        }
    }

    void DrawPath(tesselation T, Lens L,HyperTesser h){
        for (tesselation.pArc pe:T.Path){
            HyperUtils.gArc temparc=T.getgArc(pe);
            drawArc(temparc.z1,temparc.z2,L,h.PathColor,h);
        }
    }

    void DrawLensScheme(int[] LensSchemeColor,HyperTesser h){
        switch(type){
            case POINCARE:
                for(Lens L:Lenses){
                    L.IHarc.display(R,LensSchemeColor,h);
                }
                break;
            case BELTRAMKLEIN:
                for(Lens L:Lenses){
                    L.IHarc.displayline(R,h);
                }
                break;
        }
    }

    //Draw elements
    void drawPoint(Complex z,Label label,Lens L,int[] spotColor,int[] textColor,HyperTesser h){
        Complex displayz=switch (type){
            case BELTRAMKLEIN -> ComplexUtils.PtoBK(L.Motion.apply(dismotion.apply(z)));
            case POINCARE -> L.Motion.apply((dismotion.apply(z)));
        };if (L.inView(displayz)){
            Complex zz=displayz.add(L.shift);
            double r;
            double d=R*(1-displayz.abs());
            if (d>=h.SpotTreshold){
                r= h.SpotR;
            }else
            if (d>=h.DotThreshold){
                r=(h.SpotR*(d-h.DotThreshold)/(h.SpotTreshold-h.DotThreshold));
            }else{r=0;}
            Arc.displaySpot(zz,r,R,spotColor,spotColor,h);
            h.fill(textColor);
            label.display(zz.re*R,zz.im*R,(r/h.SpotR)*h.labelTSize,h);
        }
    }


    void drawLine(double phi, double psi, Lens L,int[] Color,HyperTesser h){
        double phip=L.Motion.apply(dismotion.apply(phi));
        double psip=L.Motion.apply(dismotion.apply(psi));
        h.noFill();
        switch(type){
            case POINCARE:
                new Arc(L.shift.re,L.shift.im, 1,phip,psip,ArcType.HYPER).display(R,Color,h);
                break;
            case BELTRAMKLEIN:
                HyperUtils.gArc temp=L.trimLine(ComplexUtils.eip(phip), ComplexUtils.eip(psip));
                h.stroke(Color);
                h.line((float)(temp.z1.add(L.shift).re*R),(float)(temp.z1.add(L.shift).im*R),(float)(temp.z2.add(L.shift).re*R),(float)(temp.z2.add(L.shift).im*R));
                break;
        }
    }

    void drawSemiLine(double phi,Complex z, Lens L,int[] Color,HyperTesser h){
        double phip=L.Motion.apply(dismotion.apply(phi));
        Complex displayz;
        h.noFill();
        switch(type){
            case POINCARE:
                displayz=L.Motion.apply(dismotion.apply(z));
                new Arc(L.shift,phip,displayz.add(L.shift)).display(R,Color,h);
                break;
            case BELTRAMKLEIN:
                displayz=ComplexUtils.PtoBK(L.Motion.apply(dismotion.apply(z)));
                HyperUtils.gArc temp=L.trimLine(ComplexUtils.eip(phip),displayz);
                h.stroke(Color);
                h.line((float)(temp.z1.add(L.shift).re*R),(float)(temp.z1.add(L.shift).im*R),(float)(temp.z2.add(L.shift).re*R),(float)(temp.z2.add(L.shift).im*R));
                break;
        }
    }


    void drawArc(Complex z1,Complex z2, Lens L,int[] Color,HyperTesser h){
        Complex displayz1=L.Motion.apply(dismotion.apply(z1));
        Complex displayz2=L.Motion.apply(dismotion.apply(z2));
        h.noFill();
        switch(type){
            case POINCARE:
                if (Arc.angdiff(displayz1.arg(),displayz2.arg())<h.toleranceToZero||displayz1.abs()<h.toleranceToZero||displayz2.abs()<h.toleranceToZero){
                    h.stroke(Color);
                    h.line((float)(displayz1.add(L.shift).re*R),(float)(displayz1.add(L.shift).im*R),(float)(displayz2.add(L.shift).re*R),(float)(displayz2.add(L.shift).im*R));
                }else {
                    Arc temparc = new Arc(displayz1, displayz2);
                    temparc.move(L.shift);
                    temparc.display(R, Color, h);
                }
                break;
            case BELTRAMKLEIN:
                displayz1=ComplexUtils.PtoBK(displayz1);
                displayz2=ComplexUtils.PtoBK(displayz2);
                HyperUtils.gArc temp=L.trimLine(displayz1,displayz2);
                h.stroke(Color);
                h.line((float)(temp.z1.add(L.shift).re*R),(float)(temp.z1.add(L.shift).im*R),(float)(temp.z2.add(L.shift).re*R),(float)(temp.z2.add(L.shift).im*R));
                break;
        }
    }

    //functions
    //Add Lens
    public void DrawAddLens0(HyperTesser h){
        savedz=pinput;
        savedz.setAbs(1);
        Arc.displaySpot(savedz,h.SpotR,R,h.NewBorderPointColor,h.NewBorderPointColor,h);

        savedphi= pinput.arg();
    }

    public void DrawAddLens1(diagram D, tesselation T, myGroup G,HyperTesser h){
        pinput.setAbs(1);
        double diff=Arc.angdiff(savedz.arg(),pinput.arg());
        if (diff>0.01f && diff<HALF_PI){
            OverFlag=true;
            savedphi2= pinput.arg();
            //newLens=new Lens(savedphi,savedphi2);
            DrawLens(D,T,G,new Lens(savedphi,savedphi2),h);
        }else{
            OverFlag=false;
        }
        h.stroke(h.NewBorderPointColor);
        Arc.displaySpot(savedz,h.SpotR,R,h.NewBorderPointColor,h.NewBorderPointColor,h);
        Arc.displaySpot(pinput,h.SpotR,R,h.NewBorderPointColor,h.NewBorderPointColor,h);
    }

    //Select Lens
    public void DrawSelectLens(HyperTesser h){
        for(Lens L:Lenses){
            if(pinput.div(R).sub(L.shift).abs()<1){
                newLens=L;
                h.stroke(h.NewBorderPointColor);
                h.strokeWeight(3);
                DrawLensTop(L,h.lensColor,h.modelColor,h);
                DrawLensBot(L,h.lensColor,h);
            }
        }
    }

    //Add Point
    public void DrawAddPoint(HyperTesser h){
        if (pinput.abs()<R){
            h.stroke(h.NewArcColor);
            drawPoint(newz,new Label(),new Lens(),h.NewArcColor,h.NewArcColor,h);
            savedz=newz;
            OverFlag=true;
        }else{
            OverFlag=false;
        }
    }

    //Add Line
    public void DrawAddLine0(HyperTesser h){
        if (pinput.abs()<R){
            savedz=newz;
            OverFlag=true;
            drawPoint(savedz,new Label(),new Lens(),h.NewArcColor,h.NewArcColor,h);
        }else{
            OverFlag=false;
            savedphi=newphi;
            h.stroke(h.NewBorderPointColor);
            pinput.setAbs(1);
            Arc.displaySpot(pinput,h.SpotR,R,h.NewBorderPointColor,h.NewBorderPointColor,h);
        }
    }

    public void DrawAddLine1(diagram D,HyperTesser h){
        if (pinput.abs()<R){
            if (OverFlag){
                savedz2=newz;
                newArcMode=newArcType.ARC;
            }else{
                savedz=newz;
                newArcMode=newArcType.SEMILINE;
            }
        }
        else{
            if (OverFlag){
                savedphi=newphi;
                newArcMode=newArcType.SEMILINE;
            }else{
                savedphi2=newphi;
                newArcMode=newArcType.LINE;
            }
            pinput.setAbs(R);
        }
        h.strokeWeight(3);
        h.noFill();
        switch (newArcMode){
            case ARC:
                drawArc(savedz,savedz2, new Lens(),h.NewArcColor,h);
                drawPoint(savedz,new Label(),new Lens(),h.NewArcColor,h.NewArcColor, h);
                drawPoint(savedz2,new Label(),new Lens(),h.NewArcColor,h.NewArcColor, h);
                break;
            case SEMILINE:
                drawSemiLine(savedphi,savedz,new Lens(),h.NewSemiLineColor,h);
                drawPoint(savedz,new Label(),new Lens(),h.NewSemiLineColor,h.NewSemiLineColor, h);
                break;
            case LINE:
                drawLine(savedphi,savedphi2,new Lens(),h.NewLineColor, h);
                h.fill(h.NewLineColor);
                break;
        }
    }

    //Add Line to Path
    public void DrawAddPathLine0(tesselation T,HyperTesser h){
        //HyperTesser h=HyperTesser.getInstance();
        if (pinput.abs()<R){
            T.newpathel=T.getNearestPathel(newz,(int)(2* ComplexUtils.distFromZero(newz)/T.side));
            drawPoint(T.getPoint(T.newpathel),new Label(),new Lens(),h.AddPathColor,h.textColor,h);
            OverFlag=true;
        }else{
            OverFlag=false;
        }
    }

    public void DrawAddPathLine1(tesselation T,HyperTesser h){
        //HyperTesser h=HyperTesser.getInstance();
        if (pinput.abs()<R){
            new Complex();
            Complex z2 = switch (type) {
                case POINCARE -> dismotion.applyinv(pinput.div(R));
                case BELTRAMKLEIN -> dismotion.applyinv(ComplexUtils.BKtoP(pinput.div(R)));
            };
            if (OverFlag){
                tesselation.pathel pathel2=T.getNearestPathel(z2,(int)(2* ComplexUtils.distFromZero(z2)/T.side));
                T.newparc=new tesselation.pArc(T.newpathel,pathel2);
                HyperUtils.gArc temparc =T.getgArc(T.newparc);
                drawArc(temparc.z1,temparc.z2,new Lens(),h.AddPathColor,h);
            }else{
                h.G.Mode=0;
            }
        }
    }

    //misc
    void zoomIncrease(){
        R*=1.2;
    }
    void zoomDecrease(){
        R/=1.2;
        if(R<50){R=50;}
    }
    public void dragReset(){
        dismotion=new HyperUtils.gMotion();
    }

    public void Reset(){
        Lenses = new ArrayList<>();
        dismotion=new HyperUtils.gMotion();
    }

    void Pan(HyperTesser h){
        //HyperTesser h=HyperTesser.getInstance();
        xpt+=(h.mouseX-h.pmouseX);
        ypt+=(h.mouseY-h.pmouseY);
    }

    void Drag(int mx,int my,int pmx,int pmy,HyperTesser h){
        pinput=new Complex(mx-xpt,my-ypt);
        ppinput=new Complex(pmx-xpt,pmy-ypt);
        if(pinput.abs()<0.9f*R){
            newz =switch (type){
                case POINCARE -> dismotion.applyinv(pinput.div(R));
                case BELTRAMKLEIN -> dismotion.applyinv(ComplexUtils.BKtoP(pinput.div(R)));
            };
            oldz =switch (type){
                case POINCARE -> dismotion.applyinv(ppinput.div(R));
                case BELTRAMKLEIN -> dismotion.applyinv(ComplexUtils.BKtoP(ppinput.div(R)));
            };
            dismotion.preupdate(new HyperUtils.gMotion(oldz,newz));
        }
    }
}

enum modelType{
    POINCARE,
    BELTRAMKLEIN
}
