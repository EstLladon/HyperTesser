import complexnumbers.Complex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static processing.core.PApplet.println;
import static processing.core.PConstants.*;

class hModel{
    //radius of base circle
    double R;
    //canvas shift
    float xpt;
    float ypt;
    //Lenses
    //ArrayList<Lens> Lenses;
    Tree<hLens> lensTree;
    ArrayList<gArc> TessPath;
    //Inner shift
    hMotion dismotion;
    //type of Model
    modelType type=modelType.POINCARE;
    //Label style
    LabelStyle labelstyle=LabelStyle.IDONLY;
    //Aux variables
    newArcType newArcMode;
    boolean OverFlag;
    Complex savedz=new Complex(),savedz2=new Complex(),newz=new Complex(),oldz,ppinput,pinput;
    double savedphi,savedphi2,newphi;

    TreeNode<hLens> selectedLens,savedLens;
    hMotiondispenser disp;

    hModel(double iR,float ixpt,float iypt,hMotiondispenser disp){
        //init Lenses and Motion
        lensTree=new Tree<>(new hLens(disp));
        dismotion=disp.id();
        //display parameters
        R=iR;
        xpt= ixpt;
        ypt= iypt;
        this.disp=disp;
    }
    //Draw Main Model
    int drawModel(diagram D, htesselation T,myGroup Gr, HyperTesser h,int State, int Mode){
        Complex z=pinput.div(R);
        selectedLens=findLens(z);
        hLens L=selectedLens.getValue();
        Complex zz=z.sub(L.shift);
        newphi=L.Motion.applyinv(zz.arg());
        newz =switch (type){
            case POINCARE -> dismotion.applyinv(L.Motion.applyinv(disp.fromP(zz)));
            case BELTRAMKLEIN -> dismotion.applyinv(L.Motion.applyinv(disp.fromBK(zz)));
        };
        calcTessPath(T,h);
        DrawLenses(D,T,Gr,h);
        switch(State){
            case 3://Add lens
                switch(Mode){
                    case 0:
                        DrawAddLens0(h);
                        return 30;
                    case 1:
                        DrawAddLens1(D,T,Gr,h);
                        return 31;
                    case 2:
                        hLens NL=new hLens(savedphi,savedphi2,selectedLens.getValue(),disp);
                        //if(OverFlag){Lenses.add(L);}
                        selectedLens.addChild(new TreeNode<>(NL));
                        return 20;
                }
                break;
            case 4:
                DrawLens(D,T,Gr,lensTree.root,h);
                DrawLensScheme(h.LensSchemeColor,h);
                return 40;
            case 5://Add point
                switch(Mode){
                    case 0:
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
                                D.gArcs.add(new gArc(savedz,savedz2));
                                break;
                            case SEMILINE:
                                D.gSemiLines.add(new gSemiLine(savedphi,savedz));
                                break;
                            case LINE:
                                D.gLines.add(new gLine(savedphi,savedphi2));
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
                        lensTree.removeNode(selectedLens);
                        if (lensTree.root.getChildren().isEmpty()){return 20;}else{return 110;}
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
                if(!T.Path.isEmpty()){
                    gArc temparc=T.getgArc(T.Path.get(T.upForDeletionIndex));
                    drawArc(temparc.z1(),temparc.z2(),new hLens(disp),h.defLineColor,5,h);
                }
                return 130;
        }
        return 20;
    }

    void DrawBase(@NotNull HyperTesser h){
        //h.Circle(0,0,R);
        h.fill(h.modelColor);
        h.strokeWeight(3);
        h.ellipse( 0,0,(float)R,(float)R);
    }

    //Lenses


    void DrawLenses(diagram D, htesselation T, myGroup G,HyperTesser h){
        for(TreeNode<hLens> L:lensTree){
            DrawLens(D,T,G,L,h);
        }
    }

    void DrawLens(diagram D, htesselation T, myGroup G, hLens L,hLens PL,HyperTesser h){
        DrawLensTop(L,h.lensColor,h.modelColor,h);
        drawPoint(new Complex(),new Label(),L,h.diagramColor,h.textColor,h,true);
        DrawTesselationtrie(T,G,L,h);
        DrawPath(T,L,h);
        DrawDiagram(D,L,h);
        DrawLensBot(L,PL,h.lensColor,h);
    }

    void DrawLens(diagram D, htesselation T, myGroup G, @NotNull TreeNode<hLens> TL, HyperTesser h){
        hLens L=TL.getValue();
        DrawLensTop(L,h.lensColor,h.modelColor,h);
        drawPoint(new Complex(),new Label(),L,h.diagramColor,h.textColor,h,true);
        DrawTesselationtrie(T,G,L,h);
        DrawPath(T,L,h);
        DrawDiagram(D,L,h);
        DrawLensBot(TL,h.lensColor,h);
    }

    void DrawLensTop(@NotNull hLens L, int[] lensColor, int[] modelColor, HyperTesser h){
        if (L.isNullLens()){DrawBase(h);}else{
            L.Larc.display(R,lensColor,modelColor,3,h);
        }
    }

    void DrawLensBot(@NotNull hLens L, hLens TL, int[] lensColor, HyperTesser h){
        if (!L.isNullLens()) {
            double phi=TL.Motion.applyinv(L.phi);
            double psi=TL.Motion.applyinv(L.psi);
            drawLine(phi,psi,L,lensColor,1,h,true,true);
            drawLine(phi,psi,TL,lensColor,1,h,true,true);
        }
    }

    void DrawLensBot(@NotNull TreeNode<hLens> TL, int[] lensColor, HyperTesser h){
        if (TL.getParent() != null) {
            DrawLensBot(TL.getValue(),TL.getParent().getValue(),lensColor,h);
        }
    }

    TreeNode<hLens> findLens(@NotNull Complex z){
        TreeNode<hLens> res=lensTree.root;
        double min=z.abs();
        for(TreeNode<hLens> L:lensTree){
            if(z.sub(L.getValue().shift).abs()<1){
                res=L;
                min=1;
            }else{
                if(z.sub(L.getValue().shift).abs()<min){
                    res=L;
                    min=z.sub(L.getValue().shift).abs();
                }
            }
        }
        return res;
    }

    void DrawLensScheme(int[] LensSchemeColor,HyperTesser h){
        for(TreeNode<hLens> L:lensTree){
            if(L.getParent()!=null) {
                hMotion m = L.getParent().getValue().Motion;
                double phip = L.getValue().phi;
                double psip = L.getValue().psi;
                drawLine(m.applyinv(phip), m.applyinv(psip), new hLens(disp), LensSchemeColor, 2, h, false, true);
            }
        }
    }
    //Draw Diagram

    void DrawDiagram(@NotNull diagram D, hLens L, HyperTesser h){
        for(gLine l:D.gLines){drawLine(l.phi(),l.psi(),L,h.diagramColor,2,h,false,false);}
        for(gSemiLine s:D.gSemiLines){drawSemiLine(s.phi(),s.z(),L,h.diagramColor,2,h);}
        for(gArc a:D.gArcs){drawArc(a.z1(),a.z2(),L,h.diagramColor,2,h);}
        for(dPoint g:D.dPoints){drawPoint(g.z(),g.label(),L,h.diagramColor,h.textColor,h,true);}
    }

    //Draw Tesselation

    void DrawTesselationtrie(@NotNull htesselation T, myGroup G, @NotNull hLens L, HyperTesser h){
        hMotion tm;
        Complex tt=disp.toP(dismotion.applyinv(L.Motion.applyinv(new Complex())));
        htesselation.trgroupel te=T.getNearestGroupel(T.disp.fromP(tt),(int)(2* ComplexUtils.distFromZero(tt)/T.side));
        //h.Message+=te.address();
        for (String word : T.trie){
            hMotion wm=T.trie.getbody(word);
            tm=wm.combine(te.Motion());
            Complex zz=disp.fromP(tm.getz());
            //Complex za=disp.fromP(tm.apply(T.MA.getz()));
            //Complex zb=disp.fromP(tm.apply(T.MB.getz()));
            Complex za=disp.fromP(T.MA.combine(tm).getz());
            Complex zb=disp.fromP(T.MB.combine(tm).getz());
            drawArc(zz,za,L,h.tessFillColor,1,h);
            drawArc(zz,zb,L,h.tessFillColor,1,h);
            Label label = G.getelement(te.address() + word).getlabel(labelstyle);
            drawPoint(tm.getz(),label,L,h.tessFillColor,h.textColor,h,false);
        }
    }

    void calcTessPath(@NotNull htesselation T, HyperTesser h){
        TessPath=new ArrayList<>();
        htesselation.trgroupel te=T.getNearestGroupel(newz,(int)(2* ComplexUtils.distFromZero(newz)/T.side));
        String word=te.address();
        TrieNode<String> inode;
        int i;
        int j=0;
        while (j<word.length()) {
            i=j;
            inode = T.bads.root;
            while (i < word.length()) {
                if (inode.isEndOfWord) {
                    word = word.substring(0,j)+inode.body + word.substring(i);
                    i = j;
                    inode = T.bads.root;
                } else if (inode.children.containsKey(word.charAt(i))) {
                    inode = inode.children.get(word.charAt(i));
                    i++;
                } else {
                    i = word.length();
                }
            }
            j++;
        }
        hMotion m=disp.id();
        hMotion next;
        for(i=0;i<word.length();i++){
            next=switch (word.charAt(i)){
                case 'a'->T.MA;
                case 'b'->T.MB;
                case 'c'->T.MAb;
                case 'd'->T.MBb;
                default -> disp.id();
            };
            //drawArc(m.b,m.apply(next.b),lensTree.root.getValue(),h.BLUE,2,h);
            TessPath.add(new gArc(m.getz(), m.apply(next.getz())));
            //for(TreeNode<Lens> L:lensTree) {
            //    drawArc(m.b, m.apply(next.b), L.getValue(), h.BLUE, 2, h);
            //    DrawLensBot(L,h.lensColor,h);
            //}
            m.preupdate(next);
        }
        //h.Message+="**"+te.address()+"**"+newz.arg();//+ "***"+ pdiff+" "+qdiff;
    }

    void GroupPrintout(@NotNull htesselation T, myGroup G){
        for (String word : T.trie){
            println(word + " " + G.getelement(word).toString());
        }
    }
    void TriePrintout(@NotNull htesselation T, myGroup G){
        for (String word : T.trie){
            hMotion g=T.trie.getbody(word);
            println(word + " " + g.toString()+" " + G.getelement(word).toString());
        }
    }

    void DrawPath(@NotNull htesselation T, hLens L, HyperTesser h){
        for (pArc pe:T.Path){
            gArc temparc=T.getgArc(pe);
            drawArc(temparc.z1(),temparc.z2(),L,h.PathColor,2,h);
        }
        for (gArc ga:TessPath){
            drawArc(ga.z1(),ga.z2(),L,h.PathColor,2,h);
        }
    }

    //Draw elements
    void drawPoint(Complex z,Label label,hLens L,int[] spotColor,int[] textColor,HyperTesser h,boolean spot){
        Complex displayz=switch (type){
            case BELTRAMKLEIN -> disp.toBK(L.Motion.apply(dismotion.apply(z)));
            case POINCARE -> disp.toP(L.Motion.apply((dismotion.apply(z))));
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
            if (spot){
                Arc.displaySpot(zz,r,R,spotColor,spotColor,h);
            }
            h.fill(textColor);
            label.display(zz.re*R,zz.im*R,(r/h.SpotR)*h.labelTSize,h);
        }
    }

    void DrawIdealPoint(double phi, @NotNull hLens L, int[] spotColor, HyperTesser h){
        double displayphi=L.Motion.apply((dismotion.apply(phi)));
        if (L.inView(displayphi)){
            Complex zz=ComplexUtils.eip(displayphi).add(L.shift);
            Arc.displaySpot(zz,h.SpotR,R,spotColor,spotColor,h);
        }
    }

    void drawLine(double phi, double psi, hLens L,int[] Color,int weight,HyperTesser h,boolean filled,boolean absolute){
        double phip= absolute ? L.Motion.apply(phi) : L.Motion.apply(dismotion.apply(phi));
        double psip= absolute ? L.Motion.apply(psi) :L.Motion.apply(dismotion.apply(psi));
        switch(type){
            case POINCARE:
                if(filled){
                    new Arc(L.shift.re,L.shift.im, 1,phip,psip,ArcType.HYPER).display(R,Color,Color,weight,h);
                }
                else{
                    new Arc(L.shift.re,L.shift.im, 1,phip,psip,ArcType.HYPER).display(R,Color,weight,h);
                }
                break;
            case BELTRAMKLEIN:
                gArc temp=L.trimLine(ComplexUtils.eip(phip), ComplexUtils.eip(psip));
                h.stroke(Color);
                h.strokeWeight(weight);
                h.line((float)(temp.z1().add(L.shift).re*R),(float)(temp.z1().add(L.shift).im*R),(float)(temp.z2().add(L.shift).re*R),(float)(temp.z2().add(L.shift).im*R));
                break;
        }
    }

    void drawSemiLine(double phi, Complex z, @NotNull hLens L, int[] Color, int weight, HyperTesser h){
        double phip=L.Motion.apply(dismotion.apply(phi));
        Complex displayz;
        switch(type){
            case POINCARE:
                displayz=disp.toP(L.Motion.apply(dismotion.apply(z)));
                new Arc(L.shift,phip,displayz.add(L.shift)).display(R,Color,weight,h);
                break;
            case BELTRAMKLEIN:
                displayz=disp.toBK(L.Motion.apply(dismotion.apply(z)));
                gArc temp=L.trimLine(ComplexUtils.eip(phip),displayz);
                h.stroke(Color);
                h.strokeWeight(weight);
                h.line((float)(temp.z1().add(L.shift).re*R),(float)(temp.z1().add(L.shift).im*R),(float)(temp.z2().add(L.shift).re*R),(float)(temp.z2().add(L.shift).im*R));
                break;
        }
    }

    void drawArc(Complex z1, Complex z2, @NotNull hLens L, int[] Color, int weight, HyperTesser h){
        Complex displayz1;
        Complex displayz2;
        switch(type){
            case POINCARE:
                displayz1=disp.toP(L.Motion.apply(dismotion.apply(z1)));
                displayz2=disp.toP(L.Motion.apply(dismotion.apply(z2)));
                if (Arc.angdiff(displayz1.arg(),displayz2.arg())<h.toleranceToZero||displayz1.abs()<h.toleranceToZero||displayz2.abs()<h.toleranceToZero){
                    h.stroke(Color);
                    h.strokeWeight(weight);
                    h.line((float)(displayz1.add(L.shift).re*R),(float)(displayz1.add(L.shift).im*R),(float)(displayz2.add(L.shift).re*R),(float)(displayz2.add(L.shift).im*R));
                }else {
                    Arc temparc = new Arc(displayz1, displayz2);
                    temparc.move(L.shift);
                    temparc.display(R, Color,weight, h);
                }
                break;
            case BELTRAMKLEIN:
                displayz1=disp.toBK(L.Motion.apply(dismotion.apply(z1)));
                displayz2=disp.toBK(L.Motion.apply(dismotion.apply(z2)));
                gArc temp=L.trimLine(displayz1,displayz2);
                h.stroke(Color);
                h.strokeWeight(weight);
                h.line((float)(temp.z1().add(L.shift).re*R),(float)(temp.z1().add(L.shift).im*R),(float)(temp.z2().add(L.shift).re*R),(float)(temp.z2().add(L.shift).im*R));
                break;
        }
    }

    //functions
    //Add Lens
    public void DrawAddLens0(HyperTesser h){
        savedphi=newphi;
        savedLens=selectedLens;
        DrawIdealPoint(savedphi,selectedLens.getValue(),h.NewBorderPointColor,h);
    }

    public void DrawAddLens1(diagram D, htesselation T, myGroup G,HyperTesser h){
        //pinput.setAbs(1);
        hLens l1=savedLens.getValue();
        hLens l2=selectedLens.getValue();
        double newangle=pinput.div(R).sub(l2.shift).arg();
        double diff=Arc.angdiff(l1.Motion.apply(savedphi),newangle);
        if (diff>0.01f && diff<HALF_PI && savedLens==selectedLens){
            savedphi2=l2.Motion.applyinv(newangle);
            OverFlag=true;
            DrawLens(D,T,G,new hLens(savedphi,savedphi2,l1,disp),l1,h);
            DrawIdealPoint(savedphi2, l1, h.NewBorderPointColor, h);
        }else{
            OverFlag=false;
        }
        DrawIdealPoint(savedphi, l1, h.NewBorderPointColor, h);
    }

    //Select Lens
    public void DrawSelectLens(HyperTesser h){
        selectedLens=findLens(pinput.div(R));
        DrawLensTop(selectedLens.getValue(),h.lensColor,h.modelColor,h);
        DrawLensBot(selectedLens,h.lensColor,h);
    }

    //Add Point
    public void DrawAddPoint(HyperTesser h){
        if (pinput.div(R).sub(selectedLens.getValue().shift).abs()<1){
            drawPoint(newz, new Label(), selectedLens.getValue(), h.NewArcColor, h.NewArcColor, h, true);
            savedz=newz;
            OverFlag=true;
        }else{
            OverFlag=false;
        }
    }

    //Add Line
    public void DrawAddLine0(HyperTesser h){
        if (pinput.div(R).sub(selectedLens.getValue().shift).abs()<1){
            savedz=newz;
            OverFlag=true;
            drawPoint(newz, new Label(), selectedLens.getValue(), h.NewArcColor, h.NewArcColor, h, true);
        }else{
            OverFlag=false;
            savedphi=newphi;
            pinput.setAbs(1);
            DrawIdealPoint(savedphi,selectedLens.getValue(),h.NewBorderPointColor,h);
            //Arc.displaySpot(pinput,h.SpotR,R,h.NewBorderPointColor,h.NewBorderPointColor,h);
        }
        savedLens=selectedLens;
    }

    public void DrawAddLine1(HyperTesser h){
        if (pinput.div(R).sub(selectedLens.getValue().shift).abs()<1){
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
                TreeNode<hLens> temp=savedLens;
                savedLens=selectedLens;
                selectedLens=temp;
                newArcMode=newArcType.SEMILINE;
            }else{
                savedphi2=newphi;
                newArcMode=newArcType.LINE;
            }
        }
        switch (newArcMode){
            case ARC:
                drawPoint(savedz, new Label(), savedLens.getValue(), h.NewArcColor, h.NewArcColor, h, true);
                drawPoint(savedz2, new Label(), selectedLens.getValue(), h.NewArcColor, h.NewArcColor, h, true);
                for(TreeNode<hLens> L:lensTree) {
                    drawArc(savedz, savedz2, L.getValue(), h.NewArcColor, 3, h);
                    DrawLensBot(L,h.lensColor,h);
                }
                break;
            case SEMILINE:
                DrawIdealPoint(savedphi, savedLens.getValue(), h.NewSemiLineColor, h);
                drawPoint(savedz, new Label(), selectedLens.getValue(), h.NewSemiLineColor, h.NewSemiLineColor, h, true);
                for(TreeNode<hLens> L:lensTree) {
                    drawSemiLine(savedphi, savedz, L.getValue(), h.NewSemiLineColor, 3, h);
                    DrawLensBot(L,h.lensColor,h);
                }
                break;
            case LINE:
                for(TreeNode<hLens> L:lensTree) {
                    drawLine(savedphi, savedphi2, L.getValue(), h.NewLineColor, 3, h, false,false);
                    DrawLensBot(L,h.lensColor,h);
                }
                DrawIdealPoint(savedphi, savedLens.getValue(), h.NewLineColor, h);
                DrawIdealPoint(savedphi2, selectedLens.getValue(), h.NewLineColor, h);
                break;
        }
    }

    //Add Line to Path
    public void DrawAddPathLine0(htesselation T,HyperTesser h){
        //HyperTesser h=HyperTesser.getInstance();
        if (pinput.div(R).sub(selectedLens.getValue().shift).abs()<1){
            T.newpathel=T.getNearestPathel(newz,(int)(2* ComplexUtils.distFromZero(newz)/T.side));
            savedLens=selectedLens;
            savedz=T.getPoint(T.newpathel);
            drawPoint(savedz,new Label(),selectedLens.getValue(),h.AddPathColor,h.textColor,h,true);
            OverFlag=true;
        }else{
            OverFlag=false;
        }
    }

    public void DrawAddPathLine1(htesselation T,HyperTesser h){
        //HyperTesser h=HyperTesser.getInstance();
        if (pinput.div(R).sub(selectedLens.getValue().shift).abs()<1){
            //new Complex();
            if (OverFlag){
                pathel pathel2=T.getNearestPathel(newz,(int)(2* ComplexUtils.distFromZero(newz)/T.side));
                T.newparc=new pArc(T.newpathel,pathel2);
                savedz2=T.getPoint(pathel2);
                drawPoint(savedz, new Label(), savedLens.getValue(), h.NewArcColor, h.NewArcColor, h, true);
                drawPoint(savedz2, new Label(), selectedLens.getValue(), h.NewArcColor, h.NewArcColor, h, true);
                for(TreeNode<hLens> L:lensTree) {
                    drawArc(savedz, savedz2, L.getValue(), h.NewArcColor, 3, h);
                    DrawLensBot(L,h.lensColor,h);
                }
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
        dismotion=disp.id();
    }

    void Pan(int mx,int my,int pmx,int pmy){
        xpt+=(mx-pmx);
        ypt+=(my-pmy);
    }

    void Drag(int mx,int my,int pmx,int pmy){
        pinput=new Complex(mx-xpt,my-ypt);
        ppinput=new Complex(pmx-xpt,pmy-ypt);
        Complex z=pinput.div(R);
        selectedLens=findLens(z);
        hLens L=selectedLens.getValue();
        if(z.sub(L.shift).abs()<0.9){
            newz =switch (type){
                case POINCARE -> dismotion.applyinv(L.Motion.applyinv(z.sub(L.shift)));
                case BELTRAMKLEIN -> dismotion.applyinv(L.Motion.applyinv(ComplexUtils.BKtoP(z.sub(L.shift))));
            };
            Complex zz=ppinput.div(R);
            oldz =switch (type){
                case POINCARE -> dismotion.applyinv(L.Motion.applyinv(zz.sub(L.shift)));
                case BELTRAMKLEIN -> dismotion.applyinv(L.Motion.applyinv(ComplexUtils.BKtoP(zz.sub(L.shift))));
            };
            dismotion.preupdate(disp.getmotion(oldz,newz));
        }
    }
}

enum modelType{
    POINCARE,
    BELTRAMKLEIN
}

enum newArcType{
    ARC,
    SEMILINE,
    LINE
}