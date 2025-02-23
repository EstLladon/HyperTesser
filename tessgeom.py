import cmath
import numpy as np
from mpmath import mp
mp.dps = 40
F = mp.fraction
sp = lambda a, b: mp.sinpi(F(a,b))
cp = lambda a, b: mp.cospi(F(a,b))

sopr=(lambda x:complex(x.real,-x.imag))

def translate(z, t):
    """Translate point z by the vector t."""
    return (z+t)/(mp.conj(t)*z + 1)

def translatemotion(t):
    """Translation by the vector t."""
    return (1,t,mp.conj(t))

# This can also be expressed as 1/t * A/conj(A) * translate(z, D)
# where t = expjpi(k), A = c*conj(c) - t, D = c*(t-1) / A,
# but that would be overkill.
def rotatepi(z, k, c=0):
    """Rotate point z by angle k*pi around point c."""
    return translate(translate(z, -c) * mp.expjpi(k), c)

def rotatepimotion(k, z=0):
    """Rotation by angle k*pi around point z."""
    a,b,c=translatemotion(-z)

    #return translate(translate(z, -c) * mp.expjpi(k), c)
    return combinemotion((mp.expjpi(k)*a,mp.expjpi(k)*b,c),translatemotion(z))


#def motiontomat(phi,b):
#    return ((cmath.exp(1j*phi),b*cmath.exp(1j*phi),sopr(b),1))


def motiontomat(phi,b):
    return ((cmath.exp(1j*phi),b*cmath.exp(1j*phi),sopr(b)))

def motionbc(b,c):
    motion1=motiontomat(0,-b)
    motion2=motiontomat(0,applymotion(motion1,c))
    motion3=combinemotion(motion1,motion2)
    return combinemotion(motion3,motiontomat(0,b))


def applymotion(motion,arg):
    a,b,c=motion
    return ((a*arg+b/(c*arg+1)))

def combinemotion(motion1,motion2):
    a,b,c=motion1
    
    e,f,g=motion2
    B=b*g+1
    
    return ((a*e+c*f)/B,(b*e+f)/B,(a*g+c)/B)

def powermotion(motion,n):
    m=(1,0,0)
    for _ in range(n):
        m=combinemotion(m,motion)
    return m

def calculatebpbq(p,q,r): 
        cosp=mp.cospi(mp.fraction(1,p))
        cosq=mp.cospi(mp.fraction(1,q))
        cosr=mp.cospi(mp.fraction(1,r))
        sinp=mp.sinpi(mp.fraction(1,p))
        sinq=mp.sinpi(mp.fraction(1,q))
        E=cosr+cosp*cosq
        sh2A=(E*E-sinp*sinp*sinq*sinq)/(2*E+sinp*sinp+sinq*sinq)
        shRp=mp.sqrt(sh2A)/sinp
        shRq=mp.sqrt(sh2A)/sinq
        coshRp=mp.sqrt(sh2A/(sinp*sinp)+1)
        coshRq=mp.sqrt(sh2A/(sinq*sinq)+1)
        bp=shRp/(coshRp+1)
        bq=shRq/(coshRq+1)
        return bp,bq