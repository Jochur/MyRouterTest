//package com.grechur.mytest;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//
//import java.util.List;
//
//public abstract class LazyFragment extends Fragment {
//
//    private static final String TAG = "LazyFragment";
//    private boolean mIsViewCreated = false;//fragment是否已经创建
//    private boolean mIsFirstVisible = false;//fragment是否第一次可见
//    private boolean mCurrentVisibleState = false;//标记保存Fragment的可见状态，表示当前Fragment是否分发过，  可见到不可见  不可见到可见才可以调用 disPatchVisibaleHint  防止重复调用
//    protected View mRootView;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        //两个抽象方法，让子类实现initView和getLayoutRes
//        if (mRootView == null) {
//            mRootView = inflater.inflate(getLayoutRes(), null);
//        }
//        initView(mRootView);
//        //1：创建了Fragment  控制下面分发的前提，因为分发事件由setUserVisibleHint方法控制，而setUserVisibleHint最先执行
//        mIsViewCreated = true;
//
//        //2：对于默认Fragment的加载，可以在此分发一下，可见才分发
//        if (getUserVisibleHint() && !isHidden()) {
//            //可见
//            disPatchVisibleHint(true);
//        }
//        return mRootView;
//    }
//
//    public abstract int getLayoutRes();
//
//    public abstract void initView(View view);
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (mIsViewCreated) {
//            if (isVisibleToUser && !mCurrentVisibleState) {
//                //用户可见时进行分发事件
//                disPatchVisibleHint(true);
//            } else {
//                //用户不可见时不分发事件
//                disPatchVisibleHint(false);
//            }
//        }
//    }
//
//    /**
//     * 分发可见
//     * 调用的前提：可见--》不可见    或者不可见--》可见
//     *
//     * @param isVisible
//     */
//    private void disPatchVisibleHint(boolean isVisible) {
//        if (mCurrentVisibleState == isVisible) {
//            return;
//        }
//        mCurrentVisibleState = isVisible;
//        if (isVisible) {
//            if (mIsFirstVisible) {
//                mIsFirstVisible = false;
//                //处理第一次可见时
//                //公共方法，由子类实现
//                onFragmentFirstVisible();
//            }
//            //复写onFragmentResume分发事件，网路请求
//            onFragmentResume();
//            //对viewpager嵌套使用时处理子的fragment
//            dispatChChildVisibleState(true);
//        } else {
//            onFragmentPause();
//            dispatChChildVisibleState(false);
//        }
//
//    }
//
//    /**
//     * 第一次可见时特殊处理
//     */
//    public void onFragmentFirstVisible() {
//        Log.e(TAG, "onFragmentFirstVisiable");
//    }
//
//    /**
//     * 不可见时处理相关动作  停止数据的加载
//     */
//    public void onFragmentPause() {
//        Log.e(TAG, "onFragmentPause");
//    }
//
//    /**
//     * 表面可见时  加载数据
//     */
//    public void onFragmentResume() {
//        Log.e(TAG, "onFragmentResume");
//    }
//
//    @Override
//    public void onPause() {
//        Log.e(TAG, "onPause");
//        if (mCurrentVisibleState && getUserVisibleHint()) {
//            disPatchVisibleHint(false);
//        }
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//        Log.e(TAG, "onResume");
//        //只有当前可见的Fragment  才更新shuju  点击home键又返回
//        if (!mCurrentVisibleState && getUserVisibleHint() && !isHidden()) {
//            disPatchVisibleHint(true);
//        }
//        super.onResume();
//    }
//
//    @Override
//    public void onDestroyView() {
//        Log.e(TAG, "onDestroyView");
//        super.onDestroyView();
//        //将所有的变量复位
//        mIsFirstVisible = true;
//        mCurrentVisibleState = false;
//        mIsViewCreated = false;
//    }
//
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (hidden) {
//            disPatchVisibleHint(false);
//        } else {
//            disPatchVisibleHint(true);
//        }
//    }
//
//    private void dispatChChildVisibleState(boolean isVisible) {
//        FragmentManager childFragmentManager = getChildFragmentManager();
//        List<Fragment> childFragmentManagerFragments = childFragmentManager.getFragments();
//        if (childFragmentManagerFragments != null) {
//            for (Fragment fragment : childFragmentManagerFragments) {
//                //进行类型校验，只有继承了LazyFragment才进行懒加载的处理
//                if (fragment instanceof LazyFragment && !fragment.isHidden() && fragment.getUserVisibleHint()) {
//                    ((LazyFragment) fragment).disPatchVisibleHint(isVisible);
//                }
//            }
//        }
//    }
//}
