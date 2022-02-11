import controlP5.*;

float xRotation = 90;
float zRotation = 0;
int y = 0;
int yDir = 1;
float mouseXpos;
float mouseYpos;
int diamondSize = 50;
PShape diamond;
PShape satellite;
OnOffButton lightsButton;
OnOffButton pointLightButton;
OnOffButton satelliteButton;
ControlP5 cp5;
int R;
int G;
int B;
int shapesCount = 0;
float satelliteRotation = 0;
PFont categoryFont;
float satelliteRotationSpeed;

void setup(){
    fullScreen(P3D);
    background(0);
    smooth(4);
    //create diamond at the center
    diamond = createShape(GROUP);
    Pyramid p1 = new Pyramid(diamondSize, diamondSize*1.5);
    Pyramid p2 = new Pyramid(diamondSize, diamondSize*3, -1);
    diamond.addChild(p1.getShape());
    diamond.addChild(p2.getShape());
    diamond.setFill(color(225,54,215));
    diamond.setStroke(color(240, 25,220));
    
    //create orbiting diamond
    Pyramid p3 = new Pyramid(diamondSize/5, diamondSize/5*1.5);
    Pyramid p4 = new Pyramid(diamondSize/5, diamondSize/5*1.5, -1);
    satellite = createShape(GROUP);
    satellite.addChild(p3.getShape());
    satellite.addChild(p4.getShape());
    satellite.setFill(color(225,54,215));
    satellite.setStroke(color(240, 25,220));
    
    //create buttons for satellite and lights
    lightsButton = new OnOffButton(40, 50, 100, 40, "Lights");
    pointLightButton = new OnOffButton(40, 100, 130, 40, "Point Light");
    satelliteButton = new OnOffButton(40, 350, 130, 40, "Satellite");
    
    //Create sliders to adjust the color of the shapes
    cp5 = new ControlP5(this);
    for(int i = 0; i<3; i++){
        String property = "";
        switch(i){
            case 0: property = "R"; break;
            case 1: property = "G"; break;
            case 2: property = "B"; break;
        }
        cp5.addSlider(property)
           .setPosition (40, 210 + i*30)
           .setRange(0, 255)
           .setWidth(200)
           .setHeight(20)
           .setValue(125);
    }
    
    cp5.addSlider("satelliteRotationSpeed")
       .setPosition(40, 400)
       .setRange(-0.5, 0.5)
       .setWidth(200)
       .setHeight(20)
       .setLabel("Rotation Speed");
    //set the font to be used
    categoryFont = createFont("Arial Bold Italic", 20);
}

void draw(){
    background(0);
    //display the buttons responsible for the lighting
    textAlign(LEFT, BOTTOM);
    textFont(categoryFont);
    fill(255,255,255);
    text("Lighting", 40, 40);
    text("Color Adjustment", 40, 200);
    text("Satellite Control", 40, 340);
    lightsButton.display();
    satelliteButton.display();
    diamond.setFill(color(R, G, B));
    satellite.setFill(color(R, G, B));
    if(lightsButton.getState()){
        lights();
    }else{
        pointLightButton.display();
        if(pointLightButton.getState())
            pointLight(255,255,255, mouseXpos, mouseYpos, 0);
    }
    //Create and manipulate the large crystal shape at the center
    pushMatrix(); //save current state
    translate(width/2, height/2 -diamondSize + y, 0); 
    rotateX(PI/2); 
    rotateZ(zRotation);
    //move crystal up and down
    if(abs(y) >= 35){
        yDir *= -1;
    }
    y+= yDir;
    zRotation-=0.025;
    shape(diamond); //finally show the actual shape
    //Create and manipulate orbiting crystal
    if(satelliteButton.getState()){
        rotateZ(satelliteRotation); // control crystal rotation speed
        translate(diamondSize*3, 0,0); // Position crystal to the right of the larger one
        shape(satellite); // Show the actual shape
        satelliteRotation += satelliteRotationSpeed;
    }
    popMatrix();
}

void mousePressed(){
    mouseXpos = mouseX;
    mouseYpos = mouseY;
    lightsButton.clickEvent();
    pointLightButton.clickEvent();
    satelliteButton.clickEvent();
}
