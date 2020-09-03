package application;
// by Damian
import java.io.File;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class Main extends Application {
	private double time = 0;
	private Image imgMonster;
	private boolean inputCorrect;
	private boolean[] bombPresent, bombExplode, deadPlayer, monsterDead;	//[0] for player 1, [1] for player 2
	private int bomb1Timer, bomb2Timer, item1XPos, item1YPos, item2XPos, item2YPos;
	private int[] playerLife;
	private Alert lose, exit;
	private AnimationTimer playerDead;
	private Runnable playerDeath;
	private ImageView ivItem1, ivItem2;
	private String name1, name2;
	private MediaPlayer music;
	private AudioClip[] sounds;
	private Bomb[] bomb;
	private Block[][] blocks = new Block[9][11]; 
	private BorderPane background;
	private TextField txtTime;
	private Timeline tl, tlBomb1Explode, tlBomb2Explode;

	public void start(Stage primaryStage) {
		try {			
			DecimalFormat df = new DecimalFormat("00");
			Random rnd = new Random();

			//get background music
			File song = new File("music.mp3");
			Media media = new Media(song.toURI().toString());
			music = new MediaPlayer(media);
			music.setCycleCount(MediaPlayer.INDEFINITE);	//loop song

			bombExplode = new boolean[2];	//checks if players' bombs have blown up
			bombPresent = new boolean[2];	//checks if player's bombs are on the map
			deadPlayer = new boolean[2];	//checks if players are dead
			bomb = new Bomb[2];	//players' bomb object
			playerLife = new int[]{1,1};	//amount of lives the players have
			monsterDead = new boolean[3];	//checks if monsters are dead

			//initialize variables
			for (int i = 0; i < 2; i++) {
				bombExplode[i] = false;
				bombPresent[i] = false;
				deadPlayer[i] = false;
				monsterDead[i] = false;
				playerLife[i] = 1;

				//import images
				bomb[i] = new Bomb(new Image("file:images/bomb1.png"));	//bomb image
				bomb[i].setSize(25, 25);	//size of bomb
				bomb[i].setVisible(false);
			}

			// Create health items
			ivItem1 = new ImageView(new Image("file:images/item_health.png"));
			ivItem1.setFitWidth(33);
			ivItem1.setFitHeight(33);

			ivItem2 = new ImageView(new Image("file:images/item_health.png"));
			ivItem2.setFitWidth(33);
			ivItem2.setFitHeight(33);

			inputCorrect = false;

			//import images
			Image[] imgPlayer1 = new Image[] {	//sprites for player 1
					new Image("file:images/p1_right.png"),
					new Image("file:images/p1_left.png"),
					new Image("file:images/p1_back.png"),
					new Image("file:images/p1_front.png")
			};

			Image[] imgPlayer2 = new Image[] {	//sprites for player 2
					new Image("file:images/p2_right.png"),
					new Image("file:images/p2_left.png"),
					new Image("file:images/p2_back.png"),
					new Image("file:images/p2_front.png")
			};
			Image[] imgExplode = new Image[]{new Image("file:images/bomb_explode.png"),//[0]
					new Image("file:images/explode_grass.png"),//[1]
					new Image("file:images/explode_block.png")};//[2]

			Image imgMonster = new Image("file:images/chungus.png");
			Image imgGrass = new Image("file:images/grass.png");

			//imageview objects for top labels
			ImageView iv1 = new ImageView(imgPlayer1[3]);
			ImageView iv2 = new ImageView(imgPlayer2[3]);

			//create players
			Entity player1 = new Entity(imgPlayer1);
			player1.getNode().setFitHeight(33);
			player1.getNode().setFitWidth(22);
			player1.getNode().setLayoutX(38.5);
			player1.getNode().setLayoutY(33);
			player1.setLocation(38.5, 33);
			player1.setGridX(0);
			player1.setGridY(0);

			Entity player2 = new Entity(imgPlayer2);
			player2.getNode().setFitHeight(33);
			player2.getNode().setFitWidth(22);
			player2.getNode().setLayoutX(370);
			player2.getNode().setLayoutY(297);
			player2.setLocation(370, 297);
			player2.setGridX(10);
			player2.setGridY(8);

			// create monsters
			Entity[] monster = new Entity[3];
			for (int i = 0; i < 3; i++) {
				monster[i] = new Entity();
				monster[i].setImage(imgMonster);
				monster[i].getNode().setFitHeight(33);
				monster[i].getNode().setFitWidth(33);
				monster[i].getNode().setLayoutX(363);
				monster[i].getNode().setLayoutY(33);
			}
			monster[0].setLocation(363, 33);
			monster[0].setMonsterDirection(Entity.LEFT);
			monster[0].setGridX(10);
			monster[0].setGridY(0);

			monster[1].setLocation(132, 165);
			monster[1].setMonsterDirection(Entity.RIGHT);
			monster[1].setGridX(3);
			monster[1].setGridY(4);

			monster[2].setLocation(33, 297);
			monster[2].setMonsterDirection(Entity.RIGHT);
			monster[2].setGridX(0);
			monster[2].setGridY(8);

			//score
			Label lblPlayer1 = new Label();
			Label lblPlayer2 = new Label();

			//Timer
			txtTime = new TextField();
			txtTime.setAlignment(Pos.CENTER);
			txtTime.setFocusTraversable(false);
			txtTime.setEditable(false);
			txtTime.setPrefSize(100, 10);
			txtTime.setText((int)time/60 + ":" + df.format(time%60));

			//initialize alerts
			lose = new Alert(AlertType.INFORMATION);
			lose.setTitle("Winner!");
			
			exit = new Alert(AlertType.INFORMATION);
			exit.setTitle("Exit");
			exit.setContentText("Thanks for playing Mo Bomberman!");
			exit.setHeaderText(null);
			
			//runnable when an entity dies
			playerDeath = new Runnable() {
				public void run() {
					
					if (deadPlayer[0]) {	//if player 1 is dead
						playerLife[0]--;	//player 1 lose life
						if (playerLife[0] < 1) {	//if player 1 has no lives left
							music.stop();
							tl.stop();	//stop timers
							tlBomb1Explode.stop();
							tlBomb2Explode.stop();
							lose.setHeaderText(name2 + " wins!");	//display winner
							lose.setContentText("Player 2 won in " + txtTime.getText() + "!");
							lose.showAndWait();
							exit.showAndWait();
							System.exit(0);	//exit game
						}
					}
					if (deadPlayer[1]) {	//if player 2 is dead
						playerLife[1]--;	//player 2 lose life
						if (playerLife[0] < 1) {
							music.stop();
							tl.stop();
							tlBomb1Explode.stop();
							tlBomb2Explode.stop();
							lose.setHeaderText(name1 + " wins!");
							lose.setContentText("Player 1 won in " + txtTime.getText() + "!");
							lose.showAndWait();
							exit.showAndWait();
							System.exit(0);
						}
					}
					for (int i = 0; i < 3; i++) {
						if (monsterDead[i]) {	//if a monster dies
							background.getChildren().remove(monster[i].getNode());	//take out monster from game
						}
					}
					playerDead.start();	//restart the timer
				}
			};
			
			//initialize layouts
			background = new BorderPane(); 
			HBox topHB = new HBox(); 
			HBox bottomHB = new HBox(); 
			VBox leftVB = new VBox(); 
			VBox rightVB = new VBox(); 

			GridPane top = new GridPane();
			top.setHgap(50);
			top.setPadding(new Insets(15,15,15,15));
			top.setStyle("-fx-background-color: orange");
			top.setAlignment(Pos.CENTER);
			top.add(lblPlayer1, 0, 0);
			top.add(txtTime, 1, 0);
			top.add(lblPlayer2, 2, 0);
			
			FlowPane center = new FlowPane(); 
			center.setOrientation(Orientation.HORIZONTAL); 

			BorderPane root = new BorderPane();
			BorderPane.setAlignment(top, Pos.CENTER);
			root.setTop(top);
			root.setCenter(background);
			Scene scene = new Scene(root,429,418);

			//timer keyframe
			KeyFrame kf = new KeyFrame(Duration.millis(250), e->{	//start timer
				time+= (1.0/4.0);
				txtTime.setText((int)time/60 + ":" + df.format(time%60));
				//move monster
				monsterMove(monster[0]);	//move monsters
				monsterMove(monster[1]);
				monsterMove(monster[2]);
			});
			tl = new Timeline(kf);
			tl.setCycleCount(Timeline.INDEFINITE);

			//Player 1 Bomb explode
			KeyFrame kfBomb1Explode = new KeyFrame(Duration.seconds(1), e->{
				bomb1Timer++;
				if (bomb1Timer >= 2){	//if it's been 2 seconds
					//checks around the bomb
					if (bomb[0].getGridY() > 0){	//check top block
						if (blocks[bomb[0].getGridY() - 1][bomb[0].getGridX()].isBreakable()){	//if it's breakable
							if (bombExplode[0]){	//if bomb has already exploded and need to change back to grass
								blocks[bomb[0].getGridY() - 1][bomb[0].getGridX()].setProperty(imgGrass, Block.GRASS);
							}
							else{
								if (blocks[bomb[0].getGridY() - 1][bomb[0].getGridX()].getProperty() == Block.GRASS){//if it's grass
									blocks[bomb[0].getGridY() - 1][bomb[0].getGridX()].setProperty(imgExplode[1], Block.BURN);	//burn the block
								}
								else{	//if its a brick block (breakable block)
									blocks[bomb[0].getGridY() - 1][bomb[0].getGridX()].setProperty(imgExplode[2], Block.BURN);	//burn the block
								}
							}
						}
					}
					if (bomb[0].getGridY() < 8){	//check bottom block
						if (blocks[bomb[0].getGridY() + 1][bomb[0].getGridX()].isBreakable()){	//if it's breakable
							if (bombExplode[0]){
								blocks[bomb[0].getGridY() + 1][bomb[0].getGridX()].setProperty(imgGrass, Block.GRASS);
							}
							else{
								if (blocks[bomb[0].getGridY() + 1][bomb[0].getGridX()].getProperty() == Block.GRASS){//if it's grass
									blocks[bomb[0].getGridY() + 1][bomb[0].getGridX()].setProperty(imgExplode[1], Block.BURN);
								}
								else{
									blocks[bomb[0].getGridY() + 1][bomb[0].getGridX()].setProperty(imgExplode[2], Block.BURN);
								}
							}
						}
					}
					if (bomb[0].getGridX() > 0){	//check left block
						if (blocks[bomb[0].getGridY()][bomb[0].getGridX() - 1].isBreakable()){	//if it's breakable
							if (bombExplode[0]){
								blocks[bomb[0].getGridY()][bomb[0].getGridX() - 1].setProperty(imgGrass, Block.GRASS);
							}
							else{
								if (blocks[bomb[0].getGridY()][bomb[0].getGridX() - 1].getProperty() == Block.GRASS){//if it's grass
									blocks[bomb[0].getGridY()][bomb[0].getGridX() - 1].setProperty(imgExplode[1], Block.BURN);
								}
								else{
									blocks[bomb[0].getGridY()][bomb[0].getGridX() - 1].setProperty(imgExplode[2], Block.BURN);
								}
							}
						}
					}
					if (bomb[0].getGridX() < 10){	//check right block
						if (blocks[bomb[0].getGridY()][bomb[0].getGridX() + 1].isBreakable()){	//if it's breakable
							if (bombExplode[0]){
								blocks[bomb[0].getGridY()][bomb[0].getGridX() + 1].setProperty(imgGrass, Block.GRASS);
							}
							else{
								if (blocks[bomb[0].getGridY()][bomb[0].getGridX() + 1].getProperty() == Block.GRASS){//if it's grass
									blocks[bomb[0].getGridY()][bomb[0].getGridX() + 1].setProperty(imgExplode[1], Block.BURN);
								}
								else{
									blocks[bomb[0].getGridY()][bomb[0].getGridX() + 1].setProperty(imgExplode[2], Block.BURN);
								}	
							}
						}
					}
					if (bombExplode[0] == false){	//if bomb wasn't blown up before
						bomb[0].setVisible(false);	//make bomb invisible
						blocks[bomb[0].getGridY()][bomb[0].getGridX()].setProperty(imgExplode[1], Block.BURN);	//burn the block where bomb was at
					}
					else {
						blocks[bomb[0].getGridY()][bomb[0].getGridX()].setProperty(imgGrass, Block.GRASS);
					}
					bombExplode[0] = true;	//bomb has exploded
				}
				if (bomb1Timer == 3) {	//after 3 seconds
					bombPresent[0] = false;	//bomb has disappeared, allows player to drop bomb again
				}
			});

			tlBomb1Explode = new Timeline(kfBomb1Explode);
			tlBomb1Explode.setCycleCount(3);

			//Player 2 Bomb
			KeyFrame kfBomb2Explode = new KeyFrame(Duration.seconds(1), e->{
				bomb2Timer++;
				if (bomb2Timer >= 2){
					//checks around the bomb
					if (bomb[1].getGridY() > 0){	//top block
						if (blocks[bomb[1].getGridY() - 1][bomb[1].getGridX()].isBreakable()){	//if it's breakable
							if (bombExplode[1]){
								blocks[bomb[1].getGridY() - 1][bomb[1].getGridX()].setProperty(imgGrass, Block.GRASS);
							}
							else{
								if (blocks[bomb[1].getGridY() - 1][bomb[1].getGridX()].getProperty() == Block.GRASS){//if it's grass
									blocks[bomb[1].getGridY() - 1][bomb[1].getGridX()].setProperty(imgExplode[1], Block.BURN);
								}
								else{	//if its a brick block
									blocks[bomb[1].getGridY() - 1][bomb[1].getGridX()].setProperty(imgExplode[2], Block.BURN);
								}
							}
						}
					}
					if (bomb[1].getGridY() < 8){	//bottom block
						if (blocks[bomb[1].getGridY() + 1][bomb[1].getGridX()].isBreakable()){	//if it's breakable
							if (bombExplode[1]){
								blocks[bomb[1].getGridY() + 1][bomb[1].getGridX()].setProperty(imgGrass, Block.GRASS);
							}
							else{
								if (blocks[bomb[1].getGridY() + 1][bomb[1].getGridX()].getProperty() == Block.GRASS){//if it's grass
									blocks[bomb[1].getGridY() + 1][bomb[1].getGridX()].setProperty(imgExplode[1], Block.BURN);
								}
								else{
									blocks[bomb[1].getGridY() + 1][bomb[1].getGridX()].setProperty(imgExplode[2], Block.BURN);
								}
							}
						}
					}
					if (bomb[1].getGridX() > 0){	//left block
						if (blocks[bomb[1].getGridY()][bomb[1].getGridX() - 1].isBreakable()){	//if it's breakable
							if (bombExplode[1]){
								blocks[bomb[1].getGridY()][bomb[1].getGridX() - 1].setProperty(imgGrass, Block.GRASS);
							}
							else{
								if (blocks[bomb[1].getGridY()][bomb[1].getGridX() - 1].getProperty() == Block.GRASS){//if it's grass
									blocks[bomb[1].getGridY()][bomb[1].getGridX() - 1].setProperty(imgExplode[1], Block.BURN);
								}
								else{
									blocks[bomb[1].getGridY()][bomb[1].getGridX() - 1].setProperty(imgExplode[2], Block.BURN);
								}
							}
						}
					}
					if (bomb[1].getGridX() < 10){	//right block
						if (blocks[bomb[1].getGridY()][bomb[1].getGridX() + 1].isBreakable()){	//if it's breakable
							if (bombExplode[1]){
								blocks[bomb[1].getGridY()][bomb[1].getGridX() + 1].setProperty(imgGrass, Block.GRASS);
							}
							else{
								if (blocks[bomb[1].getGridY()][bomb[1].getGridX() + 1].getProperty() == Block.GRASS){//if it's grass
									blocks[bomb[1].getGridY()][bomb[1].getGridX() + 1].setProperty(imgExplode[1], Block.BURN);
								}
								else{
									blocks[bomb[1].getGridY()][bomb[1].getGridX() + 1].setProperty(imgExplode[2], Block.BURN);
								}	
							}
						}
					}
					if (bombExplode[1] == false){
						bomb[1].setVisible(false);	//make bomb invisible
						blocks[bomb[1].getGridY()][bomb[1].getGridX()].setProperty(imgExplode[1], Block.BURN);
					}
					else {
						blocks[bomb[1].getGridY()][bomb[1].getGridX()].setProperty(imgGrass, Block.GRASS);	//switch back to grass
					}
					bombExplode[1] = true;
				}
				if (bomb2Timer == 3) {
					bombPresent[1] = false;
				}
			});
			tlBomb2Explode = new Timeline(kfBomb2Explode);
			tlBomb2Explode.setCycleCount(3);

			//collision with fire or monster
			playerDead = new AnimationTimer() {
				public void handle(long now) {
					if (blocks[player1.getGridY()][player1.getGridX()].getProperty() == Block.BURN) {	//if player 1 is on a burning block
						playerDead.stop();	//stop timer
						deadPlayer[0] = true;	//player 1 dies
						Platform.runLater(playerDeath);	//runnable
					}
					
					if (blocks[player2.getGridY()][player2.getGridX()].getProperty() == Block.BURN) { //if player 2 is on a burning block
						playerDead.stop();
						deadPlayer[1] = true;
						Platform.runLater(playerDeath);
					}
					for (int i = 0; i < 3; i++) {
						if (player1.getGridX() == monster[i].getGridX() && player1.getGridY() == monster[i].getGridY()) {	//if monster hits player 1
							playerDead.stop();
							deadPlayer[0] = true;	//player 1 dies
							Platform.runLater(playerDeath);
						}
						if (player2.getGridX() == monster[i].getGridX() && player2.getGridY() == monster[i].getGridY()) {	//if monster hits player 2
							playerDead.stop();
							deadPlayer[1] = true;
							Platform.runLater(playerDeath);
						}
						if (blocks[monster[i].getGridY()][monster[i].getGridX()].getProperty() == Block.BURN) {	//if monster is on a burning block
							playerDead.stop();
							monsterDead[i] = true;
							Platform.runLater(playerDeath);
						}
					}
				}
			};
			playerDead.start();	//start the timer

			// Randomize the position of item
			do
			{
				item1XPos = rnd.nextInt(9) + 1;
				item1YPos = rnd.nextInt(7) + 1;
				item2XPos = rnd.nextInt(9) + 1;
				item2YPos = rnd.nextInt(7) + 1;

			}
			while ((item1XPos % 2 != 0 && item1YPos % 2 != 0) || (item2XPos % 2 != 0 && item2YPos % 2 != 0) ||
					(item1XPos == item2XPos && item1YPos == item2YPos));
			ivItem1.setLayoutX(33*(item1XPos + 1));
			ivItem1.setLayoutY(33*(item1YPos + 1));
			ivItem2.setLayoutX(33*(item2XPos + 1));
			ivItem2.setLayoutY(33*(item2YPos + 1));

			//create grass background
			for (int row = 0; row < 9; row ++)
			{
				for (int col = 0; col < 11; col ++)
				{
					if (col % 2 != 0 && row % 2 != 0)	//alternates in the grid, creates solid blocks
					{
						Image solid = new Image("file:images/block_solid.png");
						blocks[row][col] = new Block(solid, Block.UNBREAKABLE);
					}
					else
					{
						int wall = rnd.nextInt(5);	//randomize chances of a breakable block
						if ((row == 0 && col == 0) ||
								(row == 1 && col == 0) ||
								(row == 0 && col == 1) ||
								(row == 7 && col == 10) ||
								(row == 8 && col != 7 && col != 8) ||
								(row == 0 && col != 2 && col != 3) ||
								(row == 4 && col >= 3 && col <= 7))
						{	//creates grass spaces to walk on
							Image grass = new Image("file:images/grass.png");
							blocks[row][col] = new Block(grass, Block.GRASS);
						}
						else if ((wall >= 0 && wall <= 2) || (col == 7 || col == 8) || (row == 0 && (col == 2 || col == 3))) 
						{	//creates breakable blocks
							Image breakableBlock = new Image("file:images/block_single.png");
							blocks[row][col] = new Block(breakableBlock, Block.BREAKABLE);
						}
						else 
						{
							blocks[row][col] = new Block(imgGrass, Block.GRASS);
						}
					}
					center.getChildren().add(blocks[row][col].getNode());	//adds the blocks to the center layout
				}
			}

			//left and right borders
			for(int i = 0; i < 9; i ++) 
			{ 
				Block leftSideBlock = new Block(new Image("file:images/block_solid.png")); 
				Block rightSideBlock = new Block(new Image("file:images/block_solid.png")); 
				leftVB.getChildren().add(leftSideBlock.getNode()); 
				rightVB.getChildren().add(rightSideBlock.getNode()); 
			} 
			//up and down borders
			for(int i = 0; i < 13; i ++)
			{ 
				Block topSideBlock = new Block(new Image("file:images/block_solid.png")); 
				Block bottomSideBlock = new Block(new Image("file:images/block_solid.png")); 
				topHB.getChildren().add(topSideBlock.getNode()); 
				bottomHB.getChildren().add(bottomSideBlock.getNode()); 
			} 

			background.setCenter(center); 
			background.setLeft(leftVB); 
			background.setRight(rightVB); 
			background.setTop(topHB); 
			background.setBottom(bottomHB); 
			background.getChildren().addAll(bomb[0].getNode(), bomb[1].getNode(), ivItem1, ivItem2, player1.getNode(), 
										player2.getNode(),monster[0].getNode(), monster[1].getNode(), monster[2].getNode());

			//key event	
			scene.setOnKeyPressed(e->{
				//checks the items and their collision with players
				checkItem(player1);
				checkItem(player2);
				
				//update health
				lblPlayer1.setText(name1 + " (HP: " + player1.getHealth() + ")");
				lblPlayer2.setText(name2 + " (HP: " + player2.getHealth() + ")");
				
				//player 1 controls
				checkSurround(player1);
				if (e.getCode() == KeyCode.W && player1.getTop()){	//up, and the block above is free
					player1.setGridY(player1.getGridY() - 1);
					player1.move(0, -33);
				}
				if (e.getCode() == KeyCode.A && player1.getLeft()){	//left, and the block to the left is free
					player1.setGridX(player1.getGridX() - 1);
					player1.move(-33, 0);
				}
				if (e.getCode() == KeyCode.S && player1.getDown()){
					player1.setGridY(player1.getGridY() + 1);
					player1.move(0, 33);
				}
				if (e.getCode() == KeyCode.D && player1.getRight()){
					player1.setGridX(player1.getGridX() + 1);
					player1.move(33, 0);
				}
				if (e.getCode() == KeyCode.SPACE){
					if (bombPresent[0] == false){
						bomb[0].setVisible(true);
						bomb[0].setLocation(player1.getX(), player1.getY());	//place bomb where player is
						bomb[0].setGridX(player1.getGridX());
						bomb[0].setGridY(player1.getGridY());
						bomb[0].setVisible(true);
						blocks[bomb[0].getGridY()][bomb[0].getGridX()].setProperty(imgGrass, Block.UNBREAKABLE);
						bombPresent[0] = true;
						bomb1Timer = 0;
						bombExplode[0] = false;
						tlBomb1Explode.play();	//start timer of bomb
					}
				}

				//player 2 controls
				checkSurround(player2);
				if (e.getCode() == KeyCode.UP && player2.getTop()){	//up, and the block above is free
					player2.setGridY(player2.getGridY() - 1);
					player2.move(0, -33);
				}
				if (e.getCode() == KeyCode.LEFT && player2.getLeft()){
					player2.setGridX(player2.getGridX() - 1);
					player2.move(-33, 0);
				}
				if (e.getCode() == KeyCode.DOWN && player2.getDown()){
					player2.setGridY(player2.getGridY() + 1);
					player2.move(0, 33);
				}
				if (e.getCode() == KeyCode.RIGHT && player2.getRight()){
					player2.setGridX(player2.getGridX() + 1);
					player2.move(33, 0);
				}
				if (e.getCode() == KeyCode.SHIFT){
					if (bombPresent[1] == false){
						bomb[1].setVisible(true);
						bomb[1].setLocation(player2.getX(), player2.getY());	//place bomb where player is
						bomb[1].setGridX(player2.getGridX());
						bomb[1].setGridY(player2.getGridY());
						bomb[1].setVisible(true);
						blocks[bomb[1].getGridY()][bomb[1].getGridX()].setProperty(imgGrass, Block.UNBREAKABLE);
						bombPresent[1] = true;
						bomb2Timer = 0;
						bombExplode[1] = false;
						tlBomb2Explode.play();	//start timer of bomb
					}
				}
			});

			//**********************************//


			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			do
			{
				TextInputDialog addPlayer1 = new TextInputDialog();
				addPlayer1 .setContentText("Please enter player 1's name:");
				addPlayer1.setHeaderText(null);
				addPlayer1.setTitle("Player 1");
				Optional<String> result1 = addPlayer1.showAndWait();

				if (result1.isPresent()) {
					lblPlayer1.setGraphic(iv1);
					name1 = result1.get();
					if (name1.equals(""))
					{
						// Alert to warn the player to enter a name
						Alert alert = new Alert(AlertType.INFORMATION);
						alert .setContentText("Please enter a name!");
						alert.setHeaderText(null);
						alert.setTitle("Player 1");
						alert.showAndWait();
					}
					else 
					{
						// Prompt for player 2's name
						TextInputDialog addPlayer2 = new TextInputDialog();
						addPlayer2 .setContentText("Please enter player 2's name:");
						addPlayer2.setHeaderText(null);
						addPlayer2.setTitle("Player 2");
						Optional<String> result2 = addPlayer2.showAndWait();
						if (result2.isPresent())
						{
							lblPlayer2.setGraphic(iv2);
							name2 = result2.get();
							music.play();
							if (name2.equals(""))
							{
								// warn user
								Alert alert = new Alert(AlertType.INFORMATION);
								alert .setContentText("Please enter a name!");
								alert.setHeaderText(null);
								alert.setTitle("Player 2");
								alert.showAndWait();
							}
							else 
							{
								lblPlayer1.setText(name1 + " (HP: " + player1.getHealth() + ")");
								lblPlayer2.setText(name2 + " (HP: " + player2.getHealth() + ")");
								inputCorrect = true;
								 Alert alert = new Alert(AlertType.INFORMATION); 
	                                alert .setContentText("Welcome to Mo Bomberman!\nPlayer 1 moves with W (up), S (down)," 
	                                        + " A (left), D (right) and SPACE to place bomb. Player 2 moves with Arrow keys and places bomb with SHIFT.Try to surive the Big Chungus monsters" 
	                                        + " and collect Health item.\nReminder: Some item can be placed on a block so you need to destroy the" 
	                                        + " block to collect it!"); 
	                                alert.setHeaderText(null); 
	                                alert.setTitle("Mo Bomberman"); 
	                                alert.showAndWait();
								tl.play();	
							}
						}
						else 
						{
							// warn user
							Alert alert = new Alert(AlertType.INFORMATION);
							alert .setContentText("Please enter a name!");
							alert.setHeaderText(null);
							alert.setTitle("Player 2");
							alert.showAndWait();
						}
					}		
				}
				else 
				{
					// warn user
					Alert alert = new Alert(AlertType.INFORMATION);
					alert .setContentText("Please enter a name!");
					alert.setHeaderText(null);
					alert.setTitle("Player 1");
					alert.showAndWait();
				}
			}
			while (inputCorrect == false);


		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//method to move monsters
	private void monsterMove(Entity monster) {

		if (blocks[monster.getGridY()][monster.getGridX()].getProperty() == Block.GRASS) {
			if (monster.getMonsterDirection() == Entity.RIGHT){
				monster.moveMonster(imgMonster, 33);
				monster.setGridX(monster.getGridX() + 1);
			}
			else if (monster.getMonsterDirection() == Entity.LEFT){
				monster.moveMonster(imgMonster, -33);
				monster.setGridX(monster.getGridX() - 1);
			}

			if (monster.getGridX() == 10 && monster.getMonsterDirection() == Entity.RIGHT){
				monster.setMonsterDirection(Entity.LEFT);
			}
			if (monster.getMonsterDirection() == Entity.RIGHT && 
					blocks[monster.getGridY()][monster.getGridX() + 1].isSolid()){
				monster.setMonsterDirection(Entity.LEFT);
			}
			if (monster.getGridX() == 0 && monster.getMonsterDirection() == Entity.LEFT) {
				monster.setMonsterDirection(Entity.RIGHT);
			}
			if (monster.getMonsterDirection() == Entity.LEFT && 
					blocks[monster.getGridY()][monster.getGridX() - 1].isSolid()){
				monster.setMonsterDirection(Entity.RIGHT);
			}
			monster.setX(monster.getX());
			monster.getNode().setLayoutX(monster.getX());
		}

	}

	private void checkItem(Entity player) {
		if (player.getGridX() == item1XPos && player.getGridY() == item1YPos) {
			background.getChildren().remove(ivItem1);
			item1XPos = 1000; 
			item1YPos = 1000;
			player.setHealth(player.getHealth() + 1);
		}
		if (player.getGridX() == item2XPos && player.getGridY() == item2YPos) {
			background.getChildren().remove(ivItem2);
			item2XPos = 1000; 
			item2YPos = 1000;
			player.setHealth(player.getHealth() + 1);
		}
	}
	//method to check surrounding blocks
	private void checkSurround(Entity input) {
		boolean top, left, down, right;
		if (input.getGridY() > 0){

			if (blocks[input.getGridY() - 1][input.getGridX()].isSolid())	//top block
				top = false;

			else
				top = true;
		}
		else
			top = false;

		if (input.getGridX() > 0){
			if (blocks[input.getGridY()][input.getGridX() - 1].isSolid())	//left block
				left = false;

			else
				left = true;
		}
		else
			left = false;

		if (input.getGridY() < 8){
			if (blocks[input.getGridY() + 1][input.getGridX()].isSolid())	//down block
				down = false;

			else
				down = true;
		}
		else
			down = false;

		if (input.getGridX() < 10){
			if (blocks[input.getGridY()][input.getGridX() + 1].isSolid())	//right block
				right = false;

			else
				right = true;
		}
		else
			right = false;

		input.setSurround(top, left, down, right);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
