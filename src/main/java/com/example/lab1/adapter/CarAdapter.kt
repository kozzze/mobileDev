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

class CarAdapter(
    private val cars: List<Car>,
    private val onClick: (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    class CarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val carImage: ImageView = view.findViewById(R.id.itemCarImage)
        val carName: TextView = view.findViewById(R.id.itemCarName)
        val carPrice: TextView = view.findViewById(R.id.itemCarPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car_list, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]
        holder.carName.text = "${car.brand} ${car.model}"
        holder.carPrice.text = "${car.price} $"

        val url = car.imageUrl
        if (!url.isNullOrBlank()) {
            Glide.with(holder.itemView)
                .load(url)
                .placeholder(android.R.drawable.ic_menu_camera)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(holder.carImage)
        } else {
            holder.carImage.setImageResource(android.R.drawable.ic_menu_camera)
        }

        holder.itemView.setOnClickListener { onClick(car) }
    }

    override fun getItemCount() = cars.size
}
