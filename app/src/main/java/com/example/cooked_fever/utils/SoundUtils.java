package com.example.cooked_fever.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.media.MediaPlayer;

import com.example.cooked_fever.R;

public class SoundUtils {

    private static SoundPool soundPool;
    private static boolean soundsLoaded = false;

    private static MediaPlayer bgmPlayer;

    // Sound IDs
    private static int sizzleSound;
    private static int burntSound;
    private static int pickupSound;
    private static int placeSound;
    private static int coinSound;
    private static int thankYouSound;
    private static int happy1Sound;
    private static int happy2Sound;
    private static int angry1Sound;
    private static int angry2Sound;

    private static int dingSound;

    private static int waterSound;

    private static int fizzSound;


    public static void init(Context context) {
        if (soundPool != null) return;

        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(attrs)
                .build();

        // Load all sounds
        sizzleSound = soundPool.load(context, R.raw.pan_sizzle, 1);
        burntSound = soundPool.load(context, R.raw.pan_burnt, 1);
        pickupSound = soundPool.load(context, R.raw.item_pickup, 1);
        placeSound = soundPool.load(context, R.raw.place_item, 1);
        coinSound = soundPool.load(context, R.raw.coins, 1);
        thankYouSound = soundPool.load(context, R.raw.thank_you, 1);
        happy1Sound = soundPool.load(context, R.raw.happy_1, 1);
        happy2Sound = soundPool.load(context, R.raw.happy_2, 1);
        angry1Sound = soundPool.load(context, R.raw.angry_1, 1);
        angry2Sound = soundPool.load(context, R.raw.angry_2, 1);
        dingSound = soundPool.load(context, R.raw.ding, 1);
        waterSound = soundPool.load(context, R.raw.water_pour, 1);
        fizzSound = soundPool.load(context, R.raw.fizz, 1);

        soundsLoaded = true;
    }

    public static void playSizzle() {
        if (soundsLoaded) soundPool.play(sizzleSound, 1, 1, 0, 0, 1);
    }

    public static void playBurnt() {
        if (soundsLoaded) soundPool.play(burntSound, 1, 1, 0, 0, 1);
    }

    public static void playPickup() {
        if (soundsLoaded) soundPool.play(pickupSound, 1, 1, 0, 0, 1);
    }

    public static void playPlace() {
        if (soundsLoaded) soundPool.play(placeSound, 1, 1, 0, 0, 1);
    }

    public static void playCoin() {
        if (soundsLoaded) soundPool.play(coinSound, 1, 1, 0, 0, 1);
    }

    public static void playThankYou() {
        if (soundsLoaded) soundPool.play(thankYouSound, 1, 1, 0, 0, 1);
    }

    public static void playDing() {
        if (soundsLoaded) soundPool.play(dingSound, 1, 1, 0, 0, 1);
    }

    public static void playWater() {
        if (soundsLoaded) soundPool.play(waterSound, 1, 1, 0, 0, 1);
    }
    public static void playFizz() {
        if (soundsLoaded) soundPool.play(fizzSound, 1, 1, 0, 0, 1);
    }

    public static void playHappy() {
        playRandom(happy1Sound, happy2Sound);
    }

    public static void playAngry() {
        playRandom(angry1Sound, angry2Sound);
    }
    public static void startBGM(Context context) {
        if (bgmPlayer == null) {
            bgmPlayer = MediaPlayer.create(context, R.raw.bgm);
            bgmPlayer.setLooping(true);

            bgmPlayer.setVolume(0.2f, 0.2f); // adjust if needed
            bgmPlayer.start();
        } else if (!bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    public static void stopBGM() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.stop();
            bgmPlayer.release();
            bgmPlayer = null;
        }
    }


    private static void playRandom(int sound1, int sound2) {
        if (soundsLoaded) {
            int chosen = Math.random() < 0.5 ? sound1 : sound2;
            soundPool.play(chosen, 1, 1, 0, 0, 1);
        }
    }
}
