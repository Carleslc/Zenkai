package ai.zenkai.zenkai.common.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

open class BaseRecyclerViewAdapter<T : ItemAdapter<out BaseViewHolder>>(initialItems: List<T> = listOf(),
    private val attached: RecyclerView) : RecyclerView.Adapter<BaseViewHolder>() {

    protected val items: MutableList<T> = initialItems.toMutableList()

    open fun setCustomItemViewParams(parent: ViewGroup, itemView: View) {}

    fun add(item: T) {
        items.add(item)
        val end = itemCount - 1
        notifyItemInserted(end)
        attached.scrollToPosition(end)
    }
    
    fun addAll(newItems: Collection<T>) {
        val start = itemCount
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
        attached.scrollToPosition(start)
    }
    
    final override fun getItemCount() = items.size

    final override fun getItemViewType(position: Int) = items[position].layoutId

    final override fun onCreateViewHolder(parent: ViewGroup, layoutId: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        setCustomItemViewParams(parent, itemView)
        return items.first { it.layoutId == layoutId }.onCreateViewHolder(itemView, parent)
    }

    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        items[position].onBindBaseViewHolder(holder)
    }
}
