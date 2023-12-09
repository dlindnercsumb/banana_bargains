package com.example.bananabargains;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.bananabargains.DB.AppDatabase;
import com.example.bananabargains.DB.Banana;
import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.DB.Cart;

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
    private BananaBargainsDAO mBananaBargainsDAO;
    private int mUserId;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
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
                    getDatabase();

                    // get bananas linked to user
                    List<Cart> carts = mBananaBargainsDAO.findCartByUserId(mUserId);

                    // represents the banana being clicked in recyclerview
                    Banana currentBanana = localBananaSet.get(getAdapterPosition());
                    Log.d("AddButton", currentBanana.getBananaDescription() + " was clicked!");

                    // create a new cart
                    Cart cart = new Cart(mUserId, currentBanana.getBananaId());
                    // add item to database
                    mBananaBargainsDAO.insert(cart);

                    // ==== OPTIONAL DEBUG INFO ====
                    carts = mBananaBargainsDAO.findCartByUserId(mUserId);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Cart Id: " + cart.getCartId() + "\n");
                    // output a log for the list of items the user has
                    for (Cart c : carts) {
                        sb.append("\tUser Id: ");
                        sb.append(c.getUserId());
                        sb.append(" | ");
                        sb.append("Banana Id: ");
                        sb.append(c.getBananaId());
                        sb.append("\n");
                    }
                    Log.d("AddButton", "Carts: \n" + sb);
                    // =============================
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
    public BananaListAdapter(Context context, List<Banana> bananas, int mUserId) {
        this.context = context;
        this.localBananaSet = bananas;
        this.mUserId = mUserId;

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

    private void getDatabase() {
        mBananaBargainsDAO= Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BananaBargainsDAO();
    }

}
