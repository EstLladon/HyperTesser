import java.util.ArrayList;

class gui{
    ArrayList<Button> Buttons;
    boolean[] Switches;
    int defButton=-1;
    int ButtonState;
    int Mode;
    int interfaceWidth;
    gui(int iwidth,int switchnum){
        Buttons= new ArrayList<>();
        ButtonState=-1;
        interfaceWidth=iwidth;
        Switches=new boolean[switchnum];
        Mode=0;
    }
    void addButton(int ix, int iy, int iw, int ih,String ilabel,int itsize,int iid){
        Button B=new Button(ix, iy, iw, ih,ilabel,itsize,iid);
        Buttons.add(B);
    }
    void addButton(Button B){
        Buttons.add(B);
    }
    void addDefButton(Button B){
        Buttons.add(B);
        defButton=B.id;
    }
    void display(HyperTesser h){
        //HyperTesser h=HyperTesser.getInstance();
        h.stroke(0);
        for (Button button : Buttons) {
            button.display(interfaceWidth, ButtonState,h);
        }
    }
    int whichButtonOver(HyperTesser h){
        for(int i=0;i<Buttons.size();i++){
            if (overButton(i,h)){return Buttons.get(i).id;}
        }
        return -1;
    }
    boolean overButton(int i,HyperTesser h){
        //HyperTesser h=HyperTesser.getInstance();
        float a= h.width - interfaceWidth+Buttons.get(i).x;
        float b=a+Buttons.get(i).boxx;
        float c=Buttons.get(i).y;
        float d = c+Buttons.get(i).boxy;
        return (h.mouseX >= a) && (h.mouseX <= b) && (h.mouseY >= c) && (h.mouseY <= d);
    }
}

class Button {
    int x, y;
    int boxx, boxy;
    boolean over;
    boolean press;
    String label;
    int tSize;
    int id;
    Button(int ix, int iy, int iw, int ih,String ilabel,int itsize,int iid) {
        x = ix;
        y = iy;
        boxx = iw;
        boxy = ih;
        over=false;
        press=false;
        label=ilabel;
        tSize=itsize;
        id=iid;
    }

     void display(int interfaceWidth,int State,HyperTesser h) {
        //HyperTesser h=HyperTesser.getInstance();
        if (State==id)
        {
            h.strokeWeight(3);
            h.fill(255);
        }else{
            h.strokeWeight(2);
            h.fill(200);
        }
        h.rect(h.width - interfaceWidth+x, y,boxx,boxy);
        h.fill(0);
        h.textSize(tSize);
        h.text(label,h.width - interfaceWidth+x,y,boxx,boxy);
        h.noFill();
    }
}

