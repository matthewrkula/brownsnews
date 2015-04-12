package com.mattkula.brownsnews.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mattkula.brownsnews.R;

import java.util.ArrayList;

public class ScheduleFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new GameAdapter());
    }

    @Override
    public void onResume() {
        super.onResume();
        getListView().setPadding(0, getActivity().getActionBar().getHeight(), 0, 0);
        getListView().setBackgroundColor(getResources().getColor(R.color.primary_dark));
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
    }

    private ArrayList<Game> schedule = new ArrayList<Game>(){{
//        add(new Game("", 0, "", false));
//        add(new Game("", 0, "", true));
//        add(new Game("", 0, "", true));
//        add(new Game("COMING SOON", 0, "", true));
//        add(new Game("Titans", R.drawable.ten, "Oct 05 1:00 PM", false));
//        add(new Game("Steelers", R.drawable.pit, "Oct 12 1:00 PM", true));
//        add(new Game("Jaguars", R.drawable.jax, "Oct 19 1:00 PM", false));
//        add(new Game("Raiders", R.drawable.oak, "Oct 26 4:25 PM", true));
//        add(new Game("Buccaneers", R.drawable.tb, "Nov 02 1:00 PM", true));
//        add(new Game("Bengals", R.drawable.cin, "Nov 06 8:25 PM", false));
//        add(new Game("Texans", R.drawable.hou, "Nov 16 1:00 PM", true));
//        add(new Game("Falcons", R.drawable.atl, "Nov 23 1:00 PM", false));
//        add(new Game("Bills", R.drawable.buf, "Nov 30 1:00 PM", false));
//        add(new Game("Colts", R.drawable.ind, "Dec 07 1:00 PM", true));
//        add(new Game("Bengals", R.drawable.cin, "Dec 14 1:00 PM", true));
//        add(new Game("Panthers", R.drawable.car, "Dec 21 1:00 PM", false));
//        add(new Game("Ravens", R.drawable.bal, "Dec 28 1:00 PM", false));
    }};

    private class Game {

        boolean home;
        int opponentImage;
        String opponent;
        String time;

        public Game(String o, int oi, String t, boolean h){
            this.opponent = o;
            this.opponentImage = oi;
            this.time = t;
            this.home = h;
        }

        @Override
        public String toString() {
            //TODO Add @ symbol back
            return (this.home ? "" : " ") + opponent;
        }
    }

    private class GameAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return schedule.size();
        }

        @Override
        public Object getItem(int i) {
            return schedule.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null){
                view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_game, null, false);
            }

            Game game = (Game)getItem(i);

            ((ImageView)view.findViewById(R.id.image_opponent)).setImageDrawable(game.opponentImage != 0 ? getResources().getDrawable(game.opponentImage) : null);
            ((TextView)view.findViewById(R.id.text_opponent_name)).setText(game.toString());
            ((TextView)view.findViewById(R.id.text_game_date)).setText(game.time);

            return view;
        }
    }
}
