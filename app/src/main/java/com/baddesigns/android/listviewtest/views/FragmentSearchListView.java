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
public class FragmentSearchListView extends Fragment {

    private static final String TAG = FragmentSearchListView.class.getSimpleName();

    private RecyclerView mRecyclerView;

    public static FragmentSearchListView getInstance() {
        return new FragmentSearchListView();
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
        subtitleItem.setTitle("User List Screen");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container,
                        FragmentUserListView.getInstance())
                .commit();
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        List<Item> searchItems = new ArrayList<>();
        updateSearchItems(searchItems);

        mRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new ListAdapter(searchItems));

        return v;
    }

    private void updateSearchItems(List<Item> searchItems) {
        Server.getInstance().updateSearchItems(searchItems, 1);
    }

    private void updateListAdapter() {
        ListAdapter adapter = (ListAdapter) mRecyclerView.getAdapter();
        List<Item> items = adapter.mItems;
        updateSearchItems(items);
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

        private List<Item> mItems;

        public ListAdapter(List<Item> items) {
            mItems = items;
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.search_list_item_view, parent, false);
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
                    List<Item> searchItems = new ArrayList<>();
                    updateSearchItems(searchItems);
                    int position = searchItems.indexOf(mItem);
                    Log.d(TAG, "position: " + position);
                    int result = Server.getInstance()
                            .transferQuantityFromAllToUserListItem(mItem, increment);
                    if(result == Server.TRANSFERRED_ALL) {
                        Toast.makeText(getActivity(), String.valueOf(increment) + " "
                                + mItemName.getText() + " added", Toast.LENGTH_SHORT).show();
                    } else if(result == Server.MAX_QUANTITY_REACHED){
                        Toast.makeText(getActivity(), "maximum items added",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "out of stock",
                                Toast.LENGTH_SHORT).show();
                    }
                    updateListAdapter();
                    ListAdapter adapter = (ListAdapter) mRecyclerView.getAdapter();
                    adapter.notifyItemChanged(position);
                }
            });
            mRightTouchView = (Button)itemView.findViewById(R.id.right_touch);
            mRightTouchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // decrease quantity of server item
                    int decrement = 1;
                    List<Item> searchItems = new ArrayList<>();
                    updateSearchItems(searchItems);
                    int position = searchItems.indexOf(mItem);
                    Log.d(TAG, "position: " + position);
                    Item userItem = Server.getInstance().getItemFromUserList(mItem);
                    int result = Server.getInstance()
                                       .transferQuantityFromUserListToAllItem(
                                               userItem, decrement);
                    updateListAdapter();
                    ListAdapter adapter = (ListAdapter) mRecyclerView.getAdapter();
                    adapter.notifyItemChanged(position);
                    if(result == Server.OUT_OF_STOCK) {
                        Toast.makeText(getActivity(), String.valueOf(decrement) + " "
                                + mItemName.getText() + " removed",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "all " + mItemName.getText() + " removed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mItemName = (TextView)itemView.findViewById(R.id.name);
            mPriceView = (TextView)itemView.findViewById(R.id.price);
            mQuantityView = (TextView)itemView.findViewById(R.id.quantity);
        }

        public void bindItemToView(Item item) {
            Log.d(TAG, "binding " + item.getName() + " to view");
            mItem = item;
            mItemName.setText(item.getName());
            if(item.getQuantity() > 0) {
                setViewVisibility(mLeftTouchView, View.VISIBLE);
                mPriceView.setText(String.valueOf("£" + item.getPrice()));
            } else {
                setViewVisibility(mLeftTouchView, View.INVISIBLE);
                mPriceView.setText("OOS");
            }
            int quantity = Server.getInstance().getQuantityOfItemInUserList(item);
            if(quantity == -1) {
                setViewVisibility(mRightTouchView, View.INVISIBLE);
                mQuantityView.setText("");
            } else {
                setViewVisibility(mRightTouchView, View.VISIBLE);
                mQuantityView.setText(String.valueOf(quantity));
            }
        }

        private void setViewVisibility(View view, int visibility) {
            if(view.getVisibility() != visibility) {
                view.setVisibility(visibility);
            }
        }
    }
}
