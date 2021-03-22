package com.hazz.kotlinmvp.ui.activity

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.hazz.kotlinmvp.Constants
import com.hazz.kotlinmvp.R
import com.hazz.kotlinmvp.base.BaseActivity
import com.hazz.kotlinmvp.mvp.model.bean.TabEntity
import com.hazz.kotlinmvp.showToast
import com.hazz.kotlinmvp.ui.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity() {
    private val mTitles = arrayOf(
            R.string.title_daily_featured,
            R.string.title_discovery,
            R.string.title_hot,
            R.string.title_mine)

    private val mTags = arrayOf(
            R.string.tag_home,
            R.string.tag_discovery,
            R.string.tag_hot,
            R.string.tag_mine)

    // 未被选中的图标
    private val mIconUnSelectIds = intArrayOf(
            R.mipmap.ic_home_normal, R.mipmap.ic_discovery_normal,
            R.mipmap.ic_hot_normal, R.mipmap.ic_mine_normal)

    // 被选中的图标
    private val mIconSelectIds = intArrayOf(
            R.mipmap.ic_home_selected, R.mipmap.ic_discovery_selected,
            R.mipmap.ic_hot_selected, R.mipmap.ic_mine_selected)

    private val mTabEntities = ArrayList<CustomTabEntity>()

    private var mHomeFragment: HomeFragment? = null
    private var mDiscoveryFragment: DiscoveryFragment? = null
    private var mHotFragment: HotFragment? = null
    private var mMineFragment: MineFragment? = null

    //默认为0
    private var mIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt(Constants.CURRENT_TAB_INDEX)
        }
        super.onCreate(savedInstanceState)
        initTab()
        tab_layout.currentTab = mIndex
        switchFragment(mIndex)
    }

    override fun layoutId(): Int {
        return R.layout.activity_main
    }


    //初始化底部菜单
    private fun initTab() {
        // 遍历，赋值
        (mTitles.indices).mapTo(mTabEntities) {
            TabEntity(getString(mTitles[it]), mIconSelectIds[it], mIconUnSelectIds[it])
        }
        //为Tab赋值
        tab_layout.setTabData(mTabEntities)
        tab_layout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                //切换Fragment
                switchFragment(position)
            }

            override fun onTabReselect(position: Int) {}
        })
    }

    /**
     * 切换Fragment
     * @param position 下标
     */
    private fun switchFragment(position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)

        val title = getString(mTitles[position])
        val tag = getString(mTags[position])

        when (position) {
            // 首页
            0 -> mHomeFragment?.let {
                transaction.show(it)
            } ?: HomeFragment.getInstance(title).let {
                mHomeFragment = it
                transaction.add(R.id.fl_container, it, tag)
            }

            //发现
            1 -> mDiscoveryFragment?.let {
                transaction.show(it)
            } ?: DiscoveryFragment.getInstance(title).let {
                mDiscoveryFragment = it
                transaction.add(R.id.fl_container, it, tag)
            }

            //热门
            2 -> mHotFragment?.let {
                transaction.show(it)
            } ?: HotFragment.getInstance(title).let {
                mHotFragment = it
                transaction.add(R.id.fl_container, it, tag)
            }

            //我的
            3 -> mMineFragment?.let {
                transaction.show(it)
            } ?: MineFragment.getInstance(title).let {
                mMineFragment = it
                transaction.add(R.id.fl_container, it, tag)
            }

            else -> { }
        }

        mIndex = position
        tab_layout.currentTab = mIndex
        transaction.commitAllowingStateLoss()
    }


    /**
     * 隐藏所有的Fragment
     * @param transaction transaction
     */
    private fun hideFragments(transaction: FragmentTransaction) {
        mHomeFragment?.let { transaction.hide(it) }
        mDiscoveryFragment?.let { transaction.hide(it) }
        mHotFragment?.let { transaction.hide(it) }
        mMineFragment?.let { transaction.hide(it) }
    }


    override fun onSaveInstanceState(outState: Bundle) {
//        showToast("onSaveInstanceState->"+mIndex)
//        super.onSaveInstanceState(outState)
        //记录fragment的位置,防止崩溃 activity被系统回收时，fragment错乱
        if (tab_layout != null) {
            outState.putInt(Constants.CURRENT_TAB_INDEX, mIndex)
        }
    }

    override fun initView() {}

    override fun initData() {}

    override fun start() {}

    private var mExitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis().minus(mExitTime) <= 2000) {
                finish()
            } else {
                mExitTime = System.currentTimeMillis()
                showToast("再按一次退出程序")
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
