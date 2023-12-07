package com.example.bananabargains;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bananabargains.DB.Banana;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the list of bananas into views that can be displayed
 */
public class BananaListAdapter extends RecyclerView.Adapter<BananaListAdapter.ViewHolder> {
    private Context context; // of the activity using this adapter, either MainActivity or AdminLanding... I think
    private List<String> localDataStrings;
    private List<Double> localDataDoubles;
    private List<Banana> localBananaSet;
    // TODO: GIVE BANANALISTADAPTER ACCESS TO CURRENT USER SO THAT CARTS CAN BE LINKED

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewForString; // holds descriptions of bananas
        private final TextView textViewForDouble; // holds prices of bananas
        private final Button addButton;

        public ViewHolder(View view) {
            super(view);

            textViewForString = (TextView) view.findViewById(R.id.bananaDescriptionText);
            textViewForDouble = (TextView) view.findViewById(R.id.bananaPriceText);
            addButton = (Button) view.findViewById(R.id.userAddBananaButton);

            // when "Add" is clicked
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("AddButton", "CLICK: " + textViewForString.getText().toString());
                    // TODO: MAKE CHANGES TO USER'S CART
                    // if user doesn't have a cart
                        // create a new cart
                        // add item to cart
                    // else
                        // get cart that belongs to user
                        // add item to cart
                }
            });
        }


        public TextView getTextViewForString() {
            return textViewForString;
        }
        public TextView getTextViewForDouble() {
            return textViewForDouble;
        }
    }

    /**
     * Constructor: Initializes the adapter from list of bananas
     * @param context context of the activity using this adapter
     * @param bananas list of bananas which came from database
     */
    public BananaListAdapter(Context context, List<Banana> bananas) {
        this.context = context;
        this.localBananaSet = bananas;

        // create list of descriptions and prices from bananas list
        this.localDataStrings = new ArrayList<String>();
        this.localDataDoubles = new ArrayList<Double>();
        for (Banana b : bananas) {
            localDataStrings.add(b.getBananaDescription());
            localDataDoubles.add(b.getBananaPrice());
            Log.d("InsertingBananaWithAdapter", "BananaListAdapter: " +
                    b.getBananaDescription() + b.getBananaPrice());
        }
    }

    /**
     * Create new views (invoked by the layout manager)
     * @param viewGroup The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return Viewholder that holds the view to display
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     *
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextViewForString().setText(localDataStrings.get(position));

        // format the Double price
        String formattedPrice =  String.format("$%.2f",localDataDoubles.get(position));
        viewHolder.getTextViewForDouble().setText(formattedPrice);

        // uncomment this for unformatted version for Double price
        //viewHolder.getTextViewForDouble().setText(localDataDoubles.get(position).toString());
    }

    /**
     * used to get the size of the local list of bananas (should be same size as list from database)
     * @return size of set of bananas
     */
    @Override
    public int getItemCount() {
        return localBananaSet.size();
    }
}
