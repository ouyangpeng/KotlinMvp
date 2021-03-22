package com.hazz.kotlinmvp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.hazz.kotlinmvp.utils.DisplayManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import kotlin.properties.Delegates


/**
 * Created by xuhao on 2017/11/16.
 *
 */
class MyApplication : Application() {
    private var refWatcher: RefWatcher? = null

    companion object {
        private const val TAG = "MyApplication"
        var context: Context by Delegates.notNull()
            private set  //setter()访问器的私有化，并且它拥有kotlin的默认实现

        fun getRefWatcher(context: Context): RefWatcher? {
            val myApplication = context.applicationContext as MyApplication
            return myApplication.refWatcher
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        refWatcher = setupLeakCanary()
        initConfig()
        DisplayManager.init(this)
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

    private fun setupLeakCanary(): RefWatcher {
        return when {
            LeakCanary.isInAnalyzerProcess(this) -> {
                RefWatcher.DISABLED
            }
            else -> LeakCanary.install(this)
        }
    }


    /**
     * 初始化配置
     */
    private fun initConfig() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // 隐藏线程信息 默认：显示
                .methodCount(0)         // 决定打印多少行（每一行代表一个方法）默认：2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("hao_zz")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }


    private val mActivityLifecycleCallbacks by lazy {
        return@lazy object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d(TAG, "onActivityCreated: " + activity.componentName.className)
            }

            override fun onActivityStarted(activity: Activity) {
                Log.d(TAG, "onActivityStarted: " + activity.componentName.className)
            }

            override fun onActivityResumed(activity: Activity) {
                Log.d(TAG, "onActivityResumed: " + activity.componentName.className)
            }

            override fun onActivityPaused(activity: Activity) {
                Log.d(TAG, "onActivityPaused: " + activity.componentName.className)
            }

            override fun onActivityStopped(activity: Activity) {
                Log.d(TAG, "onActivityStopped: " + activity.componentName.className)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.d(TAG, "onActivitySaveInstanceState: " + activity.componentName.className)
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.d(TAG, "onActivityDestroyed: " + activity.componentName.className)
            }
        }
    }


}
