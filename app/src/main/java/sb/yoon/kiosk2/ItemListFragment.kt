package sb.yoon.kiosk2

import android.os.Bundle
import sb.yoon.kiosk2.controller.KioskListAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.ListFragment
import sb.yoon.kiosk2.model.Menu

class ItemListFragment(private val menuList: List<Menu>) : ListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivity = activity as KioskListActivity?

        // 인덱스 표시 어댑터 설정
        val adapter = KioskListAdapter(menuList, mainActivity)
        // 어댑터를 설정
        listAdapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_fragment, container, false)
    }
}