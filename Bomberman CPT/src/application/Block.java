package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Block{
	
	private int xPos, yPos, property;
	private double width, height;
	private Image imgBlock;
	private ImageView ivBlock;
	public static final int UNBREAKABLE = 0;
	public static final int BREAKABLE = 1;
	public static final int GRASS = 2;
	public static final int BURN = 3;
	
	public Block(){
		xPos = 0;
		yPos = 0;
		imgBlock = new Image("");
		ivBlock = new ImageView();
		property = BREAKABLE;
		createBlock();
	}

	public Block (Image img){
		xPos = 0;
		yPos = 0;
		imgBlock = img;
		ivBlock = new ImageView();
		property = BREAKABLE;
		createBlock();
	}
	public Block(int status){
		xPos = 0;
		yPos = 0;
		imgBlock = new Image("");
		ivBlock = new ImageView();
		property = status;
		createBlock();
	}
	
	public Block(Image img, int status){
		xPos = 0;
		yPos = 0;
		imgBlock = img;
		ivBlock = new ImageView();
		property = status;
		createBlock();
	}
	
	public void setProperty(Image img,int status){
		imgBlock = img;
		ivBlock.setImage(imgBlock);
		property = status;
	}
	
	public int getProperty(){
		return property;
	}
	
	public ImageView getNode(){
		return ivBlock;
	}
	
	public boolean isBreakable() {
		if (property == BREAKABLE || property == GRASS || property == BURN)
			return true;
		
		else
			return false;
	}
	
	public boolean isSolid() {
		if (property == GRASS || property == BURN)
			return false;
		
		else
			return true;
	}
	
	public void setImage(Image userImg){
		imgBlock = userImg;
		ivBlock.setImage(userImg);
	}
	
	private void createBlock(){
		ivBlock.setImage(imgBlock);	
		width = 33;	
		height = 33;
		ivBlock.setFitWidth(33);
		ivBlock.setFitHeight(33);
	}
};