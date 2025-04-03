import complexnumbers.Complex;
import org.jetbrains.annotations.NotNull;

//import static complexnumbers.Cpx.*;

public class ComplexUtils {
    //different models
    static Complex PtoBK(@NotNull Complex z){
        return z.div((1+z.absq())/2);
    }

    static Complex BKtoP(@NotNull Complex z){
        return z.div(1+Math.sqrt(1-z.absq()));
    }

    static Complex PtoH(@NotNull Complex z){
        return z.div((1-z.absq())/2);
    }

    static Complex HtoP(@NotNull Complex z){
        return z.div(1+Math.sqrt(1+z.absq()));
    }

    static Complex BKtoH(@NotNull Complex z){
        return (z.div(Math.sqrt(1-z.absq())));
    }

    static Complex HtoBK(@NotNull Complex z){
        return z.div(Math.sqrt(1+z.absq()));
    }
    //misc functions
    static @NotNull Complex eip(double phi){
        Complex res = new Complex();
        res.setPolar(1,phi);
        return res;
    }

    static double distFromZero(@NotNull Complex z){
        double r=z.abs();
        return Math.log((1+r)/(1-r));
    }

    public static int findMinIndex(double a, double b, double c, double d, double e) {
        double[] values = {a, b, c, d, e};
        int minIndex = 0;

        for (int i = 1; i < values.length; i++) {
            if (values[i] < values[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex; // Returns index of the smallest element
    }

    static int modInverseP(int i, int p){
        int u=p;
        int v=i;
        int r=0;
        int s=1;
        while (v>0){
            if (v%2==0){
                v/=2;
                if(s%2==0){s/=2;}else{s=(s+p)/2;}
            }else
            if (u%2==0){
                u/=2;
                if(r%2==0){r/=2;}else{r=(r+p)/2;}
            }else
            if (u>v){
                u=(u-v)/2;
                if((r-s)%2==0){r=(r-s)/2;}else{r=(r-s+p)/2;}
            }else{
                v=(v-u)/2;
                if((s-r)%2==0){s=(s-r)/2;}else{s=(s-r+p)/2;}
            }
        }
        if(r<0){r+=p;}
        return r;
    }
    static int modNeg (int a, int mod){
        return (mod-a)%mod;
    }

    static int[][] normalize(int[][] mat,int size,int field){
        int i=0;
        while(mat[i][0]==0&&i<size){
            i++;
        }
        if (mat[i][0]>field/2){
            for (i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    mat[i][j] = modNeg(mat[i][j],field);
                }
            }
        }
        return mat;
    }
}
