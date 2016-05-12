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
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import com.mob.tools.gui.PullToRefreshListAdapter;
import com.mob.tools.gui.PullToRefreshView;
import com.mob.tools.utils.UIHandler;

/** 濂藉弸鍒楄〃鐨勯�閰嶅櫒 */
public class FriendAdapter extends PullToRefreshListAdapter implements PlatformActionListener {
	private FriendListPage activity;
	private boolean hasNext;
	private Platform platform;
	/** 璇锋眰濂藉弸鍒楄〃鏃讹紝姣忛〉15涓�*/
	private final int pageCount = 15;
	/** 褰撳墠鐨勫ソ鍙嬪垪琛ㄦ槸绗嚑椤�*/
	private int curPage;
	/** 濂藉弸鍒楄〃鏁版嵁 */
	private ArrayList<Following> follows;
	/** 鍒ゆ柇褰撳墠鐨勫ソ鍙嬪垪琛ㄦ暟鎹笌璇锋眰鐨勬柊鏁版嵁鏄惁鏈夐噸澶�*/
	private HashMap<String, Boolean> map;
	/** 濂藉弸鍒楄〃鐨勫ご閮╒iew */
	private PRTHeader llHeader;
	/** 鏍规嵁璁捐锛屾寜鐓ф瘮渚嬫潵甯冨眬锛屼互姝ゆ潵閫傞厤鎵�湁鎵嬫満 */
	private float ratio;

	public FriendAdapter(FriendListPage activity, PullToRefreshView view) {
		super(view);
		this.activity = activity;

		curPage = -1;
		hasNext = true;
		map = new HashMap<String, Boolean>();
		follows = new ArrayList<Following>();

		getListView().setDivider(new ColorDrawable(0xffeaeaea));
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
		getListView().setDividerHeight((int) (ratio < 1 ? 1 : ratio));
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		getListView().setOnItemClickListener(listener);
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
		platform.setPlatformActionListener(this);
	}

	private void next() {
		if (hasNext) {
			platform.listFriend(pageCount, curPage + 1, null);
		}
	}

	public void onComplete(Platform plat, int action, HashMap<String, Object> res) {
		final FollowersResult followersResult = parseFollowers(platform.getName(), res, map);
		if (followersResult == null) {
			UIHandler.sendEmptyMessage(0, new Callback() {
				public boolean handleMessage(Message msg) {
					notifyDataSetChanged();
					return false;
				}
			});
			return;
		}

		hasNext = followersResult.hasNextPage;
		if (followersResult.list != null && followersResult.list.size() > 0) {
			curPage++;
			Message msg = new Message();
			msg.what = 1;
			msg.obj = followersResult.list;
			UIHandler.sendMessage(msg, new Callback() {
				public boolean handleMessage(Message msg) {
					if (curPage <= 0) {
						follows.clear();
					}
					follows.addAll(followersResult.list);
					notifyDataSetChanged();
					return false;
				}
			});
		}
	}

	private FollowersResult parseFollowers(String platform, HashMap<String, Object> res, HashMap<String, Boolean> uidMap) {
		if (res == null || res.size() <= 0) {
			return null;
		}

		boolean hasNext = false;
		ArrayList<Following> data = new ArrayList<Following>();
		if ("SinaWeibo".equals(platform)) {
			// users[id, name, description]
			@SuppressWarnings("unchecked")
			ArrayList<HashMap<String, Object>> users = (ArrayList<HashMap<String,Object>>) res.get("users");
			for (HashMap<String, Object> user : users) {
				String uid = String.valueOf(user.get("id"));
				if (!uidMap.containsKey(uid)) {
					Following following = new Following();
					following.uid = uid;
					following.screenName = String.valueOf(user.get("name"));
					following.description = String.valueOf(user.get("description"));
					following.icon = String.valueOf(user.get("profile_image_url"));
					following.atName = following.screenName;
					uidMap.put(following.uid, true);
					data.add(following);
				}
			}
			hasNext = (Integer) res.get("total_number") > uidMap.size();
		} else if ("TencentWeibo".equals(platform)) {
			hasNext = ((Integer)res.get("hasnext") == 0);
			// info[nick, name, tweet[text]]
			@SuppressWarnings("unchecked")
			ArrayList<HashMap<String, Object>> infos = (ArrayList<HashMap<String,Object>>) res.get("info");
			for (HashMap<String, Object> info : infos) {
				String uid = String.valueOf(info.get("name"));
				if (!uidMap.containsKey(uid)) {
					Following following = new Following();
					following.screenName = String.valueOf(info.get("nick"));
					following.uid = uid;
					following.atName = uid;
					@SuppressWarnings("unchecked")
					ArrayList<HashMap<String, Object>> tweets = (ArrayList<HashMap<String,Object>>) info.get("tweet");
					for (HashMap<String, Object> tweet : tweets) {
						following.description = String.valueOf(tweet.get("text"));
						break;
					}
					following.icon = String.valueOf(info.get("head")) + "/100";
					uidMap.put(following.uid, true);
					data.add(following);
				}
			}
		} else if ("Facebook".equals(platform)) {
			// data[id, name]
			@SuppressWarnings("unchecked")
			ArrayList<HashMap<String, Object>> datas = (ArrayList<HashMap<String,Object>>) res.get("data");
			for (HashMap<String, Object> d : datas) {
				String uid = String.valueOf(d.get("id"));
				if (!uidMap.containsKey(uid)) {
					Following following = new Following();
					following.uid = uid;
					following.atName = "["+uid+"]";
					following.screenName = String.valueOf(d.get("name"));
					@SuppressWarnings("unchecked")
					HashMap<String, Object> picture = (HashMap<String, Object>) d.get("picture");
					if (picture != null) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> pData = (HashMap<String, Object>) picture.get("data");
						following.icon = String.valueOf(pData.get("url"));
					}
					uidMap.put(following.uid, true);
					data.add(following);
				}
			}
			@SuppressWarnings("unchecked")
			HashMap<String, Object> paging = (HashMap<String, Object>) res.get("paging");
			hasNext = paging.containsKey("next");
		} else if ("Twitter".equals(platform)) {
			// users[screen_name, name, description]
			@SuppressWarnings("unchecked")
			ArrayList<HashMap<String, Object>> users = (ArrayList<HashMap<String,Object>>) res.get("users");
			for (HashMap<String, Object> user : users) {
				String uid = String.valueOf(user.get("screen_name"));
				if (!uidMap.containsKey(uid)) {
					Following following = new Following();
					following.uid = uid;
					following.atName = uid;
					following.screenName = String.valueOf(user.get("name"));
					following.description = String.valueOf(user.get("description"));
					following.icon = String.valueOf(user.get("profile_image_url"));
					uidMap.put(following.uid, true);
					data.add(following);
				}
			}
		}

		FollowersResult ret = new FollowersResult();
		ret.list = data;
		ret.hasNextPage = hasNext;
		return ret;
	}

	public void onError(Platform plat, int action, Throwable t) {
		t.printStackTrace();
	}

	public void onCancel(Platform plat, int action) {
		UIHandler.sendEmptyMessage(0, new Callback() {
			public boolean handleMessage(Message msg) {
				activity.finish();
				return false;
			}
		});
	}

	public Following getItem(int position) {
		return follows.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getCount() {
		return follows == null ? 0 : follows.size();
	}

	public View getHeaderView() {
		if (llHeader == null) {
			llHeader = new PRTHeader(getContext());
		}
		return llHeader;
	}

	public void onPullDown(int percent) {
		llHeader.onPullDown(percent);
	}

	public void onRequest() {
		llHeader.onRequest();
		curPage = -1;
		hasNext = true;
		map.clear();
		next();
	}

	public void onReversed() {
		llHeader.reverse();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			FriendListItem llItem = new FriendListItem(parent.getContext(), ratio);
			convertView = llItem;
		}
		FriendListItem llItem = (FriendListItem) convertView;
		llItem.update(getItem(position), isFling());

		if (position == getCount() - 1) {
			next();
		}
		return convertView;
	}

	public static class Following {
		public boolean checked;
		public String screenName;
		public String description;
		public String uid;
		public String icon;
		//@Name 鐢ㄤ簬寰崥绛夋彁绀烘垨鍏宠仈鏌愪釜浜�		
		public String atName;
	}

	private static class FollowersResult {
		public ArrayList<Following> list;
		public boolean hasNextPage = false;
	}

}
