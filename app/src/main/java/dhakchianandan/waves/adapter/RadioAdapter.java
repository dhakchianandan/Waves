package dhakchianandan.waves.adapter;

import android.media.MediaPlayer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import dhakchianandan.waves.R;
import dhakchianandan.waves.model.Radio;

/**
 * Created by Dhakchianandan on 02-07-2015.
 */
public class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.ViewHolder> {

    public static interface Listener {
        public void onClick(int position);
    }

    private List<Radio> radios;
    private Listener listener;

    public RadioAdapter() {
    }

    public RadioAdapter(List<Radio> radios) {
        this.radios = radios;
    }

    public RadioAdapter(List<Radio> radios, Listener listener) {
        this.radios = radios;
        this.listener = listener;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public List<Radio> getRadios() {
        return radios;
    }

    public void setRadios(List<Radio> radios) {
        this.radios = radios;
    }

    @Override
    public RadioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.radio_view, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        CardView radioView = viewHolder.cardView;
        TextView radioName = (TextView)radioView.findViewById(R.id.radio_name);
        ImageView radioImage = (ImageView)radioView.findViewById(R.id.radio_image);

        final Radio radio = radios.get(i);
        radioName.setText(radio.getName());
        radioImage.setImageResource(radio.getImage());

        radioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return radios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView view) {
            super(view);
            cardView = view;
        }
    }
}
