package com.example.xujf.listcountdowndemo

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import java.util.*
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    private val list: ArrayList<Date> = ArrayList()
    private var countDownAdapter: CountDownAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        getDate()
        setDate()
    }

    private fun setDate() {
        if (countDownAdapter == null) {
            countDownAdapter = CountDownAdapter(this, list, Date())
            lv_count_down.adapter = countDownAdapter
            lv_count_down.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val intent = Intent(ListActivity@this, Main2Activity::class.java)
                startActivity(intent)
            }
        } else {
            //刷新数据时，重置本地服务器时间
            countDownAdapter!!.reSetTimer(Date())
            countDownAdapter!!.notifyDataSetChanged()
        }
    }

    private fun getDate() {
        for (i in 1..20) {
            var date = Date(Date().time + i * 1000 * 60 * 30)
            list.add(date)
        }

    }

    override fun onDestroy() {
        countDownAdapter?.cancelAllTimers()
        countDownAdapter?.removeTimer()
        super.onDestroy()
    }
}
