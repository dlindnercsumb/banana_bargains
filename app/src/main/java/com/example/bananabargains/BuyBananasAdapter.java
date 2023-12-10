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

import java.util.List;

public class BuyBananasAdapter extends RecyclerView.Adapter<BuyBananasAdapter.ViewHolder> {
    private Context context; // of the activity using this adapter
    private BananaBargainsDAO mBananaBargainsDAO;
    private List<Banana> mBananaList;
    private int mUserId;




    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewForDescription; // holds descriptions of bananas
        private final TextView textViewForAmount; // holds quantity of banana
        private final TextView textViewForPrice; // holds prices of bananas
        private final TextView textViewForSubtotal; // holds subtotal for individual banana item
        private final Button removeButton;

        public ViewHolder(View view) {
            super(view);

            textViewForDescription = (TextView) view.findViewById(R.id.buyBananaDescriptionText);
            textViewForPrice = (TextView) view.findViewById(R.id.buyBananaCost);
            textViewForAmount = (TextView) view.findViewById(R.id.buyBananaAmount);
            textViewForSubtotal = (TextView) view.findViewById(R.id.buyBananaSubtotalCost);
            removeButton = (Button) view.findViewById(R.id.userRemoveBananaButton);

            // when "remove" is clicked
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("BuyBananasAdapter", "onClick: Size: " + mBananaBargainsDAO.findCartsByBananaIdAndUserId(mBananaList.get(getAdapterPosition()).getBananaId(),mUserId));
                    // remove cart from table
                    mBananaBargainsDAO.deleteCartFromUser(mUserId, mBananaList.get(getAdapterPosition()).getBananaId());
                    Log.d("BuyBananasAdapter", "onClick: Size: " + mBananaBargainsDAO.findCartsByBananaIdAndUserId(mBananaList.get(getAdapterPosition()).getBananaId(),mUserId));

                    // if the position clicked is a valid position
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        // remove from local list (probably isn't necessary)
                        mBananaList.remove(getAdapterPosition());
                        // remove the view
                        notifyItemRemoved(getAdapterPosition());
                    }
                }
            });
        }


        public TextView getTextViewForDescription() {
            return textViewForDescription;
        }
        public TextView getTextViewForPrice() {
            return textViewForPrice;
        }
        public TextView getTextViewForAmount() {
            return textViewForAmount;
        }
        public TextView getTextViewForSubtotal() {
            return textViewForSubtotal;
        }
    }

    /**
     * Constructor: Initializes the adapter
     * @param context context of the activity using this adapter
     * @param mUserId user id
     */
    public BuyBananasAdapter(Context context, List<Banana> bananas,int mUserId) {
        this.context = context;
        this.mUserId = mUserId;

        // get list of bananas
        mBananaList = bananas;
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

        getDatabase();

        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_buy_bananas, viewGroup, false);

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

        // sets the banana description
        viewHolder.getTextViewForDescription().setText(mBananaList.get(position).getBananaDescription());

        // get banana amount
        List<Cart> carts = mBananaBargainsDAO.findCartsByBananaIdAndUserId(mBananaList.get(position).getBananaId(),mUserId);
        Log.d("BuyBananasAdapter", "Amount: " + carts.size());
        int amount = carts.size();
        // set the banana amount
        viewHolder.getTextViewForAmount().setText("" + amount);

        // get banana subtotal (amount of specific banana owned * banana price)
        double subtotal = amount * mBananaBargainsDAO.getBananaById(mBananaList.get(position).getBananaId()).getBananaPrice();
        String formattedSubtotal = String.format("$%.2f",subtotal);
        viewHolder.getTextViewForSubtotal().setText(formattedSubtotal);

        // format the Double price
        String formattedPrice =  String.format("$%.2f",mBananaList.get(position).getBananaPrice());
        viewHolder.getTextViewForPrice().setText(formattedPrice);
    }

    /**
     * used to get the size of the local list of bananas (should be same size as list from database)
     * @return size of set of bananas
     */
    @Override
    public int getItemCount() {
        return mBananaList.size();
    }

    private void getDatabase() {
        mBananaBargainsDAO= Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BananaBargainsDAO();
    }

}
