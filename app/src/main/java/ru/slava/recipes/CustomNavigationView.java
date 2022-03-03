package ru.slava.recipes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.rtoshiro.secure.SecureSharedPreferences;
import com.google.android.material.internal.ScrimInsetsFrameLayout;



@SuppressLint("RestrictedApi")
public class CustomNavigationView extends ScrimInsetsFrameLayout {
    String APP_PREFERENCES_PHONE = "phone";
    private NavAdapter adapter;
    private Drawable background;
    private LinearLayout linearLayout,mainLinearLayout;
    private View[] childView;
    public static final int FULL_SCROLLABLE = -1;
    public static final int MENU_ITEM_SCROLLABLE = -2;
    private static NavigationItemSelectedListner navigationItemSelectedListner;
    private int state = FULL_SCROLLABLE;
    private boolean isHeaderDrawn=false;
    private final Context mCtx;
    public CustomNavigationView(Context context) {
        super(context);
        init(context);
        this.mCtx = context;
    }


    public CustomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.mCtx = context;
    }

    private void init(Context context) {
        FrameLayout.LayoutParams listParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ScrollView scrollView = new ScrollView(context);
        linearLayout = new LinearLayout(context);
        mainLinearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(listParams);
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLinearLayout.setLayoutParams(listParams);
        linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white_50));
        linearLayout.setPadding(0, 40, 0, 40);
        listParams.gravity = Gravity.START;
        scrollView.setLayoutParams(listParams);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);
        if (background != null)
            setBackground(background);
        else
            setBackgroundColor(0xffffffff);
        scrollView.addView(linearLayout);
        mainLinearLayout.addView(scrollView);
        addView(mainLinearLayout);
        setFitsSystemWindows(true);

    }

    /**
     * Sets a List adapter to display navigation items
     *
     * @param adapter adapter to set the nav action items
     */
    public void setAdapter(final NavAdapter adapter, Activity activity, DrawerLayout drawer) {
        this.adapter = adapter;
        childView = new View[adapter.getCount()];
        LinearLayout lin, lin1;
        LinearLayout.LayoutParams listParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lin = new LinearLayout(mCtx);
        lin.setLayoutParams(listParams1);
        lin.setOrientation(LinearLayout.HORIZONTAL);
        lin1 = new LinearLayout(mCtx);
        LinearLayout.LayoutParams listParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listParams3.setMargins(0, 60, 0, 0);
        lin1.setLayoutParams(listParams3);
        lin1.setOrientation(LinearLayout.HORIZONTAL);

        for (int index = 0; index < adapter.getCount(); index++) {

            childView[index] = adapter.getView(index, null, this);
            childView[index].setTag(index);
            if(index==0) {
                childView[0].setSelected(true);
                TextView txt = childView[0].findViewById(R.id.text1);
                txt.setTextColor(ContextCompat.getColor(activity, R.color.ed3851));
            }
            final int finalIndex = index;
            int finalIndex1 = index;
            childView[index].setOnClickListener(v -> {
                if(finalIndex1==0) {
                    if(!childView[finalIndex1].isSelected()) {
                        GlavActivity gla = (GlavActivity) activity;
                        gla.navController.navigate(R.id.ideas);
                    }
                }
                if(finalIndex1==1) {
                    if(!childView[finalIndex1].isSelected()) {
                        GlavActivity gla = (GlavActivity) activity;
                        gla.navController.navigate(R.id.news);
                    }
                }
                if(finalIndex1==5) {
                    if(!childView[finalIndex1].isSelected()) {
                        SecureSharedPreferences mSettings = new SecureSharedPreferences(mCtx);
                        SecureSharedPreferences.Editor editor = mSettings.edit();
                        editor.remove(APP_PREFERENCES_PHONE);
                        editor.apply();
                        Intent intent = new Intent(mCtx, MainActivity.class);
                        mCtx.startActivity(intent);
                    }
                }
                for (int innerIndex = 0; innerIndex < adapter.getCount(); innerIndex++) {
                    if ((int) childView[innerIndex].getTag() != finalIndex) {
                        childView[innerIndex].setSelected(false);
                        TextView txt = childView[innerIndex].findViewById(R.id.text1);
                        txt.setTextColor(ContextCompat.getColor(activity, R.color.c919191));
                    } else {
                        childView[innerIndex].setSelected(true);
                        TextView txt = childView[innerIndex].findViewById(R.id.text1);
                        txt.setTextColor(ContextCompat.getColor(activity, R.color.ed3851));
                    }
                }
                if (navigationItemSelectedListner != null)
                    navigationItemSelectedListner.onItemSelected(childView[finalIndex], finalIndex);
                drawer.closeDrawer(GravityCompat.START);
            });
            LinearLayout.LayoutParams listParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            childView[index].setLayoutParams(listParams2);
            if(index==0) {
                lin.addView(childView[index]);
                linearLayout.addView(lin);
            }
            if(index==1) {
                lin.addView(childView[index]);
            }
            if(index==2) {
                lin.addView(childView[index]);
            }
            if(index==3) {
                lin1.addView(childView[index]);
                linearLayout.addView(lin1);
            }
            if(index==4) {
                lin1.addView(childView[index]);
            }
            if(index==5) {
                lin1.addView(childView[index]);
            }
        }
    }

    /**
     * Sets the header view for the navigation drawer along with the bottom margin
     *
     * @param view         header view for the navigation drawer
     * @param marginBottom bottom margin of header view
     */
    public void setHeaderView(View view, int marginBottom) {
        if(state==FULL_SCROLLABLE) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, marginBottom);
            view.setLayoutParams(layoutParams);
            linearLayout.addView(view, 0);
        }else if(state==MENU_ITEM_SCROLLABLE){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, marginBottom);
            view.setLayoutParams(layoutParams);
            mainLinearLayout.addView(view, 0);
        }
        isHeaderDrawn=true;
    }

    /**
     * Sets the specified drawable to the background of navigation drawer
     *
     * @param backGround drawable for the background
     */
    public void setBackGround(Drawable backGround) {
        background = backGround;
        setBackground(backGround);
    }

    /**
     * Sets the specified background color to the navigation drawer
     *
     * @param backGround background color for the navigation drawer
     */
    public void setBackGround(int backGround) {
        setBackgroundColor(backGround);
    }

    /**
     * Sets the background color for the item selection indicator
     *
     * @param color background color to set
     */
    public void setSelectionBackGround(int color) {
    }

    /**
     * Returns the nav item adapter
     *
     * @return ListAdapter
     */
    public NavAdapter getAdapter() {
        return adapter;
    }

    /**
     * interface to notify click actions on navigation items
     */
    public interface NavigationItemSelectedListner {
        void onItemSelected(View view, int position);
    }

    /**
     * Sets a item click listner,which notifies the click actions on the action items
     *
     * @param navigationItemSelectedListner listner notifies item click with particular position
     */
    public void setOnNavigationItemSelectedListner(NavigationItemSelectedListner navigationItemSelectedListner) {
        CustomNavigationView.navigationItemSelectedListner = navigationItemSelectedListner;
    }

    /**
     * Sets scroll state to navigation view
     *
     * @param state FULL_SCROLLABLE - whole layout scrollable
     *              MENU_ITEM_SCROLLABLE - only menu items will be scrollable
     */
    public void setScrollState(int state) {
        if(this.state!=state) {
            if(isHeaderDrawn) {
                if (this.state == FULL_SCROLLABLE) {
                    View removedView = linearLayout.getChildAt(0);
                    linearLayout.removeViewAt(0);
                    mainLinearLayout.addView(removedView,0);
                }else if(this.state ==MENU_ITEM_SCROLLABLE){
                    View removedView = mainLinearLayout.getChildAt(0);
                    mainLinearLayout.removeViewAt(0);
                    linearLayout.addView(removedView,0);
                }

            }
        }
        this.state = state;
    }

}


