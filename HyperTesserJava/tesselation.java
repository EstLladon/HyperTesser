import complexnumbers.Complex;

import java.util.ArrayList;

import static processing.core.PConstants.*;

class tesselation {
    int p, q, r;
    double bp, bq, br, side;
    double shA, shRp, shRq, shRr;
    Trie<HyperUtils.gMotion> trie;
    Trie<HyperUtils.hMotion> htrie;
    String curword = "";
    int tessdepth;
    HyperUtils.gMotion MA, MB, MAb, MBb, bpshift, bqshift;
    HyperUtils.hMotion hMA, hMB, hMAb, hMBb, hbpshift, hbqshift;
    Complex aP, bP, cP, cQ, cR, cRb;
    ArrayList<pArc> Path;
    pathel newpathel;
    int upForDeletionIndex = 0;
    pArc newparc;

    tesselation(int ip, int iq, int ir, int idepth) {
        p = ip;
        q = iq;
        r = ir;
        tessdepth = idepth;
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
        cP = new Complex(bp);
        MA = new HyperUtils.gMotion(TWO_PI / p, cP);
        hMA = new HyperUtils.hMotion(TWO_PI / p, cP);
        aP = MA.b;
        MAb = MA.inv();
        hMAb = hMA.inv();
        cQ = new Complex(-bq);
        MB = new HyperUtils.gMotion(TWO_PI / q, cQ);
        hMB = new HyperUtils.hMotion(TWO_PI / q, cQ);
        bP = MB.b;
        MBb = MB.inv();
        hMBb = hMB.inv();
        bpshift = new HyperUtils.gMotion(cP.neg());
        bqshift = new HyperUtils.gMotion(cQ.neg());
        hbpshift = new HyperUtils.hMotion(cP.neg());
        hbqshift = new HyperUtils.hMotion(cQ.neg());
        cR = new Complex();
        cR.setPolar(br, Math.acos(cosC));
        cRb = cR.conj();
        Path = new ArrayList<>();
        trie=new Trie<>(new HyperUtils.gMotion());
        htrie=new Trie<>(new HyperUtils.hMotion());
    }

    //tesselation base

    void populateeltrie(int i) {
        String ta;
        trgroupel tg;
        while (curword.length() <= i) {
            tg = lazytrueaddress(curword);
            ta = tg.address;
            double phi = bpshift.apply(tg.Motion.b).arg();
            if ((curword.equals(ta) && ((phi <= -PI + PI / p - HyperTesser.angleToleranceToZero) || (phi >= PI - PI / p - HyperTesser.angleToleranceToZero))) && ((ta.isEmpty()) || (ta.charAt(0) == 'b') || (ta.charAt(0) == 'd'))) {
                trie.insert(ta,tg.Motion);
            }
            curword = getNextWord(curword);
        }
    }

    void hpopulateeltrie(int i) {
        String ta;
        trhgroupel tg;
        while (curword.length() <= i) {
            tg = hlazytrueaddress(curword);
            ta = tg.address;
            double phi = bpshift.apply(tg.Motion.getz()).arg();
            if ((curword.equals(ta) && ((phi <= -PI + PI / p - HyperTesser.angleToleranceToZero) || (phi >= PI - PI / p - HyperTesser.angleToleranceToZero))) && ((ta.isEmpty()) || (ta.charAt(0) == 'b') || (ta.charAt(0) == 'd'))) {
                htrie.insert(ta,tg.Motion);
            }
            curword = getNextWord(curword);
        }
    }

    //triange group functions
    trgroupel lazytrueaddress(String v) {
        StringBuilder res = new StringBuilder();
        Complex goal = calculatemotion(v).b;
        HyperUtils.gMotion resm = new HyperUtils.gMotion();
        Complex ga, gb, gab, gbb;
        int j;
        Complex best = new Complex();
        int i = v.length();
        while ((goal.abs() > shA / 10) && (i > 0)) {
            ga = MA.applyinv(goal);
            gb = MB.applyinv(goal);
            gab = MAb.applyinv(goal);
            gbb = MBb.applyinv(goal);
            double tol = (1 - goal.abs()) / 1000000;
            j = min4(goal.abs(), ga.abs(), gb.abs(), gab.abs(), gbb.abs(), tol);
            switch (j) {
                case 0:
                    i = 0;
                case 1:
                    best = ga;
                    res.append('a');
                    resm.preupdate(MA);
                    break;
                case 2:
                    best = gb;
                    res.append('b');
                    resm.preupdate(MB);
                    break;
                case 3:
                    best = gab;
                    res.append('c');
                    resm.preupdate(MAb);
                    break;
                case 4:
                    best = gbb;
                    res.append('d');
                    resm.preupdate(MBb);
                    break;
            }
            goal = best;
            i--;
        }
        return new trgroupel(res.toString(), resm);
    }

    trhgroupel hlazytrueaddress(String v) {
        StringBuilder res = new StringBuilder();
        Complex goal = hcalculatemotion(v).getz();
        HyperUtils.hMotion resm = new HyperUtils.hMotion();
        Complex ga, gb, gab, gbb;
        int j;
        Complex best = new Complex();
        int i = v.length();
        while ((goal.abs() > shA / 10) && (i > 0)) {
            ga = hMA.applyinv(goal);
            gb = hMB.applyinv(goal);
            gab = hMAb.applyinv(goal);
            gbb = hMBb.applyinv(goal);
            double tol = (1 - goal.abs()) / 1000000;
            j = min4(goal.abs(), ga.abs(), gb.abs(), gab.abs(), gbb.abs(), tol);
            switch (j) {
                case 0:
                    i = 0;
                case 1:
                    best = ga;
                    res.append('a');
                    resm.preupdate(hMA);
                    break;
                case 2:
                    best = gb;
                    res.append('b');
                    resm.preupdate(hMB);
                    break;
                case 3:
                    best = gab;
                    res.append('c');
                    resm.preupdate(hMAb);
                    break;
                case 4:
                    best = gbb;
                    res.append('d');
                    resm.preupdate(hMBb);
                    break;
            }
            goal = best;
            i--;
        }
        return new trhgroupel(res.toString(), resm);
    }

    trgroupel getNearestGroupel(Complex goal, int limit) {
        Complex ga, gb, gab, gbb;
        int j;
        int i = 0;
        HyperUtils.gMotion resM = new HyperUtils.gMotion();
        StringBuilder resadd = new StringBuilder();
        Complex best = new Complex();
        while ((goal.abs() > aP.abs() * 0.8f) && i < limit) {
            ga = MA.applyinv(goal);
            gb = MB.applyinv(goal);
            gab = MAb.applyinv(goal);
            gbb = MBb.applyinv(goal);
            j = min4(goal.abs(), ga.abs(), gb.abs(), gab.abs(), gbb.abs(), HyperTesser.groupelTolerance);
            //j = min4(goal.abs(), ga.abs(), gb.abs(), gab.abs(), gbb.abs(), 0);
            switch (j) {
                case 0:
                    i = limit;
                    break;
                case 1:
                    best = ga;
                    resadd.append('a');
                    resM.preupdate(MA);
                    break;
                case 2:
                    best = gb;
                    resadd.append('b');
                    resM.preupdate(MB);
                    break;
                case 3:
                    best = gab;
                    resadd.append('c');
                    resM.preupdate(MAb);
                    break;
                case 4:
                    best = gbb;
                    resadd.append('d');
                    resM.preupdate(MBb);
                    break;
            }
            goal = best;
            i++;
        }

        return new trgroupel(resadd.toString(),resM);
    }

    trhgroupel getNearesthGroupel(Complex goal, int limit) {
        Complex ga, gb, gab, gbb;
        int j;
        int i = 0;
        HyperUtils.hMotion resM = new HyperUtils.hMotion();
        StringBuilder resadd = new StringBuilder();
        Complex best = new Complex();
        while ((goal.abs() > aP.abs() * 0.8f) && i < limit) {
            ga = hMA.applyinv(goal);
            gb = hMB.applyinv(goal);
            gab = hMAb.applyinv(goal);
            gbb = hMBb.applyinv(goal);
            j = min4(goal.abs(), ga.abs(), gb.abs(), gab.abs(), gbb.abs(), HyperTesser.groupelTolerance);
            switch (j) {
                case 0:
                    i = limit;
                    break;
                case 1:
                    best = ga;
                    resadd.append('a');
                    resM.preupdate(hMA);
                    break;
                case 2:
                    best = gb;
                    resadd.append('b');
                    resM.preupdate(hMB);
                    break;
                case 3:
                    best = gab;
                    resadd.append('c');
                    resM.preupdate(hMAb);
                    break;
                case 4:
                    best = gbb;
                    resadd.append('d');
                    resM.preupdate(hMBb);
                    break;
            }
            goal = best;
            i++;
        }

        return new trhgroupel(resadd.toString(),resM);
    }

    HyperUtils.gMotion calculatemotion(String s) {
        HyperUtils.gMotion resM = new HyperUtils.gMotion();
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

    HyperUtils.hMotion hcalculatemotion(String s) {
        HyperUtils.hMotion resM = new HyperUtils.hMotion();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case 'a':
                    resM.preupdate(hMA);
                    break;
                case 'b':
                    resM.preupdate(hMB);
                    break;
                case 'c':
                    resM.preupdate(hMAb);
                    break;
                case 'd':
                    resM.preupdate(hMBb);
                    break;
            }
        }
        return resM;
    }

    //Path functions
    HyperUtils.gArc getgArc(pArc pe) {
        return new HyperUtils.gArc(getPoint(pe.p1), getPoint(pe.p2));
    }

    Complex getPoint(pathel pe) {
        HyperUtils.gMotion pem = calculatemotion(pe.address);
        return switch (pe.type) {
            case O -> pem.b;
            case P -> pem.apply(cP);
            case Q -> pem.apply(cQ);
            case R -> pem.apply(cR);
            case RB -> pem.apply(cRb);
        };
    }

    pathel getNearestPathel(Complex goal, int limit) {
        trgroupel tr = getNearestGroupel(goal, limit);
        Complex tm = tr.Motion.applyinv(goal);
        int j = min4(tm.abs(), tm.sub(cP).abs(), tm.sub(cQ).abs(), tm.sub(cR).abs(), tm.sub(cRb).abs(), 0.0001f);
        pathelType ptype= pathelType.O;
        switch (j){
            case 1:
                ptype= pathelType.P;
            case 2:
                ptype= pathelType.Q;
            case 3:
                ptype= pathelType.R;
            case 4:
                ptype= pathelType.RB;
        }
        return new pathel(tr.address, ptype);
    }

   static class trgroupel {
        String address;
        HyperUtils.gMotion Motion;

        trgroupel(String iadd, HyperUtils.gMotion imotion) {
            address = iadd;
            Motion = imotion;
        }
    }

    static class trhgroupel {
        String address;
        HyperUtils.hMotion Motion;

        trhgroupel(String iadd, HyperUtils.hMotion imotion) {
            address = iadd;
            Motion = imotion;
        }
    }

    static class pathel {
        String address;
        pathelType type;

        pathel(String iadd, pathelType itype) {
            address = iadd;
            type = itype;
        }
    }

    static class pArc {
        pathel p1, p2;

        pArc(pathel ip1, pathel ip2) {
            p1 = ip1;
            p2 = ip2;
        }
    }

    static int min4(double o, double a, double b, double c, double d, double tolerance) {
        double best;
        int res;
        if (a <= b - tolerance) {
            best = a;
            res = 1;
        } else {
            best = b;
            res = 2;
        }
        if (c < best - tolerance) {
            best = c;
            res = 3;
        }
        if (d < best - tolerance) {
            res = 4;
        }
        if (o < best - tolerance) {
            res = 0;
        }
        return res;
    }

    static String getNextWord(String word) {
        char[] chars = word.toCharArray();
        int i = chars.length - 1;
        while (i >= 0 && chars[i] == 'd') {
            chars[i] = 'a';
            i--;
        }
        if (i >= 1) {
            chars[i]++;
            return new String(chars);
        } else if (i == 0) {
            if ((chars[0] == 'a')) {
                chars[0] = 'b';
            } else {
                chars[0] = 'd';
            }
            return new String(chars);
        }
        return new String(chars) + 'a';
    }
}

enum pathelType{
    O,
    P,
    Q,
    R,
    RB
}