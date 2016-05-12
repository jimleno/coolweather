/*
 * 瀹樼綉鍦扮珯:http://www.mob.com
 * 鎶�湳鏀寔QQ: 4006852216
 * 瀹樻柟寰俊:ShareSDK   锛堝鏋滃彂甯冩柊鐗堟湰鐨勮瘽锛屾垜浠皢浼氱涓�椂闂撮�杩囧井淇″皢鐗堟湰鏇存柊鍐呭鎺ㄩ�缁欐偍銆傚鏋滀娇鐢ㄨ繃绋嬩腑鏈変换浣曢棶棰橈紝涔熷彲浠ラ�杩囧井淇′笌鎴戜滑鍙栧緱鑱旂郴锛屾垜浠皢浼氬湪24灏忔椂鍐呯粰浜堝洖澶嶏級
 *
 * Copyright (c) 2013骞�mob.com. All rights reserved.
 */

package cn.sharesdk.onekeyshare.themes.classic;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeySharePage;
import cn.sharesdk.onekeyshare.OnekeyShareThemeImpl;
import cn.sharesdk.onekeyshare.themes.classic.FriendAdapter.Following;

import com.mob.tools.gui.PullToRefreshView;
import com.mob.tools.utils.R;

/** 缂栬緫鐣岄潰锛孈濂藉弸鏃讹紝寮瑰嚭鐨勫ソ鍙嬪垪琛�*/
public abstract class FriendListPage extends OnekeySharePage implements OnClickListener, OnItemClickListener {
	private static final int DESIGN_LEFT_PADDING = 40;

	private Platform platform;
	private LinearLayout llPage;
	private RelativeLayout rlTitle;
	private TextView tvCancel;
	private TextView tvConfirm;
	private FriendAdapter adapter;
	private int lastPosition = -1;
	/** 灞曠ず濂藉弸鍒楄〃鏃讹紝宸查�鎷╄鈥楡鈥欑殑濂藉弸涓暟 */
	private int checkNum = 0;

	public FriendListPage(OnekeyShareThemeImpl impl) {
		super(impl);
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public void onCreate() {
		activity.getWindow().setBackgroundDrawable(new ColorDrawable(0xfff3f3f3));

		llPage = new LinearLayout(activity);
		llPage.setOrientation(LinearLayout.VERTICAL);
		activity.setContentView(llPage);

		rlTitle = new RelativeLayout(activity);
		float ratio = getRatio();
		int titleHeight = (int) (getDesignTitleHeight() * ratio);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, titleHeight);
		llPage.addView(rlTitle, lp);
		initTitle(rlTitle, ratio);

		View line = new View(activity);
		LinearLayout.LayoutParams lpline = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (ratio < 1 ? 1 : ratio));
		line.setBackgroundColor(0xffdad9d9);
		llPage.addView(line, lpline);

		FrameLayout flPage = new FrameLayout(getContext());
		LinearLayout.LayoutParams lpFl = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpFl.weight = 1;
		flPage.setLayoutParams(lpFl);
		llPage.addView(flPage);

		// 鍏虫敞锛堟垨鏈嬪弸锛夊垪琛�		
		PullToRefreshView followList = new PullToRefreshView(getContext());
		FrameLayout.LayoutParams lpLv = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		followList.setLayoutParams(lpLv);
		flPage.addView(followList);

		adapter = new FriendAdapter(this, followList);
		adapter.setPlatform(platform);
		adapter.setRatio(ratio);
		adapter.setOnItemClickListener(this);
		followList.setAdapter(adapter);

		// 璇锋眰鏁版嵁
		followList.performPulling(true);
	}

	protected abstract float getRatio();

	protected abstract int getDesignTitleHeight();

	private void initTitle(RelativeLayout rlTitle, float ratio) {
		tvCancel = new TextView(activity);
		tvCancel.setTextColor(0xff3b3b3b);
		tvCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvCancel.setGravity(Gravity.CENTER);
		int resId = R.getStringRes(activity, "ssdk_oks_cancel");
		if (resId > 0) {
			tvCancel.setText(resId);
		}
		int padding = (int) (DESIGN_LEFT_PADDING * ratio);
		tvCancel.setPadding(padding, 0, padding, 0);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		rlTitle.addView(tvCancel, lp);
		tvCancel.setOnClickListener(this);

		TextView tvTitle = new TextView(activity);
		tvTitle.setTextColor(0xff3b3b3b);
		tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		tvTitle.setGravity(Gravity.CENTER);
		resId = R.getStringRes(activity, "ssdk_oks_contacts");
		if (resId > 0) {
			tvTitle.setText(resId);
		}
		lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		rlTitle.addView(tvTitle, lp);

		tvConfirm = new TextView(activity);
		tvConfirm.setTextColor(0xffff6d11);
		tvConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvConfirm.setGravity(Gravity.CENTER);
		resId = R.getStringRes(activity, "ssdk_oks_confirm");
		if (resId > 0) {
			tvConfirm.setText(resId);
		}
		tvConfirm.setPadding(padding, 0, padding, 0);
		lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlTitle.addView(tvConfirm, lp);
		tvConfirm.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.equals(tvCancel)) {
			finish();
		} else {
			ArrayList<String> selected = new ArrayList<String>();
			for (int i = 0, size = adapter.getCount(); i < size; i++) {
				if (adapter.getItem(i).checked) {
					selected.add(adapter.getItem(i).atName);
				}
			}

			HashMap<String, Object> res = new HashMap<String, Object>();
			res.put("selected", selected);
			res.put("platform", platform);
			setResult(res);
			finish();
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if ("FacebookMessenger".equals(platform.getName())) {
			if(lastPosition >= 0) {
				Following lastFollwing = adapter.getItem(lastPosition);
				lastFollwing.checked = false;
			}
			lastPosition = position;
		}
		Following following = adapter.getItem(position);
		following.checked = !following.checked;

		if(following.checked) {
			checkNum++;
		} else {
			checkNum--;
		}

		updateConfirmView();
		adapter.notifyDataSetChanged();
	}

	private void updateConfirmView() {
		int resId = R.getStringRes(activity, "ssdk_oks_confirm");
		String confirm = "Confirm";
		if(resId > 0) {
			confirm = getContext().getResources().getString(resId);
		}
		if(checkNum == 0) {
			tvConfirm.setText(confirm);
		} else if(checkNum > 0) {
			tvConfirm.setText(confirm + "(" + checkNum + ")");
		}
	}

}
