//Design of the ground
class Ground{
  float posX = width;
  float posY = ground + 20;
  int w = 50;
  
  Ground(){
  }
  
  void show(){
    stroke(255);
    strokeWeight(5);
    line(posX, posY, posX + w, posY);
  }
  
  void move(float speed){
    posX -= speed;
  }
}
