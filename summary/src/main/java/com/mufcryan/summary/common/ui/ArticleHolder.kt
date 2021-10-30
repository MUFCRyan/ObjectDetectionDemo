package com.mufcryan.summary.common.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mufcryan.summary.R

class ArticleHolder(view: View): RecyclerView.ViewHolder(view) {
  val ivCover = view.findViewById<ImageView>(R.id.iv_cover)
  val tvTitle = view.findViewById<TextView>(R.id.tv_title)
  val tvContent = view.findViewById<TextView>(R.id.tv_content)
  val tvDate = view.findViewById<TextView>(R.id.tv_date)
}