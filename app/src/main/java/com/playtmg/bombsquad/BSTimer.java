/*
 * BSTimer.java
 * BombSquad
 *
 * Created by Andy Foulke on 11/10/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */

package com.playtmg.bombsquad;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

/**
 * Main timer workhorse class for the app
 */
public class BSTimer implements MediaPlayer.OnCompletionListener {
	public static final String TAG = BSTimer.class.getSimpleName();
	private Handler timerHandler = new Handler();
	private RunningActivity activity = null; // Either AllBombsActivity or SingleBombActivity
	private long startMillis = 0;
	private long elapsedMillis = 0;
	private long now = 0;
	private long duration = 0;
	private boolean isTimerRunning = false;
	private boolean bombDetonated = false;
	private boolean shouldPlaySoundtrack = false;
	private boolean shouldPlayBombSounds = false;
	private boolean shouldPlayCountdown = false;
	private boolean isPlayingCountdown = false;
	private BombList bombs;
	private Bomb urgentBomb;
	private Bomb b;
	private BURN burn;
	private MediaPlayer soundtrackPlayer;
	private MediaPlayer smallBombPlayer;
	private MediaPlayer bigBombPlayer;
	private MediaPlayer countdownPlayer;

	public BSTimer(BURN b) {
		burn = b;
	}

	/**
	 * Queues the requested soundtrack resource to play while the game is running
	 *
	 * @param willPlay true if soundtrack is supposed to play
	 * @param resource Resource ID from res/raw/*.ogg
	 * @param volume from 0.0 to 1.0
	 */
	public void enableSoundtrack(boolean willPlay, int resource, float volume) {
		shouldPlaySoundtrack = willPlay;
		if (shouldPlaySoundtrack) {
			if (soundtrackPlayer == null || !soundtrackPlayer.isPlaying()) {
				soundtrackPlayer = MediaPlayer.create(activity, SettingsActivity.soundtrackRes[resource]);
				soundtrackPlayer.setLooping(true);
				soundtrackPlayer.setVolume(volume, volume);
			}
		} else {
			if (soundtrackPlayer != null) {
				soundtrackPlayer.release();
				soundtrackPlayer = null;
			}
		}
	}

	/**
	 * Queues the bomb explosion sounds to play when a bomb detonates
	 *
	 * @param willPlay true if explosion sounds are supposed to play
	 * @param volume from 0.0 to 1.0
	 */
	public void enableBombSounds(boolean willPlay, float volume) {
		shouldPlayBombSounds = willPlay;
		if (shouldPlayBombSounds) {
			smallBombPlayer = MediaPlayer.create(activity, R.raw.smallbomb);
			if (smallBombPlayer != null) {
				smallBombPlayer.setLooping(false);
				smallBombPlayer.setVolume(volume, volume);
				// BURN lost clip must wait for explosion to finish
				smallBombPlayer.setOnCompletionListener(this);
			}
			bigBombPlayer = MediaPlayer.create(activity, R.raw.bigbomb);
			if (bigBombPlayer != null) {
				bigBombPlayer.setLooping(false);
				bigBombPlayer.setVolume(volume, volume);
				// BURN lost clip must wait for explosion to finish
				bigBombPlayer.setOnCompletionListener(this);
			}
		} else {
			smallBombPlayer = null;
			bigBombPlayer = null;
		}
	}

	/**
	 * Queues the countdown sound to play when 10 seconds remain on a timer
	 *
	 * @param willPlay true if countdown should play
	 */
	public void enableCountdown(boolean willPlay) {
		shouldPlayCountdown = willPlay;
		countdownPlayer = null;
		if (shouldPlayCountdown) {
			countdownPlayer = MediaPlayer.create(activity, R.raw.countdown);
			if (countdownPlayer != null) {
				countdownPlayer.setLooping(false);
				countdownPlayer.setVolume(100.0f, 100.0f);
				countdownPlayer.setOnCompletionListener(this);
			}
		}
	}

	/**
	 * Start playing the soundtrack
	 */
	public void startMusic() {
		if (shouldPlaySoundtrack && soundtrackPlayer != null && !soundtrackPlayer.isPlaying()) {
			soundtrackPlayer.start();
		}
	}

	/**
	 * Pause the soundtrack
	 */
	public void stopMusic() {
		if (soundtrackPlayer != null) {
			soundtrackPlayer.pause();
		}
	}

	/**
	 * Runs at 10Hz
	 * - Updates main time textview with game-ending bomb time remaining
	 * - Plays BURN Won clip if all game-ending bombs defused
	 * - Updates time for each bomb
	 * - Checks for detonations
	 * - Starts countdown audio when appropriate
	 */
	private Runnable timerTick = new Runnable() {
		@Override
		public void run() {
			now = System.currentTimeMillis();
			duration = elapsedMillis + now - startMillis;
			urgentBomb = bombs.findUrgentBomb(); // game ending bomb
			if (urgentBomb != null) {
				activity.updateMainTime(urgentBomb.timeLeftFromElapsed(duration));
			} else {
				if (!bombDetonated) { // If no game-ending bombs remain, players win
					burn.stopAll();
					burn.playWon();
					activity.updatePlayButton(RunningActivity.ButtonState.PLAY_DISABLED);
				}
				stop(); // timer
				stopMusic();
			}
			for (int idx = 0; idx < bombs.bombCount(); ++idx) {
				b = bombs.getBomb(idx);
				if (b.getState() == Bomb.BombState.LIVE) {
					if (duration > b.getMillisDuration()) {
						// Bomb blew up
						Log.d(TAG, "Bomb " + b + " blew up!");
						b.setState(Bomb.BombState.DETONATED);
						if (b.isFatal()) { // players lost
							bombDetonated = true;
							stop(); // timer
							stopMusic();
							activity.updatePlayButton(RunningActivity.ButtonState.PLAY_DISABLED);
							burn.stopAll();
						}
						activity.updateBomb(b, idx, -1);
						if (shouldPlayBombSounds) {
							if (b.getLevel() < 3) {
								// Small bomb
								if (smallBombPlayer != null) {
									if (smallBombPlayer.isPlaying()) {
										smallBombPlayer.seekTo(0);
									}
									smallBombPlayer.start();
								}
							} else {
								// Big bomb
								if (bigBombPlayer != null) {
									if (bigBombPlayer.isPlaying()) {
										bigBombPlayer.seekTo(0);
									}
									bigBombPlayer.start();
								}
							}
						}
					} else {
						activity.updateBomb(b, idx, duration);
					}
				}
			}
			if (!isPlayingCountdown && shouldPlayCountdown) {
				Bomb b = bombs.findMinTimeBomb();
				if (b != null) {
					long timeToBomb = b.getMillisDuration() - duration;
					if (timeToBomb < 11000) {
						isPlayingCountdown = true;
						countdownPlayer.seekTo(11000 - (int)timeToBomb);
						countdownPlayer.start();
					}
				}
			}
			if (isTimerRunning) {
				timerHandler.postDelayed(timerTick, 100);
			}
		}
	};

	/**
	 * Start the timer
	 */
	public void start() {
		if (isTimerRunning) {
			return;
		}
		Log.d(TAG, "Timer starting");
		isTimerRunning = true;
		startMillis = System.currentTimeMillis();
		timerHandler.removeCallbacks(timerTick);
		timerHandler.postDelayed(timerTick, 100);
	}

	/**
	 * Stop the timer
	 */
	public void stop() {
		if (!isTimerRunning) {
			return;
		}
		Log.d(TAG, "Timer stopping");
		isTimerRunning = false;
		elapsedMillis += System.currentTimeMillis() - startMillis;
		timerHandler.removeCallbacks(timerTick);
	}

	/**
	 * Let {@link BSTimer} know which activity should be receiving updates
	 *
	 * @param a {@link RunningActivity} to receive updates
	 */
	public void setActivity(RunningActivity a) {
		activity = a;
	}

	public void setBombList(BombList bl) {
		bombs = bl;
	}

	public long getElapsedMillis() {
		return elapsedMillis;
	}

	public void reset() {
		elapsedMillis = 0;
	}

	public boolean isRunning() {
		return isTimerRunning;
	}

	/**
	 * A sound clip finished - either bomb explosion or 10 sec countdown.  If a bomb explosion,
	 * should play BURN Lost message.
	 *
	 * @param mp Particular {@link MediaPlayer} that just finished
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		if (mp == countdownPlayer) {
			isPlayingCountdown = false;
		} else if (mp == smallBombPlayer || mp == bigBombPlayer) {
			if (bombDetonated) {
				burn.playLost();
			}
		}
	}
}
