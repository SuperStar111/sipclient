package scn.com.sipclient.tab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import scn.com.sipclient.main.CallHistory;
import scn.com.sipclient.main.ContactList;
import scn.com.sipclient.main.FavoriteList;
import scn.com.sipclient.R;


public class MyTabHostProvider extends TabHostProvider 
{
	private Tab favorTab;
	private Tab contactTab;
	private Tab historyTab;
	private Tab moreTab;
	
	private TabView tabView;
	private GradientDrawable gradientDrawable, transGradientDrawable;

	public MyTabHostProvider(Activity context) {
		super(context);
	}

	@Override
	public TabView getTabHost(String category) 
	{
		tabView = new TabView(context);
		tabView.setOrientation(TabView.Orientation.BOTTOM);
		tabView.setBackgroundID(R.drawable.tab_background_gradient);
		
		gradientDrawable = new GradientDrawable(
	            GradientDrawable.Orientation.TOP_BOTTOM,
	            new int[] {0xFFB2DA1D, 0xFF85A315});
	    gradientDrawable.setCornerRadius(0f);
	    gradientDrawable.setDither(true);
	    
	    transGradientDrawable = new GradientDrawable(
	            GradientDrawable.Orientation.TOP_BOTTOM,
	            new int[] {0x00000000, 0x00000000});
	    transGradientDrawable.setCornerRadius(0f);
	    transGradientDrawable.setDither(true);

		favorTab = new Tab(context, category);
		favorTab.setIcon(R.drawable.ic_star_border_white_18dp);
		favorTab.setIconSelected(R.drawable.ic_star_border_white_18dp);
		favorTab.setBtnText("Home");
		favorTab.setBtnTextColor(Color.WHITE);
		favorTab.setSelectedBtnTextColor(Color.BLACK);
//		homeTab.setBtnColor(Color.parseColor("#00000000"));
//		homeTab.setSelectedBtnColor(Color.parseColor("#0000FF"));
		favorTab.setBtnGradient(transGradientDrawable);
		favorTab.setSelectedBtnGradient(gradientDrawable);
		favorTab.setIntent(new Intent(context, FavoriteList.class));

		historyTab = new Tab(context, category);
		historyTab.setIcon(R.drawable.ic_history_white_18dp);
		historyTab.setIconSelected(R.drawable.ic_history_white_18dp);
		historyTab.setBtnText("Share");
		historyTab.setBtnTextColor(Color.WHITE);
		historyTab.setSelectedBtnTextColor(Color.BLACK);
//		shareTab.setBtnColor(Color.parseColor("#00000000"));
//		shareTab.setSelectedBtnColor(Color.parseColor("#0000FF"));
		historyTab.setBtnGradient(transGradientDrawable);
		historyTab.setSelectedBtnGradient(gradientDrawable);
		historyTab.setIntent(new Intent(context, CallHistory.class));



		contactTab = new Tab(context, category);
		contactTab.setIcon(R.drawable.ic_person_white_18dp);
		contactTab.setIconSelected(R.drawable.ic_person_white_18dp);
		contactTab.setBtnText("Contact");
		contactTab.setBtnTextColor(Color.WHITE);
		contactTab.setSelectedBtnTextColor(Color.BLACK);
//		contactTab.setBtnColor(Color.parseColor("#00000000"));
//		contactTab.setSelectedBtnColor(Color.parseColor("#0000FF"));
		contactTab.setBtnGradient(transGradientDrawable);
		contactTab.setSelectedBtnGradient(gradientDrawable);
		contactTab.setIntent(new Intent(context, ContactList.class));

		moreTab = new Tab(context, category);
		moreTab.setIcon(R.drawable.ic_keyboard_white_18dp);
		moreTab.setIconSelected(R.drawable.ic_keyboard_white_18dp);
		moreTab.setBtnText("Keyboard");
		moreTab.setBtnTextColor(Color.WHITE);
		moreTab.setSelectedBtnTextColor(Color.BLACK);
//		moreTab.setBtnColor(Color.parseColor("#00000000"));
//		moreTab.setSelectedBtnColor(Color.parseColor("#0000FF"));
		moreTab.setBtnGradient(transGradientDrawable);
		moreTab.setSelectedBtnGradient(gradientDrawable);
        moreTab.setDialog(null);
//		moreTab.setIntent(new Intent(context, MoreActivity.class));

		tabView.addTab(favorTab);
		tabView.addTab(historyTab);
		tabView.addTab(contactTab);
		tabView.addTab(moreTab);

		return tabView;
	}
}