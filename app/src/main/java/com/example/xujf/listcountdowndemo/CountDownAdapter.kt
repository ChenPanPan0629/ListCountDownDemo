package com.example.xujf.listcountdowndemo

import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

/**
 * Created by xujf on 2017/9/20.
 */
class CountDownAdapter(private var activity: ListActivity, private var data: ArrayList<Date>, private var systemDate: Date) : BaseAdapter() {

    private val timeMap = HashMap<TextView, MyCountDownTimer>()
    private val handler = Handler()
    private val runnable = object : Runnable {
        override fun run() {
            if (systemDate != null) {
                systemDate.time = systemDate.time + 1000
                Log.i("xujf", "服务器时间线程===" + systemDate + "==for==" + this)
                handler.postDelayed(this, 1000)
            }
        }
    }

    init {
        handler.postDelayed(runnable, 1000)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v: View
        var tag: ViewHolder
        var vo = data[position]
        if (null == convertView) {
            v = activity.layoutInflater.inflate(R.layout.item_count_down, null)
            tag = ViewHolder(v)

            v.tag = tag
        } else {
            v = convertView
            tag = v.tag as ViewHolder
        }

        //获取控件对应的倒计时控件是否存在, 存在就取消, 解决时间重叠问题
        var tc: MyCountDownTimer? = timeMap[tag.tvTime]
        if (tc != null) {
            tc.cancel()
            tc = null
        }

        //计算时间差
        val time = getDistanceTimeLong(systemDate, vo)
        //创建倒计时，与控件绑定
        val cdu = MyCountDownTimer(position, time, 1000, tag.tvTime)
        cdu.start()

        //[醒目]此处需要map集合将控件和倒计时类关联起来
        timeMap.put(tag.tvTime, cdu)

        return v
    }

    /**
     * 退出时清空所有item的计时器
     */
    fun cancelAllTimers() {
        var s: Set<MutableMap.MutableEntry<TextView, MyCountDownTimer>>? = timeMap.entries
        var it: Iterator<*>? = s!!.iterator()
        while (it!!.hasNext()) {
            try {
                val pairs = it.next() as MutableMap.MutableEntry<*, *>
                var cdt: MyCountDownTimer? = pairs.value as MyCountDownTimer
                cdt!!.cancel()
                cdt = null
            } catch (e: Exception) {
            }

        }
        it = null
        s = null
        timeMap.clear()
    }

    fun removeTimer(){
        handler?.removeCallbacks(runnable)
    }

    fun reSetTimer(date: Date) {
        removeTimer()
        systemDate = date
        handler.postDelayed(runnable, 1000)
    }

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = 0L

    override fun getCount(): Int = data.size

    internal inner class ViewHolder(view: View) {
        var tvTime = view.findViewById<TextView>(R.id.tv_time)
    }

    /**
     * 倒计时类，每间隔countDownInterval时间调用一次onTick()
     * index参数可去除，在这里只是为了打印log查看倒计时是否运行
     */
    private inner class MyCountDownTimer(internal var index: Int, millisInFuture: Long,
                                         internal var countDownInterval: Long, internal var tv: TextView
    ) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            //millisUntilFinished为剩余时间长
            Log.i("xujf", "====倒计时还活着===第 $index 项item======")
            //设置时间格式
            val m = millisUntilFinished / countDownInterval
            val hour = m / (60 * 60)
            val minute = (m / 60) % 60
            val s = m % 60
            tv.text = "倒计时  (${hour}小时${minute}分${s}秒)"
        }

        override fun onFinish() {
            tv.text = "倒计时结束"
            //todo 可以做一些刷新动作
        }
    }

    /**
     * 时间工具，返回间隔时间长
     */
    fun getDistanceTimeLong(one: Date, two: Date): Long {
        var diff = 0L
        try {
            val time1 = one.time
            val time2 = two.time
            if (time1 < time2) {
                diff = time2 - time1
            } else {
                diff = time1 - time2
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return diff
    }
}