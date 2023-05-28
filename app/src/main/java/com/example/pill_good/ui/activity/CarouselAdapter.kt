package com.example.pill_good.ui.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_good.R
import com.example.pill_good.data.model.CarouselItem

class CarouselAdapter(private val carouselItems: List<CarouselItem>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(viewType, parent, false)
        return CarouselViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val carouselItem = carouselItems[position]
        // 아이템에 따라 처리할 로직 작성
        when (holder.itemViewType) {
            R.layout.carousel_image_item -> {
                // 이미지 아이템 처리
                val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)

                // 이미지 URI를 사용하여 이미지 로드 등의 작업 수행
                imageView.setImageURI(carouselItem.imageUri)
            }
            R.layout.carousel_linear_item -> {
                // LinearLayout 아이템 처리
                val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView2)

                // 이미지 URI를 사용하여 이미지 로드 등의 작업 수행
                imageView.setImageURI(carouselItem.imageUri)

                // 필요한 로직 작성

            }
        }
    }

    override fun getItemCount(): Int = carouselItems.size

    override fun getItemViewType(position: Int): Int = carouselItems[position].layoutResId

    class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
