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
import pl.covid19.database.AreaDB
import pl.covid19.databinding.ListItemAreaCovidBinding


private val ITEM_VIEW_TYPE_ITEM = 1

class AreasCovidAdapter(val clickListener: AreasCovidListener) : ListAdapter<DataItem,
        RecyclerView.ViewHolder>(AreaCovidDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun SubmitList(list: List<AreaDB>?) {
        adapterScope.launch {
            val items = list?.map { DataItem.AreaCovidItem(it) }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val areaItem = getItem(position) as DataItem.AreaCovidItem
                holder.bind(clickListener, areaItem.areaCovid)
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
            // is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.AreaCovidItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    fun getNoteAt(pos: Int): Long {
        return  getItem(pos).id
    }

    class ViewHolder private constructor(val binding: ListItemAreaCovidBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: AreasCovidListener, item: AreaDB) {
            binding.area = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAreaCovidBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class AreaCovidDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class AreasCovidListener(
    var clickListener: (areaId: Long) -> Unit,
) {
    fun onClick(area: AreaDB) = clickListener(area.areaAutoId)
}

sealed class DataItem {
    data class AreaCovidItem(val areaCovid: AreaDB) : DataItem() {
        override val id = areaCovid.areaAutoId
    }

    abstract val id: Long
}

