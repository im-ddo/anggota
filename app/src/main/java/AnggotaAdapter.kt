package com.nim2411500037.anggota

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class AnggotaAdapter(
    private val context: Context,
    private var items: MutableList<AnggotaItem>
) : BaseAdapter() {

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = items[position].idAnggota.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.items_list, parent, false)

            holder = ViewHolder()
            holder.tvNamaLengkap = view.findViewById(R.id.tvNamaLengkap)
            holder.tvAlamat = view.findViewById(R.id.tvAlamat)
            holder.imgFotoProfil = view.findViewById(R.id.imgFotoProfil)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = items[position]

        holder.tvNamaLengkap?.text = item.namaLengkap
        holder.tvAlamat?.text = item.alamat

        Glide.with(context)
            .load(item.fotoProfil)
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .into(holder.imgFotoProfil!!)

        return view
    }

    fun updateList(newItems: MutableList<AnggotaItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    private class ViewHolder {
        var tvNamaLengkap: TextView? = null
        var tvAlamat: TextView? = null
        var imgFotoProfil: ImageView? = null
    }
}
