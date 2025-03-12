interface Group<T> {
    T mul(T a, T b);
    T id();
    T inv(T a);
    T getelement(String s);
    Label getlabel(T a, LabelStyle s);
}

class intMatrix {
     int[][] elements;
     final int size;
     final int field;
    
    intMatrix(int[][] elements,int ifield){
        size = elements.length;
        field=ifield;
        this.elements = new int[size][size];
        for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++){
             this.elements[i][j]=elements[i][j]%field;      
          }
        }
    }
    
    intMatrix(int isize,int ifield){
      size=isize;
      field=ifield;
      elements = new int[size][size];
        for (int i = 0; i < size; i++) {
            elements[i][i] = 1;
        }     
    }
    
    boolean isIdentity() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ((i == j && elements[i][j] != 1) || (i != j && elements[i][j] != 0)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    intMatrix multiply(intMatrix other) {
        int[][] result = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i][j] += this.elements[i][k] * other.elements[k][j];
                }
                result[i][j]%=field;
            }
        }
        return new intMatrix(result,field);
    }
        
    intMatrix inverse() {
        int[][] augmented = new int[size][2 * size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                augmented[i][j] = elements[i][j];
            }
            augmented[i][size + i] = 1;
        }
        
        for (int i = 0; i < size; i++) {
            int pivot = -1;
            for (int j = i; j < size; j++) {
                if (augmented[j][i] != 0) {
                    pivot = j;
                    break;
                }
            }
            if (pivot == -1) throw new UnsupportedOperationException("Matrix is not invertible");
            
            int[] temp = augmented[i];
            augmented[i] = augmented[pivot];
            augmented[pivot] = temp;
            
            int inv = modInverseP(augmented[i][i], field);
            for (int j = 0; j < 2 * size; j++) {
                augmented[i][j] = (augmented[i][j] * inv) % field;
            }
            
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    int factor = augmented[j][i];
                    for (int k = 0; k < 2 * size; k++) {
                        augmented[j][k] = (augmented[j][k] - factor * augmented[i][k] + field) % field;
                    }
                }
            }
        }
        int[][] inverseMatrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(augmented[i], size, inverseMatrix[i], 0, size);
        }
        return new intMatrix(inverseMatrix, field);
   }  

   String toString() {
        return Arrays.deepToString(elements);
   }    
}

class pslMatrixGroup implements Group<intMatrix> {
    final int size;
    final int field;
    
    final intMatrix gena;
    final intMatrix genb;
    final intMatrix genc;
    final intMatrix gend;
    
    pslMatrixGroup(int size,int ifield,int[][] ia, int[][] ib) {
        this.size = size;
        field=ifield;
        gena=normalize(new intMatrix(ia,field));
        genc=inv(gena);
        genb=normalize(new intMatrix(ib,field));
        gend=inv(genb);
    }
    
    intMatrix mul(intMatrix a, intMatrix b) {
        return normalize(a.multiply(b));
    }
    
    intMatrix id() {
        return new intMatrix(size,field);
    }
    
    intMatrix inv(intMatrix a) {
        return normalize(a.inverse());
    }
    
    intMatrix normalize(intMatrix mat){
      int i=0;
      while(mat.elements[i][0]==0&&i<size){
        i++;
      }
      if (mat.elements[i][0]>mat.field/2){
        for (i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mat.elements[i][j] = modNeg(mat.elements[i][j],field);
            }
        }
      }
      return mat;
     }
     
     intMatrix getelement(String s){
       intMatrix res=new intMatrix(size,field); 
       for(int i=0;i<s.length();i++){
         switch (s.charAt(i)){
          case 'a':
            res=mul(res,gena);
            break;
          case 'b':
            res=mul(res,genb);
            break;
          case 'c':
            res=mul(res,genc);
            break;
          case 'd':
            res=mul(res,gend);
            break;
         }
       }
       return res;
     }
     Label getlabel(intMatrix mat,LabelStyle s){
       Label l=new Label();
       switch(s){
       case FULL:
         if(mat.size>2){
           l.N=""+mat.elements[0][1];
           l.S=""+mat.elements[mat.size-1][1];
           l.E=""+mat.elements[1][mat.size-1];
           l.W=""+mat.elements[1][0];;
         }
         l.NE=""+mat.elements[0][mat.size-1];
         l.NW=""+mat.elements[0][0];
         l.SE=""+mat.elements[mat.size-1][mat.size-1];
         l.SW=""+mat.elements[mat.size-1][0];
         break;
       case IDONLY:
         if (mat.isIdentity()){
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
}

class Permutation {
     final int[] mapping;
    
     Permutation(int[] mapping) {
        this.mapping = mapping.clone();
    }
    
    Permutation(int size){
        mapping = new int[size];
        for (int i = 0; i < size; i++) {
            mapping[i] = i;
        }
    }
    
     Permutation compose(Permutation other) {
        int[] newMapping = new int[mapping.length];
        for (int i = 0; i < mapping.length; i++) {
            newMapping[i] = mapping[other.mapping[i]];
        }
        return new Permutation(newMapping);
    }
    
     Permutation inverse() {
        int[] inverseMapping = new int[mapping.length];
        for (int i = 0; i < mapping.length; i++) {
            inverseMapping[mapping[i]] = i;
        }
        return new Permutation(inverseMapping);
    }
    
     String toString() {
        return Arrays.toString(mapping);
    }
}


class PermutationGroup implements Group<Permutation> {
     final int size;
    Permutation gena,genb,genc,gend;
    
     PermutationGroup(Permutation a,Permutation b) {
        this.size = a.mapping.length;
        gena = new Permutation(a.mapping);
        genc=inv(gena);
        genb = new Permutation(b.mapping);
        gend=inv(genb);
    }
    
    Permutation mul(Permutation a, Permutation b) {
        return a.compose(b);
    }
    
    Permutation id() {
        return new Permutation(size);
    }
    
    Permutation inv(Permutation a) {
        return a.inverse();
    }
    
    Permutation getelement(String s){
      Permutation res=new Permutation(size); 
       for(int i=0;i<s.length();i++){
         switch (s.charAt(i)){
          case 'a':
            res=mul(res,gena);
            break;
          case 'b':
            res=mul(res,genb);
            break;
          case 'c':
            res=mul(res,genc);
            break;
          case 'd':
            res=mul(res,gend);
            break;
         }
       }
       return res;
    }
    Label getlabel(Permutation p,LabelStyle s){
      return new Label(p.toString());
    }
}

//aux functions
//Penk's algorithm for modular inverse in the field Z_p
//The algorithm starts with u=p,v=i,r=0,s=1. In the course of the algorithm, u and v decrease, with the following conditions being met: 
//GCD(u, v) = 1, u = ri (mod p), and v = si (mod p). The algorithm repeats the following steps until v = 0:
//– if v is even, replace it with v/2, and replace s with either s/2 or (s + p)/2, whichever is a whole number;
//– if u is even, replace it with u/2, and replace r with either r/2 or (r + p)/2, whichever is a whole number;
//– if u,v are odd and u > v, replace u with (u−v)/2 and r with either (r−s)/2 or (r − s + p)/2, whichever is a whole number;
//– if u,v are odd and v ≥ u, replace v with (v−u)/2 and s with either (s−r)/2 or (s − r + p)/2, whichever is a whole number.
int modInverseP(int i, int p){
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

int modNeg (int a, int mod){
 return (mod-a)%mod; 
}
