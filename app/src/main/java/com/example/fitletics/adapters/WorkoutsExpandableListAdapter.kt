package com.example.fitletics.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import com.example.fitletics.R
import com.example.fitletics.models.support.Workout
import java.util.*

class WorkoutsExpandableListAdapter internal constructor
    (private val context: Activity, private val titleList: List<String>, private val dataList: HashMap<String, List<Workout>>, private val exp: ExpandableListView?) : BaseExpandableListAdapter() {


    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return this.dataList[this.titleList[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(listPosition: Int, expandedListPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val expandedListWorkout = getChild(listPosition, expandedListPosition) as Workout
        val expandedListText = expandedListWorkout.name
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.expandable_list_item, null)
        }
        val expandedListTextView = convertView!!.findViewById<TextView>(R.id.expandedListItem)
        expandedListTextView.text = expandedListText
        exp?.setDividerHeight(0);

        if (isLastChild && (listPosition == groupCount - 1)) {
//        if (isLastChild)
            convertView.background = context!!.resources.getDrawable(R.drawable.last_expandable_item, context!!.theme)
            Log.d("GROUP", "listPosition = $listPosition, groupCount - 1 = ${groupCount - 1} ");
        }
//        else if (listPosition == 0 )
//            convertView.background = context!!.resources.getDrawable(R.drawable.first_expandable_item, context!!.theme)
        else
            convertView.background = context!!.resources.getDrawable(R.drawable.middle_expandable_item, context!!.theme)

        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.dataList[this.titleList[listPosition]]!!.size
    }

    override fun getGroup(listPosition: Int): Any {
        return this.titleList[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.titleList.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val listTitle = getGroup(listPosition) as String

        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.expandable_list_group, null)
        }
        val listTitleTextView = convertView!!.findViewById<TextView>(R.id.listTitle)
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle


        if (listPosition == this.titleList.size - 1 ) {
            if (isExpanded)
                convertView.background = context!!.resources.getDrawable(R.drawable.middle_expandable_group, context!!.theme)
            else
                convertView.background = context!!.resources.getDrawable(R.drawable.last_expandable_group, context!!.theme)
        }
        else if (listPosition == 0 )
            convertView.background = context!!.resources.getDrawable(R.drawable.first_expandable_group, context!!.theme)
        else
            convertView.background = context!!.resources.getDrawable(R.drawable.middle_expandable_group, context!!.theme)

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}
