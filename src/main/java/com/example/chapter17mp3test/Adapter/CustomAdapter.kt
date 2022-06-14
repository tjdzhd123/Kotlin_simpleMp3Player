package com.example.chapter17mp3test.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chapter17mp3test.Member
import com.example.chapter17mp3test.databinding.ItemViewBinding


class CustomAdapter(val context: Context,val merberList: MutableList<Member>?): RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        //아이템을 객체화한 순간
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //다운캐스팅을 합니다.
        val binding = (holder as CustomViewHolder).binding

        binding.tvItemViewId.text =  merberList?.get(position)?.id
        binding.tvItemViewPass.text = merberList?.get(position)?.pass
        binding.tvItemViewName.text = merberList?.get(position)?.name

    }

    //3. CustomAdapter에서 recyclerView 제공해줘야할 데이터 갯수 파악합니다.
    override fun getItemCount(): Int {
        return merberList!!.size
    }

    //2. 뷰홀더 클래스 정의
    class CustomViewHolder(val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root)
}//end of class