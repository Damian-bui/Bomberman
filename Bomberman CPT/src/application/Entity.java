package application;

import java.util.Random;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class Entity {

	private Random rand;
	private Image imgEast, imgWest, imgNorth, imgSouth, imgDead;
	private ImageView ivPlayer;
	private boolean isDead;
	private boolean[] surround;
	private int direction, gridX, gridY, health;
	private double xPos, yPos, width, height;
	public static final int EAST = 0;
	public static final int WEST = 1;
	public static final int SOUTH = 2;
	public static final int NORTH = 3;
	public static final int LEFT = 4;
	public static final int RIGHT = 5;

	public Entity(){	//no-arg constr, init variables
		rand = new Random();
		isDead = false;
		direction = EAST;
		xPos = 0;
		yPos = 0;
		gridX = 0;
		gridY = 0;
		health = 1;
		surround = new boolean[] {false,false,false,false};
		imgEast = new Image("file:images/shooterEast.png");
		imgWest = new Image("file:images/shooterWest.png");
		imgDead = new Image("file:images/deadPlayer.png");
		ivPlayer = new ImageView(imgEast);	//default face right
		width = imgEast.getHeight();
		height = imgEast.getHeight();	
	}

	public Entity(Image[] imgArray){
		rand = new Random();
		imgEast = imgArray[0];
		imgWest = imgArray[1];
		imgNorth = imgArray[2];
		imgSouth = imgArray[3];
		//		imgDead = imgArray[4];

		gridX = 0;
		gridY = 0;
		health = 1;
		surround = new boolean[] {false,false,false,false};
		ivPlayer = new ImageView(imgSouth);	//South by default
		direction = SOUTH;
		isDead = false;
		xPos = 0;
		yPos = 0;
		width = imgEast.getWidth();
		height = imgEast.getHeight();
	}

	public ImageView getNode(){	//return iv
		return ivPlayer;
	}

	public int getMonsterDirection(){
		return direction;
	}

	public double getHeight(){
		return height;
	}

	public double getWidth(){
		return width;
	}

	public double getX(){
		return xPos;
	}

	public double getY(){
		return yPos;
	}

	public Image getImage(){
		return ivPlayer.getImage();
	}

	public int getGridX() {
		return gridX;
	}

	public int getGridY() {
		return gridY;
	}

	public void setGridX(int userX) {
		gridX = userX;
	}

	public void setGridY(int userY) {
		gridY = userY;
	}

	public int getDirection(){
		if (direction == EAST)
			ivPlayer.setImage(imgEast);
		else
			ivPlayer.setImage(imgWest);

		return direction;

	}

	public void setX(double userX){
		xPos = userX;
	}

	public void setY(double userY){
		yPos = userY;
	}

	public void setImage(Image img){
		ivPlayer.setImage(img);
	}

	public void setDirection(int dir){
		direction = dir;

		if (direction == EAST)
			ivPlayer.setImage(imgEast);
		else
			ivPlayer.setImage(imgWest);
	}

	public void setLocation(double userX, double userY){
		xPos = userX;
		yPos = userY;
		ivPlayer.setLayoutX(xPos);
		ivPlayer.setLayoutY(yPos);
	}

	public void setSurround(boolean userTop, boolean userLeft, boolean userDown, boolean userRight) {
		surround[0] = userTop;
		surround[1] = userLeft;
		surround[2] = userDown;
		surround[3] = userRight;
	}

	public void setMonsterDirection(int dir){ 
		direction = dir; 
		if (dir == LEFT) { 
			ivPlayer.setImage(new Image("file:images/chungus_left.png")); 
		} 
		else if (dir == RIGHT) { 
			ivPlayer.setImage(new Image("file:images/chungus.png")); 
		} 
	} 

	public int getHealth() { 
		return health; 
	}

	public void setHealth(int hp) { 
		health = hp; 
	}

	public boolean getTop() {
		return surround[0];
	}

	public boolean getLeft() {
		return surround[1];
	}

	public boolean getDown() {
		return surround[2];
	}

	public boolean getRight() {
		return surround[3];
	}

	public void move(double userX, double userY){
		xPos += userX;
		yPos += userY;

		if (userX < 0){
			ivPlayer.setImage(imgWest);
			direction = WEST;
		}

		else if (userX > 0){
			ivPlayer.setImage(imgEast);
			direction = EAST;
		}

		if (userY > 0){
			ivPlayer.setImage(imgSouth);
			direction = SOUTH;
		}

		else if (userY < 0){
			ivPlayer.setImage(imgNorth);
			direction = NORTH;
		}

		ivPlayer.setLayoutX(xPos);
		ivPlayer.setLayoutY(yPos);
	}

	public void moveMonster(Image img, double x){
		ivPlayer.setImage(img);
		xPos += x;

		if (x > 0){
			direction = RIGHT;
			ivPlayer.setImage(new Image("file:images/chungus.png"));
		}
		if (x < 0){
			direction = LEFT;
			ivPlayer.setImage(new Image("file:images/chungus_left.png"));
		}
	}
	
	public void die() {	//die
		ivPlayer.setImage(imgDead);
	}

	public Bounds getObjectBounds() {	//create rectangle boundary
		Rectangle rect = new Rectangle(xPos+10, yPos, width-20, height);
		return rect.getBoundsInParent();	//return the boundaries
	}

	public void spawn(double sceneWidth, double sceneHeight) {
		xPos = rand.nextInt((int)(sceneWidth - imgEast.getWidth()));
		yPos = rand.nextInt((int)(sceneHeight - imgEast.getHeight()));

	}




}
