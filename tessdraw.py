import numpy as np
import cmath
import tessgeom as g
from numpy.typing import NDArray
from PIL import Image
import matplotlib.pyplot as plt
from mpmath import mp

def radius(ps):
    return np.sqrt(np.sum(ps * ps, axis=-1, keepdims=True))

width=512
eps=1/width

coord = np.linspace(-1, 1, num=width, dtype=np.complex128)
data = np.array([[x, y] for y in coord for x in coord]).reshape(width, width, 2)
disk = radius(data) < 1

def arc(p,q,width):
    motion=g.translatemotion(-p)
    qq=g.applymotion(motion,q)
    l=qq*np.linspace(0,1,width)
    #print (l,type(l))
    return g.applymotion(g.translatemotion(p),l)
    #return l

def draw(p,q):
    
    arcres=arc(p,q,width)
    #np.array(matr.tolist(),dtype=np.float32)
    #x, y = mp.real(arcres), mp.imag(arcres)
    x=np.array([i.real for i in arcres],dtype=np.float32)
    y=np.array([i.imag for i in arcres],dtype=np.float32)
    print(x,y)
    plt.plot(x, y)
    plt.show()
    #l=[(i.real,i.imag) for i in arcres]
    #print(arcres)
    #img=Image.fromarray(l)
    #img.show()

if __name__=='__main__':
    p,q=0.5, 0.5*1j
    draw(p,q)
    #a = np.full((1, 1), 300)
    #print(a)
    #im = Image.fromarray(a, mode="L")
    #print(im.getpixel((0, 0)))  # 44
    #print(im)
    #im = Image.fromarray(a, mode="RGB")
    #print(im.getpixel((0, 0)))  # (44, 1, 0)
