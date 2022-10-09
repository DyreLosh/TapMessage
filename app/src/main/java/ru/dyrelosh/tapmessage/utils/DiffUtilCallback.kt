package ru.dyrelosh.tapmessage.utils

import androidx.recyclerview.widget.DiffUtil
import ru.dyrelosh.tapmessage.models.Common


class DiffUtilCallback (
    private val oldList: List<Common>,
    private val newList: List<Common>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].timeStamp == newList[newItemPosition].timeStamp

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

}