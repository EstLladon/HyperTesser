import complexnumbers.Complex;

import java.util.ArrayList;

import static processing.core.PConstants.*;

class Model2 {
    //radius of base circle
    double R;
    //canvas shift
    float xpt;
    float ypt;
    //diagram
    //hDiagram hD;
    //Lenses
    ArrayList<Lens> Lenses;
    //Inner shift
    HyperUtils.hMotion dismotion;
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

    Model2(double iR,float ixpt,float iypt){
        //init Lenses and Motion
        Lenses = new ArrayList<>();
        dismotion=new HyperUtils.hMotion();
        //display parameters
        R=iR;
        xpt= ixpt;
        ypt= iypt;
        //hD=new hDiagram();
    }
    int drawModel(hDiagram hD, tesselation T,myGroup Gr, HyperTesser h,int State, int Mode){
        h.translate(xpt,ypt);
        pinput=new Complex(h.mouseX-xpt,h.mouseY-ypt);
        ppinput=new Complex(h.pmouseX-xpt,h.pmouseY-ypt);
        newz =switch (type){
            case POINCARE -> dismotion.applyinv(ComplexUtils.PtoH(pinput.div(R)));
            case BELTRAMKLEIN -> dismotion.applyinv(ComplexUtils.BKtoH(pinput.div(R)));
        };
        newphi= dismotion.applyinv(pinput.arg());
        h.fill(h.modelColor);
        h.strokeWeight(3);
        DrawBase(h);
        h.noFill();
        //h.stroke(h.tessFillColor);
        //h.strokeWeight(1);
        //
        DrawTesselationtrie(T,Gr,new Lens(),h);
        //h.stroke(h.PathColor);
        //h.strokeWeight(2);
        DrawPath(T,new Lens(),h);
        DrawDiagram(hD,new Lens(),h);
        DrawLensScheme(h.LensSchemeColor,h);

        switch(State){
            case 2://Model Pan
                DrawLenses(hD,T,Gr,h);
                return 20;
            case 3://Add lens
                switch(Mode){
                    case 0:
                        DrawAddLens0(h);
                        return 30;
                    case 1:
                        DrawAddLens1(hD,T,Gr,h);
                        return 31;
                    case 2:
                        if(OverFlag){Lenses.add(newLens);}
                        return 20;
                }
                break;
            case 4:
                return 40;
            case 5://Add point
                switch(Mode){
                    case 0:
                        DrawAddPoint(h);
                        return 50;
                    case 1:
                        if(OverFlag){
                            String pname = String.format("P%d", hD.dPoints.size());
                            hD.dPoints.add(new dPoint(savedz,new Label(pname)));
                        }
                        return 50;
                }
                break;
            case 6://Model drag
                DrawLenses(hD,T,Gr,h);
                return 60;
            case 7://add line
                switch(Mode){
                    case 0:
                        DrawAddLine0(h);
                        return 70;
                    case 1:
                        DrawAddLine1(h);
                        return 71;
                    case 2:
                        switch (newArcMode){
                            case ARC:
                                hD.gArcs.add(new HyperUtils.gArc(savedz,savedz2));
                                break;
                            case SEMILINE:
                                hD.gSemiLines.add(new HyperUtils.gSemiLine(savedphi,savedz));
                                break;
                            case LINE:
                                hD.gLines.add(new HyperUtils.gLine(savedphi,savedphi2));
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
                        //DrawAddPathLine0(T,h);
                        return 120;
                    case 1:
                        //DrawAddPathLine1(T,h);
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
        h.ellipse( 0,0,(float)R,(float)R);
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

    void DrawDiagram(hDiagram hD,Lens L,HyperTesser h){
        for(HyperUtils.gLine l:hD.gLines){drawLine(l.phi,l.psi,L,h.diagramColor,h);}
        for(HyperUtils.gSemiLine s:hD.gSemiLines){drawSemiLine(s.z,s.phi,L,h.diagramColor,h);}
        for(HyperUtils.gArc a:hD.gArcs){drawArc(a.z1,a.z2,L,h.diagramColor,h);}
        for(dPoint g:hD.dPoints){drawPoint(g.z,g.label,L,h.diagramColor,h.textColor,h);}
    }

    void drawPoint(Complex z,Label label,Lens L,int[] spotColor,int[] textColor,HyperTesser h){
        Complex displayz=switch (type){
            case BELTRAMKLEIN -> L.Motion.apply(ComplexUtils.HtoBK(dismotion.apply(z)));
            case POINCARE -> L.Motion.apply(ComplexUtils.HtoP(dismotion.apply(z)));
        };
        if (L.inView(displayz)){
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

    void drawLine(double phi,double psi, Lens L,int[] Color,HyperTesser h){
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

    void drawSemiLine(Complex z,double phi, Lens L,int[] Color,HyperTesser h){
        double phip=L.Motion.apply(dismotion.apply(phi));
        Complex zz;
        h.noFill();
        switch (type){
            case BELTRAMKLEIN:
                zz=ComplexUtils.PtoBK(L.Motion.apply(ComplexUtils.HtoP(dismotion.apply(z))));
                HyperUtils.gArc temp=L.trimLine(ComplexUtils.eip(phip),zz);
                h.stroke(Color);
                h.line((float)(temp.z1.add(L.shift).re*R),(float)(temp.z1.add(L.shift).im*R),(float)(temp.z2.add(L.shift).re*R),(float)(temp.z2.add(L.shift).im*R));
                break;
            case POINCARE:
                zz=L.Motion.apply(ComplexUtils.HtoP(dismotion.apply(z)));
                new Arc(L.shift,phip,zz.add(L.shift)).display(R,Color,h);
                break;
        };
    }

    void drawArc(Complex z1,Complex z2, Lens L,int[] Color,HyperTesser h){
        Complex zz1,zz2;
        h.noFill();
        h.stroke(Color);
        switch (type){
            case BELTRAMKLEIN:
                zz1= ComplexUtils.PtoBK(L.Motion.apply(ComplexUtils.HtoP(dismotion.apply(z1))));
                zz2=ComplexUtils.PtoBK(L.Motion.apply(ComplexUtils.HtoP(dismotion.apply(z2))));
                HyperUtils.gArc temp=L.trimLine(zz1,zz2);
                h.line((float)(temp.z1.add(L.shift).re*R),(float)(temp.z1.add(L.shift).im*R),(float)(temp.z2.add(L.shift).re*R),(float)(temp.z2.add(L.shift).im*R));
                break;
            case POINCARE:
                zz1=L.Motion.apply(ComplexUtils.HtoP(dismotion.apply(z1)));
                zz2=L.Motion.apply(ComplexUtils.HtoP(dismotion.apply(z2)));
                Arc temparc = new Arc(zz1,zz2);
                temparc.move(L.shift);
                temparc.display(R,Color,h);
                break;
        };
    }

    void DrawLenses(hDiagram hD,tesselation T, myGroup G,HyperTesser h){
        for(Lens L:Lenses){
            DrawLens(hD,T,G,L,h);
        }
    }

    void DrawLens(hDiagram hD,tesselation T, myGroup G, Lens L,HyperTesser h){
        DrawLensTop(L,h.lensColor,h.modelColor,h);
        h.noFill();
        h.stroke(h.tessFillColor);
        h.strokeWeight(1);
        //DrawTesselation(T,G,L,h);
        //DrawTesselationtrie(T,G,L,h);
        h.stroke(h.PathColor);
        h.strokeWeight(2);
        //DrawPath(T,L,h);
        h.stroke(h.diagramColor);
        DrawDiagram(hD,L,h);
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

    //tesselation
    void DrawTesselationtrie(tesselation T, myGroup G, Lens L,HyperTesser h){
        HyperUtils.hMotion tm;
        Complex tt=dismotion.applyinv(L.Motion.applyinv(T.cP));
        tesselation.trhgroupel te=T.getNearesthGroupel(tt,(int)(2* ComplexUtils.distFromZero(tt)/T.side));
        h.Message+=te.address;
        for (String word : T.trie){
            HyperUtils.hMotion wm=T.htrie.getbody(word);
            StringBuilder ta= new StringBuilder(word);
            for(int i=0;i<T.p;i++){
                tm=wm.combine(te.Motion);
                drawArc(tm.apply(new Complex()),tm.apply(T.aP),L,h.tessFillColor,h);
                drawArc(tm.apply(new Complex()),tm.apply(T.bP),L,h.tessFillColor,h);
                Complex z=switch (type){
                    case POINCARE -> L.Motion.apply(ComplexUtils.HtoP(dismotion.apply(tm.getz())));
                    case BELTRAMKLEIN -> ComplexUtils.PtoBK(L.Motion.apply(ComplexUtils.HtoP(dismotion.apply(tm.getz()))));
                };
                float size=h.getSpotSizeBase(z,R);
                z=z.add(L.shift);
                Label label= G.getlabel(G.getelement(te.address+ta),labelstyle);
                h.fill(h.textColor);
                label.display(z.re*R,z.im*R,(size/h.SpotR)*h.labelTSize,h);
                wm.update(T.hMA);
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
    //functions
    //Add Lens
    public void DrawAddLens0(HyperTesser h){
        pinput.setAbs(1);
        Arc.displaySpot(pinput,h.SpotR,R,h.NewBorderPointColor,h.NewBorderPointColor,h);
        savedz=pinput;
    }

    public void DrawAddLens1(hDiagram hD,tesselation T, myGroup G,HyperTesser h){
        pinput.setAbs(1);
        double diff=Arc.angdiff(savedz.arg(),pinput.arg());
        if (diff>0.01f && diff<HALF_PI){
            OverFlag=true;
            newLens=new Lens(savedz.arg(),pinput.arg());
            DrawLens(hD,T,G,newLens,h);
        }else{
            OverFlag=false;
        }
        h.stroke(h.NewBorderPointColor);
        Arc.displaySpot(savedz,h.SpotR,R,h.NewBorderPointColor,h.NewBorderPointColor,h);
        Arc.displaySpot(pinput,h.SpotR,R,h.NewBorderPointColor,h.NewBorderPointColor,h);
    }

    //Add point
    public void DrawAddPoint(HyperTesser h){
        if (pinput.abs()<R*0.9){
            drawPoint(newz,new Label(),new Lens(),h.GREEN,h.textColor,h);
            savedz=newz;
            OverFlag=true;
        }else{
            OverFlag=false;
        }
    }
    //Lens select
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

    public void DrawAddLine1(HyperTesser h){
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
                drawSemiLine(savedz,savedphi,new Lens(),h.NewSemiLineColor,h);
                drawPoint(savedz,new Label(),new Lens(),h.NewSemiLineColor,h.NewSemiLineColor, h);
                break;
            case LINE:
                drawLine(savedphi,savedphi2,new Lens(),h.NewLineColor, h);
                h.fill(h.NewLineColor);
                break;
        }
    }
    //Model drag
    void Drag(int mx,int my,int pmx,int pmy,HyperTesser h){
        pinput=new Complex(mx-xpt,my-ypt);
        ppinput=new Complex(pmx-xpt,pmy-ypt);
        if(pinput.abs()<0.9f*R){
            newz =switch (type){
                case POINCARE -> dismotion.applyinv(ComplexUtils.PtoH(pinput.div(R)));
                case BELTRAMKLEIN -> dismotion.applyinv(ComplexUtils.BKtoH(pinput.div(R)));
            };
            oldz =switch (type){
                case POINCARE -> dismotion.applyinv(ComplexUtils.PtoH(ppinput.div(R)));
                case BELTRAMKLEIN -> dismotion.applyinv(ComplexUtils.BKtoH(ppinput.div(R)));
            };
            dismotion.preupdate(new HyperUtils.hMotion(oldz,newz));
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
    void dragReset(){
        dismotion=new HyperUtils.hMotion();
    }

}
enum newArcType{
    ARC,
    SEMILINE,
    LINE
}