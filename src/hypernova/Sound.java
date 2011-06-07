package hypernova;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;

import org.apache.log4j.Logger;

public class Sound {

    public static boolean enabled;
    private static Synthesizer synth;
    private static MidiChannel[] channels;

    private static Logger log = Logger.getLogger("Sound");

    public static void init() {
        try {
            log.info("Setting up MIDI");
            synth = MidiSystem.getSynthesizer();
            synth.open();
            enabled = true;
        } catch (Exception e) {
            log.warn("MIDI unavailable, sound disabled: " + e.getMessage());
            return;
        }
        channels = synth.getChannels();
        channels[0].programChange(32);
        log.info("MIDI setup complete");
    }

    public static void play(int channel, int note, int velocity) {
        if (enabled)
            channels[channel].noteOn(note, velocity);
    }
}
