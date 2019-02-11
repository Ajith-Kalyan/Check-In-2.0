package gavs.first.com.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {


    private List<ListItem> listData;
    Context context;
    Activity activity;
    private ArrayList<UsageEvents.Event> mEvents;
    int claimbtnCount = 0;
    private FragmentManager fm;


    PopupWindow popupWindow;


    public MyListAdapter(List<ListItem> listData, Context context, Activity activity) {
        this.listData = listData;
        this.context = context;
        this.activity = activity;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.lost_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ListItem myListItem = listData.get(position);
        holder.mainText.setText(myListItem.title);
        holder.subText.setText(myListItem.description);
        holder.itemId.setText(myListItem.id);


        holder.claimbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                Fragment prev = activity.getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                CustomDialogFragment dialogFragment = new CustomDialogFragment(activity,"lost&found");
                dialogFragment.setValues(holder.itemId.getText().toString());
                dialogFragment.show(ft, "dialog");

//                LayoutInflater layoutInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View customView = layoutInflater.inflate(R.layout.popup_layout,null);
//
//
////                CustomDialogFragment dialogFragment = new CustomDialogFragment();
////                dialogFragment.show(fm,"dialog");
//
//                //instantiate popup window
//                //popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//
//                //display the popup window
//                popupWindow.showAtLocation(holder.itemLayout, Gravity.CENTER, 0, 0);



            }
        });

    }




    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView mainText, subText;
        TextView itemId;
        LinearLayout itemLayout;
        Button claimbutton;

        public ViewHolder(View itemView) {
            super(itemView);
            itemId = itemView.findViewById(R.id.itemid);
            claimbutton = itemView.findViewById(R.id.claimbutton);
            mainText = itemView.findViewById(R.id.tvmaintext);
            subText = itemView.findViewById(R.id.tvsubtext);
            itemLayout = itemView.findViewById(R.id.itemlayout);

        }
    }
}
