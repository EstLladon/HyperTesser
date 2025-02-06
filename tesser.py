import math,cmath
import tessgeom as g
from math import pi,sin,cos,sinh,cosh,asinh,sqrt,exp
from pynverse import inversefunc
from mpmath import mp

from sympy.combinatorics import Permutation, PermutationGroup

#poinc=(lambda a:(exp(a)-1)/(exp(a)+1))


class TessPoint:
    def __init__(self,id,label,dlabel=False,motion=(1,0,0),edges=[None,None,None,None],faces=[None,None,None,None]):
        self.id=id
        self.label=label
        self.dlabel=dlabel
        self.motion=motion
        self.edges=edges
        self.faces=faces
        
class TessEdge:
    def __init__(self,p1=None,p2=None,label='',dlabel=False,type=0):
        self.start=p1
        self.finish=p2
        self.label=label
        self.dlabel=dlabel
        self.type=type

class TessFace:
    def __init__(self,vertices=[],label='',dlabel=False,type=''):
        self.vertices=vertices
        self.edges=[]
        self.label=label
        self.dlabel=dlabel
        self.type=type

class Path:
    def __init__(self,edges=[],label='',dlabel=False,type=''):
        self.vertices=edges
        self.edges=[]
        self.label=label
        self.dlabel=dlabel
        self.type=type










class Tesselation:
    def __init__(self,A,B):
        self.p = A.order()
        self.q = B.order()
        self.r = (A*B).order()
        self.G=PermutationGroup(A,B)
        bp,bq=g.calculatebpbq(p,q,r)
        self.motionA=g.rotatepimotion(2/p,bp)
        self.motionAb=g.rotatepimotion(-2/p,bp)
        self.motionB=g.rotatepimotion(2/q,-bq)
        self.motionBb=g.rotatepimotion(-2/q,-bq)
        self.points=[]
        self.auxpoints=[]
        self.edges=[]
        self.faces=[]
        self.border=[]
        self.paths=[]

    def draw(self,Model):
        Model.draw(self.points,self.edges,self.faces)

    def initfirstface(self):
        fp=TessPoint(0,label=self.G.identity)
        self.points.append(fp)
        curr=0
        for i in range(self.p-1):
            newp=TessPoint(len(self.points),self.points[curr].label*A,False,g.combinemotion(self.motionA,self.points[curr].motion))
            


    def add_layer(self):
        pass

    def add_path(self,add1='',add2='',color=''):
        pass


class HyperModel:
    def __init__(self,type="P",K=-1):
        self.type=type
        self.K=K
        self.partition=[]
        pass

    def add_circle(self,point):
        pass

    def draw(points=[], edges=[],faces=[]):
        pass

if __name__=='__main__':
    A = Permutation(1,2,3,4,5,6,7)
    B = Permutation(1, 8)(2,7)(3,4)(5,6)
    Gr = PermutationGroup(A, B)
    p,q,r=A.order(),B.order(),(A*B).order()
    print(cmath.exp(1j*pi/p),cmath.exp(1j*pi/q),cmath.exp(1j*pi/r))
    
    tess=Tesselation(A,B)
    print(g.applymotion(tess.motionA,0),g.applymotion(tess.motionAb,0),g.applymotion(tess.motionB,0),g.applymotion(tess.motionBb,0))
    print("ma:",g.powermotion(tess.motionA,p))
    print(g.powermotion(tess.motionAb,p))
    print(g.powermotion(tess.motionB,q))
    print(g.powermotion(tess.motionBb,q))
    comm=g.combinemotion(tess.motionA,tess.motionB)
    commr=g.powermotion(comm,2*r)
    print(commr)
    print(g.applymotion(commr,0))
    eip=cmath.exp(1j*pi/r)
    
    
    