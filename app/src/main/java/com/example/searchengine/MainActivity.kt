package com.example.searchengine

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.jacksonandroidnetworking.JacksonParserFactory
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    var curpage : Int = 1
    var searchf : String = "0"
    var keywordf : String = ""

    val list = ArrayList<HashMap<String, String>>()
    private var sa: SimpleAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AndroidNetworking.initialize(getApplicationContext())
        AndroidNetworking.setParserFactory(JacksonParserFactory())
        var host: String= resources.getString(R.string.host_api)

        val s_eng: Spinner = search_eng
        val s_res: ListView = search_res
        var DropDownSEListitems = arrayOf<String>()
        var DropDownSEListItemValue = arrayOf<String>()
        AndroidNetworking.post(host+"/dropdown-search-engine-lists")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    // do anything with response
                    var records = response.getJSONObject("data").getJSONArray("records")
                    for (i in 0 .. records.length()-1){
                        DropDownSEListitems = append(DropDownSEListitems, records.getJSONObject(i).getString("sc_name"))
                        DropDownSEListItemValue = append(DropDownSEListItemValue, records.getJSONObject(i).getString("id"))
                        dropdownSearchEngines(DropDownSEListitems, DropDownSEListItemValue, s_eng)
                    }
                    searchf=(records.length()-1).toString()
                }

                override fun onError(error: ANError) {
                    // handle error
                    var jsonString: String=error.errorBody
                    var jsonObject=JSONObject(jsonString)
                    toast(jsonObject.getString("message"))
                }
            })

        btn1.setOnClickListener {
            keywordf = keyword.getText().toString()
            toast("Please Wait")
            AndroidNetworking.post(host+"/search")
                .addBodyParameter("sc_id", searchf)
                .addBodyParameter("keyword", keywordf)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        // do anything with response
                        toast("Processing is Completed")

                        //Log.d("DataRecords",response.getJSONObject("data").getJSONArray("records").toString())
                        val records = response.getJSONObject("data").getJSONArray("records")
                        for(i in 0 .. records.length()-1){
                            if(searchf=="1"){
                                //Log.d("Data Search : "+searchf,"Title "+records.getJSONObject(i).getString("title")+" Link "+searchf +" "+records.getJSONObject(i).getString("link")+" Snippet "+searchf +" "+records.getJSONObject(i).getString("snippet"))
                                var item = HashMap<String, String>()
                                item.put("line1", records.getJSONObject(i).getString("title"))
                                item.put("line2", records.getJSONObject(i).getString("snippet"))
                                item.put("url_link", records.getJSONObject(i).getString("link"))
                                list.add(item)
                            }else if(searchf=="2"){
                                //Log.d("Data Search : "+searchf,"Name "+records.getJSONObject(i).getString("name")+" Url "+searchf +" "+records.getJSONObject(i).getString("url")+" Snippet "+searchf +" "+records.getJSONObject(i).getString("snippet"))
                                var item = HashMap<String, String>()
                                item.put("line1", records.getJSONObject(i).getString("name"))
                                item.put("line2", records.getJSONObject(i).getString("snippet"))
                                item.put("url_link", records.getJSONObject(i).getString("url"))
                                list.add(item)
                            }
                        }
                        listWebsite(s_res)

                    }

                    override fun onError(error: ANError) {
                        // handle error
                        var jsonString: String=error.errorBody
                        var jsonObject=JSONObject(jsonString)
                        toast(jsonObject.getString("message"))
                    }
                })
        }

    }

    fun listWebsite(s_res: ListView){

        sa = SimpleAdapter(
            this,
            list,
            R.layout.multi_lines,
            arrayOf("line1", "line2"),
            intArrayOf(R.id.line_a, R.id.line_b)
        )
        s_res.adapter=sa


        s_res.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val browserPage = Intent(this, BrowserActivity::class.java).apply {}
            browserPage.putExtra("Browse_Url", list.get(position).get("url_link"));
            startActivity(browserPage)
        })



        s_res.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                    && s_res.getLastVisiblePosition() - s_res.getHeaderViewsCount() -
                    s_res.getFooterViewsCount() >= s_res.adapter.count - 1
                ) {

                    // Now your listview has hit the bottom
                    toast("Loading More Data")
                    curpage++
                    keywordf = keyword.getText().toString()
                    var host: String= resources.getString(R.string.host_api)
                    toast("Please Wait")
                    AndroidNetworking.post(host+"/search")
                        .addBodyParameter("sc_id", searchf)
                        .addBodyParameter("keyword", keywordf)
                        .addBodyParameter("page", curpage.toString())
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) {
                                // do anything with response
                                toast("Processing is Completed")

                                //Log.d("DataRecords",response.getJSONObject("data").getJSONArray("records").toString())
                                val records = response.getJSONObject("data").getJSONArray("records")
                                for(i in 0 .. records.length()-1){
                                    if(searchf=="1"){
                                        //Log.d("Data Search : "+searchf,"Title "+records.getJSONObject(i).getString("title")+" Link "+searchf +" "+records.getJSONObject(i).getString("link")+" Snippet "+searchf +" "+records.getJSONObject(i).getString("snippet"))
                                        var item = HashMap<String, String>()
                                        item.put("line1", records.getJSONObject(i).getString("title"))
                                        item.put("line2", records.getJSONObject(i).getString("snippet"))
                                        item.put("url_link", records.getJSONObject(i).getString("link"))
                                        list.add(item)
                                    }else if(searchf=="2"){
                                        //Log.d("Data Search : "+searchf,"Name "+records.getJSONObject(i).getString("name")+" Url "+searchf +" "+records.getJSONObject(i).getString("url")+" Snippet "+searchf +" "+records.getJSONObject(i).getString("snippet"))
                                        var item = HashMap<String, String>()
                                        item.put("line1", records.getJSONObject(i).getString("name"))
                                        item.put("line2", records.getJSONObject(i).getString("snippet"))
                                        item.put("url_link", records.getJSONObject(i).getString("url"))
                                        list.add(item)
                                    }
                                }
                                listWebsite(s_res)

                            }

                            override fun onError(error: ANError) {
                                // handle error
                                var jsonString: String=error.errorBody
                                var jsonObject=JSONObject(jsonString)
                                toast(jsonObject.getString("message"))
                            }
                        })
                }
            }

            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.about_app -> {
                aboutApp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun aboutApp() {
        val aboutPage = Intent(this, AppInfoActivity::class.java).apply {}
        startActivity(aboutPage)
    }

    fun dropdownSearchEngines(DropDownSEListitems: Array<String>, DropDownSEListItemValue: Array<String>, s_eng: Spinner){
        println(DropDownSEListitems.contentToString())
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, DropDownSEListitems)
        s_eng.adapter = adapter
        s_eng.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                val id: String = DropDownSEListItemValue.get(i)
                searchf=id
                list.clear()
                val s_res: ListView = search_res
                listWebsite(s_res)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }
}