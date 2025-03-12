import complexnumbers.Complex;

enum LabelStyle{
    FULL,
    IDONLY
}

class Label{
    String N,S,E,W,NE,NW,SE,SW,O;
    Label(){
        O="";
        N="";
        S="";
        E="";
        W="";
        NE="";
        NW="";
        SE="";
        SW="";
    }
    Label(String ilabel){
        O=ilabel;
        N="";
        S="";
        E="";
        W="";
        NE="";
        NW="";
        SE="";
        SW="";
    }
    Label(String ilabeln,String ilabels){
        N=ilabeln;
        O="";
        S=ilabels;
        E="";
        W="";
        NE="";
        NW="";
        SE="";
        SW="";
    }
    void display(double x,double y, double tsize,HyperTesser h) {
        //HyperTesser h=HyperTesser.getInstance();
        if (tsize > 5) {
            h.textSize((float) tsize);
            h.text(O, (float) (x), (float) (y));
            h.text(N, (float) (x), (float) (y- tsize));
            h.text(NW, (float) ((x) - 0.7f * tsize), (float) (y - 0.7f * tsize));
            h.text(NE, (float) (x + 0.7f * tsize), (float) (y - 0.7f * tsize));
            h.text(E, (float) (x + tsize), (float) (y));
            h.text(W, (float) (x - tsize), (float) (y));
            h.text(SE, (float) (x + 0.7f * tsize), (float) (y + 0.7f * tsize));
            h.text(SW, (float) (x - 0.7f * tsize), (float) (y + 0.7f * tsize));
            h.text(S, (float) (x), (float) (y + tsize));
        }
    }
}
