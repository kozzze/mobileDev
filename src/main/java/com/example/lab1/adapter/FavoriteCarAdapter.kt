package com.example.lab1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab1.R
import com.example.lab1.model.Car

class FavoriteCarAdapter(
    private val cars: List<Car>,
    private val onClick: (Car) -> Unit
) : RecyclerView.Adapter<FavoriteCarAdapter.FavCarViewHolder>() {

    class FavCarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.favCarImage)
        val name: TextView = view.findViewById(R.id.favCarName)
        val price: TextView = view.findViewById(R.id.favCarPrice)
        val body: TextView = view.findViewById(R.id.favCarBody)
        val star: ImageView = view.findViewById(R.id.favStarIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavCarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_car, parent, false)
        return FavCarViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavCarViewHolder, position: Int) {
        val car = cars[position]
        holder.name.text = "${car.brand} ${car.model}"
        holder.price.text = "${car.price} $"
        holder.body.text = car.body ?: "—"

        Glide.with(holder.itemView)
            .load(car.imageUrl)
            .placeholder(android.R.drawable.ic_menu_camera)
            .error(android.R.drawable.ic_menu_report_image)
            .centerCrop()
            .into(holder.image)

        holder.star.setOnClickListener { onClick(car) }
        holder.itemView.setOnClickListener { onClick(car) }
    }

    override fun getItemCount() = cars.size
}
