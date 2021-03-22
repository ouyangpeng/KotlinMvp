package com.hazz.kotlinmvp.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import com.hazz.kotlinmvp.MyApplication
import com.hazz.kotlinmvp.R
import com.hazz.kotlinmvp.base.BaseActivity
import com.hazz.kotlinmvp.showToast
import com.hazz.kotlinmvp.utils.AppUtils
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_splash.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem

class SplashActivity : BaseActivity() {
    private var textTypeface: Typeface? = null

    private var descTypeFace: Typeface? = null

    private var alphaAnimation: AlphaAnimation? = null

    init {
        textTypeface = Typeface.createFromAsset(MyApplication.context.assets, "fonts/Lobster-1.4.otf")
        descTypeFace = Typeface.createFromAsset(MyApplication.context.assets, "fonts/FZLanTingHeiS-L-GB-Regular.TTF")
    }


    override fun layoutId(): Int = R.layout.activity_splash

    override fun initData() {}

    @SuppressLint("SetTextI18n")
    override fun initView() {
        tv_app_name.typeface = textTypeface
        tv_splash_desc.typeface = descTypeFace
        tv_version_name.text = String.format(getString(R.string.app_version), AppUtils.getVerName(MyApplication.context))

        //渐变展示启动屏
        alphaAnimation = AlphaAnimation(0.3f, 1.0f)
        alphaAnimation?.let {
            it.duration = 2000
            it.setAnimationListener(object : AnimationListener {
                override fun onAnimationEnd(arg0: Animation) {
                    redirectTo()
                }

                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationStart(animation: Animation) {}
            })
        }

        checkPermission()
    }

    override fun start() {}

    fun redirectTo() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * 6.0以下版本(系统自动申请) 不会弹框
     * 有些厂商修改了6.0系统申请机制，他们修改成系统自动申请权限了
     */
    private fun checkPermission() {
        val permissionItems = arrayListOf<PermissionItem>(
                PermissionItem(Manifest.permission.READ_PHONE_STATE,
                        getString(R.string.permission_name_read_phone_state),
                        R.drawable.permission_ic_phone),

                PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        getString(R.string.permission_name_write_external_storage),
                        R.drawable.permission_ic_storage))

        HiPermission.create(this)
                .title(getString(R.string.permission_title))
                .msg(getString(R.string.permission_msg))
                .permissions(permissionItems)
                .style(R.style.PermissionDefaultBlueStyle)
                .animStyle(R.style.PermissionAnimScale)
                .checkMutiPermission(object : PermissionCallback {
                    override fun onClose() {
                        Logger.i("permission_onClose")
                        showToast(getString(R.string.permission_user_close))
                    }

                    override fun onFinish() {
                        showToast(getString(R.string.init_finish))
                        layout_splash.startAnimation(alphaAnimation)
                    }

                    override fun onDeny(permission: String, position: Int) {
                        Logger.i("permission_onDeny")
                    }

                    override fun onGuarantee(permission: String, position: Int) {
                        Logger.i("permission_onGuarantee")
                    }
                })
    }

}