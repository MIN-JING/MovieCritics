package com.jim.moviecritics.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.databinding.ItemSearchSectionBinding
import com.jim.moviecritics.databinding.ItemSearchSectionItemMovieBinding
import com.jim.moviecritics.ext.setOnSingleClickListener


class ExpandAdapter(
    private val onSectionClickListener: (LookItem) -> Unit)
    : ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback){

    class SectionViewHolder(
        private var binding: ItemSearchSectionBinding,
        onSectionClickListener: (LookItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnSingleClickListener {
                binding.lookItem?.let {
                    onSectionClickListener(it)
                }
            }
        }
        fun bind(lookItem: LookItem) {
            binding.lookItem = lookItem
//            binding.root.setOnClickListener { onClickListener.onClick(look) }
            binding.executePendingBindings()
        }
    }

    class SectionItemViewHolder(private var binding: ItemSearchSectionItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lookItemMovie: LookItem.LookMovie) {
            binding.lookItemMovie = lookItemMovie
//            binding.root.setOnClickListener { onClickListener.onClick(look) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is LookItem && newItem is LookItem -> {
//                    oldItem.id == newItem.id
                    oldItem === newItem
                }
                oldItem is LookItem.LookMovie && newItem is LookItem.LookMovie -> {
                    oldItem.look === newItem.look
                }
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is LookItem && newItem is LookItem -> {
//                    oldItem == newItem
                    oldItem.id == newItem.id
                }
                oldItem is LookItem.LookMovie && newItem is LookItem.LookMovie -> {
                    oldItem.look.id == newItem.look.id
                }
                else -> false
            }
        }

        private const val VIEW_TYPE_SECTION = 0x01
        private const val VIEW_TYPE_SECTION_ITEM = 0x02
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SECTION -> SectionViewHolder(
                ItemSearchSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onSectionClickListener
            )
            VIEW_TYPE_SECTION_ITEM -> SectionItemViewHolder(
                ItemSearchSectionItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw(Throwable("View type not matching"))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (holder) {
//            is SectionViewHolder -> {
//                holder.bind((getItem(position) as LookItem))
//            }
//            is SectionItemViewHolder -> {
//                holder.bind((getItem(position) as LookItem.LookMovie))
//            }
//        }
        when (val item = getItem(position)) {
            is LookItem -> (holder as? SectionViewHolder)?.bind(item)
            is LookItem.LookMovie -> (holder as? SectionItemViewHolder)?.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is LookItem -> VIEW_TYPE_SECTION
            is LookItem.LookMovie -> VIEW_TYPE_SECTION_ITEM
            else -> super.getItemViewType(position)
        }
    }

}

//class RecycleAdapter(var mContext: Context, val list: MutableList<ParentData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//
//        return if(viewType== Constants.PARENT){
//            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.parent_row, parent,false)
//            GroupViewHolder(rowView)
//        } else {
//            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.child_row, parent,false)
//            ChildViewHolder(rowView)
//        }
//    }
//
//    override fun getItemCount(): Int = list.size
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//
//        val dataList = list[position]
//        if (dataList.type == Constants.PARENT) {
//            holder as GroupViewHolder
//            holder.apply {
//                parentTV?.text = dataList.parentTitle
//                downIV?.setOnClickListener {
//                    expandOrCollapseParentItem(dataList,position)
//                }
//            }
//        } else {
//            holder as ChildViewHolder
//
//            holder.apply {
//                val singleService = dataList.subList.first()
//                childTV?.text =singleService.childTitle
//            }
//        }
//    }
//    private fun expandOrCollapseParentItem(singleBoarding: ParentData,position: Int) {
//
//        if (singleBoarding.isExpanded) {
//            collapseParentRow(position)
//        } else {
//            expandParentRow(position)
//        }
//    }
//
//    private fun expandParentRow(position: Int){
//        val currentBoardingRow = list[position]
//        val services = currentBoardingRow.subList
//        currentBoardingRow.isExpanded = true
//        var nextPosition = position
//        if(currentBoardingRow.type==Constants.PARENT){
//
//            services.forEach { service ->
//                val parentModel = ParentData()
//                parentModel.type = Constants.CHILD
//                val subList : ArrayList<ChildData> = ArrayList()
//                subList.add(service)
//                parentModel.subList=subList
//                list.add(++nextPosition,parentModel)
//            }
//            notifyDataSetChanged()
//        }
//    }
//
//    private fun collapseParentRow(position: Int){
//        val currentBoardingRow = list[position]
//        val services = currentBoardingRow.subList
//        list[position].isExpanded = false
//        if(list[position].type==Constants.PARENT){
//            services.forEach { _ ->
//                list.removeAt(position + 1)
//            }
//            notifyDataSetChanged()
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int = list[position].type
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//
//    class GroupViewHolder(row: View) : RecyclerView.ViewHolder(row) {
//        val parentTV = row.findViewById(R.id.parent_Title) as TextView?
//        val downIV = row.findViewById(R.id.down_iv) as ImageView?
//    }
//    class ChildViewHolder(row: View) : RecyclerView.ViewHolder(row) {
//        val childTV = row.findViewById(R.id.child_Title) as TextView?
//
//    }
//}