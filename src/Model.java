import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import util.GameObject;
import util.Music;
import util.Point3f;
import util.Vector3f; 
/*
 * Created by Abraham Campbell on 15/01/2020.
 *   Copyright (c) 2020  Abraham Campbell

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
   
   (MIT LICENSE ) e.g do what you want with this :-) 
 */

//Lukasz Filanowski 18414616
public class Model {
	
	 private final GameObject Player;
	 private final Controller controller = Controller.getInstance();
	 private final CopyOnWriteArrayList<GameObject> EntityList = new CopyOnWriteArrayList<GameObject>();
	 private final CopyOnWriteArrayList<GameObject> BulletList  = new CopyOnWriteArrayList<GameObject>();
	 private final CopyOnWriteArrayList<GameObject> DeadEnemyList  = new CopyOnWriteArrayList<GameObject>();
	 private final util.Music Music = new Music(null, null);
	 private int baseHealth = 12;
	 private int waveCounter = 0;
	 private int playerShields = 0;

	 private int invulnerable = 120;
	 private int explosionTime = 15;
	 private int formationSize = 10;

	 String explosion = "res/sounds/Explosion.wav";

	public Model() {
		 //setup game world
		//Player 
		Player= new GameObject("res/XWing.png",55,55,new Point3f(500,600,0), "player", 5, 0, 0);
		//Enemies  starting with four
		EntityList.add(new GameObject("res/Asteroid.png",60,60,new Point3f( 250,0,0), "asteroid", 2, 0, 0));
		EntityList.add(new GameObject("res/Asteroid.png",60,60,new Point3f(550,0,0), "asteroid", 2, 0, 0));
		EntityList.add(new GameObject("res/Asteroid.png",60,60,new Point3f(410,0,0), "asteroid", 2, 0, 0));
		EntityList.add(new GameObject("res/Asteroid.png",60,60,new Point3f(760,0,0), "asteroid", 2, 0, 0));
		
		
	    
	}
	
	// This is the heart of the game , where the model takes in all the inputs ,decides the outcomes and then changes the model accordingly. 
	public void gamelogic() 
	{
		// Player Logic first 
		playerLogic(); 
		// Enemy Logic next
		enemyLogic();
		// Bullets move next 
		bulletLogic();
		// interactions between objects 
		gameLogic(); 
	   
	}

	private void gameLogic() {

		// this is a way to increment across the array list data structure
		invulnerable++;
		explosionTime++;

		//see if they hit anything 
		// using enhanced for-loop style as it makes it alot easier both code wise and reading wise too 
		for (GameObject entity : EntityList)
		{
			//Collision with enemy or laser
			if(Math.abs(entity.getCentre().getX()- Player.getCentre().getX())< entity.getWidth()
					&& Math.abs(entity.getCentre().getY()- Player.getCentre().getY()) < entity.getHeight() && !(entity.getType().equals("repair"))){

				if(getPlayerShields() > 0 && getInvulnerable() >= 120){
					invulnerable = 0;
					setPlayerShields(getPlayerShields() - 1);
					DeadEnemyList.add(entity);
					explosionTime = 0;
					Music.playSoundEffect(explosion);
					EntityList.remove(entity);
				}


				if(getInvulnerable() >= 120){
					invulnerable = 0;
					DeadEnemyList.add(entity);
					explosionTime = 0;
					Music.playSoundEffect(explosion);
					Player.setHealth(Player.getHealth() - 1);
					EntityList.remove(entity);
				}

			}

			if((Math.abs(entity.getCentre().getX()- Player.getCentre().getX())< entity.getWidth()
					&& Math.abs(entity.getCentre().getY()- Player.getCentre().getY()) < entity.getHeight()) && entity.getType().equals("repair")) {

				String r2Sound = "C:\\Game Dev\\Assignment 1\\res\\sounds\\r2.wav";
				Music.playSoundEffect(r2Sound);

				if(Player.getHealth() < 5){
					Player.setHealth(Player.getHealth() + 1);
				}
				
				else{
					setPlayerShields(getPlayerShields() + 1);
				}
				
				EntityList.remove(entity);
			}


		for (GameObject Bullet : BulletList) 
		{
			if ( Math.abs(entity.getCentre().getX() - Bullet.getCentre().getX())< entity.getWidth()
				&& Math.abs(entity.getCentre().getY() - Bullet.getCentre().getY()) < entity.getHeight() && !(entity.getType().equals("repair") || entity.getType().equals("enemy_laser") || entity.getType().equals("bomber_laser")))
			{
				entity.setHealth(entity.getHealth() - 1);


				if(entity.getHealth() <= 0){

					DeadEnemyList.add(entity);
					Music.playSoundEffect(explosion);
					explosionTime = 0;

					EntityList.remove(entity);
				}
				BulletList.remove(Bullet);
			}
		}
		}

		DeadEnemyList.removeIf(entity -> getExplosionTime() >= 20);
		
	}

	private void enemyLogic() {
		// TODO Auto-generated method stub
		for (GameObject entity : EntityList)
		{
		    // Move enemies
			entity.setEnemyShootDelay(entity.getEnemyShootDelay() + 1);

			switch (entity.getType()) {
				case "asteroid":
					entity.getCentre().ApplyVector(new Vector3f((1 * (-1 + new Random().nextDouble() * (1 - (-1)))), new Random().nextDouble() * (3 - 0.5) * -1, 0));
					break;
				case "tie":
					entity.getCentre().ApplyVector(new Vector3f(0, -1.2, 0));
					break;
				case "repair":
					entity.getCentre().ApplyVector(new Vector3f(0, -1.5, 0));
					break;
				case "enemy_laser":
					entity.getCentre().ApplyVector(new Vector3f(0,  -4.5, 0));
					break;
				case "bomber_laser":
					entity.getCentre().ApplyVector(new Vector3f(0,  -3.5, 0));
					break;
				case "bomber":
					entity.getCentre().ApplyVector(new Vector3f(0, -1.0, 0));
					break;
			}

			if((checkRange((int)entity.getCentre().getX(), (int)Player.getCentre().getX())) && (entity.getType().equals("tie") ||  entity.getType().equals("bomber")) && (int)entity.getCentre().getY() < (int)Player.getCentre().getY() && entity.getEnemyShootDelay() >= 95){
				String tieShootSound = "C:\\Game Dev\\Assignment 1\\res\\sounds\\TIE-Fire.wav";
				entity.setEnemyShootDelay(0);
				Music.playSoundEffect(tieShootSound);

				if(entity.getType().equals("tie")){
					EntityList.add(new GameObject("res/laser_green.png", 16, 32, new Point3f(entity.getCentre().getX()+28,entity.getCentre().getY()+24,0), "enemy_laser", 0, 0, 0));
					EntityList.add(new GameObject("res/laser_green.png", 16, 32, new Point3f(entity.getCentre().getX()+12,entity.getCentre().getY()+24,0), "enemy_laser", 0, 0, 0));
				}

				else if(entity.getType().equals("bomber")){
					EntityList.add(new GameObject("res/laser_green.png", 16, 32, new Point3f(entity.getCentre().getX()+28,entity.getCentre().getY()+34,0), "bomber_laser", 0, 0, 0));
				}
			}
			 
			//see if they get to the top of the screen ( remember 0 is the top 
			if (entity.getCentre().getY()==900.0f)  // current boundary need to pass value to model
			{
				EntityList.remove(entity);

				if(entity.getType().equals("tie")){
					// enemies get to end so base health decreased
					baseHealth--;
				}

				if(entity.getType().equals("bomber")){
					baseHealth = baseHealth - 2;
				}

			} 
		}

		if(EntityList.size() == 0) {
				int trackNumber = 1 + (int) (Math.random() * ((4 - 1) + 1));
				setWaveCounter(getWaveCounter() + 1);
				if (trackNumber == 1 && getWaveCounter() % 5 != 0) {
					String tieFlySound1 = "C:\\Game Dev\\Assignment 1\\res\\sounds\\TIE-Fly1.wav";
					Music.playSoundEffect(tieFlySound1);
				}

				if (trackNumber == 2 && getWaveCounter() % 5 != 0) {
					String tieFlySound2 = "C:\\Game Dev\\Assignment 1\\res\\sounds\\TIE-Fly2.wav";
					Music.playSoundEffect(tieFlySound2);
				}

				if (trackNumber == 3 && getWaveCounter() % 5 != 0) {
					String tieFlySound3 = "C:\\Game Dev\\Assignment 1\\res\\sounds\\TIE-Fly6.wav";
					Music.playSoundEffect(tieFlySound3);
				}
				if (trackNumber == 4 && getWaveCounter() % 5 != 0) {
					String tieFlySound4 = "C:\\Game Dev\\Assignment 1\\res\\sounds\\TIE-Fly7.wav";
					Music.playSoundEffect(tieFlySound4);
				}

			while (EntityList.size() < getFormationSize()) {
				//spawn power up
				if (getWaveCounter()%5 == 0) {
					EntityList.add(new GameObject("res/repair.png", 32, 32, new Point3f(((float) Math.random() * 1000), 0, 0), "repair", 0, 0, 0));
					break;
				} else {

					int formationNumber = 1 + (int) (Math.random() * ((11 - 1) + 1));

					//SPAWN ENEMIES
					if(getWaveCounter() < 10 && (formationNumber == 10 || formationNumber == 11)){
						formationNumber = 5;
					}

					if(getWaveCounter() > 14){
						formationNumber = formationNumber+4;

						if(formationNumber > 11){
							int diff = formationNumber - 11;
							formationNumber = formationNumber - diff;
						}
					}
					spawnFormation(formationNumber);
				}
			}
		}
		}

	private void spawnFormation(int formation){

		//2 and 2
		if(formation == 1){
			setFormationSize(4);
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(650,-70,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(350,-70,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(550,-70,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(450,-70,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
		}

		//3 V and 1
		if(formation == 2){
			setFormationSize(4);
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(600,-80,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(400,-80,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(500,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(500,-80,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
		}

		//Reverse 3 V
		if(formation == 3){
			setFormationSize(3);
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(500,-80,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(600,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(400,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
		}

		//Diamond
		if(formation == 4){
			setFormationSize(4);
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(500,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(500,-130,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(600,-70,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(400,-70,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
		}

		//5 V and 2
		if(formation == 5){
			setFormationSize(7);
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(500,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(600,-80,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(400,-80,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(300,-150,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(700,-150,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(450,-150,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(550,-150,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
		}

		//5 reverse V
		if(formation == 6){
			setFormationSize(5);
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(500,-150,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(600,-80,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(400,-80,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(300,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(700,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
		}

		//5 line with lead at front and staggered back bomber
		if(formation == 7){
			setFormationSize(8);
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(500,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(650,-50,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(750,-50,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(350,-50,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(250,-50,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(500,-80,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(600,-100,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(400,-100,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
		}

		//5 line
		if(formation == 8){
			setFormationSize(5);
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(500,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(650,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(800,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(350,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(200,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
		}

		//Double 3 V
		if(formation == 9){
			setFormationSize(8);
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(700,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(800,-70,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(600,-70,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(700,-70,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));


			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(300,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(400,-70,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(200,-70,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(300,-70,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));

		}

		//Doubled up line
		if(formation == 10){
			setFormationSize(9);
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(500,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 100));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(650,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 100));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(800,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 100));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(350,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 100));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(200,-10,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 100));

			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(575,-100,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 100));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(725,-100,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 100));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(425,-100,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 100));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(275,-100,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 100));
		}

		//2 bomber wings
		if(formation == 11){
			setFormationSize(9);
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(800,-90,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(600,-90,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(700,-10,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(550,-40,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(450,-40,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));
			EntityList.add(new GameObject("res/Tie.png",54,54,new Point3f(500,0,0), "tie", 3, 1 + (int) (Math.random() * ((4 - 1) + 1)), 95));

			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(400,-90,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(200,-90,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
			EntityList.add(new GameObject("res/bomber.png",54,54,new Point3f(300,-10,0), "bomber", 5, 1 + (int) (Math.random() * ((2 - 1) + 1)), 120));
		}


	}

	private void bulletLogic() {
		// TODO Auto-generated method stub
		// move bullets 
	  
		for (GameObject temp : BulletList) 
		{
		    //check to move them
			  
			temp.getCentre().ApplyVector(new Vector3f(0,4.6,0));
			//see if they hit anything 
			
			//see if they get to the top of the screen ( remember 0 is the top 
			if (temp.getCentre().getY() <= 0)
			{
			 	BulletList.remove(temp);
			} 
		} 
		
	}

	private void playerLogic() {
		
		// smoother animation is possible if we make a target position  // done but may try to change things for students  
		 
		//check for movement and if you fired a bullet

		  
		if(Controller.getInstance().isKeyAPressed() || Controller.getInstance().isKeyLeftPressed()){Player.getCentre().ApplyVector( new Vector3f(-2.7,0,0)); }
		
		if(Controller.getInstance().isKeyDPressed() || Controller.getInstance().isKeyRightPressed())
		{
			Player.getCentre().ApplyVector( new Vector3f(2.7,0,0));
		}
			
		if(Controller.getInstance().isKeyWPressed() || Controller.getInstance().isKeyUpPressed())
		{
			Player.getCentre().ApplyVector( new Vector3f(0,2.7,0));
		}
		
		if(Controller.getInstance().isKeySPressed() || Controller.getInstance().isKeyDownPressed()){Player.getCentre().ApplyVector( new Vector3f(0,-2.7,0));}
		
		if(Controller.getInstance().isKeySpacePressed())
		{
			CreateBullet();
			String xwingShootSound = "C:\\Game Dev\\Assignment 1\\res\\sounds\\XWing-Laser.wav";
			Music.playSoundEffect(xwingShootSound);
			Controller.getInstance().setKeySpacePressed(false);
		} 
		
	}

	private void CreateBullet() {
		BulletList.add(new GameObject("res/laser.png",16,32,new Point3f(Player.getCentre().getX()+44,Player.getCentre().getY()-8,0.0f), "bullet", 0,  0, 0));
		BulletList.add(new GameObject("res/laser.png",16,32,new Point3f(Player.getCentre().getX()-8,Player.getCentre().getY()-8,0.0f), "bullet", 0, 0, 0));
	}

	private boolean checkRange(int enemyX, int playerX){
		for(int i = enemyX; i <= enemyX + 9; i++){
				if(i == playerX){
					return true;
				}
			}

		for(int j = enemyX; j <= enemyX - 9; j++){
				if(j == playerX){
					return true;
				}
			}

		return false;
	}

	public GameObject getPlayer() {
		return Player;
	}

	public CopyOnWriteArrayList<GameObject> getEnemies() {
		return EntityList;
	}

	public CopyOnWriteArrayList<GameObject> getDeadEnemy() {
		return DeadEnemyList;
	}
	
	public CopyOnWriteArrayList<GameObject> getBullets() {
		return BulletList;
	}

	public int getBaseHealth() {
		return baseHealth;
	}

	public int getInvulnerable() {
		return invulnerable;
	}

	public void setInvulnerable(int invulnerable) {
		this.invulnerable = invulnerable;
	}

	public int getWaveCounter() {
		return waveCounter;
	}

	public void setWaveCounter(int waveCounter) {
		this.waveCounter = waveCounter;
	}

	public int getPlayerShields() {
		return playerShields;
	}

	public void setPlayerShields(int playerShields) {
		this.playerShields = playerShields;
	}

	public int getExplosionTime() {
		return explosionTime;
	}

	public void setExplosionTime(int explosionTime) {
		this.explosionTime = explosionTime;
	}

	public int getFormationSize() {
		return formationSize;
	}

	public void setFormationSize(int formationSize) {
		this.formationSize = formationSize;
	}

}


/* MODEL OF your GAME world 
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWNNNXXXKKK000000000000KKKXXXNNNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWNXXK0OOkkxddddooooooolllllllloooooooddddxkkOO0KXXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWNXK0OkxddooolllllllllllllllllllllllllllllllllllllllloooddxkO0KXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0OkdooollllllllooddddxxxkkkOOOOOOOOOOOOOOOkkxxdddooolllllllllllooddxO0KXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kxdoollllllloddxkO0KKXNNNNWWWWWWMMMMMMMMMMMMMWWWWNNNXXK00Okkxdoollllllllloodxk0KNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXKOxdooolllllodxkO0KXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNXK0OkxdolllllolloodxOKXWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOxdoolllllodxO0KNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXKOkdolllllllloodxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdolllllooxk0KNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kdolllllllllodk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xdolllllodk0XNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWMMMMMMMMMMMWN0kdolllllllllodx0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xoollllodxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWMMMMMMMMMMWNXKOkkkk0WMMMMMMMMMMMMWNKkdolllloololodx0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0kdolllllox0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0kxk0KNWWWWNX0OkdoolllooONMMMMMMMMMMMMMMMWXOxolllllllollodk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdollllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0xooollloodkOOkdoollllllllloxXWMMMMMMMMMMMMMMMWXkolllllllllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0koolllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllllox0XWWMMMMMMMMMWNKOdoloooollllllllllllok0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xoolllllllllllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxllolllllllllllllllllloollllllolodxO0KXNNNXK0kdoooxO0K0Odolllollllllllox0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdolllllllllllllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllllllllloolllllllllllllllllllolllloddddoolloxOKNWMMMWNKOxdolollllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMWXOdolllolllllllllllllloxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxlllolllllloxkxolllllllllllllllllolllllllllllllxKWMWWWNNXXXKKOxoollllllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMWXOdollllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollllllllllxKNKOxooollolllllllllllllllllllolod0XX0OkxdddoooodoollollllllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMN0xollllllllllllllllllllllld0NMMMMMMMMMMMMMMMMMMMMMMMWWNKKNMMMMMMMMMMMW0dlllllllllokXWMWNKkoloolllllllllllllllllllolokkxoolllllllllllllollllllllllllllox0NMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllloONMMMMMMMMMMMMMMMMMMMWNKOxdookNMMMMMMMMMWXkollllllodx0NWMMWWXkolooollllllllllllllllllllooollllllllllllllolllllllllllloooolloxKWMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMWXOdllllllllllllllooollllllllollld0WMMMMMMMMMMMMMMMMWXOxollllloOWMMMMMMMWNkollloodxk0KKXXK0OkdoollllllllllllllllllllllllllllllollllllllloollllllollllllllllllldOXWMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMN0xolllllllllllolllllllllllloodddddONMMMMMMMMMMMMMMMNOdolllllllokNMMMMMMWNkolllloddddddoooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllox0NMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMWXkolllllllllllllllllllodxxkkO0KXNNXXXWMMMMMMMMMMMMMMNkolllllllllod0NMMMMMNOollllloollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllolllllllllllllokXWMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMWKxollllllllllllllllllox0NWWWWWMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllookKNWWNOolollloolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxKWMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMN0dlllllllllllllllllllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkoloolllollllolloxO0Odllllllllllllllllllllllllllllllllllllllllllllollllllllllllllllllllllllllllllllllllllllllllllld0NWMMMMMMMMMMMMM
MMMMMMMMMMMMMXkolllllllllllllllllolllxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXOO0KKOdollllllllllooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloONWMMMMMMMMMMMM
MMMMMMMMMMMWXkollllllllllllllllllllllxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWMMMMWNKOxoollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMM
MMMMMMMMMMWKxollllllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWKxollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxKWMMMMMMMMMM
MMMMMMMMMWKxollllllllllllodxkkkkkkkO0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMNKOkO0KK0OkdolllllloolllllllllllloooollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxKWMMMMMMMMM
MMMMMMMMWKxllllllllllolodOXWWWWWWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxolloooollllllllllllllllloollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxKWMMMMMMMM
MMMMMMMWKxlllllllllollokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxololllllllooolloollllloolloooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxKWMMMMMMM
MMMMMMWXxllllllllooodkKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKdloollllllllllololodxxddddk0KK0kxxxdollolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXWMMMMMM
MMMMMMXkolllllodk0KXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKdllollllllllllllodOXWWNXXNWMMMMWWWNX0xolollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokNMMMMMM
MMMMMNOollllodONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dooollllllllllllodOXNWWWWWWMMMMMMMMMWXOddxxddolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloONMMMMM
MMMMW0dllllodKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKKK0kdlllllllllllloodxxxxkkOOKNWMMMMMMWNNNNNXKOkdooooollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllld0WMMMM
MMMWKxllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllllollllllllllllllllodOKXWMMMMMMMMMMMMWNXKK0OOkkkxdooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxKWMMM
MMMNkollllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWXOdlllllolllllllllllloloolllooxKWMMMMMMMMMMMMMMMMMMMMWWWNXKOxoollllllllllllllllllllllllllllllllllllllllllllllllllllllllllolokNMMM
MMW0ollllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOOkxdollllllllllllllllllllllllllllox0NWMMMMWWNNXXKKXNWMMMMMMMMMWNKOxolllolllllllllllllllllllllllllllllllllllllllllllllllllllllllo0WMM
MMXxllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXkolllllllllllllllllllllllllllllllllllooxO000OkxdddoooodkKWMMMMMMMMMMWXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXWM
MWOollllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXkollllllllllllllllllllllllllllllllllllllllllllllllllllllld0WMMMMMMMMMWKdlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloOWM
MXxllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXkollllllllllllllllllllllllllllllllllooollllllllllllllllllold0WMMMMMMWN0dolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXM
W0ollllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNKkdolllllllllllllllllllllllllllllllllllllllllllllllllolllllllllokKXNWWNKkollllllllloxdollllllllolllllllllllllllllllllllllolllllllllllllolo0W
NkllllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllodxkkdoolollllllllxKOolllllllllllllllllllllllollooollllllloolllllloolllllkN
KxllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0doolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllkX0dlllllllllllllllllllloollloOKKOkxdddoollllllllllllllxK
Oolllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXXkollllooolllllllllllllllloONMMMWNNNXX0xolllllllllolloO
kolllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXWXkollollllllllllllllllllodKMMMMMMMMMMWKxollllolollolok
kllllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloolllllllllxXWWXkolllllllllllllllolllloONMMMMMMMMMMMW0dllllllllllllk
xollolld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloollllllolloONMMN0xoolllllllolllllllloxXWMMMMMMMMMMMMXxollllllloollx
dollllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloollld0WMMWWXOdollollollllllloxXWMMMMMMMMMMMMMNOollllllokkold
olllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNxlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllldONMMMMWXxollllolllllox0NWMMMMMMMMMMMMMMNOollllllxXOolo
llllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloONMMMMMXxddxxxxkkO0XWMMMMMMMMMMMMMMMMMNOolllllxKW0olo
llllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllldONWMMMWNXNNWWWMMMMMMMMMMMMMMMMMMMMMMMW0dllollOWW0oll
llllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxO0KXXXXKKKXNWMMMMMMMMMMMMMMMMMMMMMMMNOdolllkNWOolo
ollllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllooooddooloodkKWMMMMMMMMMMMMMMMMMMMMMMWXOolldKNOooo
dollllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloollllo0WMMMMMMMMMMMMMMMMMMMMMMMMXkold0Nkold
xollllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllollokNMMMMMMMMMMMMMMMMMMMMMMMMMWOookXXxolx
xolllllloONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMN00XW0dlox
kollllllloxOKXXNNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOxollllllllllllllllllllllllllllllolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllolllllolo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWOollk
OolllllllllloodddkKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOkkxddooooollllllllllooodxxdollolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkoloO
KdllllllllllllllllxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNXXXK0OOkkkkkkkkOKXXXNNX0xolllllllllllllllllllllllllllllllllllllllllllllllllllllllloollllllllloox0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKdlldK
NkllllollloolllllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWWWMMMMMMMMMWNOdlllllllllllllllllllllllllllllllllllllllllllllllllllllllollllllllodOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWOolokN
WOolllllllllllolllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllod0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXxolo0W
WXxllllllllllllllllox0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxollllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollxXM
MWOollllllllllllllooloxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdllllllllllllllllllllllllllllllllllllllllllllllllllllloolld0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlloOWM
MWXxllolllllllllllllllldOXWWNNK00KXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllllllllllllllllllllllllllllllllllllllllllllllllllod0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxollxXWM
MMWOollllllllloollllllolodxkxdollodk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollllllllllllllllllllllllllllllllllllllllllllllllllodxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWN0dlllo0WMM
MMMXxllolllllllllllllllllllllllllllloox0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0dooollllllllllllllllllllllllllllllllllllllllllllodOXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKOkxxolllokNMMM
MMMW0dlllllllllllllllllllolllllllollollokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdoolllllllllllllllllllllllllllllllllllllllllllxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNOoollllllldKWMMM
MMMMNOollllllllllllllllllllllllllllllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXKOdolllllllllllllllllllllllllllllllllllllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllloOWMMMM
MMMMMXkollllllllllllllllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllllllllllllllllllllllllllllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllolllllokNMMMMM
MMMMMWXxlllllllllllllllllllllllllllllllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0ollllllllllllllllllllllllllllllllllllllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWOollllllllxXWMMMMM
MMMMMMWKdlllllllllllllllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxolllllllllllllllllllllllllllllllllllllloONWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllxKWMMMMMM
MMMMMMMW0dlllllllllllllllllllllllllllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllloollllllllllllllllllllllllllllllllloxkOKKXXKKXNMMMMMMMMMMMMMMMMMMMMMMMMNOolllllldKWMMMMMMM
MMMMMMMMW0dllllllllllllllllllllllllllllldKMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlllllllllllllllllllllllllllllllllllllllllllloooood0WMMMMMMMMMMMMMMMMMMMMMMMNOollolldKWMMMMMMMM
MMMMMMMMMW0dlllllllllllllllllllllllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllllllllllllllllllllllllllolllllllllllllllllld0WMMMMMMMMMMMMMMMMMMMMMMWKxllllldKWMMMMMMMMM
MMMMMMMMMMW0dlllllllllllllllllllllllllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllllllllllllllllllllllllllllllllllllllllllllllllxXMMMMMMMMMMMMMMMMMMMMMWXOdolllldKWMMMMMMMMMM
MMMMMMMMMMMWKxollllllllllllllllllllllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllllllllllllllllllllllloolllllllolllollllkNMMMMMMMMMMMMMMMMMMMWXOdolllloxKWMMMMMMMMMMM
MMMMMMMMMMMMWKxollllllllllllllllllllllllod0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkoloollllllllllllllllllllllllllllloddollllllllllllld0WMMMMMMMMMMMMMMMWWNKOdolllllokXWMMMMMMMMMMMM
MMMMMMMMMMMMMWXkollllllllllllllllllllllllldKMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllollllllllllllllllllllllllllllld0XOollllllllllllkNMMMMMMMMMMMMWNK0OkxollllllloONWMMMMMMMMMMMMM
MMMMMMMMMMMMMMMNOdlllllllllllllllllllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0dlllllllllllllllllllllllllolllld0NWN0dlllllloodxkKWMMMMMMMMMMMMNOollllllllllld0NMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMWKxolollllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllllllllllllllllllllllldONMMMWKkdoooxOXNNWMMMMMMMMMMMMMNOollllllllllokXWMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMWXOdlllllllllllllllllloONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdllllllllllllllllllllllllllld0NMMMMMWWXXXXNWMMMMMMMMMMMMMMMMW0dlllllllllod0NMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMWKxollolllllllllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllllllllllllllllllllllloxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllokXWMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMWNOdollllllloolllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllloollllooolllllllllodONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkllllllolox0NMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMWXkollllllolllllox0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlllllollllllllllllllodkKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllodOXWMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMWKxoolllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0dollllllllllooddxxk0KNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdollllldOXWMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMN0xolllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllodk0KXNNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKkdollolodkXWMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMNKxoolllllllllodOKNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdolldOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xoollllodkXWMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNKkolllollllllloxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOx0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOdolllllldOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKOdollllllllllodx0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOdoollllloxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xollollollollodxOXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdooollllodk0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKkdooolllllllllooxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOxdollllllloxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0kdllllllllollllodkOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWXKOkdoolllllloodOKNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdolllllllllllllodxO0XNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNX0OxdollloolllloxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdoolllllllllllllooxkO0XNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWNX0OkxoololllllllooxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kdoolllllllllllloooodxkO0KXNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNXK0Okxdoolllllollllloxk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOkdoollllllllloolllllloodxkkO00KXXNNWWWWWWMMMMMMMMMWWWWWWWNNXXK00Okxxdoolllllllllllloooxk0KNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kxdoollllllllllllllllllllloodddxxxkkOOOOOOOOOOOkkkxxxdddoollllllllllllllllloodxO0XNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0OxdooollllllllllllooolllllllllllllllllllllllllllllllllllllllllllooodkO0KNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0OkxdooollllllllllllllllllllllllllllllllllllllllllloooddxkO0KXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNNXK0OOkkxdddoooooollllllllllllllllooooooddxxkOO0KKXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNNXXXKK00OOOOOOOOOOOOOOOO00KKXXXNNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 */

