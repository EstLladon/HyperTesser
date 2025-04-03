import complexnumbers.Complex;
import org.jetbrains.annotations.NotNull;

import static processing.core.PConstants.TWO_PI;

abstract class groupel<T>{
    T a;
    groupel(){this.a=null;}
    groupel(T a){this.a=a;}
    abstract Label getlabel(LabelStyle s);
    public abstract String toString();
}

abstract class groupeldispenser<T>{
    abstract groupel<T> mul(groupel<T> a,groupel<T> b);
    abstract groupel<T> inv(groupel<T> a);
    abstract groupel<T> id();
    abstract groupel<T> getgroupel(T a);
}

abstract class myGroup<T> {
    groupeldispenser<T> disp;
    groupel<T> gena;
    groupel<T> genb;
    groupel<T> genc;
    groupel<T> gend;

    myGroup(groupel<T> gena,groupel<T> genb,groupeldispenser<T> disp) {
        this.gena = gena;
        this.genc = disp.inv(gena);
        this.genb = genb;
        this.gend = disp.inv(genb);
        this.disp=disp;
    }

    groupel<T> mul(groupel<T> a,groupel<T> b){return disp.mul(a,b);}
    groupel<T> id(){return disp.id();};
    groupel<T> inv(groupel<T> a){return disp.inv(a);}
    groupel<T> getelement(String s){
        groupel<T> res=disp.id();
        for(int i=0;i<s.length();i++){
            res = switch (s.charAt(i)) {
                case 'a' -> disp.mul(res, gena);
                case 'b' -> disp.mul(res, genb);
                case 'c' -> disp.mul(res, genc);
                case 'd' -> disp.mul(res, gend);
                default -> res;
            };
        }
        return res;
    }
}

abstract class myMotion<T> extends groupel<T>{
    T a;
    myMotion(){}
    myMotion(T a){super(a);}
    abstract myMotion<T> inv();
    abstract Complex apply(Complex z);
    Complex applyinv(Complex z){return inv().apply(z);}
    abstract double apply(double phi);
    double applyinv(double phi){
        return inv().apply(phi);
    }
    abstract myMotion<T>combine(myMotion<T> b);
    //void update (myMotion<T> ia){a=mul(a,ia.a);}
    void update (myMotion<T> ia){a=combine(ia).a;}
    //void preupdate (myMotion<T> ia){a=mul(ia.a,a);}
    void preupdate (myMotion<T> ia){a=ia.combine(this).a;}
    abstract Complex getz();

    public myMotion<T> power(int n, @NotNull motionDispenser<T> disp) {
        myMotion<T> res = disp.id();
        for (int i = 0; i < n; i++) {
            res.update(this);
        }
        return res;
    }
}

abstract class motionDispenser<T> extends groupeldispenser<T>{
    abstract myMotion<T> id();
    abstract myMotion<T> getgroupel(T a);
    myMotion<T> mul(T a, T b){
        myMotion<T> res=getgroupel(a);
        res.update(getgroupel(b));
        return res;
    }
    abstract myMotion<T> getmotion(Complex z);
    abstract myMotion<T> getmotion(double phi);
    abstract myMotion<T> getmotion(double phi,Complex z);
    abstract myMotion<T> getmotion(Complex pz,Complex cz);
    abstract addMot<T> getaddressedGroupel(T a,String address);
    abstract myMotionGroup<T> getMotionGroup(int p, Complex cP, int q, Complex cQ);
    abstract Complex toP(Complex z);
    abstract Complex toBK(Complex z);
    abstract Complex fromP(Complex z);
    abstract Complex fromBK(Complex z);
    abstract double distFromZero(Complex z);
    myMotion<T> square(myMotion<T> m){return mul(m.a,m.a);}
}

record addMot<T>(myMotion<T> Motion,String address){}

abstract class myMotionGroup<T> extends myGroup<T>{
    motionDispenser<T> disp;
    myMotion<T> gena;
    myMotion<T> genb;
    myMotion<T> genc;
    myMotion<T> gend;

    myMotionGroup(myMotion<T> gena, myMotion<T> genb,motionDispenser<T> disp) {
        super(gena, genb,disp);
        this.gena = gena;
        this.genc = inv(gena);
        this.genb = genb;
        this.gend = inv(genb);
        this.disp=disp;
    }

    myMotion<T> mul(myMotion<T> a,myMotion<T> b){
        return disp.mul(a.a,b.a);
    }
    myMotion<T> id(){
        return disp.id();
    }
    myMotion<T> inv(myMotion<T> a){return a.inv();}

    myMotion<T> getelement(String s) {
        return disp.getgroupel(super.getelement(s).a);
    }
}





