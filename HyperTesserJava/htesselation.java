import complexnumbers.Complex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static processing.core.PApplet.println;
import static processing.core.PConstants.*;

class htesselation {
    int p, q, r;
    double side;
    double shA, shRp, shRq, shRr;
    Trie<hMotion> trie;
    Trie<String> bads=new Trie<>("");
    hMotion MA, MB, MAb, MBb, bpshift, bqshift;
    Complex aP, bP, cP, cQ, cR, cRb;
    ArrayList<pArc> Path;
    pathel newpathel;
    int upForDeletionIndex = 0;
    pArc newparc;
    hMotiondispenser disp;

    htesselation(int ip, int iq, int ir,hMotiondispenser disp) {
        p = ip;
        q = iq;
        r = ir;
        double bp, bq, br;
        double cosp = Math.cos(PI / p);
        double cosq = Math.cos(PI / q);
        double cosr = Math.cos(PI / r);
        double cosr2 = Math.cos(PI / (2 * r));
        double sinp = Math.sin(PI / p);
        double sinq = Math.sin(PI / q);
        double sinr = Math.sin(PI / r);
        double sinr2 = Math.sin(PI / (2 * r));
        double E = cosr + cosp * cosq;
        double sh2A = (E * E - sinp * sinp * sinq * sinq) / (2 * E + sinp * sinp + sinq * sinq);
        shA = Math.sqrt(sh2A);
        shRp = shA / sinp;
        shRq = shA / sinq;
        shRr = shA / sinr2;
        double coshRp = Math.sqrt(sh2A / (sinp * sinp) + 1);
        double coshRq = Math.sqrt(sh2A / (sinq * sinq) + 1);
        double coshRr = Math.sqrt(sh2A / (sinr2 * sinr2) + 1);
        double coshA = Math.sqrt(sh2A + 1);
        bp = shRp / (coshRp + 1);
        bq = shRq / (coshRq + 1);
        br = shRr / (coshRr + 1);
        double ba = shA / (coshA + 1);
        side = Math.log((1 + ba) / (1 - ba));
        double coshc = (cosq + cosp * cosr) / (sinp * sinr);
        double cosC = -cosp * cosr2 + sinp * sinr2 * coshc;
        cP = disp.fromP(new Complex(bp));
        MA = disp.getmotion(TWO_PI / p, cP);
        aP = MA.getz();
        MAb = MA.inv();
        cQ = disp.fromP(new Complex(-bq));
        MB = disp.getmotion(TWO_PI / q, cQ);
        bP = MB.getz();
        MBb = MB.inv();
        bpshift = disp.getmotion(cP.neg());
        bqshift = disp.getmotion(cQ.neg());
        cR = new Complex();
        double rphi=Math.acos(cosC);
        cR.setPolar(br, rphi);
        cR=disp.fromP(cR);
        cRb = cR.conj();
        Path = new ArrayList<>();
        trie=new Trie<>(disp.id());
        trie.root.isEndOfWord=false;
        this.disp=disp;
        println(shA, shRp, shRq, shRr);
        println(disp.toP(cP).re,disp.toP(cP).im,disp.toP(cQ).re,disp.toP(cQ).im,disp.toP(cR).re,disp.toP(cR).im);
        println(MA.getz());
        println(MB.getz());
    }

    //tesselation base

    void populateeltrie(int i) {
        Queue<String> queue = new LinkedList<>();
        queue.add("");
        String curword= queue.poll();
        while (curword.length()<=i){
            StringBuilder revword=new StringBuilder(curword).reverse();
            if (curword.isEmpty()||bads.begsearch(revword.toString()).isEmpty()){
                trgroupel res=lazytrueaddress(curword);
                if (!res.address.isEmpty() && res.address.charAt(0)!=curword.charAt(0)){
                    String newbad=new StringBuilder(res.address).reverse().toString();
                    if(!bads.search(newbad)){bads.insert(revword.toString(),newbad);}
                }
                if (!trie.search(res.address)){
                    trie.insert(res.address,res.Motion);
                    //println(curword+"->"+res.address);
                    queue.add(res.address+"a");
                    queue.add(res.address+"b");
                    queue.add(res.address+"c");
                    queue.add(res.address+"d");
                } //else {println("already have:"+res.address +" for "+curword);}
            }//else{println("empty or bad:"+curword);}
            curword= queue.poll();
        }
    }

    //triangle group functions

    trgroupel lazytrueaddress(String v) {
        return (getNearestGroupel(calculatemotion(v).getz(),v.length()));
    }

    trgroupel getNearestGroupel(Complex goal, int limit) {
        int i = 0;
        hMotion resM = disp.id();
        StringBuilder resadd = new StringBuilder();
        Complex best = new Complex();
        char nextletter=firstLetter(goal);
        while (!(nextletter=='e') && i < limit) {
            resadd.append(nextletter);
            switch (nextletter){
                case 'a':
                    best = MA.applyinv(goal);
                    resM.preupdate(MA);
                    break;
                case 'b':
                    best = MB.applyinv(goal);
                    resM.preupdate(MB);
                    break;
                case 'c':
                    best = MAb.applyinv(goal);
                    resM.preupdate(MAb);
                    break;
                case 'd':
                    best = MBb.applyinv(goal);
                    resM.preupdate(MBb);
                    break;
            }
            goal=best;
            nextletter=firstLetter(goal);
            i++;
        }
        return new trgroupel(resadd.toString(),resM);
    }

    char firstLetter(Complex goal){
        double bparg=bpshift.apply(goal).arg();
        double bqarg=bqshift.apply(goal).arg();
        boolean pflag=bparg<PI-PI/p-0.001 && bparg>-PI+PI/p+0.001;
        boolean qflag=bqarg<=-PI/q+0.001 || bqarg>=PI/q-0.001;
        if (!pflag&& !qflag){return 'e';}
        if (!pflag&& goal.im<=0) {
            return 'd';
        } else if (!pflag&& goal.im>0) {
            return 'b';
        } else if (goal.im<=0){
            return 'a';
        } else if (goal.im>0) {
            return 'c';
        }
        return 'e';
    }

    hMotion calculatemotion(@NotNull String s) {
        hMotion resM = disp.id();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case 'a':
                    resM.preupdate(MA);
                    break;
                case 'b':
                    resM.preupdate(MB);
                    break;
                case 'c':
                    resM.preupdate(MAb);
                    break;
                case 'd':
                    resM.preupdate(MBb);
                    break;
            }
        }
        return resM;
    }

    //Path functions
    gArc getgArc(@NotNull pArc pe) {
        return new gArc(getPoint(pe.p1()), getPoint(pe.p2()));
    }

    Complex getPoint(@NotNull pathel pe) {
        hMotion pem = calculatemotion(pe.address());
        return switch (pe.type()) {
            case O -> pem.getz();
            case P -> pem.apply(cP);
            case Q -> pem.apply(cQ);
            case R -> pem.apply(cR);
            case RB -> pem.apply(cRb);
        };
    }

    pathel getNearestPathel(Complex goal, int limit) {
        trgroupel tr = getNearestGroupel(goal, limit);
        Complex tm = tr.Motion.applyinv(goal);
        //int j = ComplexUtils.min4(tm.abs(), tm.sub(cP).abs(), tm.sub(cQ).abs(), tm.sub(cR).abs(), tm.sub(cRb).abs(), 0.0001f);
        int j = ComplexUtils.findMinIndex(tm.abs(), tm.sub(cP).abs(), tm.sub(cQ).abs(), tm.sub(cR).abs(), tm.sub(cRb).abs());
        pathelType ptype;
        ptype=switch (j){
            case 1 ->pathelType.P;
            case 2 ->pathelType.Q;
            case 3 -> pathelType.R;
            case 4 -> pathelType.RB;
            default -> pathelType.O;
        };
        return new pathel(tr.address, ptype);
    }

    record trgroupel(String address, hMotion Motion){}
}

record pathel(String address,pathelType type){}

record pArc(pathel p1,pathel p2){}

enum pathelType{
    O,
    P,
    Q,
    R,
    RB
}