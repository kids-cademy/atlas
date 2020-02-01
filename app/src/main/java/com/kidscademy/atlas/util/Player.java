package com.kidscademy.atlas.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import js.lang.BugError;
import js.log.Log;
import js.log.LogFactory;
import js.util.Files;

@SuppressWarnings("unused")
public class Player implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    // this player instance implements fade out effect on stop and destroy using an asynchronous task
    // at least in theory release method, that nullify player instance, can be invoked from both asynchronous task and UI
    // thread via activities / fragments life cycle callbacks
    // for this reason all methods usable from both asynchronous task and UI thread should check for null player

    // now, while above statement is sound in theory, player usage in this application should not lead to such condition
    // since player creation, play and destroy sequence is respected
    // anyway, at least once there was a mysterious null pointer and I cannot ignored it

    private static final Log log = LogFactory.getLog(Player.class);

    /**
     * Step period by which volume fade effect is performed, in milliseconds.
     */
    private static final int FADE_TICK = 50;

    /**
     * Volume fade out effect duration, in milliseconds.
     */
    private static final float FADE_OUT_DURATION = 1000;

    private final Context context;
    private volatile MediaPlayer player;
    private State state;
    private StateListener listener;

    private volatile ProgressListener progressListener;

    private ProgressAsyncTask progressAsyncTask;

    private volatile AsyncTask<Void> fadeOutTask;

    private Queue<Object> audioSourcesQueue = new LinkedBlockingQueue<>();
    /**
     * Audio media player volume for both channels.
     */
    private float masterVolume;

    public Player(Context context) {
        log.trace("Player(Context)");
        this.context = context;
    }

    public void setStateListener(StateListener listener) {
        this.listener = listener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void create() {
        log.trace("create()");
        state = State.IDLE;
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        resetVolume();
    }

    /**
     * Release resources used by this player. If player is playing take care to stop it with volume fade out. Note that
     * {@link State#IDLE} event is not triggered.
     * <p>
     * After this method is called player instance become invalid. If want to reuse player instance need to call
     * {@link #create()}.
     */
    public void destroy() {
        log.trace("destroy()");
        audioSourcesQueue.clear();
        if (progressAsyncTask != null) {
            progressAsyncTask.cancel();
        }

        if (player == null) {
            return;
        }
        if (!player.isPlaying()) {
            release();
            return;
        }

        synchronized (this) {
            if (fadeOutTask != null) {
                return;
            }

            fadeOutTask = new AsyncTask<Void>() {
                @Override
                protected Void execute() throws Throwable {
                    fadeOut();
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    // while fade out was executing in asynchronous task is possible UI thread to already release the player
                    if (player != null) {
                        player.stop();
                    }
                    release();
                    Player.this.fadeOutTask = null;
                }
            };
            fadeOutTask.start();
        }
    }

    /**
     * Play audio file from device storage.
     *
     * @param audioFile absolute path to audio file.
     */
    public void play(File audioFile) {
        log.trace("play(File)");
        audioSourcesQueue.clear();
        playFile(audioFile);
    }

    /**
     * Play audio asset identified by asset path.
     *
     * @param assetPath audio asset path.
     */
    public void play(String assetPath) {
        log.trace("play(String)");
        audioSourcesQueue.clear();
        playAsset(assetPath);
    }

    /**
     * Play raw audio resource.
     *
     * @param audioResId audio resource ID.
     */
    public void play(int audioResId) {
        log.trace("play(int)");
        audioSourcesQueue.clear();
        play(context.getResources().openRawResourceFd(audioResId));
    }

    public void play(Context context, Uri audioUri) {
        log.trace("play(Context,Uri)");
        if (player == null) {
            return;
        }
        if (fadeOutTask != null) {
            fadeOutTask.cancel();
            fadeOutTask = null;
        }

        if (progressListener != null) {
            if (progressAsyncTask != null) {
                progressAsyncTask.cancel();
            }
            progressAsyncTask = new ProgressAsyncTask(player, progressListener);
            progressAsyncTask.execute();
        }

        try {
            player.reset();
            player.setVolume(masterVolume, masterVolume);

            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(context, audioUri);
            log.debug("Start preparing stream |%s|.", audioUri);
            player.prepareAsync();
        } catch (IOException e) {
            log.debug(e);
        }
    }

    @Override
    public void onPrepared(MediaPlayer unused) {
        log.trace("onPrepared(MediaPlayer)");
        log.debug("Stream ready for playback. Start playing.");
        player.start();
        setState(State.PLAYING);
    }

    public void play(FileInputStream stream, long offset, long length) throws IOException {
        log.trace("play(FileInputStream,long,long)");
        if (player == null) {
            return;
        }
        if (fadeOutTask != null) {
            fadeOutTask.cancel();
            fadeOutTask = null;
        }

        if (progressListener != null) {
            if (progressAsyncTask != null) {
                progressAsyncTask.cancel();
            }
            progressAsyncTask = new ProgressAsyncTask(player, progressListener);
            progressAsyncTask.execute();
        }

        player.reset();
        player.setVolume(masterVolume, masterVolume);

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDataSource(stream.getFD(), offset, length);
        player.prepare();
        player.start();
        setState(State.PLAYING);
    }

    public void playAll(Object... audioSources) {
        log.trace("playAll(Object...)");
        audioSourcesQueue.clear();
        for (Object audioSource : audioSources) {
            audioSourcesQueue.offer(audioSource);
        }
        playObject(audioSourcesQueue.poll());
    }

    private void playObject(Object audioSource) {
        if (audioSource instanceof File) {
            playFile((File) audioSource);
        } else if (audioSource instanceof String) {
            playAsset((String) audioSource);
        } else {
            throw new BugError("Illegal audio source type |%s| for play all.", audioSource.getClass());
        }
    }

    private void playFile(File audioFile) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(audioFile);
            play(stream, 0, audioFile.length());
        } catch (IOException e) {
            log.error(e);
        } finally {
            Files.close(stream);
        }
    }

    private void playAsset(String assetPath) {
        AssetFileDescriptor descriptor = null;
        try {
            descriptor = context.getAssets().openFd(assetPath);
            play(descriptor);
        } catch (IOException e) {
            log.error(e);
        } finally {
            if (descriptor != null) {
                try {
                    descriptor.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
    }

    /**
     * Play audio identified by given asset file descriptor. If <code>descriptor</code> argument is null this method is
     * NOP.
     *
     * @param descriptor audio asset file descriptor, null accepted.
     */
    public void play(AssetFileDescriptor descriptor) {
        log.trace("play(AssetFileDescriptor)");
        if (descriptor == null) {
            return;
        }

        // files from asset directory are all concatenated and accessed via a single file descriptor
        // if use media player with single file descriptor all files from asset directory are played one after another
        // need to use start offset and length to move to right file

        FileInputStream stream = null;
        try {
            stream = descriptor.createInputStream();
            play(stream, descriptor.getStartOffset(), descriptor.getLength());
        } catch (IOException e) {
            log.error(e);
        } finally {
            Files.close(stream);
        }
    }

    /**
     * Stop player, if currently playing, with volume fade out. On fade out effect completion perform the actual player
     * stop and fires {@link State#IDLE} event. If playback is not performing this method fires stopped event
     * immediately.
     * <p>
     * This method should be invoked on a valid player instance, that is, after {@link #create()} was performed. Otherwise
     * this method is NOP.
     */
    public void stop() {
        log.trace("stop()");
        audioSourcesQueue.clear();
        if (progressAsyncTask != null) {
            progressAsyncTask.cancel();
        }

        // stop method can be invoked from fade out asynchronous task and from UI thread
        // need to check if the other thread does not nullify player instance
        if (player == null) {
            return;
        }
        resetVolume();
        if (!player.isPlaying()) {
            player.reset();
            setState(State.IDLE);
            return;
        }

        synchronized (this) {
            if (fadeOutTask != null) {
                return;
            }
            fadeOutTask = new AsyncTask<Void>() {
                @Override
                protected Void execute() throws Throwable {
                    fadeOut();
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    // while fade out was executing in asynchronous task is possible UI thread to already release the player
                    if (player != null) {
                        player.stop();
                        player.reset();
                    }
                    setState(State.IDLE);
                    Player.this.fadeOutTask = null;
                }
            };
            fadeOutTask.start();
        }
    }

    public void switchPlay(final File audioFile) {
        log.trace("switchPlay(File)");
        AsyncTask<Void> fadeOutTask = new AsyncTask<Void>() {
            @Override
            protected Void execute() throws Throwable {
                fadeOut();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // while fade out was executing in asynchronous task is possible UI thread to already release the player
                if (player != null) {
                    player.stop();
                    player.reset();
                }
                setState(State.IDLE);

                AsyncTask<Void> fadeInTask = new AsyncTask<Void>() {
                    @Override
                    protected Void execute() throws Throwable {
                        fadeIn();
                        return null;
                    }
                };
                fadeInTask.start();

                play(audioFile);
            }
        };
        fadeOutTask.start();
    }

    public void pause() {
        log.trace("pause()");
        if (player != null && player.isPlaying()) {
            player.pause();
            setState(State.PAUSED);
        }
    }

    public void resume() {
        log.trace("resume()");
        if (state == State.PAUSED) {
            player.start();
            setState(State.PLAYING);
        }
    }

    public int getPosition() {
        return player != null && player.isPlaying() ? player.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return player != null && player.isPlaying() ? player.getDuration() : 0;
    }

    public static int getDuration(Resources resources, int audioResId) {
        return getDuration(resources.openRawResourceFd(audioResId));
    }

    public static int getDuration(Context context, String assetPath) {
        try {
            return getDuration(context.getAssets().openFd(assetPath));
        } catch (IOException e) {
            log.error(e);
        }
        return 0;
    }

    private static int getDuration(AssetFileDescriptor descriptor) {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            player.prepare();
            return player.getDuration();
        } catch (Exception e) {
            log.error(e);
        } finally {
            player.release();
        }
        return 0;
    }

    public static int getDuration(File audioFile) {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(audioFile.getAbsolutePath());
            player.prepare();
            return player.getDuration();
        } catch (Exception e) {
            log.error(e);
        } finally {
            player.release();
        }
        return 0;
    }

    @Override
    public void onCompletion(MediaPlayer unused) {
        log.trace("onCompletion(MediaPlayer)");
        setState(State.IDLE);
        if (!audioSourcesQueue.isEmpty()) {
            playObject(audioSourcesQueue.poll());
        }
    }

    public State getState() {
        return state;
    }

    public boolean isPlaying() {
        return state == State.PLAYING;
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }

    private void setState(State state) {
        log.trace("setState(State): state |%s|", state);
        this.state = state;
        if (listener != null) {
            listener.onPlayerStateChanged(state);
        }
    }

    /**
     * Reset audio player volume to system settings.
     */
    private void resetVolume() {
        log.trace("resetVolume()");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        masterVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        masterVolume /= audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        player.setVolume(masterVolume, masterVolume);
    }

    public void setVolume(float volume) {
        log.trace("setVolume(float)");
        masterVolume = volume;
        player.setVolume(masterVolume, masterVolume);
    }

    /**
     * Perform volume fade in effect with thread blocking. Be aware that this method is designed to work in a separated
     * thread and it blocks till effect completion.
     */
    private void fadeIn() throws InterruptedException {
        final float delta = 1.0F / (FADE_OUT_DURATION / FADE_TICK);
        for (float volume = 0; volume < 1.0F; volume += delta) {
            // fade in method is running in a separated thread and is possible for activities / fragments life cycle
            // callbacks invoked from UI thread to release player, therefore nullify player instance
            if (player == null) {
                break;
            }
            player.setVolume(volume, volume);
            Thread.sleep(FADE_TICK);
        }
    }

    /**
     * Perform volume fade out effect with thread blocking. Be aware that this method is designed to work in a separated
     * thread and it blocks till effect completion.
     */
    private void fadeOut() throws InterruptedException {
        final float delta = 1.0F / (FADE_OUT_DURATION / FADE_TICK);
        for (float volume = 1.0F; volume >= 0; volume -= delta) {
            // fade out method is running in a separated thread and is possible for activities / fragments life cycle
            // callbacks invoked from UI thread to release player, therefore nullify player instance
            if (player == null) {
                break;
            }
            player.setVolume(volume, volume);
            Thread.sleep(FADE_TICK);
        }
    }

    private void release() {
        // release method can be invoked from fade out asynchronous task and from UI thread
        // need to check if the other thread does not nullify player instance
        if (player == null) {
            return;
        }
        player.release();
        player.setOnCompletionListener(null);
        player.setOnErrorListener(null);
        player = null;
        state = State.IDLE;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Player automata states.
     *
     * @author Iulian Rotaru
     */
    public enum State {
        /**
         * Player is idle, that is, ready for a new playback session.
         */
        IDLE,

        /**
         * Is currently playing.
         */
        PLAYING,

        /**
         * Media is loaded but playback is stopped.
         */
        PAUSED
    }

    public interface StateListener {
        void onPlayerStateChanged(State state);
    }

    public interface ProgressListener {
        void onPlayerProgress(int currentPosition);
    }

    private static class ProgressAsyncTask extends AsyncTask<Void> {
        private final MediaPlayer player;
        private final ProgressMessageHandler progressMessageHandler;

        ProgressAsyncTask(MediaPlayer player, ProgressListener progressListener) {
            this.player = player;
            this.progressMessageHandler = new ProgressMessageHandler(progressListener);
        }

        @Override
        protected Void execute() {
            while (!player.isPlaying()) {
                if (isCancelled()) {
                    return null;
                }
                sleep(100);
            }
            while (player.isPlaying()) {
                if (isCancelled()) {
                    progressMessageHandler.sendProgress(0);
                    return null;
                }
                sleep(20);
                progressMessageHandler.sendProgress(player.getCurrentPosition());
            }
            progressMessageHandler.sendProgress(getDuration());
            return null;
        }

        private int getDuration() {
            return player.isPlaying() ? player.getDuration() : 0;
        }


        private static void sleep(long duration) {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException ignore) {
            }
        }
    }

    private static class ProgressMessageHandler extends Handler {
        private static final int PROGRESS = 1;
        private final ProgressListener progressListener;

        ProgressMessageHandler(ProgressListener progressListener) {
            this.progressListener = progressListener;
        }

        void sendProgress(int currentPosition) {
            Message message = obtainMessage();
            message.what = PROGRESS;
            message.arg1 = currentPosition;
            sendMessage(message);
        }

        @Override
        public void handleMessage(Message message) {
            if (progressListener == null) {
                return;
            }
            if (message.what == PROGRESS) {
                progressListener.onPlayerProgress(message.arg1);
            } else
                super.handleMessage(message);
        }
    }
}
