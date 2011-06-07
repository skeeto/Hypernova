package hypernova;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;

public class Sound {

    public static boolean enabled;
    private static Synthesizer synth;
    private static MidiChannel[] channels;

    public static void init() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            enabled = true;
        } catch (Throwable t) {
            System.out.println("No midi available. Sound will be disabled.");
            return;
        }
        channels = synth.getChannels();
        channels[0].programChange(32);
    }

    public static void play(int channel, int note, int velocity) {
        if (enabled)
            channels[channel].noteOn(note, velocity);
    }
}