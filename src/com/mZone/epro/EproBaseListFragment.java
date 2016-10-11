package com.mZone.epro;

import com.mZone.epro.client.utility.AppLog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class EproBaseListFragment extends EproBaseFragment implements AdapterView.OnItemClickListener {

	private static final String LOG_TAG = "EproBaseListFragment";

	/**  */
	protected RelativeLayout mProgressContainer;
	/**  */
	protected ProgressBar mProgressBar;
	/**  */
	protected RelativeLayout mListViewContainer;
	/**  */
	protected TextView mTvEmpty;
	/**  */
	protected ListView mListView;

	/**  */
	protected ListAdapter mAdapter;
	/**  */
	protected CharSequence mEmptyText;
	/**  */
	protected boolean mListShown;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		AppLog.in(LOG_TAG, "onCreateView() inflater=%s, container=%s, savedInstanceState=%s", inflater, container, savedInstanceState);

		// ビューの作成
		final View view = inflater.inflate(R.layout.epro_base_list_fragment, container, false);

		// ビューを取得
		mProgressContainer = (RelativeLayout) view.findViewById(R.id.epro_base_list_fragment_progress_container);
		mProgressBar = (ProgressBar) mProgressContainer.findViewById(R.id.epro_base_list_fragment_progress_bar);
		mListViewContainer = (RelativeLayout) view.findViewById(R.id.epro_base_list_fragment_list_view_container);
		mTvEmpty = (TextView) mListViewContainer.findViewById(R.id.epro_base_list_fragment_tv_empty);
		mListView = (ListView) mListViewContainer.findViewById(R.id.epro_base_list_fragment_list_view);

		// 変数を初期化
		mAdapter = null;
		mEmptyText = null;

		// EmptyViewを設定
		mListView.setEmptyView(mTvEmpty);

		// リスナーを設定
		mListView.setOnItemClickListener(this);

		// リストビューを非表示
		mListShown = true;
		setListShownNoAnimation(false);

		AppLog.out(LOG_TAG, "onCreateView() v=%s", view);
		return view;
	}

	@Override
	public void onDestroyView() {
		AppLog.in(LOG_TAG, "onDestroyView()");

		// EmptyViewを破棄
		mListView.setEmptyView(null);

		// リスナーを破棄
		mListView.setOnItemClickListener(null);

		// 変数を初期化
		mAdapter = null;
		mEmptyText = null;
		mListShown = false;

		// ビューを破棄
		mProgressContainer = null;
		mProgressBar = null;
		mListViewContainer = null;
		mTvEmpty = null;
		mListView = null;

		// 子クラス→親クラスの順で破棄する
		super.onDestroyView();

		AppLog.out(LOG_TAG, "onDestroyView()");
	}

	/**
	 *
	 * @return
	 */
	public ListView getListView() {
		AppLog.in(LOG_TAG, "getListView()");
		AppLog.out(LOG_TAG, "getListView() ListView=%s", mListView);
		return mListView;
	}

	/**
	 *
	 * @return
	 */
	public ListAdapter getListAdapter() {
		AppLog.in(LOG_TAG, "getListAdapter()");
		AppLog.out(LOG_TAG, "getListAdapter() ListAdapter=%s", mAdapter);
		return mAdapter;
	}

	/**
	 *
	 * @param adapter
	 */
	public void setListAdapter(final ListAdapter adapter) {
		AppLog.in(LOG_TAG, "setListAdapter() adapter=%s", adapter);

		final boolean hadAdapter = (mAdapter != null);
		mAdapter = adapter;
		if (mListView != null) {
			mListView.setAdapter(adapter);
			if (!mListShown && !hadAdapter) {
				setListShown(true, (getView().getWindowToken() != null));
			}
		}

		AppLog.out(LOG_TAG, "setListAdapter()");
	}

	/**
	 *
	 * @return
	 */
	public int getSelectedItemPosition() {
		AppLog.in(LOG_TAG, "getSelectedItemPosition()");

		final int selectedItemPosition = mListView.getSelectedItemPosition();

		AppLog.out(LOG_TAG, "getSelectedItemPosition() selectedItemPosition=%d", selectedItemPosition);
		return selectedItemPosition;
	}

	/**
	 *
	 * @return
	 */
	public long getSelectedItemId() {
		AppLog.in(LOG_TAG, "getSelectedItemId()");

		final long selectedItemId = mListView.getSelectedItemId();

		AppLog.out(LOG_TAG, "getSelectedItemId() selectedItemId=%d", selectedItemId);
		return selectedItemId;
	}

	/**
	 *
	 * @param position
	 */
	public void setSelection(final int position) {
		AppLog.in(LOG_TAG, "setSelection() position=%d", position);

		mListView.setSelection(position);

		AppLog.out(LOG_TAG, "setSelection()");
	}

	/**
	 *
	 * @param text
	 */
	public void setEmptyText(final CharSequence text) {
		AppLog.in(LOG_TAG, "setEmptyText() text=%s", text);

		mTvEmpty.setText(text);
		mEmptyText = text;

		AppLog.out(LOG_TAG, "setEmptyText()");
	}

	/**
	 *
	 * @param shown
	 */
	public void setListShown(final boolean shown) {
		AppLog.in(LOG_TAG, "setListShown() shown=%s", shown);

		// アニメーションありで表示切替
		setListShown(shown, true);

		AppLog.out(LOG_TAG, "setListShown()");
	}

	/**
	 *
	 * @param shown
	 */
	public void setListShownNoAnimation(final boolean shown) {
		AppLog.in(LOG_TAG, "setListShownNoAnimation() shown=%s", shown);

		// アニメーションなしで表示切替
		setListShown(shown, false);

		AppLog.out(LOG_TAG, "setListShownNoAnimation()");
	}

	/**
	 *
	 * @param shown
	 * @param animate
	 */
	private void setListShown(final boolean shown, final boolean animate) {
		AppLog.in(LOG_TAG, "setListShown() shown=%s, animate=%s", shown, animate);

		// リストの表示状態に変更がない場合は何もしない
		AppLog.debug(LOG_TAG, "setListShown() mListShown=%s", mListShown);
		if (mListShown == shown) {
			AppLog.out(LOG_TAG, "setListShown() No change.");
			return;
		}

		mListShown = shown;
		if (shown) {
			// リストを表示
			if (animate) {
				// アニメーションあり
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
				mListViewContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			} else {
				// アニメーションなし
				mProgressContainer.clearAnimation();
				mListViewContainer.clearAnimation();
			}
			// 表示状態を切り替え
			mProgressContainer.setVisibility(View.GONE);
			mListViewContainer.setVisibility(View.VISIBLE);
		} else {
			// リストを非表示
			if (animate) {
				// アニメーションあり
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
				mListViewContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
			} else {
				// アニメーションなし
				mProgressContainer.clearAnimation();
				mListViewContainer.clearAnimation();
			}
			// 表示状態を切り替え
			mProgressContainer.setVisibility(View.VISIBLE);
			mListViewContainer.setVisibility(View.GONE);
		}

		AppLog.out(LOG_TAG, "setListShown() mListShown=%s", mListShown);
	}

}
