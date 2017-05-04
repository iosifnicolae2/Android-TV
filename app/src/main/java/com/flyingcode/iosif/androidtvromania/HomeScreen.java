package com.flyingcode.iosif.androidtvromania;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

public class HomeScreen extends AppCompatActivity implements MediaPlayer.OnInfoListener {

    private static final String STATION_ID = "station_id";
    private String local_stream_url = "";
    private VideoView mVideoView;
    private BroadcastReceiver BroadcastReceiver_SOP =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                String stream_url = intent.getStringExtra("stream_url");
                //todo local_stream_url = intent.getStringExtra("local_url");
                Integer sop_status = intent.getIntExtra("sop_status",100);
                Integer stream_quality = intent.getIntExtra("stream_quality",-2);

                    Log.w("stream_url",stream_url);
                    Log.w("local_stream_url",local_stream_url);
                    Log.w("sop_status",sop_status+"");
                    Log.w("stream_quality", stream_quality + "");
                   /* if(!video_play){
                        video_play = true;
                        //startPlay();
                    }*/
                    if(error_txt!=null){
                        String t = error_txt.getText()+"\nsop_status: "+sop_status+" quality:"+stream_quality;
                        error_txt.setText(t);
                    }
                }
            };
    private static final IntentFilter filter_SOP = new IntentFilter("com.devaward.soptohttp.sopcast_event");
    private int station_id = 0;
    private boolean video_play = false;
    private TextView error_txt;
    private ListView lista_canale;
    private ArrayList<Canal> channels = new ArrayList<Canal>(){
    };
    private boolean needResume;
    private boolean list_oppened;
    private Channel_list_callback callback_list = new Channel_list_callback() {
        @Override
        public void play(Canal c) {
            startService(c);
        }
    };

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static final int RENDERER_COUNT = 3;

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case io.vov.vitamio.MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }
                break;
            case io.vov.vitamio.MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                break;
            case io.vov.vitamio.MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
//                            mDownloadRateView.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }

    public interface Channel_list_callback{
        public void play(Canal c);
    }


    /*
        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
           // process_intent(intent);

        }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }*/
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_home_screen);

        channels.add(new Canal(R.string.channel_speranta, "http://play.crestin.tv/content/channel/sperantatv/live/sperantatv.player.m3u8", "http://streams.magazinmixt.ro/img/logos/national_geographic.jpg"));
        channels.add(new Canal(R.string.channel_credo, "http://cdn.credonet.tv:1935/ctv/smil:livecredo.smil/playlist.m3u8", "http://streams.magazinmixt.ro/img/logos/national_geographic.jpg"));
        channels.add(new Canal(R.string.channel_tvr1_s2, "sop://broker.sopcast.com:3912/148085", "http://streams.magazinmixt.ro/img/logos/national_geographic.jpg"));
        channels.add(new Canal(R.string.channel_discovery, "sop://broker.sopcast.com:3912/256241", "http://streams.magazinmixt.ro/img/logos/national_geographic.jpg"));
        channels.add(new Canal(R.string.channel_national_geographic, "sop://broker.sopcast.com:3912/148248", "http://streams.magazinmixt.ro/img/logos/national_geographic.jpg"));
        channels.add(new Canal(R.string.channel_minimax, "sop://broker.sopcast.com:3912/148263", "http://streams.magazinmixt.ro/img/logos/national_geographic.jpg"));
        channels.add(new Canal(R.string.channel_digi24, "sop://broker.sopcast.com:3912/111947", "http://streams.magazinmixt.ro/img/logos/national_geographic.jpg"));
        channels.add(new Canal(R.string.channel_b1, "sop://broker.sopcast.com:3912/148087", "http://streams.magazinmixt.ro/img/logos/national_geographic.jpg"));
        channels.add(new Canal(R.string.channel_romania_tv, "sop://broker.sopcast.com:3912/148258", "http://streams.magazinmixt.ro/img/vlogos/national_geographic.jpg"));
        channels.add(new Canal(R.string.channel_antena3, "sop://broker.sopcast.com:3912/148084", "http://streams.magazinmixt.ro/img/vlogos/national_geographic.jpg"));



        error_txt = (TextView) findViewById(R.id.error_txt);
        lista_canale = (ListView) findViewById(R.id.lista_canale);
        ListaCanale adapter = new ListaCanale(this,R.layout.canal_row,channels,callback_list);
        lista_canale.setAdapter(adapter);


        close_channel_list();

        lista_canale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback_list.play(channels.get(position));
                close_channel_list();
            }
        });
        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_channel_list();

            }
        });





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setVisibility(View.GONE);


        if (!LibsChecker.checkVitamioLibs(this))
            Toast.makeText(HomeScreen.this, "Librariile nu s-au initializat corect.", Toast.LENGTH_LONG).show();


        mVideoView = (VideoView) findViewById(R.id.surface_view);

        mVideoView.setOnInfoListener(this);

        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
        mVideoView.setBufferSize(2048);

       /* mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
                switch (arg1) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //Begin buffer, pause playing
                        if (isPlaying()) {
                            stopPlay();
                            needResume = true;
                        }
                        //mLoadingView.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //The buffering is done, resume playing
                        if (needResume)
                            startPlay();
                        //mLoadingView.setVisibility(View.GONE);
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        //Display video download speed
                        Log.w("download rate:" ,""+ arg2);
                        break;
                }
                return true;
            }
        });*/
      /*  if (path == "") {
            // Tell the user to provide a media file URL/path.
            Toast.makeText(HomeScreen.this, "Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
            return;
        } else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 *-/
            mVideoView.setVideoPath(path);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });
        }*/


/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*-/
                startPlay(view);
            }
        });
        */




        /*Snackbar.make(, "Va rugam asteptati cateva secunde...", Snackbar.LENGTH_LONG)
                .setAction("Stop", null).show();*/

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
        if(!process_intent(getIntent())) {
            startService(channels.get(0));

        }

        error_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list_oppened)
                    close_channel_list();
                else
                open_channel_list();
                Log.w("list","clicked");
            }
        });

    }



    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    private void startService(Canal c){
        if(c==null) return;

        if(c.getUrl().contains("sop://")){
            final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(c.getUrl()));
            intent.putExtra("stream_name",c.getName());
            intent.putExtra("show_memory",false);
            intent.putExtra("show_traffic",false);
            intent.putExtra("show_counter",false);
            intent.putExtra("make_private",true);
            intent.putExtra("video_player_package", "com.flyingcode.iosif.androidtvromania");

            //Bundle data = new Bundle();
            //data.putInt(STATION_ID, station_id);

            //intent.putExtra("video_player_extras", data);

            startActivity(intent);
            finish();
        }else {
            local_stream_url = c.getUrl();
            startPlay();
            close_channel_list();
        }


    }

    private boolean process_intent(Intent i) {

        if(i==null) return false;
        Bundle d = i.getExtras();
        if(d!=null){
            int dd = d.getInt(STATION_ID);
                station_id = dd;
                Log.w(STATION_ID,dd+"");
        }
        String s = i.getStringExtra(STATION_ID);
        if(s!=null)
        Log.w("uriString",s);


        String link = i.getDataString();
        if(link!=null){
            local_stream_url = link;
            startPlay();
            return true;
        }
        return false;

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/



    public void startPlay() {

            if (!TextUtils.isEmpty(local_stream_url)) {
                //mVideoView.setVideoPath(local_stream_url);
                mVideoView.stopPlayback();
                mVideoView.setVideoURI(Uri.parse(local_stream_url));
                //mVideoView.requestFocus();
                mVideoView.start();
                video_play = true;
            }//else Snackbar.make(view, "Stream url is invalid!", Snackbar.LENGTH_SHORT).show();
    }

    public void stopPlay(){
        mVideoView.pause();
        video_play = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(BroadcastReceiver_SOP, filter_SOP);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(BroadcastReceiver_SOP);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(error_txt!=null){
            String t = error_txt.getText()+"\nkeyboard_event"+keyCode;
            error_txt.setText(t);
        }
        switch(keyCode){
            case 23:
               //select_key
            case 19:
                if(!list_oppened)
                open_channel_list();
            case 20:
                if(!list_oppened)
                open_channel_list();
            case 21:
                //stanga
            case 22:
                //dreapta

        }
        return super.onKeyUp(keyCode,event);
    }


    private void open_channel_list(){
        list_oppened = true;
        lista_canale.animate()
                //.translationX(-convertDpToPixel(201,this))
                .alpha(1.0f);
        lista_canale.requestFocus();

    }
    private void close_channel_list(){
        list_oppened = false;
        lista_canale.animate()
                //.translationX(convertDpToPixel(201,this))
                .alpha(0.0f);

    }

}


