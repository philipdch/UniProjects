//Initialization of sounds
import processing.sound.*;
SoundFile gameOverSound;
SoundFile general;
SoundFile coinsound;
SoundFile policehit;
SoundFile policelong;
SoundFile ufo;
SoundFile signbreak;
SoundFile bgmusic;
SoundFile powerUp1;
SoundFile powerUp2;

//Initialization of images
PImage[] running = new PImage[30];
PImage runner1;

PImage runnerJump;
PImage obstacle1; 
PImage obstacle2;
PImage obstacle3;
PImage uap;
PImage plat;
PImage coin;
PImage cloud1;
PImage virus;
PImage bgImage;
PImage heart;
PImage slow;
PImage car;
PImage car2;
PImage plane;
PImage heart1;
PImage sadFace;
PImage police;
PImage brokensign;

//Initialization of general variables
//ArrayLists of different objects
ArrayList<Ground> grounds ;
ArrayList<Platform> platforms;
ArrayList<Enemy> obstacles;
ArrayList<Enemy> cars;

//Message that will be displayed
String[] endMessage = new String[2];

float planec=-200;          //position of plane
int x=0;                    // x-coordinate of the background image   
int counter=0;              // counter to dispaly ground
int coins;                  
int highScore = 0;
boolean flagStart=false;    //boolean for start game
int ground=450;
Player p;                   // Initialization of Player
int speedTimer=0;           // Timer to speed up the player 
float speed = 7;           // Initialization of the speed of the Player
int obstacleTimer = 0;      //Used to determined if a new obstacle should be shown.
int minObj = 90;     //Used in addition to obstacleTimer. Determines the min distance 
                            //between obstacles. Higher value = less obstacles
int randomAddition = 0;
int score=0;
int groundCounter = 0;

int[] platformCounter = {50, 0};

//---------------------------------------------------------------
void setup(){
  
  size(1400, 500);
  
  
  frameRate(60);
  imageMode(CORNER);
 
 //set up Sounds and Images
  ufo = new SoundFile(this, "ufo.wav");
  signbreak = new SoundFile(this, "signbreak.wav");
  bgmusic = new SoundFile(this, "bgmusic.wav");
  general = new SoundFile(this, "gameover1.wav");
  coinsound = new SoundFile(this, "coinsound.wav");
  policehit = new SoundFile(this, "policeHit.wav");
  policelong = new SoundFile(this, "policeLong.wav");
  powerUp1 = new SoundFile(this,"powerup1.wav");
  powerUp2 = new SoundFile(this,"powerup2.wav");
  
  gameOverSound = general;
   
  brokensign = loadImage("obsBroken.png");
  runner1 = loadImage("running1.png");
  runnerJump = loadImage("jump.png");
  obstacle1 = loadImage("obstacle1.png");
  obstacle2 = loadImage("obstacle2.png");
  obstacle3 = loadImage("obstacle3.png");
  uap = loadImage("51.png");
  plat = loadImage("platform_new.png");
  coin = loadImage("coin_1.png");
  virus  = loadImage("virus.png");
  bgImage = loadImage("bg.jpg");
  bgImage.resize(width, height);
  heart= loadImage("heart.png");
  slow=loadImage("snail.png");
  car=loadImage("police.png");
  car2=loadImage("cars.png");
  car2.resize(car.width, car.height);
  plane=loadImage("plane.png");
  plane.resize(100, 40);
  heart1 = loadImage("heart01.png");
  sadFace = loadImage("sad.png");
  police = loadImage("police.png");
  brokensign = loadImage("obsBroken.png");
  // reset variables for new game 
  init();
  
  //setup 3d player
  for(int i =0; i < running.length; i++){
    String filename = (i<10)?"tile00" + i: "tile0" + i;
    filename += ".png";
   
    running[i] = loadImage(filename);
  }
  
}
//---------------------------------------------------------------
void init(){
   platforms = new ArrayList<Platform>();
   obstacles= new ArrayList<Enemy>();  grounds= new ArrayList<Ground>();
   cars=new ArrayList<Enemy>();
   p= new Player();
   score=0;
   speed=10;
   coins=0;
   bgmusic.amp(0.3);
   bgmusic.loop();
}
//---------------------------------------------------------------
void draw(){
   
  //If coins > 50 Player gains an extra life 
  if(coins>=50){
   coins-=50;
   p.lives++;
  }
 background(51,153,255);
 
 //Set up Background Image
 image(bgImage, x, 0);
 image(bgImage, x+bgImage.width, 0);
 x--;
 if (x<-bgImage.width) 
   x=0;
 //Set up road
  stroke(0);
  strokeWeight(2);
  fill(90, 90, 90);
  rect(0, height - 70, width, height - 70);
  fill(0);
    
  //Set up Plane that is constantly moving forward
 if(planec>2000){
    planec=-200;
  }
  planec++;
  image(plane,planec++ ,200);
 
 //set up details at the road
  if(counter==0){
    for(int i = 0; i < grounds.size(); i++){
    grounds.get(i).show();
  }
  //Show Player
    p.draw();
  }
  
  if(flagStart&&counter==0){
    
     //Set up HighScore 
    if(score > highScore) highScore =score;
     
     //Set up the timer for updating speed 
    if(speedTimer>300){
      speedTimer=0;
      speed++;
    }
    speedTimer++;
  
    //continuously increasing score 
    if(speedTimer % 3 == 0)score += 1;
  
    //Dispaly platforms/obstacles/cars
    
    for(int i = 0; i < platforms.size(); i++){
      platforms.get(i).draw();
      
    }
    
    for(int i = 0; i < obstacles.size(); i++){
      obstacles.get(i).draw();
      
    }
    for(int i = 0; i < cars.size(); i++){
      cars.get(i).draw();
      
    }

    //Player moving at specific speed
    p.move();
    //Update platforms/obstacles/cars and timers
    update();
    //Manage collisions between platforms/obstacles/cars
    collisions();
    //Remove platforms/obstacles/cars
    destroyObjects();
 
  }else{
    
    if(counter==0){
       fill(0);
       textSize(25);
       text("(Press 'r' to Start!)", 600, 230);
  
    }
  }
  //Display specific message accordingly to the cause of death
  if(counter>0){
    textSize(25);
    text(endMessage[0], 600, 230);
    text(endMessage[1], 600, 280);
    counter--;
   }
   
  //Dispaly highscore and lives remaining
  textSize(20);
  text("Score: " + score, 5, 20);
  text("High Score: " + highScore, width - (140 + (str(highScore).length() * 10)), 20);
  text("Coins: "+coins, 150, 20);
  textSize(22);
  text("Lives remaining -> ",500, 30);
  if (p.lives >0){
    int livesPos = 700;
    for (int i = 0;i < p.lives; i++){
        image(heart, width - livesPos, 5);
        livesPos = livesPos - 50;
    } 
  }else{
      image(sadFace, width - 700, 5);
  }
}

//---------------------------------------------------------------
void collisions(){
  
  boolean col=false;
  //Checks if there is a collission with a platform or if the player is on a platform
  //then set base of the player either a platform or ground
  for(int i = 0; i < platforms.size(); i++){

      if(platforms.get(i).collision(p)){
        if(platforms.get(i).onTop(p)){
           float b=platforms.get(i).getBase();
           p.setBase(b);
           col=true;
           break;
        }
        else p.fall(platforms.get(i).getBotom());   
      }
  }
  if(!col)p.setBase(ground);
  
  //Check if there is a collision with an obstacle 
  //then lose lives accordingly ,and if no more lives set flag = false 
  //and play specific game over sound
   for(int i = 0; i < obstacles.size(); i++){
 
      if(obstacles.get(i).collision(p)){
        if(obstacles.get(i).type==0)obstacles.get(i).draw();
         gameOverSound.play();
         if(!p.lifeLoss()){
              //reset variables
              init();
              flagStart=false;
             counter=80;    
         }
         gameOverSound=general;
      }
   }
  //Check if there is a collision with a car 
  //then lose lives accordingly ,and if no more lives set flag = false and reset
  //and play specific game over sound
   for(int i = 0; i < cars.size(); i++){
       if(cars.get(i).collision(p)){
           gameOverSound.play();
           if(!p.lifeLoss()){
              init();
              flagStart=false;
              counter=100;
         }
         gameOverSound=general;
      }
   }
 }

//---------------------------------------------------------------
/* Updates the elements on screen
*  Shows the obstacles currently contained at each obstacle list, as well as the player, and moves them across the screen.
*  Also determines if new obstacles or ground objects should be added
*  Lastly displays game over message when the player hits an obstacle */
void update(){
  
   for(int i = 0; i < platforms.size(); i++){
    platforms.get(i).move(speed);
  }
  for(int i = 0; i < obstacles.size(); i++){
    obstacles.get(i).move(speed);
  }
  for(int i = 0; i < cars.size(); i++){
    cars.get(i).move(speed);
  }
  //Create a platform in a random space with a random length
  createPlatform();
  
  //Update timer for obstacles and add them
  obstacleTimer+=speed/4;
    speed += 0.0001;
    if(obstacleTimer > minObj + randomAddition)  addObstacle();
    
    //Add continuously new ground 
    groundCounter++;
    if(groundCounter > 10){
      groundCounter = 0;
      grounds.add(new Ground());
    }
}
//---------------------------------------------------------------
/*Adds a random obstacle to be shown in the next frame
* The new obstacle can be either a bird with 15% chance or
* a cactus with 75% chance
* Finally the method resets the obstacle timer */
void addObstacle(){
   float enemyProb = random(1);
  // Type of enemy is 6 -> enemy is a virus
  if(enemyProb < 0.1) obstacles.add(new Enemy(ground,6));
  // Type of enemy is 4 or 5 -> enemy is a ufo initialized in different positions
  else if(enemyProb < 0.5)obstacles.add(new Enemy(ground,(int)random(4,5.99)));
  // Type of enemy is 0 or 1 or 2-> enemy is a road obstacle (3 different obstacles)
  else if(enemyProb < 0.9)obstacles.add(new Enemy(ground,(int)random(1.1, 2.1)));
  // Type of enemy is 3 -> enemy is a car)
  else cars.add(new Enemy(ground,3));
  
  randomAddition = floor(random(30));
  obstacleTimer = 0;
   
}
 //---------------------------------------------------------------
void createPlatform(){
  
    platformCounter[0]++;
    platformCounter[1]++;
    int platformType = floor(random(2)); //randomly determine the height of the platform
    //check timers for each kind of platform
    if(platformType == 0){
      if(platformCounter[0] > 80 + floor(random(30))){
        platformCounter[0] = 0;
       
        platforms.add(new Platform(plat.width*floor(random(5, 20)),plat.height,platformType));
      }
    }else{
      if(platformCounter[1] > 90 + floor(random(100))){
        platformCounter[1] = 0;
        platforms.add(new Platform(plat.width*floor(random(5, 20)),plat.height,platformType));
      }
    }
  }
//--------------------------------------------------------------- 
  void destroyObjects(){
    
    for(int i = 0; i < platforms.size(); i++){
      
      if(platforms.get(i).checkBounds()){
          platforms.remove(i);
          
      }
    }
     for(int i = 0; i < obstacles.size(); i++){
      
      if(obstacles.get(i).checkBounds()){
        obstacles.remove(i);
        
      }
    }
    for(int i = 0; i < cars.size(); i++){
      if(cars.get(i).checkBounds()){
        cars.remove(i);
        
      }
    }
     for(int i = 0; i < grounds.size(); i++){
      grounds.get(i).move(speed);
      if(grounds.get(i).posX < -20){
        grounds.remove(i);
        i--;
       }
     }
 }
 //---------------------------------------------------------------
   //When specific key is pressed , do specific action 
   // SPACE -> Player jumps ( there is double jump )
   // r -> Game starts and restarts after lose
   // s -> Player moves to the beneath platform
    void keyPressed(){
        switch(key){
        case ' ': 
              if(flagStart){
                p.jump();
              }
              break;
        case 'ρ':
        case 'R':
        case 'r': if(!flagStart&&counter==0){
                flagStart=true;
               
              }
              break;
        case 'S' :
        case 's':
        case 'σ': 
              p.moveDown();
        }
   
   }
   
 public void addLife(){
   p.lives++;
 }
