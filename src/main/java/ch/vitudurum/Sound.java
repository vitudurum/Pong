package ch.vitudurum;

import java.io.File;
import java.util.Scanner;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

    //define storage for start position
    Long nowFrame;
    Clip clip;

    // get the clip status
    String thestatus;

    AudioInputStream audioStream;
    static String thePath = "pong.wav";

    // initialize both the clip and streams
    public Sound()
            throws UnsupportedAudioFileException,
            IOException, LineUnavailableException
    {
        // the input stream object
        audioStream =
                AudioSystem.getAudioInputStream(
                        new File(thePath)
                                .getAbsoluteFile());

        // the reference to the clip
        clip = AudioSystem.getClip();

        clip.open(audioStream);
        clip.loop(1);
      //  clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    // play
    public void play()
    {
        //start the clip

        clip.loop(1);
        //System.out.println("Play...");
       // clip.loop(0);
    }
}