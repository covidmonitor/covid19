package pl.covid19.ui.mainFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.covid19.database.AreaDBGOVPLXDBFazyDB
import pl.covid19.databinding.ListItemAreaGfBinding


private val ITEM_VIEW_TYPE_ITEM = 1

class AreasGFAdapter(val clickListener: AreasGFListener) : ListAdapter<DataItemGF,
        RecyclerView.ViewHolder>(AreaGFDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun SubmitList(list: List<AreaDBGOVPLXDBFazyDB>?) {
        adapterScope.launch {
            val items = list?.map { DataItemGF.AreaGFItem(it) }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val areaItem = getItem(position) as DataItemGF.AreaGFItem
                holder.bind(clickListener, areaItem.areaGF)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItemGF.AreaGFItem -> ITEM_VIEW_TYPE_ITEM
            else -> 2
        }
    }

    fun getNoteAt(pos: Int): Long {
        return  getItem(pos).id
    }

    class ViewHolder private constructor(val binding: ListItemAreaGfBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: AreasGFListener, item: AreaDBGOVPLXDBFazyDB) {
            binding.areaGF= item
            binding.clickListener2 = clickListener
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAreaGfBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

class AreaGFDiffCallback : DiffUtil.ItemCallback<DataItemGF>() {
    override fun areItemsTheSame(oldItem: DataItemGF, newItem: DataItemGF): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: DataItemGF, newItem: DataItemGF): Boolean {
        return oldItem == newItem
    }
}

class AreasGFListener(
    var clickListener: (pair: Pair<Long,String>) -> Unit,
) {
    fun onClick(area: AreaDBGOVPLXDBFazyDB) = clickListener(Pair(area.area.areaAutoId,area.govpl.Date))
}

sealed class DataItemGF {
    data class AreaGFItem(val areaGF: AreaDBGOVPLXDBFazyDB) : DataItemGF() {
        override val id = areaGF.area.areaAutoId
    }
    abstract val id: Long
}
