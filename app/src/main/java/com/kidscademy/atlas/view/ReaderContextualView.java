package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.activity.ReaderActivity;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.EventsTree;
import com.kidscademy.atlas.model.ReaderAction;
import com.kidscademy.atlas.util.BitmapLoader;
import com.kidscademy.atlas.util.Player;

/**
 * View for atlas object contextual data, specifically contextual image. Every atlas object has an image that represent the object in its
 * natural environment or context. For example a bird in a tree or a musical instrument played by a musician.
 * <p>
 * Beside displaying object contextual image this view deals with audio sample. An object has an optional
 * audio sample; if present this view displays the sample title and a play button.
 *
 * @author Iulian Rotaru
 */
public class ReaderContextualView extends ConstraintLayout implements ReaderSectionView, View.OnClickListener, Player.StateListener {
    private ImageView imageView;
    private TextView captionText;
    private TextView sampleTitle;
    private ImageView playButton;

    private Group captionGroup;
    private Group audioGroup;
    private Group graphicsGroup;
    private ImageView waveformView;

    @Nullable
    private ReaderAction.Listener listener;
    private AtlasObject atlasObject;
    private boolean playing;
    private boolean tablet;

    /**
     * Create reader contextual view instance. This custom view should be created only from activities
     * prepared to handle user actions, that is, that implement {@link ReaderAction.Listener} interface.
     *
     * @param context view context,
     * @param attrs   attributes set.
     * @throws ClassCastException if context does not implement {@link ReaderAction.Listener}.
     */
    public ReaderContextualView(Context context, AttributeSet attrs) throws ClassCastException {
        super(context, attrs);
        if (context instanceof ReaderAction.Listener) {
            listener = (ReaderAction.Listener) context;
        }
        if (context instanceof EventsTree) {
            ((EventsTree) context).registerListener(Player.StateListener.class, this);
        }
        if (context instanceof ReaderActivity) {
            tablet = ((ReaderActivity) context).isTablet();
        }
        inflate(getContext(), R.layout.reader_contextual, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        imageView = findViewById(R.id.contextual_image);
        captionText = findViewById(R.id.contextual_caption_text);
        sampleTitle = findViewById(R.id.contextual_sample_title);

        playButton = findViewById(R.id.contextual_play_button);
        playButton.setOnClickListener(this);

        captionGroup = findViewById(R.id.contextual_caption);
        audioGroup = findViewById(R.id.contextual_audio);
        graphicsGroup = findViewById(R.id.contextual_graphics);
        waveformView = findViewById(R.id.contextual_waveform);
    }

    public void update(@NonNull AtlasObject atlasObject) {
        this.atlasObject = atlasObject;
        BitmapLoader loader = new BitmapLoader(getContext(), atlasObject.getContextualPath(), imageView);
        loader.start();
        // this image view tag is used by espresso tests
        imageView.setTag(atlasObject.getContextualPath());

        audioGroup.setVisibility(View.GONE);
        captionGroup.setVisibility(View.GONE);
        if (graphicsGroup != null) {
            graphicsGroup.setVisibility(View.GONE);
        }

        if (atlasObject.hasAudioSample()) {
            audioGroup.setVisibility(View.VISIBLE);
            sampleTitle.setText(atlasObject.getAudioSampleTitle());
            new BitmapLoader(getContext(), atlasObject.getWaveformPath(), waveformView, 1).start();
            return;
        }

        if (atlasObject.hasContextualCaption()) {
            captionGroup.setVisibility(View.VISIBLE);
            captionText.setText(atlasObject.getContextualCaption());
            return;
        }

        if (graphicsGroup != null) {
            graphicsGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contextual_play_button) {
            playButton.setImageResource(playing ? R.drawable.action_play_sample : R.drawable.action_stop_sample);
            playing = !playing;
            if (listener != null) {
                listener.onReaderAction(playing ? ReaderAction.PLAY_SAMPLE : ReaderAction.STOP_SAMPLE, atlasObject.getIndex());
            }
        }
    }

    @Override
    public void onPlayerStateChanged(Player.State state) {
        // IDLE is generated by player when finishes playing
        if (state == Player.State.IDLE) {
            playing = false;
            playButton.setImageResource(R.drawable.action_play_sample);
        }
    }
}
