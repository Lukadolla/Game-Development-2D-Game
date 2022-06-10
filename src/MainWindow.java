import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

import util.Music;
import util.UnitTests;

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

public class MainWindow {
	 private static final JFrame frame = new JFrame("Hoth Defence");
	 private static final Model gameworld = new Model();
	 private static final Viewer canvas = new  Viewer(gameworld);
	 private final KeyListener Controller = new Controller();
	 private final Music Music = new Music(null, null);
	 private static final Music Music2 = new Music(null, null);
	 private String menuTheme = "C:\\Game Dev\\Assignment 1\\res\\sounds\\MenuTheme.wav";
	 private final String gameMusic = "C:\\Game Dev\\Assignment 1\\res\\sounds\\GameplayMusic.wav";
	 private static boolean startGame= false;
	 private JLabel BackgroundImageForStartMenu;

	private JLabel BackgroundImageForLoseMenu1;
	private JLabel BackgroundImageForLoseMenu2;
	private JLabel BackgroundImageForWinMenu;
	private static JPanel endPanel1 = new JPanel();
	private static JPanel endPanel2 = new JPanel();
	private static JPanel endPanel3 = new JPanel();



	  
	public MainWindow() {
	        frame.setSize(1000, 1000);  // you can customise this later and adapt it to change on size.
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //If exit // you can modify with your way of quitting , just is a template.
	        frame.setLayout(null);
	        frame.add(canvas);  
	        canvas.setBounds(0, 0, 1000, 1000);
	        canvas.setBackground(new Color(0,0,0)); //white background  replaced by Space background but if you remove the background method this will draw a white screen
			canvas.setVisible(false);   // this will become visible after you press the key.


			JButton startMenuButton = new JButton("Start Game");  // start button
			startMenuButton.setForeground(Color.YELLOW);
			startMenuButton.setFont(new Font("Arial", Font.BOLD, 20));
			startMenuButton.setBackground(Color.BLACK);
	        startMenuButton.addActionListener(new ActionListener()
	           { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					startMenuButton.setVisible(false);
					BackgroundImageForStartMenu.setVisible(false); 
					canvas.setVisible(true); 
					canvas.addKeyListener(Controller);    //adding the controller to the Canvas  
	            	canvas.requestFocusInWindow();   // making sure that the Canvas is in focus so keyboard input will be taking in .
					startGame=true;

					Music.stopSong();
					Music.playMusic(gameMusic);
				}});

	        startMenuButton.setBounds(400, 500, 200, 40);
	        
	        //loading background image 
	        File BackgroundToLoad = new File("res/startImage.jpg");  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
			File BackgroundToLoadEnd1 = new File("res/lose_hoth.jpg");
			File BackgroundToLoadEnd2 = new File("res/lose_space.jpg");
			File BackgroundToLoadEnd3 = new File("res/win_screen.png");
		try {
				 
				 BufferedImage myPicture = ImageIO.read(BackgroundToLoad);
				 BackgroundImageForStartMenu = new JLabel(new ImageIcon(myPicture));
				 BackgroundImageForStartMenu.setBounds(0, 0, 1000, 1000);
				 frame.add(BackgroundImageForStartMenu);

				 BufferedImage myEndPicture = ImageIO.read(BackgroundToLoadEnd1);
				 BackgroundImageForLoseMenu1 = new JLabel(new ImageIcon(myEndPicture));
				 endPanel1.setSize(1000, 1000);
				 endPanel1.setBackground(Color.black);
				 endPanel1.add(BackgroundImageForLoseMenu1);

				 BufferedImage myEndPicture1 = ImageIO.read(BackgroundToLoadEnd2);
				 BackgroundImageForLoseMenu2 = new JLabel(new ImageIcon(myEndPicture1));
				 endPanel2.setSize(1000, 1000);
				 endPanel2.setBackground(Color.black);
				 endPanel2.add(BackgroundImageForLoseMenu2);

				BufferedImage myEndPicture2 = ImageIO.read(BackgroundToLoadEnd3);
				BackgroundImageForWinMenu = new JLabel(new ImageIcon(myEndPicture2));
				endPanel3.setSize(1000, 1000);
				endPanel3.setBackground(Color.black);
				endPanel3.add(BackgroundImageForWinMenu);

				 Music.playMusic(menuTheme);
			}  catch (IOException e) { 
				e.printStackTrace();
			}

		     frame.add(startMenuButton);
			 frame.add(endPanel1);
			 frame.add(endPanel2);
			 frame.add(endPanel3);
			 endPanel1.setVisible(false);
			 endPanel2.setVisible(false);
			 endPanel3.setVisible(false);
	       	 frame.setVisible(true);
	}

	private void endMusic(){
		Music.stopSong();
	}

	public static void main(String[] args) {
		MainWindow mainWindow = new MainWindow();  //sets up environment
		String winMusic = "C:\\Game Dev\\Assignment 1\\res\\sounds\\WinMusic.wav";
		String loseMusic1 = "C:\\Game Dev\\Assignment 1\\res\\sounds\\LoseMusic1.wav";
		String loseMusic2 = "C:\\Game Dev\\Assignment 1\\res\\sounds\\LoseMusic2.wav";

		while(true)   //not nice but remember we do just want to keep looping till the end.  // this could be replaced by a thread but again we want to keep things simple 
		{ 
			//swing has timer class to help us time this but I'm writing my own, you can of course use the timer, but I want to set FPS and display it 

			int targetFPS = 100;
			int TimeBetweenFrames =  1000 / targetFPS;
			long FrameCheck = System.currentTimeMillis() + (long) TimeBetweenFrames;

			//wait till next time step
		 while (FrameCheck > System.currentTimeMillis()){}

			if(startGame)
				 {
				  gameloop();
				 }

			if(gameworld.getBaseHealth() <= 0){
				displayEndScreens(mainWindow, endPanel1);
				break;

			}

			else if(gameworld.getPlayer().getHealth() <= 0){
				displayEndScreens(mainWindow, endPanel2);
				break;

			}

			else if(gameworld.getWaveCounter() == 25){
				displayEndScreens(mainWindow, endPanel3);
				break;
			}

			//UNIT test to see if framerate matches 
		 UnitTests.CheckFrameRate(System.currentTimeMillis(),FrameCheck, targetFPS);
			  
		}

		if(gameworld.getWaveCounter() == 25){
			Music2.playMusic(winMusic);
		}

		if(gameworld.getBaseHealth() <= 0){
			Music2.playMusic(loseMusic1);
		}

		if(gameworld.getPlayer().getHealth() <= 0){
			Music2.playMusic(loseMusic2);
		}

	}

	private static void displayEndScreens(MainWindow hello, JPanel endPanel) {
		endPanel.setVisible(true);
		canvas.setVisible(false);

		Font font = new Font("Monospaced", Font.BOLD, 24);
		JLabel jlabel = new JLabel("Waves Cleared: " + gameworld.getWaveCounter());
		jlabel.setFont(font);
		jlabel.setForeground(Color.yellow);
		endPanel.add(jlabel);

		hello.endMusic();

		startGame = false;
	}

	//Basic Model-View-Controller pattern 
	private static void gameloop() { 
		// GAMELOOP  
		
		// controller input  will happen on its own thread 
		// So no need to call it explicitly

		// model update   
		gameworld.gamelogic();
		// view update 
		
		  canvas.updateview(); 
		
		// Both these calls could be setup as  a thread but we want to simplify the game logic for you.  
		//score update  
		 frame.setTitle("Hoth Defence");
		
		 
	}


}

/*
 * 
 * 

Hand shake agreement 
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,=+++
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,:::::,=+++????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,:++++????+??
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,:,,:,:,,,,,,,,,,,,,,,,,,,,++++++?+++++????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,=++?+++++++++++??????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,~+++?+++?++?++++++++++?????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:::,,,,,,,,,,,,,,,,,,,,,,,,,,,~+++++++++++++++????+++++++???????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,,,,,,,,,,,,,,,,,,,:===+=++++++++++++++++++++?+++????????????????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,~=~~~======++++++++++++++++++++++++++????????????????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,::::,,,,,,=~.,,,,,,,+===~~~~~~====++++++++++++++++++++++++++++???????????????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,,~~.~??++~.,~~~~~======~=======++++++++++++++++++++++++++????????????????II
:::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,:=+++??=====~~~~~~====================+++++++++++++++++++++?????????????????III
:::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,++~~~=+=~~~~~~==~~~::::~~==+++++++==++++++++++++++++++++++++++?????????????????IIIII
::::::::::::::::::::::::::::::::::::::::::::::::,:,,,:++++==+??+=======~~~~=~::~~===++=+??++++++++++++++++++++++++?????????????????I?IIIIIII
::::::::::::::::::::::::::::::::::::::::::::::::,,:+????+==??+++++?++====~~~~~:~~~++??+=+++++++++?++++++++++??+???????????????I?IIIIIIII7I77
::::::::::::::::::::::::::::::::::::::::::::,,,,+???????++?+?+++???7?++======~~+=====??+???++++++??+?+++???????????????????IIIIIIIIIIIIIII77
:::::::::::::::::::::::::::::::::::::::,,,,,,=??????IIII7???+?+II$Z77??+++?+=+++++=~==?++?+?++?????????????III?II?IIIIIIIIIIIIIIIIIIIIIIIIII
::::::::::::::::::::::::::::::,,,,,,~=======++++???III7$???+++++Z77ZDZI?????I?777I+~~+=7+?II??????????????IIIIIIIIIIIIIIIIIIIIII??=:,,,,,,,,
::::::::,:,:,,,,,,,:::~==+=++++++++++++=+=+++++++???I7$7I?+~~~I$I??++??I78DDDO$7?++==~I+7I7IIIIIIIIIIIIIIIIII777I?=:,,,,,,,,,,,,,,,,,,,,,,,,
++=++=++++++++++++++?+????+??????????+===+++++????I7$$ZZ$I+=~$7I???++++++===~~==7??++==7II?~,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
+++++++++++++?+++?++????????????IIIII?I+??I???????I7$ZOOZ7+=~7II?+++?II?I?+++=+=~~~7?++:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
+?+++++????????????????I?I??I??IIIIIIII???II7II??I77$ZO8ZZ?~~7I?+==++?O7II??+??+=====.,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
?????????????III?II?????I?????IIIII???????II777IIII7$ZOO7?+~+7I?+=~~+???7NNN7II?+=+=++,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
????????????IIIIIIIIII?IIIIIIIIIIII????II?III7I7777$ZZOO7++=$77I???==+++????7ZDN87I??=~,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
IIII?II??IIIIIIIIIIIIIIIIIIIIIIIIIII???+??II7777II7$$OZZI?+$$$$77IIII?????????++=+.,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII?+++?IIIII7777$$$$$$7$$$$7IIII7I$IIIIII???I+=,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII???????IIIIII77I7777$7$$$II????I??I7Z87IIII?=,,,,,,,,,,,:,,::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
777777777777777777777I7I777777777~,,,,,,,+77IIIIIIIIIII7II7$$$Z$?I????III???II?,,,,,,,,,,::,::::::::,,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
777777777777$77777777777+::::::::::::::,,,,,,,=7IIIII78ZI?II78$7++D7?7O777II??:,,,:,,,::::::::::::::,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
$$$$$$$$$$$$$77=:,:::::::::::::::::::::::::::,,7II$,,8ZZI++$8ZZ?+=ZI==IIII,+7:,,,,:::::::::::::::::,:::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
$$$I~::::::::::::::::::::::::::::::::::::::::::II+,,,OOO7?$DOZII$I$I7=77?,,,,,,:::::::::::::::::::::,,,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
::::::::::::::::::::::::::::::::::::::::::::::::::::::+ZZ?,$ZZ$77ZZ$?,,,,,::::::::::::::::::::::::::,::::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::I$:::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,
                                                                                                                             GlassGiant.com
 * 
 * 
 */
