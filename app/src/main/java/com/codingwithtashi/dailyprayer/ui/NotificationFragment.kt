package com.codingwithtashi.dailyprayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingwithtashi.dailyprayer.R
import com.codingwithtashi.dailyprayer.adapter.NotificationAdapter
import com.codingwithtashi.dailyprayer.model.PrayerNotification
import com.codingwithtashi.dailyprayer.utils.CommonUtils
import com.codingwithtashi.dailyprayer.utils.PrayerPreference
import com.codingwithtashi.dailyprayer.utils.PreferenceConst
import com.codingwithtashi.dailyprayer.utils.SwipeToDeleteCallback
import com.codingwithtashi.dailyprayer.viewmodel.NotificationViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import java.util.*


class NotificationFragment : Fragment() {
    lateinit var notificationMsg: MaterialTextView;
   lateinit var recyclerView:RecyclerView;
    lateinit var relativeLayout:RelativeLayout;
    lateinit var notificationAdapter:NotificationAdapter
    lateinit var mutableListOfPrayerNotification: MutableList<PrayerNotification>
    private val notificationViewModel: NotificationViewModel by activityViewModels();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_notification, container, false)
        notificationMsg = view.findViewById(R.id.notification_text);
        relativeLayout = view.findViewById(R.id.notification_container)
        notificationMsg.visibility= View.VISIBLE;

        recyclerView = view.findViewById(R.id.recyclerView)
        mutableListOfPrayerNotification = mutableListOf()



        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as NotificationAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                CommonUtils.displaySnackBar(relativeLayout,"Deleted");
                if(mutableListOfPrayerNotification.size==0){
                    notificationMsg.visibility= View.VISIBLE;
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        notificationViewModel.notifications.observe(viewLifecycleOwner
        ) {
            if(it.size>0){

                notificationMsg.visibility= View.GONE;
                mutableListOfPrayerNotification = it
                notificationAdapter =NotificationAdapter(mutableListOfPrayerNotification)

                recyclerView.apply {
                    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                    layoutManager = LinearLayoutManager(context)
                    adapter = notificationAdapter
                }
            }else{
                notificationMsg.visibility= View.VISIBLE;

            }
        }

        return view;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { PrayerPreference.setContext(it) };

        val pref = PrayerPreference.getPreference().getBoolean(PreferenceConst.IS_NOTIFICATION_OPEN_FIRST,false);
        if(!pref){
            PrayerPreference.getPreference().edit().putBoolean(PreferenceConst.IS_NOTIFICATION_OPEN_FIRST,true).apply()
            context?.let {
                MaterialAlertDialogBuilder(it)
                    .setTitle("Notification Page")
                    .setMessage("We will send notification here. such as \n1.If new prayer is added \n2.New Feature released \n3.Any Update \nTeam Daily Prayer")
                    .setPositiveButton("Okay") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }


}