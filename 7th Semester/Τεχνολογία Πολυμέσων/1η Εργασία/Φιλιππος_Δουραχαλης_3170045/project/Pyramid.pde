public class Pyramid{
    PShape p;
    float base;
    float height;
    int[] rgb = new int[3];
    
    Pyramid(float base, float height){
        this(base, height,  1);
    }
    
    Pyramid(float base, float height, int invert){
        this.base = base;
        this.height = height;
        p = createShape();
        p.beginShape(TRIANGLE);
          p.vertex(-base, -base, 0);
          p.vertex( base, -base, 0);
          p.vertex(   0,    0,  2*invert*height);

          p.vertex( base, -base, 0);
          p.vertex( base,  base, 0);
          p.vertex(   0,    0,  2*invert*height);

          p.vertex( base, base, 0);
          p.vertex(-base, base, 0);
          p.vertex(   0,   0,  2*invert*height);

          p.vertex(-base,  base, 0);
          p.vertex(-base, -base, 0);
          p.vertex(   0,    0,  2*invert*height);
        p.endShape();
    }
    
    public PShape getShape(){
      return p;
    }
    
    public float getbase(){
      return base;
    }
    
    public float getHeight(){
        return height;
    }
    
    public void setColour(int[] rgb){
        if(rgb.length == 3){
          p.setFill(color(rgb[0], rgb[1], rgb[2]));
          this.rgb[0] = rgb[0];
          this.rgb[1] = rgb[1];
          this.rgb[2] = rgb[2];
        };
    }
    
    public void setStroke(int[] rgb){
      p.setStroke(color(rgb[0], rgb[1], rgb[2]));
    }

}
