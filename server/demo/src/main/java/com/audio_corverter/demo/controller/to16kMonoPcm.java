package com.audio_corverter.demo.controller;

import javax.sound.sampled.*;
import java.io.File;

public class to16kMonoPcm {

    // Converte qualquer AudioInputStream suportado pelo JavaSound em WAV PCM 16k mono s16le.
    public static File ConvertFiles(File input) throws Exception {
        System.out.println(input);
        try (var s = AudioSystem.getAudioInputStream(input)) {
            System.out.println("Formato de entrada: " + s.getFormat());
        } catch (Exception e) {
            System.out.println("Nao abriu no JavaSound: " + e);
        }

        try (AudioInputStream source = AudioSystem.getAudioInputStream(input)) {
            AudioFormat base = source.getFormat();

            // 1) Garante PCM_SIGNED (decodifica comprimidos suportados para PCM)
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

                // 2) Reamostra para 16 kHz mono
                AudioFormat target = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        16000,  // sample rate
                        16,     // 16-bit
                        1,      // mono
                        2,      // frame size (2 bytes)
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
            // Java Sound não entendeu o formato de entrada (ex.: M4A/AAC; alguns MP3)
            throw new IllegalArgumentException("Formato de áudio não suportado pelo Java Sound. Envie WAV/MP3 ou use ffmpeg.", e);
        }
    }
}