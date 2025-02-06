import numpy as np
import cmath
import tessgeom as g
from numpy.typing import NDArray
from PIL import Image

def radius(ps):
    return np.sqrt(np.sum(ps * ps, axis=-1, keepdims=True))

width=512
eps=1/width

coord = np.linspace(-1, 1, num=width, dtype=np.complex128)
data = np.array([[x, y] for y in coord for x in coord]).reshape(width, width, 2)
disk = radius(data) < 1

def arc(p,q,width):
    motion=g.motiontomat(0,-p)
    qq=g.applymotion(motion,q)
    l=qq*np.linspace(0,1,width)
    print (l,type(l))
    #return g.applymotion(g.motiontomat(0,p),line)
    return l

def draw(p,q):
    
    arcres=arc(p,q,width)
    l=[(i.real,i.imag) for i in arcres]
    print(arcres)
    img=Image.fromarray(l)
    img.show()

if __name__=='__main__':
    p,q=0.5, 0.5*1j
    draw(p,q)
