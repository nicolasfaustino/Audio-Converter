package com.audio_corverter.demo.controller;

import javax.sound.sampled.*;
import java.io.File;

public class to16kMonoPcm {

    public static File ConvertFiles(File input) throws Exception {
        System.out.println(input);
        try (var s = AudioSystem.getAudioInputStream(input)) {
            System.out.println("Formato de entrada: " + s.getFormat());
        } catch (Exception e) {
            System.out.println("Nao abriu no JavaSound: " + e);
        }

        try (AudioInputStream source = AudioSystem.getAudioInputStream(input)) {
            AudioFormat base = source.getFormat();

            AudioFormat pcm = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    base.getSampleRate(),
                    16,
                    base.getChannels(),
                    base.getChannels() * 2,
                    base.getSampleRate(),
                    false
            );

            try (AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcm, source)) {

                AudioFormat target = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        16000,
                        16,
                        1,
                        2,
                        16000,
                        false
                );

                try (AudioInputStream converted = AudioSystem.getAudioInputStream(target, pcmStream)) {
                    File out = File.createTempFile("audio16k_", ".wav");
                    AudioSystem.write(converted, AudioFileFormat.Type.WAVE, out);
                    return out;
                }
            }

        } catch (UnsupportedAudioFileException e) {
            throw new IllegalArgumentException("Formato de áudio não suportado pelo Java Sound. Envie WAV/MP3 ou use ffmpeg.", e);
        }
    }
}