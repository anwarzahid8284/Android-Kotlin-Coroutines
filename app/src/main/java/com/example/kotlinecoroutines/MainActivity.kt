package com.example.kotlinecoroutines

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var pendingAppsAdapter: PendingAppsAdapter
    private lateinit var pendingRecyclerView: RecyclerView
    private lateinit var pendingAppsModelList: ArrayList<PendingAppsModel>
    private lateinit var loadingProgressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pendingRecyclerView = findViewById(R.id.recyclerView_PendingID)
        pendingAppsModelList = ArrayList()
        loadingProgressBar = findViewById(R.id.loading_spinner)
        pendingAppsAdapter = PendingAppsAdapter(pendingAppsModelList,this)
        pendingRecyclerView.adapter = pendingAppsAdapter
        pendingRecyclerView.layoutManager = LinearLayoutManager(this)
        pendingRecyclerView.addItemDecoration(
            DividerItemDecoration(
                pendingRecyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        pendingRecyclerView.setHasFixedSize(true)
        CoroutineScope(Dispatchers.IO).launch {
            loadPendingUpdateApps()
        }
    }
    private suspend fun loadPendingUpdateApps(){
        val packageManager = getPackageManager()
        val packageInfos = packageManager.getInstalledPackages(0)
        var newVersion: String? = null
        for (i in 0 until packageInfos.size!!) {
            val p = packageInfos.get(i)
            if ((!isSystemPackage(p))) {
                var appName: String =
                    p.applicationInfo.loadLabel(getPackageManager()).toString()
                val icon: Drawable =
                    p.applicationInfo.loadIcon(getPackageManager())
                val packages: String = p.applicationInfo.packageName
                val versionName: String = p.versionName
                try {
                    val document =
                        Jsoup.connect("https://play.google.com/store/apps/details?id=$packages&hl=en")
                            .timeout(30000)
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .referrer("http://www.google.com")
                            .get()
                    if (document != null) {
                        val element =
                            document.getElementsContainingOwnText("Current Version")
                        for (ele in element) {
                            if (ele.siblingElements() != null) {
                                val sibElemets = ele.siblingElements()
                                for (sibElemet in sibElemets) {
                                    newVersion = sibElemet.text()
                                }
                            }
                        }

                    }
                    if (!versionName.equals(newVersion)) {
                        if (newVersion.equals("Varies with device")) {
                            newVersion = "New updates not supported your device "
                        }
                        val item = PendingAppsModel(
                            packages,
                            appName,
                            icon,
                            versionName,
                            newVersion
                        )
                        pendingAppsModelList.plusAssign(item)
                        withContext(Dispatchers.Main){
                            pendingAppsAdapter.notifyDataSetChanged()
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return (pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) !== 0
    }
}