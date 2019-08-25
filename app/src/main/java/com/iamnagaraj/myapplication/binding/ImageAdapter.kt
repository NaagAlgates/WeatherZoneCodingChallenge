package com.iamnagaraj.myapplication.binding

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.iamnagaraj.myapplication.R
import com.iamnagaraj.myapplication.ScrollingActivity
import com.iamnagaraj.myapplication.model.Photos
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_item_recycler_view.view.*


class ImageAdapter(private val dataList: Array<Photos>, private val itemListener: ScrollingActivity) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(dataList[position], itemListener)
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item_recycler_view, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        fun bind(photos: Photos?, listener: ScrollingActivity) {

            itemView.photographer_name.text = photos?.photographer
            itemView.photographer_name.setOnClickListener {photos?.let { data -> listener.onItemUploaderCliked(data) } }
            itemView.disclaimer.setOnClickListener{photos?.let { listener.onItemPexelClicked() } }
            Picasso.get()
                .load(photos?.src?.portrait)
                .fit()
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(itemView.search_image)
            itemView.setOnClickListener{ photos?.let { data -> listener.onItemClicked(data) } }
        }
    }
}