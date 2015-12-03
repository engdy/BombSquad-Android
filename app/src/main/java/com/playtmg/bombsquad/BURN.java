/*
 * BURN.java
 * BombSquad
 *
 * Created by Andy Foulke on 10/26/2015.
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * BURN class plays random audio clips for various events
 */
public class BURN implements MediaPlayer.OnCompletionListener {
	public static final String TAG = BURN.class.getSimpleName();

	public static final int NO_BURN = 0;    // Don't play any audio clips
	public static final int LIGHT_BURN = 1; // Less frequent, less hassling
	public static final int MED_BURN = 2;   // More frequent, more taunts
	public static final int HEAVY_BURN = 3; // Most frequent, most taunting

	public static final int BURN_RUNNING = 0;  // Clips to play at random intervals while timer is running
	public static final int BURN_STARTING = 1; // Clips to play when Start button pressed
	public static final int BURN_DEFUSED = 2;  // Clips to play when bomb defused
	public static final int BURN_WON = 3;      // Clips to play when players win game
	public static final int BURN_LOST = 4;     // Clips to play when players lose
	public static final int BURN_PAUSE = 5;    // Clips to play when Pause button pressed
	public static final int BURN_WAITING = 6;  // Clips to play at 30sec intervals while game is paused
	public static final int BURN_SELECT = 7;   //

	public static final int WAIT_PERIOD_MSEC = 30000;  // Waiting clips played at this millisecond interval

	private Context context;
	private int burnLevel;
	private MediaPlayer runningPlayer;
	private MediaPlayer startPlayer;
	private MediaPlayer defusedPlayer;
	private MediaPlayer wonPlayer;
	private MediaPlayer lostPlayer;
	private MediaPlayer pausePlayer;
	private MediaPlayer waitingPlayer;
	private MediaPlayer selectPlayer;
	private List<MediaPlayer> currentPlayers;
	private List<Integer> lastPlayed;
	private List<Integer> choices;
	private BURNTimer burnTimer;
	private Random rand;
	private int clips[][][] = {
			// Running
			{
					{},     // NO_BURN
					{       // LIGHT_BURN - includes clips from NO_BURN category
							R.raw._01female_04_youlikeloudnoises,
							R.raw._01female_07_insuranceuptodate,
							R.raw._01male_31_ourplanisperfect,
							R.raw._01male_33_youvealreadylost,
							R.raw._01male_34_getburned_a,
							R.raw._01male_35_getburned_b,
							R.raw._01male_39_isthisyourfirsttime_a,
							R.raw._01male_40_isthisyourfirsttime_b,
							R.raw._01male_41_arentverygood,
							R.raw._01male_42_inevitable,
							R.raw._01sarge_03_madeitthroughworse,
							R.raw._01sarge_08_bestsquadevertrained,
							R.raw._01sarge_12_keepatitteam,
							R.raw._01sarge_13_gettingwarmer
					},
					{       // MED_BURN - includes clips from NO_BURN and LIGHT_BURN
							R.raw._02female_02_hotinhere,
							R.raw._02female_03_fuseisshort,
							R.raw._02female_04_youlikeloudnoises,
							R.raw._02male_06_boomjustkidding,
							R.raw._02male_19_itscute_a,
							R.raw._02male_20_itscute_b,
							R.raw._02male_21_itscute_c,
							R.raw._02male_22_itscute_d,
							R.raw._02male_23_itscute_e,
							R.raw._02male_38_yourerunningoutoftime,
							R.raw._02sarge_14_badfeelingaboutthis
					},
					{       // HEAVY_BURN - includes clips from NO_BURN, LIGHT_BURN, MED_BURN
							R.raw._03female_01_cutthewhitewire,
							R.raw._03female_05_turnrightmovetwo,
							R.raw._03female_06_numbers,
							R.raw._03female_08_twentyquestions,
							R.raw._03male_32_remembertheredwires,
							R.raw._03male_36_redwireorblue,
							R.raw._03male_37_54321
					}
			},
			// Starting
			{
					{},     // NO_BURN
					{       // LIGHT_BURN
							R.raw._11sarge_02_youcandothis,
							R.raw._11sarge_04_youlldofine,
							R.raw._11sarge_07_wereallcountingonyou,
							R.raw._11sarge_15_bestsquadintheroom
					},
					{},     // MED_BURN
					{}      // HEAVY_BURN
			},
			// Defused
			{
					{},     // NO_BURN
					{       // LIGHT_BURN
							R.raw._21female_09_jokesonyou,
							R.raw._21male_01_tooeasy_a,
							R.raw._21male_02_tooeasy_b,
							R.raw._21male_03_tooeasy_c,
							R.raw._21male_24_bombwasmyfavorite_a,
							R.raw._21male_25_bombwasmyfavorite_b,
							R.raw._21male_26_bombwasmyfavorite_c,
							R.raw._21male_43_wastedtimeondecoy,
							R.raw._21male_44_nextbombintime_a,
							R.raw._21male_45_nextbombintime_b,
							R.raw._21male_46_nextbombintime_c,
							R.raw._21male_47_igaveyouthatone,
							R.raw._21male_48_alwaysmorebombs_a,
							R.raw._21male_49_alwaysmorebombs_b,
							R.raw._21sarge_01_youguysarebest,
							R.raw._21sarge_06_staycalm,
							R.raw._21sarge_11_almostthere,
							R.raw._21sarge_16_bd_goodwork_mightmakeitout,
							R.raw._21sarge_17_bd_thatwascuttingitclose,
							R.raw._21sarge_19_bd_cantbelieveyougotone,
							R.raw._21sarge_20_bd_dontgetcocky,
							R.raw._21sarge_21_bd_haventcompletelyfailed,
							R.raw._21sarge_22_bd_princessinanothercastle
					},
					{       // MED_BURN
							R.raw._22male_04_earthshattering_a,
							R.raw._22male_05_earthshattering_b
					},
					{}      // HEAVY_BURN

			},
			// Won
			{
					{},     // NO_BURN
					{       // LIGHT_BURN
							R.raw._31female_10_cursesfoiledagain,
							R.raw._31female_11_plantedmore,
							R.raw._31male_07_acme_a,
							R.raw._31male_08_acme_b,
							R.raw._31male_27_thisaintover_a,
							R.raw._31male_28_thisaintover_b,
							R.raw._31male_29_thisaintover_c,
							R.raw._31male_30_thisaintover_d,
							R.raw._31male_50_neverstopburn_a,
							R.raw._31male_51_neverstopburn_b,
							R.raw._31male_52_disabledourbombs,
							R.raw._31male_53_iletyouwin,
							R.raw._31male_54_iwontplaynice,
							R.raw._31male_55_illgetyou,
							R.raw._31sarge_05_wouldnthavesentyou,
							R.raw._31sarge_09_dontletitgotoyourhead,
							R.raw._31sarge_18_bd_ineverdoubtedyou,
							R.raw._31sarge_23_mc_surprisedyourealive,
							R.raw._31sarge_24_mc_whenaplancomestogether,
							R.raw._31sarge_25_mc_icecreamforeveryone,
							R.raw._31sarge_26_mc_thatsallofthem,
							R.raw._31sarge_27_mc_youdiditandinrecordtime,
							R.raw._31sarge_28_mc_lookslikethegoodguyswin,
							R.raw._31sarge_29_mc_outstandingwork
					},
					{},     // MED_BURN
					{}      // HEAVY_BURN
			},
			// Lost
			{
					{},     // NO_BURN
					{       // LIGHT_BURN
							R.raw._41female_12_bombvoyage_a,
							R.raw._41female_13_bombvoyage_b,
							R.raw._41female_14_blast,
							R.raw._41female_15_greatjobofmessingup,
							R.raw._41female_16_dontfeelbad_a,
							R.raw._41female_16_dontfeelbad_b,
							R.raw._41male_13_dynomite_a,
							R.raw._41male_14_dynomite_b,
							R.raw._41male_15_dynomite_c,
							R.raw._41male_16_knockknock_a,
							R.raw._41male_17_knockknock_b,
							R.raw._41male_18_knockknock_c,
							R.raw._41male_56_justthebeginning,
							R.raw._41male_57_tooeasy,
							R.raw._41sarge_30_fm_weknewthiswouldhappen,
							R.raw._41sarge_31_fm_yourbestwasntenough,
							R.raw._41sarge_32_fm_youhavefailed
					},
					{       // MED_BURN
							R.raw._42sarge_33_fm_whatdoyouwantaparade
					},
					{}      // HEAVY_BURN
			},
			// Pause
			{
					{},     // NO_BURN
					{       // LIGHT_BURN
							R.raw._51sarge_10_stayontarget
					},
					{},     // MED_BURN
					{}      // HEAVY_BURN
			},
			// Waiting
			{
					{},     // NO_BURN
					{       // LIGHT_BURN
							R.raw._61male_09_tickticktick_a,
							R.raw._61male_10_tickticktick_b,
							R.raw._61male_11_tickticktick_c,
							R.raw._61male_12_tickticktick_d
					},
					{},     // MED_BURN
					{}      // HEAVY_BURN
			}
	};

	/**
	 * Constructor
	 *
	 * @param context needed to create {@link MediaPlayer} instances
	 */
	public BURN(Context context) {
		this.context = context;
		SharedPreferences prefs = context.getSharedPreferences(SettingsActivity.BOMB_SQUAD_PREFS, Context.MODE_PRIVATE);
		int bl = prefs.getInt(SettingsActivity.BURN_LEVEL, 0);
		switch (bl) {
			case 0: burnLevel = NO_BURN;
				break;
			case 1: burnLevel = LIGHT_BURN;
				break;
			case 2: burnLevel = MED_BURN;
				break;
			case 3: burnLevel = HEAVY_BURN;
				break;
			default: burnLevel = NO_BURN;
		}
		currentPlayers = new ArrayList<>();
		choices = new ArrayList<>();
		rand = new Random();
		lastPlayed = new ArrayList<>(Arrays.asList(-1, -1, -1, -1, -1, -1, -1, -1));
		resetClips();
		burnTimer = new BURNTimer();
	}

	public void setBurnLevel(int level) {
		burnLevel = level;
		resetClips();
	}

	/**
	 * Get a clip of each event type ready to play
	 */
	public void resetClips() {
		currentPlayers.clear();
		runningPlayer = selectClip(BURN_RUNNING, burnLevel);
		startPlayer = selectClip(BURN_STARTING, burnLevel);
		defusedPlayer = selectClip(BURN_DEFUSED, burnLevel);
		wonPlayer = selectClip(BURN_WON, burnLevel);
		lostPlayer = selectClip(BURN_LOST, burnLevel);
		pausePlayer = selectClip(BURN_PAUSE, burnLevel);
		waitingPlayer = selectClip(BURN_WAITING, burnLevel);
	}

	/**
	 * Choose an audio clip of the requested type and BURN level
	 *
	 * @param type
	 * @param level
	 * @return media player of the requested type and level
	 */
	private MediaPlayer selectClip(int type, int level) {
		choices.clear();
		for (int i = 0; i < clips[type][NO_BURN].length; ++i) {
			choices.add(clips[type][NO_BURN][i]);
		}
		if (level >= LIGHT_BURN) {
			for (int i = 0; i < clips[type][LIGHT_BURN].length; ++i) {
				choices.add(clips[type][LIGHT_BURN][i]);
			}
		}
		if (level >= MED_BURN) {
			for (int i = 0; i < clips[type][MED_BURN].length; ++i) {
				choices.add(clips[type][MED_BURN][i]);
			}
		}
		if (level >= HEAVY_BURN) {
			for (int i = 0; i < clips[type][HEAVY_BURN].length; ++i) {
				choices.add(clips[type][HEAVY_BURN][i]);
			}
		}
		int clipcount = choices.size();
		if (clipcount == 0) {
			return null;
		}
		int choice;
		do {
			choice = rand.nextInt(clipcount);
		} while (choice == lastPlayed.get(type) && clipcount > 1);
		lastPlayed.set(type, choice);
		MediaPlayer player = MediaPlayer.create(context, choices.get(choice));
		if (player != null) {
			player.setVolume(1.0f, 1.0f);
			player.setOnCompletionListener(this);
			currentPlayers.add(player);
		}
		return player;
	}

	/**
	 * Generate a random interval to put between Running clips
	 *
	 * @return interval in seconds
	 */
	private int getInterval() {
		int interval = 0;
		if (burnLevel == LIGHT_BURN) {
			interval = rand.nextInt(50) + 10;
		} else if (burnLevel == MED_BURN) {
			interval = rand.nextInt(25) + 10;
		} else if (burnLevel == HEAVY_BURN) {
			interval = rand.nextInt(10) + 10;
		}
		return interval;
	}

	/**
	 * Start the timer associated with Running clips
	 * Stop the timer associated with Waiting clips
	 * Play a clip associated with Starting
	 */
	public void start() {
		burnTimer.startRunning();
		burnTimer.stopWaiting();
		playStart();
	}

	/**
	 * Stop the timer associated with Running clips
	 * Start the timer associated with Waiting clips
	 * Play a clip associated with Pausing
	 */
	public void pause() {
		burnTimer.stopRunning();
		burnTimer.startWaiting();
		playPause();
	}

	/**
	 * Stop all pending and playing audio clips
	 * Stop all BURN timers
	 */
	public void stopAll() {
		burnTimer.stopWaiting();
		burnTimer.stopRunning();
		for (MediaPlayer mp : currentPlayers) {
			mp.stop();
			mp.release();
		}
		currentPlayers.clear();
		resetClips();
	}

	public void playStart() {
		if (startPlayer != null) {
			startPlayer.start();
			startPlayer = selectClip(BURN_STARTING, burnLevel);
		}
	}

	public void playDefused() {
		if (defusedPlayer != null) {
			defusedPlayer.start();
			defusedPlayer = selectClip(BURN_DEFUSED, burnLevel);
		}
	}

	public void playWon() {
		if (wonPlayer != null) {
			wonPlayer.start();
			wonPlayer = selectClip(BURN_WON, burnLevel);
		}
	}

	public void playLost() {
		if (lostPlayer != null) {
			lostPlayer.start();
			lostPlayer = selectClip(BURN_LOST, burnLevel);
		}
	}

	public void playSelect() {
		if (selectPlayer != null) {
			selectPlayer.start();
			selectPlayer = selectClip(BURN_SELECT, burnLevel);
		}
	}

	public void playPause() {
		if (pausePlayer != null) {
			pausePlayer.start();
			pausePlayer = selectClip(BURN_PAUSE, burnLevel);
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.d(TAG, "onCompletion");
		mp.release();
		currentPlayers.remove(mp);
	}

	/**
	 * Inner class for the BURN timer - used to space out Running and Waiting clips
	 */
	private class BURNTimer {
		private Handler burnHandler = new Handler();

		public BURNTimer() {}

		private Runnable waitingTick = new Runnable() {
			@Override
			public void run() {
				if (waitingPlayer != null) {
					waitingPlayer.start();
					waitingPlayer = selectClip(BURN_WAITING, burnLevel);
					burnHandler.postDelayed(waitingTick, WAIT_PERIOD_MSEC);
				}
			}
		};

		private Runnable runningTick = new Runnable() {
			@Override
			public void run() {
				if (runningPlayer != null) {
					runningPlayer.start();
					runningPlayer = selectClip(BURN_RUNNING, burnLevel);
					burnHandler.postDelayed(runningTick, getInterval() * 1000);
				}
			}
		};

		public void startWaiting() {
			burnHandler.removeCallbacks(waitingTick);
			burnHandler.postDelayed(waitingTick, WAIT_PERIOD_MSEC);
		}

		public void stopWaiting() {
			burnHandler.removeCallbacks(waitingTick);
		}

		public void startRunning() {
			burnHandler.removeCallbacks(runningTick);
			burnHandler.postDelayed(runningTick, getInterval() * 1000);
		}

		public void stopRunning() {
			burnHandler.removeCallbacks(runningTick);
		}
	}
}
