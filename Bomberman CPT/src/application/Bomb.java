package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bomb {
	private boolean isPresent;
	private Image imgBomb,imgMiddle, imgEnd, imgExtend;
	private Image[] imgExplode;
	private ImageView ivBomb;
	private double xPos, yPos;
	private double imgHeight, imgWidth;
	private int gridX, gridY;
	
	public Bomb() {
		imgBomb = new Image("file:images/bomb.png");
		ivBomb = new ImageView(imgBomb);
		imgHeight = imgBomb.getHeight();
		imgWidth = imgBomb.getWidth();
		xPos = 0;
		yPos = 0;
		gridX = 0;
		gridY = 0;
		ivBomb.setLayoutX(xPos);
		ivBomb.setLayoutY(yPos);
		isPresent = true;
	}
	
	public Bomb(Image userImg){
		imgBomb = userImg;
		ivBomb = new ImageView(imgBomb);
		imgHeight = imgBomb.getHeight();
		imgWidth = imgBomb.getWidth();
		xPos = 0;
		yPos = 0;
		gridX = 0;
		gridY = 0;
		ivBomb.setLayoutX(xPos);
		ivBomb.setLayoutY(yPos);
		isPresent = true;
	}
	
	public Bomb(Image userImg, int userX, int userY){
		imgBomb = userImg;
		ivBomb = new ImageView(imgBomb);
		imgHeight = imgBomb.getHeight();
		imgWidth = imgBomb.getWidth();
		xPos = userX;
		yPos= userY;
		gridX = 0;
		gridY = 0;
		ivBomb.setLayoutX(userX);
		ivBomb.setLayoutY(userY);
		isPresent = true;
	}
	
	public Bomb(int userX, int userY){
		imgBomb = new Image("file:images/bomb.png");
		ivBomb = new ImageView(imgBomb);
		imgHeight = imgBomb.getHeight();
		imgWidth = imgBomb.getWidth();
		xPos = userX;
		yPos = userY;
		gridX = 0;
		gridY = 0;
		ivBomb.setLayoutX(userX);
		ivBomb.setLayoutY(userY);
		isPresent = true;
	}
	
	public void setImage(Image userImg){
		imgBomb = userImg;
		ivBomb.setImage(imgBomb);
	}
	
	public void setLocation(double userX, double userY){
		xPos = userX;
		yPos = userY;
		ivBomb.setLayoutX(userX);
		ivBomb.setLayoutY(userY);
	}
	
	public void setSize(double width, double height){
		imgWidth = width;
		imgHeight = height;
		ivBomb.setFitHeight(height);
		ivBomb.setFitWidth(width);
	}
	
	public double getX(){
		return xPos;
	}
	
	public double getY(){
		return yPos;
	}
	
	public void explode(){
		imgBomb = imgMiddle;
		ivBomb.setImage(imgBomb);
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

	public void setVisible(boolean visibility) {
		if (visibility) {
			ivBomb.setImage(imgBomb);
		}
		else
			ivBomb.setImage(null);
	}
	
	public ImageView getNode(){
		return ivBomb;
	}
	
}
