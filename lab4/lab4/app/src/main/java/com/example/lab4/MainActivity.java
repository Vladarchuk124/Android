package com.example.lab4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.lab4.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ExoPlayer player;
    private Uri currentMediaUri;

    private final ActivityResultLauncher<String[]> openDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                    currentMediaUri = uri;
                    binding.tvSelected.setText("Обрано: " + uri.toString());
                    prepareAndPlay(uri, isVideoSelected());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initPlayer();
        createDemoFilesInInternalStorage();
        setupButtons();
    }

    private void initPlayer() {
        player = new ExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(player);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(androidx.media3.common.PlaybackException error) {
                Toast.makeText(MainActivity.this,
                        "Помилка відтворення: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupButtons() {
        binding.btnPlayInternal.setOnClickListener(v -> playFromInternalStorage());

        binding.btnPickFile.setOnClickListener(v -> {
            if (isVideoSelected()) {
                openDocumentLauncher.launch(new String[]{"video/*"});
            } else {
                openDocumentLauncher.launch(new String[]{"audio/*"});
            }
        });

        binding.btnPlayUrl.setOnClickListener(v -> {
            String url = binding.etUrl.getText().toString().trim();
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(this, "Введіть URL", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = Uri.parse(url);
            currentMediaUri = uri;
            binding.tvSelected.setText("URL: " + url);
            prepareAndPlay(uri, isVideoSelected());
        });

        binding.btnPlay.setOnClickListener(v -> {
            if (player != null) {
                player.play();
            }
        });

        binding.btnPause.setOnClickListener(v -> {
            if (player != null) {
                player.pause();
            }
        });

        binding.btnStop.setOnClickListener(v -> {
            if (player != null) {
                player.stop();
                player.clearMediaItems();
            }
        });
    }

    private boolean isVideoSelected() {
        return binding.rbVideo.isChecked();
    }

    private void playFromInternalStorage() {
        File mediaFile;

        if (isVideoSelected()) {
            mediaFile = new File(getFilesDir(), "sample_video.mp4");
        } else {
            mediaFile = new File(getFilesDir(), "sample_audio.mp3");
        }

        if (!mediaFile.exists()) {
            Toast.makeText(this,
                    "Файл у внутрішньому сховищі не знайдено: " + mediaFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
            return;
        }

        Uri uri = Uri.fromFile(mediaFile);
        currentMediaUri = uri;
        binding.tvSelected.setText("Внутрішній файл: " + mediaFile.getAbsolutePath());
        prepareAndPlay(uri, isVideoSelected());
    }

    private void prepareAndPlay(Uri uri, boolean isVideo) {
        if (player == null) return;

        MediaItem mediaItem;

        if (isVideo) {
            mediaItem = new MediaItem.Builder()
                    .setUri(uri)
                    .setMimeType(MimeTypes.VIDEO_MP4)
                    .build();
            binding.playerView.setUseController(true);
            binding.playerView.setVisibility(android.view.View.VISIBLE);
        } else {
            mediaItem = new MediaItem.Builder()
                    .setUri(uri)
                    .setMimeType(MimeTypes.AUDIO_MPEG)
                    .build();
            binding.playerView.setUseController(true);
            binding.playerView.setVisibility(android.view.View.VISIBLE);
        }

        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void createDemoFilesInInternalStorage() {
        copyRawToInternalIfNeeded(R.raw.sample_audio, "sample_audio.mp3");
        copyRawToInternalIfNeeded(R.raw.sample_video, "sample_video.mp4");
    }

    private void copyRawToInternalIfNeeded(int rawResId, String targetName) {
        File outFile = new File(getFilesDir(), targetName);
        if (outFile.exists()) return;

        try (InputStream in = getResources().openRawResource(rawResId);
             FileOutputStream out = new FileOutputStream(outFile)) {

            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            Toast.makeText(this,
                    "Не вдалося скопіювати demo-файл: " + targetName,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player == null) {
            initPlayer();
            if (currentMediaUri != null) {
                prepareAndPlay(currentMediaUri, isVideoSelected());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}