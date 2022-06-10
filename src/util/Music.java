package util;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

//Lukasz Filanowski 18414616
//Code adapted from https://www.codegrepper.com/code-examples/java/how+to+control+clip+volume+java and https://stackoverflow.com/questions/6045384/playing-mp3-and-wav-in-java
public class Music {

    private AudioInputStream inputStream;
    private Clip clip;

    public Music(AudioInputStream inputStream, Clip clip) {
        this.clip = clip;
        this.inputStream = inputStream;
    }

    public void playMusic(String song) {
        try {
            inputStream = AudioSystem.getAudioInputStream(new File(song).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch(Exception ex) {
            System.out.println("Error when playing song.");
            ex.printStackTrace();
        }
    }

    public void stopSong(){
        clip.stop();
    }

    public void playSoundEffect(String effect){
        try {
            inputStream = AudioSystem.getAudioInputStream(new File(effect).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(inputStream);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            double gain = 1;
            float dB = (float) (Math.log(gain) / Math.log(20.0) * 40.0);
            gainControl.setValue(dB);

            clip.start();
        } catch(Exception ex) {
            System.out.println("Error when playing sound effect.");
            ex.printStackTrace();
        }
    }
}
