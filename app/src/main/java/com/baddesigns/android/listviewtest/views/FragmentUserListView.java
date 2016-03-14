package com.baddesigns.android.listviewtest.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baddesigns.android.listviewtest.R;
import com.baddesigns.android.listviewtest.models.Item;
import com.baddesigns.android.listviewtest.models.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jon-Ross on 13/03/2016.
 */
public class FragmentUserListView extends Fragment {

    private static final String TAG = FragmentUserListView.class.getSimpleName();

    private RecyclerView mRecyclerView;

    public static FragmentUserListView getInstance() {
        return new FragmentUserListView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);

        MenuItem subtitleItem = menu.findItem(R.id.new_screen);
        subtitleItem.setTitle("Search Screen");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, FragmentSearchListView.getInstance())
                .commit();
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        List<Item> items = new ArrayList<>();
        Server.getInstance().copyUserItemsToList(items);

        mRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new ListAdapter(items));

        return v;
    }

    private void updateListAdapter() {
        ListAdapter adapter = (ListAdapter) mRecyclerView.getAdapter();
        List<Item> items = adapter.mItems;
        Server.getInstance().copyUserItemsToList(items);
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

        private List<Item> mItems;

        public ListAdapter(List<Item> items) {
            mItems = items;
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.user_list_item_view, parent, false);
            return new ListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            Item item = mItems.get(position);
            holder.bindItemToView(item);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {

        private Item mItem;

        private Button mLeftTouchView;
        private Button mRightTouchView;
        private TextView mItemName;
        private TextView mPriceView;
        private TextView mQuantityView;

        public ListViewHolder(View itemView) {
            super(itemView);

            mLeftTouchView = (Button)itemView.findViewById(R.id.left_touch);
            mLeftTouchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // increase quantity of server item
                    int increment = 1;
                    int position = Server.getInstance().getUserItems().indexOf(mItem);
                    Log.d(TAG, "position: " + position);
                    boolean addAll = Server.getInstance()
                            .increaseQuantityOfUserListItem(mItem, increment);
                    Log.d(TAG, "added all: " + addAll);
                    ListAdapter adapter = (ListAdapter) mRecyclerView.getAdapter();
                    if(addAll) {
                        Toast.makeText(getActivity(), String.valueOf(increment) + " "
                                + mItemName.getText() + " added",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "maximum items added",
                                Toast.LENGTH_SHORT).show();
                    }
                    updateListAdapter();
                    adapter.notifyItemChanged(position);
                }
            });
            mRightTouchView = (Button)itemView.findViewById(R.id.right_touch);
            mRightTouchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // decrease quantity of server item
                    int decrement = 1;
                    int position = Server.getInstance().getUserItems().indexOf(mItem);
                    Log.d(TAG, "position: " + position);
                    boolean removeAll = !Server.getInstance()
                                               .decreaseQuantityOfUserListItem(
                                                       mItem, decrement);
                    Log.d(TAG, "removed all: " + removeAll);
                    updateListAdapter();
                    ListAdapter adapter = (ListAdapter) mRecyclerView.getAdapter();
                    if(removeAll) {
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(getActivity(), "all " + mItemName.getText() + " removed",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.notifyItemChanged(position);
                        Toast.makeText(getActivity(), String.valueOf(decrement) + " "
                                + mItemName.getText() + " removed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mItemName = (TextView)itemView.findViewById(R.id.name);
            mPriceView = (TextView)itemView.findViewById(R.id.price);
            mQuantityView = (TextView)itemView.findViewById(R.id.quantity);
        }

        public void bindItemToView(Item item) {
            mItem = item;
            mItemName.setText(item.getName());
            mPriceView.setText(String.valueOf("£" + item.getPrice()));
            mQuantityView.setText(String.valueOf(item.getQuantity()));
        }
    }
}
