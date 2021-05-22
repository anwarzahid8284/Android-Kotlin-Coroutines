package com.example.kotlinecoroutines

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PendingAppsAdapter(
    private val pendingAppsModelList: List<PendingAppsModel>,
    val context: Context
) :
    RecyclerView.Adapter<PendingAppsAdapter.PendingVHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingVHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_pending_apps, parent, false)
        return PendingVHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pendingAppsModelList.size
    }

    override fun onBindViewHolder(holder: PendingVHolder, position: Int) {
        val currentItem = pendingAppsModelList[position]
        holder.appIconImageView.setImageDrawable(currentItem.appIcon)
        holder.appNameText.text = currentItem.appName
        holder.appOlderVersionText.text = currentItem.appOldVersion
        holder.appNewVersionText.text = currentItem.appNewVersion
        holder.appUpdateButton.setOnClickListener {
            openInGPlay(currentItem.appPackageName)
        }
    }

    private fun openInGPlay(appPackageName: String) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    inner class PendingVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIconImageView: ImageView = itemView.findViewById(R.id.pending_app_imageViewID)
        val appNameText: TextView = itemView.findViewById(R.id.pending_app_nameID)
        val appOlderVersionText: TextView = itemView.findViewById(R.id.pending_app_oldVersionID)
        val appNewVersionText: TextView = itemView.findViewById(R.id.pending_app_newVersionID)
        val appUpdateButton: Button = itemView.findViewById(R.id.pending_app_checkUpdateBtnID)
    }


}