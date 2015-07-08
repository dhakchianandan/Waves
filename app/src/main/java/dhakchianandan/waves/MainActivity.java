package dhakchianandan.waves;

import android.app.ActionBar;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import dhakchianandan.waves.adapter.RadioAdapter;
import dhakchianandan.waves.model.Radio;


public class MainActivity extends ActionBarActivity {
    private boolean IS_RADIO_SELECTED = false;
    final MediaPlayer player = new MediaPlayer();
    private boolean BACK_BUTTON_PRESSED = false;
    final List<Radio> radios = new ArrayList<Radio>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton playerControl = (ImageButton) findViewById(R.id.player_controls);
        final TextView playingStation = (TextView) findViewById(R.id.playing_station);
        final TextView playerStatus = (TextView) findViewById(R.id.player_status);
        populateStations(radios);

        RecyclerView radiosView = (RecyclerView) findViewById(R.id.radios_view);

        RecyclerView.LayoutManager layout = new GridLayoutManager(this, 2);
        radiosView.setLayoutManager(layout);

        RadioAdapter radioAdapter = new RadioAdapter(radios);
        radiosView.setAdapter(radioAdapter);

        radioAdapter.setListener(new RadioAdapter.Listener() {
            @Override
            public void onClick(int position) {
                if(!NetworkUtils.getConnectionStatus(getApplicationContext())) {
                    handleNetworkError(playerControl, playerStatus);
                    return;
                }
                Radio radio = radios.get(position);
                if (!IS_RADIO_SELECTED) IS_RADIO_SELECTED = true;
                playerControl.setImageResource(R.drawable.ic_pause_circle);
                player.reset();
                try {
                    player.setDataSource(radio.getUrl());
                    playingStation.setText(radio.getName());
                    playerStatus.setText(R.string.playing);
                    player.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Toast.makeText(getApplicationContext(), "Playing radio", Toast.LENGTH_SHORT).show();
                player.start();
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getBaseContext(), "Problem with streaming. Please try other stations.", Toast.LENGTH_SHORT).show();
//                player.release();
                return false;
            }
        });

        playerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkUtils.getConnectionStatus(getApplicationContext())) {
                    handleNetworkError(playerControl, playerStatus);
                    return;
                }
                if (IS_RADIO_SELECTED) {
                    if (player.isPlaying()) {
                        playerControl.setImageResource(R.drawable.ic_play_circle);
                        playerStatus.setText(R.string.play);
                        player.pause();
                    } else {
                        playerControl.setImageResource(R.drawable.ic_pause_circle);
                        playerStatus.setText(R.string.playing);
                        player.start();
                    }
                } else {
                    Radio radio = getRadio();
//                    player.stop();
                    player.reset();
                    IS_RADIO_SELECTED = true;
                    try {
                        player.setDataSource(radio.getUrl());
                        playingStation.setText(radio.getName());
                        player.prepareAsync();
                        playerControl.setImageResource(R.drawable.ic_pause_circle);
                        playerStatus.setText(R.string.playing);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        PhoneStateListener phoneStateListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if(!NetworkUtils.getConnectionStatus(getApplicationContext())) {
                    handleNetworkError(playerControl, playerStatus);
                    super.onCallStateChanged(state, incomingNumber);
                    return;
                }
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if(player != null) {
                            if(player.isPlaying()) {
                                player.pause();
                                playerControl.setImageResource(R.drawable.ic_play_circle);
                                playerStatus.setText(R.string.play);
                            }
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if(player != null && IS_RADIO_SELECTED) {
                            android.os.Handler handler = new android.os.Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    player.start();
                                    playerStatus.setText(R.string.playing);
                                    playerControl.setImageResource(R.drawable.ic_pause_circle);
                                }
                            }, 2000);
                        }
                        break;

                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        if(telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void handleNetworkError(ImageButton playerControl, TextView playerStatus) {
        Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();
        if(player != null && player.isPlaying()) player.stop();
        playerControl.setImageResource(R.drawable.ic_play_circle);
        playerStatus.setText(R.string.play);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            player.release();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(this.BACK_BUTTON_PRESSED) {
            player.release();
            super.onBackPressed();
        } else {
            this.BACK_BUTTON_PRESSED = true;
            Toast.makeText(getApplicationContext(), "Press again to Exit...",  Toast.LENGTH_SHORT).show();

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BACK_BUTTON_PRESSED = false;
                }
            }, 10000);
        }
    }

    private void populateStations(List<Radio> radios) {
        radios.add(new Radio("Radio City", "http://prclive1.listenon.in:9948/", R.drawable.ic_radiocity));
        radios.add(new Radio("Suryan FM", "http://37.130.229.188:8050/", R.drawable.ic_suryan));
        radios.add(new Radio("Radio Mirchi", "http://192.99.46.219:8202/", R.drawable.ic_radiomirchi));
        radios.add(new Radio("Red FM", "http://195.154.176.33:8000/", R.drawable.ic_redfm));
        radios.add(new Radio("Radio Desi Beat", "http://198.178.123.14:8040/", R.drawable.ic_radio_desi_beat));
        radios.add(new Radio("Radio City Hindi", "http://216.245.201.73:9960/", R.drawable.ic_radiocity_hindi));
        radios.add(new Radio("Rainbow FM", "http://192.99.46.219:8000/", R.drawable.ic_rainbow));
        radios.add(new Radio("Hungama", "http://123.176.41.8:8632/", R.drawable.ic_hungama));
    }

    private Radio getRadio() {
        return radios.get(0);
    }
}
