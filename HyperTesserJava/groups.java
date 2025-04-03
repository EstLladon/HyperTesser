import static java.util.Arrays.deepToString;

class intMatrix2by2 extends groupel<int[][]>{

    final int field;
    intMatrix2by2(int[][] a,int field){
        super(a);
        this.field=field;
        this.a=a.clone();
    }

    int[][] mul(int[][] a, int[][] b) {
        int[][] res=new int[][]{{(a[0][0]*b[0][0]+a[0][1]*b[1][0])%field,(a[0][0]*b[0][1]+a[0][1]*b[1][1])%field},{(a[1][0]*b[0][0]+a[1][1]*b[1][0])%field,(a[1][0]*b[0][1]+a[1][1]*b[1][1])%field}};
        return ComplexUtils.normalize(res,2,field);
    }

    Label getlabel(LabelStyle s) {
        Label l=new Label();
        switch(s){
            case FULL:
                l.NE=""+a[0][1];
                l.NW=""+a[0][0];
                l.SE=""+a[1][1];
                l.SW=""+a[1][0];
                break;
            case IDONLY:
                if (isIdentity()){
                    l.O="I";
                }else{
                    l.O="";
                }
                l.N="";
                l.S="";
                l.E="";
                l.W="";
                l.NE="";
                l.NW="";
                l.SE="";
                l.SW="";
                break;
        }
        return l;
    }

    public String toString() {
        return deepToString(a);
    }

    private boolean isIdentity(){
        return (a[0][0]==1&&a[0][1]==0&&a[1][0]==0&&a[1][1]==1);
    }
}

class pslMatrixDispenser extends groupeldispenser<int[][]>{
    int field;

    pslMatrixDispenser(int field){
        this.field=field;
    }

    intMatrix2by2 mul(groupel<int[][]> a, groupel<int[][]> b) {
        intMatrix2by2 res=new intMatrix2by2(a.a,field);
        res=new intMatrix2by2(res.mul(res.a,b.a),field);
        return res;
    }

    intMatrix2by2 inv(groupel<int[][]> a) {
        int[][] res=new int[2][2];
        res[0][0]=a.a[1][1];
        res[0][1]=field-a.a[0][1];
        res[1][0]=field-a.a[1][0];
        res[1][1]=a.a[0][0];
        return new intMatrix2by2(ComplexUtils.normalize(res,2,field),field);
    }

    intMatrix2by2 id() {
        return new intMatrix2by2(new int[][]{{1,0},{0,1}},field);
    }

    intMatrix2by2 getgroupel(int[][] a) {
        return new intMatrix2by2(a,field);
    }
}


class newpsl2PMatrixGroup extends myGroup<int[][]> {
    final int field;
    pslMatrixDispenser disp;
    intMatrix2by2 gena;
    intMatrix2by2 genb;
    intMatrix2by2 genc;
    intMatrix2by2 gend;

    newpsl2PMatrixGroup(intMatrix2by2 gena,intMatrix2by2 genb,int field,pslMatrixDispenser disp){
        super (gena,genb,disp);
        this.field=field;
        this.gena = gena;
        this.genc = disp.inv(gena);
        this.genb = genb;
        this.gend = disp.inv(genb);
        this.disp=disp;
    }

    //intMatrix2by2 mul(groupel<int[][]> a, groupel<int[][]> b) {
    //    return disp.mul(a,b);
    //}

    intMatrix2by2 id() {
        return new intMatrix2by2(new int[][]{{1,0},{0,1}},field);
    }

    groupel<int[][]> inv(groupel<int[][]> a) {
        return null;
    }

    intMatrix2by2 inv(intMatrix2by2 a) {
        return disp.inv(a);
    }

    intMatrix2by2 getelement(String s){
        return new intMatrix2by2(super.getelement(s).a,field);
    }
}