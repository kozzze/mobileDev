package com.example.lab1.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1.CarDetailActivity
import com.example.lab1.R
import com.example.lab1.Car

class CarAdapter(private val cars: List<Car>, private val onItemClick: (Car) -> Unit) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {
    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carImage: ImageView = itemView.findViewById(R.id.carImage)
        val carName: TextView = itemView.findViewById(R.id.carName)
        val carPrice: TextView = itemView.findViewById(R.id.carPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car_list, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]
        holder.carName.text = "${car.brand} ${car.model}"
        holder.carPrice.text = car.price
        holder.carImage.setImageResource(car.imageResId)
        holder.itemView.setOnClickListener { onItemClick(car) }
    }

    override fun getItemCount(): Int = cars.size
}