package com.mufcryan.base.ui

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<Data, Holder: RecyclerView.ViewHolder>(): RecyclerView.Adapter<Holder>() {
  var list = ArrayList<Data>()
  override fun getItemCount() = list.size

  fun setList(list: List<Data>){
    this.list.clear()
    this.list.addAll(list)
    notifyDataSetChanged()
  }

  fun addList(list: List<Data>){
    val index = this.list.size
    this.list.addAll(list)
    notifyItemRangeInserted(index, list.size)
  }

  fun updateData(data: Data){
    val index = list.indexOf(data)
    if(index >= 0){
      list[index] = data
      notifyItemChanged(index)
    }
  }
}