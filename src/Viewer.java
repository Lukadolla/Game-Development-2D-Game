import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


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
 
 * Credits: Kelly Charles (2020)
 */

//Lukasz Filanowski 18414616
public class Viewer extends JPanel {
	private long CurrentAnimationTime= 0; 
	
	Model gameworld =new Model();
	Image background;
	Image bomber;
	Image bomberD1;
	Image bomberD2;
	Image tie;
	Image tieD1;
	Image tieD2;
	Image tieD3;
	Image tieD4;
	Image repair;
	Image asteroid;
	Image bullet;
	Image player;
	Image playerS;
	Image empty;
	Image explosion;
	Image enemyLaser;
	 
	public Viewer(Model World) {
		this.gameworld=World;
		preload();
		// TODO Auto-generated constructor stub
	}

	public Viewer(LayoutManager layout) {
		super(layout);
		preload();
		// TODO Auto-generated constructor stub
	}

	public Viewer(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		preload();
		// TODO Auto-generated constructor stub
	}

	public Viewer(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		preload();
		// TODO Auto-generated constructor stub
	}

	public void updateview() {
		
		this.repaint();
		// TODO Auto-generated method stub
		
	}

	private void preload() {

		File backgroundFile = new File("res/background.jpg");
		File bomberFile = new File("res/bomber.png");
		File bomberD1File = new File("res/bomber_damaged1.png");
		File bomberD2File = new File("res/bomber_damaged2.png");
		File asteroidFile = new File("res/Asteroid.png");
		File tieFile = new File("res/Tie.png");
		File tieD1File = new File("res/Tie_damaged1.png");
		File tieD2File = new File("res/Tie_damaged2.png");
		File tieD3File = new File("res/Tie_damaged3.png");
		File tieD4File = new File("res/Tie_damaged4.png");
		File bulletFile = new File("res/laser.png");
		File playerFile = new File("res/XWing.png");
		File repairFile = new File("res/repair.png");
		File playerSFile = new File("res/XWing-Shielded.png");
		File emptyFile = new File("res/empty.png");
		File explosionFile = new File("res/explosions.png");
		File enemyLaserFile = new File("res/laser_green.png");

		try{
			asteroid = ImageIO.read(asteroidFile);
			bomber = ImageIO.read(bomberFile);
			tie = ImageIO.read(tieFile);
			bomberD1 = ImageIO.read(bomberD1File);
			bomberD2 = ImageIO.read(bomberD2File);
			tieD1 = ImageIO.read(tieD1File);
			tieD2 = ImageIO.read(tieD2File);
			tieD3 = ImageIO.read(tieD3File);
			tieD4 = ImageIO.read(tieD4File);
			background = ImageIO.read(backgroundFile);
			bullet = ImageIO.read(bulletFile);
			player = ImageIO.read(playerFile);
			repair = ImageIO.read(repairFile);
			playerS = ImageIO.read(playerSFile);
			empty = ImageIO.read(emptyFile);
			explosion = ImageIO.read(explosionFile);
			enemyLaser = ImageIO.read(enemyLaserFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		CurrentAnimationTime++; // runs animation time step
		
		//Draw player Game Object 
		int x = (int) gameworld.getPlayer().getCentre().getX();
		int y = (int) gameworld.getPlayer().getCentre().getY();
		int width = (int) gameworld.getPlayer().getWidth();
		int height = (int) gameworld.getPlayer().getHeight();
		String texture = gameworld.getPlayer().getTexture();
		AtomicBoolean drawOnce = new AtomicBoolean(false);
		
		//Draw background 
		drawBackground(g);

		drawUI(g);

		//Draw player
		drawPlayer(x, y, width, height, texture,g);
		  
		//Draw Bullets 
		// change back 
		gameworld.getBullets().forEach((temp) -> 
		{ 
			drawBullet((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		}); 
		
		//Draw Enemies   

		gameworld.getEnemies().forEach((entity) ->
		{
			try {
				drawEnemies((int) entity.getCentre().getX(), (int) entity.getCentre().getY(), (int) entity.getWidth(), (int) entity.getHeight(),g, entity.getType(), entity.getHealth(), entity.getImageNumber());
			} catch (IOException e) {
				e.printStackTrace();
			}

		});

		if(!drawOnce.get()){
			gameworld.getDeadEnemy().forEach((entity) ->
			{
				drawOnce.set(true);
				drawExplosions((int) entity.getCentre().getX(), (int) entity.getCentre().getY(), (int) entity.getWidth(), (int) entity.getHeight(),g, entity.getType(), entity.getHealth(), entity.getImageNumber());

			});
		}

		drawOnce.set(false);
	}

	private void drawExplosions(int x, int y, int width, int height, Graphics g, String type, int health, int imageNumber)
	{
		//64 by 128
		int currentPositionInAnimation= ((int) (CurrentAnimationTime%4 )*128);
		g.drawImage(explosion, x,y, x+width, y+height, currentPositionInAnimation, 0, currentPositionInAnimation+127, 128, null);
	}
	
	private void drawEnemies(int x, int y, int width, int height, Graphics g, String type, int health, int imageNumber) throws IOException {

		//The sprite is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time
		//remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
		int currentPositionInAnimation1= ((int) (CurrentAnimationTime%4 )*32); //slows down animation so every 10 frames we get another frame so every 100ms
		//int currentPositionInAnimation2= ((int) (CurrentAnimationTime%4 )*128);

		if(type.equals("asteroid")){
			g.drawImage(asteroid, x,y, x+width, y+height, currentPositionInAnimation1  , 0, currentPositionInAnimation1+31, 32, null);
		}

		if(type.equals("bomber")){

			if(health == 1){
				if (imageNumber == 1){
					g.drawImage(bomberD1, x,y, x+width, y+height, 0, 0, 763, 630, null);
				}
			}

			if(health == 1){
				if (imageNumber == 2){
					g.drawImage(bomberD2, x,y, x+width, y+height, 0, 0, 763, 630, null);
				}
			}

			else{
				g.drawImage(bomber, x,y, x+width, y+height, 0, 0, 763, 630, null);
			}
		}

		if(type.equals("tie")){

			if(health == 1){

				if (imageNumber == 1){
					g.drawImage(tieD1, x,y, x+width, y+height, 0, 0, 529, 479, null);
				}

				if(imageNumber == 2){
					g.drawImage(tieD2, x,y, x+width, y+height, 0, 0, 529, 479, null);
				}

				if(imageNumber == 3){
					g.drawImage(tieD3, x,y, x+width, y+height, 0, 0, 529, 479, null);
				}

				if(imageNumber == 4){
					g.drawImage(tieD4, x,y, x+width, y+height, 0, 0, 529, 479, null);
				}
			}

			else{
				g.drawImage(tie, x,y, x+width, y+height, 0, 0, 529, 479, null);
			}

		}

		if(type.equals("repair")){
			g.drawImage(repair, x,y, x+width, y+height, 0, 0, 594, 675, null);
		}

		if(type.equals("enemy_laser")){
			g.drawImage(enemyLaser, x,y, x+width, y+height, 0, 0, 63, 127, null);
		}

		if(type.equals("bomber_laser")){
			g.drawImage(enemyLaser, x,y, x+width, y+height, 0, 0, 63, 127, null);
		}


	}

	private void drawBackground(Graphics g)
	{
		  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
		g.drawImage(background, 0,0, 1000, 1000, 0 , 0, 1000, 1000, null);

	}
	
	private void drawBullet(int x, int y, int width, int height, String texture,Graphics g)
	{

		//64 by 128
		g.drawImage(bullet, x,y, x+width, y+height, 0 , 0, 63, 127, null);

	}
	

	private void drawPlayer(int x, int y, int width, int height, String texture,Graphics g) {

		//The sprite is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time
		//remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
		//int currentPositionInAnimation= ((int) ((CurrentAnimationTime%40)/10))*38; //slows down animation so every 10 frames we get another frame so every 100ms

		if(gameworld.getPlayerShields() > 0){

			drawFlickerandPlayer(x, y, width, height, g);
		}

		if(gameworld.getPlayerShields() <= 0){

			drawFlickerandPlayer(x, y, width, height, g);
		}
		//g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer));
	}

	private void drawFlickerandPlayer(int x, int y, int width, int height, Graphics g) {
		if((gameworld.getInvulnerable()%10 == 0 || gameworld.getInvulnerable()%11 == 0 || gameworld.getInvulnerable()%12 == 0 || gameworld.getInvulnerable()%13 == 0) && gameworld.getInvulnerable() <= 100){
			g.drawImage(empty, x,y, x+width, y+height, 0, 0, 31, 31, null);
		}
		else if(gameworld.getPlayerShields() > 0){
			g.drawImage(playerS, x,y, x+width, y+height, 0, 0, 800, 650, null);
		}

		else{
			g.drawImage(player, x,y, x+width, y+height, 0, 0, 800, 650, null);
		}
	}

	private void drawUI(Graphics g){
		//Draw Base Health
		Font font = new Font("Monospaced", Font.BOLD, 24);
		g.setFont(font);
		g.setColor(Color.yellow);

		g.drawString("Hoth Base Health: " + gameworld.getBaseHealth(), 20, 40);

		//Draw player Health
		Font font2 = new Font("Monospaced", Font.BOLD, 17);
		g.setFont(font2);
		g.setColor(Color.red);

		g.drawString("X-Wing Health: " + gameworld.getPlayer().getHealth() + "/5" , 20, 750);

		//Draw player Health
		Font font3 = new Font("Monospaced", Font.BOLD, 17);
		g.setFont(font3);
		g.setColor(Color.blue);

		g.drawString("Shield Charge: " + gameworld.getPlayerShields(), 20, 770);

		//Draw Wave number
		Font font4 = new Font("Monospaced", Font.BOLD, 20);
		g.setFont(font4);
		g.setColor(Color.yellow);

		g.drawString("Wave: " + gameworld.getWaveCounter(), 850, 40);

		//Draw Instructions
		if(gameworld.getWaveCounter() == 0){
			Font font5 = new Font("Monospaced", Font.BOLD, 22);
			g.setFont(font5);
			g.setColor(Color.yellow);

			g.drawString("Use WASD/Arrow Keys to move", 300, 600);
			g.drawString("SPACE to shoot", 400, 650);
		}

		if(gameworld.getWaveCounter() == 1){
			Font font5 = new Font("Monospaced", Font.BOLD, 22);
			g.setFont(font5);
			g.setColor(Color.yellow);

			g.drawString("Survive the Empire's attack", 300, 600);
		}

	}


}


/*
 * 
 * 
 *              VIEWER HMD into the world                                                             
                                                                                
                                      .                                         
                                         .                                      
                                             .  ..                              
                               .........~++++.. .  .                            
                 .   . ....,++??+++?+??+++?++?7ZZ7..   .                        
         .   . . .+?+???++++???D7I????Z8Z8N8MD7I?=+O$..                         
      .. ........ZOZZ$7ZZNZZDNODDOMMMMND8$$77I??I?+?+=O .     .                 
      .. ...7$OZZ?788DDNDDDDD8ZZ7$$$7I7III7??I?????+++=+~.                      
       ...8OZII?III7II77777I$I7II???7I??+?I?I?+?+IDNN8??++=...                  
     ....OOIIIII????II?I??II?I????I?????=?+Z88O77ZZO8888OO?++,......            
      ..OZI7III??II??I??I?7ODM8NN8O8OZO8DDDDDDDDD8DDDDDDDDNNNOZ= ......   ..    
     ..OZI?II7I?????+????+IIO8O8DDDDD8DNMMNNNNNDDNNDDDNDDNNNNNNDD$,.........    
      ,ZII77II?III??????DO8DDD8DNNNNNDDMDDDDDNNDDDNNNDNNNNDNNNNDDNDD+.......   .
      7Z??II7??II??I??IOMDDNMNNNNNDDDDDMDDDDNDDNNNNNDNNNNDNNDMNNNNNDDD,......   
 .  ..IZ??IIIII777?I?8NNNNNNNNNDDDDDDDDNDDDDDNNMMMDNDMMNNDNNDMNNNNNNDDDD.....   
      .$???I7IIIIIIINNNNNNNNNNNDDNDDDDDD8DDDDNM888888888DNNNNNNDNNNNNNDDO.....  
       $+??IIII?II?NNNNNMMMMMDN8DNNNDDDDZDDNN?D88I==INNDDDNNDNMNNMNNNNND8:..... 
   ....$+??III??I+NNNNNMMM88D88D88888DDDZDDMND88==+=NNNNMDDNNNNNNMMNNNNND8......
.......8=+????III8NNNNMMMDD8I=~+ONN8D8NDODNMN8DNDNNNNNNNM8DNNNNNNMNNNNDDD8..... 
. ......O=??IIIIIMNNNMMMDDD?+=?ONNNN888NMDDM88MNNNNNNNNNMDDNNNMNNNMMNDNND8......
........,+++???IINNNNNMMDDMDNMNDNMNNM8ONMDDM88NNNNNN+==ND8NNNDMNMNNNNNDDD8......
......,,,:++??I?ONNNNNMDDDMNNNNNNNNMM88NMDDNN88MNDN==~MD8DNNNNNMNMNNNDND8O......
....,,,,:::+??IIONNNNNNNDDMNNNNNO+?MN88DN8DDD888DNMMM888DNDNNNNMMMNNDDDD8,.... .
...,,,,::::~+?+?NNNNNNNMD8DNNN++++MNO8D88NNMODD8O88888DDDDDDNNMMMNNNDDD8........
..,,,,:::~~~=+??MNNNNNNNND88MNMMMD888NNNNNNNMODDDDDDDDND8DDDNNNNNNDDD8,.........
..,,,,:::~~~=++?NMNNNNNNND8888888O8DNNNNNNMMMNDDDDDDNMMNDDDOO+~~::,,,.......... 
..,,,:::~~~~==+?NNNDDNDNDDNDDDDDDDDNNND88OOZZ$8DDMNDZNZDZ7I?++~::,,,............
..,,,::::~~~~==7DDNNDDD8DDDDDDDD8DD888OOOZZ$$$7777OOZZZ$7I?++=~~:,,,.........   
..,,,,::::~~~~=+8NNNNNDDDMMMNNNNNDOOOOZZZ$$$77777777777II?++==~::,,,......  . ..
...,,,,::::~~~~=I8DNNN8DDNZOM$ZDOOZZZZ$$$7777IIIIIIIII???++==~~::,,........  .  
....,,,,:::::~~~~+=++?I$$ZZOZZZZZ$$$$$777IIII?????????+++==~~:::,,,...... ..    
.....,,,,:::::~~~~~==+?II777$$$$77777IIII????+++++++=====~~~:::,,,........      
......,,,,,:::::~~~~==++??IIIIIIIII?????++++=======~~~~~~:::,,,,,,.......       
.......,,,,,,,::::~~~~==+++???????+++++=====~~~~~~::::::::,,,,,..........       
.........,,,,,,,,::::~~~======+======~~~~~~:::::::::,,,,,,,,............        
  .........,.,,,,,,,,::::~~~~~~~~~~:::::::::,,,,,,,,,,,...............          
   ..........,..,,,,,,,,,,::::::::::,,,,,,,,,.,....................             
     .................,,,,,,,,,,,,,,,,.......................                   
       .................................................                        
           ....................................                                 
               ....................   .                                         
                                                                                
                                                                                
                                                                 GlassGiant.com
                                                                 */
