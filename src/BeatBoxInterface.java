import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;

public interface BeatBoxInterface {

	public void buildGUI();

	public void setUpMidi();

	public void buildTrackAndStart();

	public void makeTracks(int[] list);

	public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick);
	
}
