package s1531567.songle

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_faq.*
import org.jetbrains.anko.toast

/* A class for introducing new players to the game. */
class FaqActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        val expandableListView = findViewById<ExpandableListView>(R.id.expandableListView)
        val expandableListDetail = ExpandableListDataPump().getData()
        val expandableListTitle = ArrayList<String>(expandableListDetail.keys)
        val expandableListAdapter = ExpandableListAdapter(this, expandableListTitle, expandableListDetail)
        expandableListView.setAdapter(expandableListAdapter)



    }




    inner class ExpandableListDataPump {

        fun getData() : HashMap<String, String> {
            val map = HashMap<String, String>()

            map.put("What is Songle?", getString(R.string.what_is_songle))
            map.put("How do I collect a Placemark?", getString(R.string.collecting_placemarks))
            map.put("How do I guess the song?", getString(R.string.guess_song))
            map.put("How do I change the song?", getString(R.string.change_song))
            map.put("What are the Dollar and Music Buttons for?", getString(R.string.dollar_music))
            map.put("What can I spend my currency on?", getString(R.string.purchases))
            map.put("Placemark Cost", getString(R.string.placemark_cost))
            map.put("Currency Conversion", getString(R.string.currency_conversion))



            return map
        }
    }

    inner class ExpandableListAdapter(context: Context, title : List<String>, detail : HashMap<String, String>) : BaseExpandableListAdapter() {
        private val mContext = context
        private val mTitle = title
        private val mDetail = detail

        override fun getChild(pos: Int, expandPos: Int): Any {
            return mDetail.get(mTitle.get(pos)) as Any
        }

        override fun getChildId(p0: Int, p1: Int): Long {
            return p1.toLong()
        }

        override fun getChildView(listPosition: Int,
                                  expandListPosition: Int,
                                  isLastChild: Boolean,
                                  convertView: View?,
                                  parent: ViewGroup?): View {

            val expandedListText : String = getChild(listPosition, expandListPosition) as String
            var newView : View

            if (convertView == null) {
                val layoutInflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                newView = layoutInflator.inflate(R.layout.faq_item, null )
                val expandedListTextView = newView!!.findViewById<TextView>(R.id.expandedListItem) as TextView
                expandedListTextView.text = expandedListText
                return newView
            }

            val expandedListTextView = convertView!!.findViewById<TextView>(R.id.expandedListItem) as TextView
            expandedListTextView.text = expandedListText
            return convertView

        }

        override fun getChildrenCount(p0: Int): Int {
            return 1
        }

        override fun getGroup(p0: Int): Any {
            return mTitle.get(p0)
        }

        override fun getGroupCount(): Int {
            return mTitle.size
        }

        override fun getGroupId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getGroupView(listPos: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {

            val listTitle = getGroup(listPos) as String
            var newView : View

            if (convertView == null ){
                val layoutInflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                newView = layoutInflator.inflate(R.layout.faq_layout, null )
                val listTitleTextView = newView!!.findViewById<TextView>(R.id.listTitle) as TextView
                listTitleTextView.setTypeface(null, Typeface.BOLD)
                listTitleTextView.text = listTitle
                return newView
            }

            val listTitleTextView = convertView!!.findViewById<TextView>(R.id.listTitle) as TextView
            listTitleTextView.setTypeface(null, Typeface.BOLD)
            listTitleTextView.text = listTitle
            return convertView

        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun isChildSelectable(p0: Int, p1: Int): Boolean {
            return true
        }

    }
}
