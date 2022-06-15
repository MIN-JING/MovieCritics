package com.jim.moviecritics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jim.moviecritics.data.Actor
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.data.Rating
import com.jim.moviecritics.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        binding.button.setOnClickListener {
            Log.d("Jim", "Button onClick")
            loadMockDataMovie()
            listenData()
        }
    }

    private fun listenData() {
        val movies = FirebaseFirestore.getInstance()
            .collection("movie")
            .orderBy("id", Query.Direction.DESCENDING)

        movies.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("Jim", "listen: error", e)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                for (dc in snapshots.documents) {
                    Log.d("Jim", "snapshots.documents = ${dc.data}")
                    Log.d("Jim", "snapshots.documents = ${dc.data?.get("title")}")

                }

            }
        }
    }

    private fun loadMockDataMovie() {
        val movies = FirebaseFirestore.getInstance().collection("movie")
        val document = movies.document()
        val data = Movie(
            12345,
            "tt0343818",
            "67890",
            "I, Robot",
            "https://m.media-amazon.com/images/M/MV5BNmE1OWI2ZGItMDUyOS00MmU5LWE0MzUtYTQ0YzA1YTE5MGYxXkEyXkFqcGdeQXVyMDM5ODIyNw@@._V1_SX300.jpg",
            "https://www.imdb.com/video/vi160497945/?playlistId=tt0343818?ref_=ext_shr_lnk",
            "16 Jul 2004",
            "Action, Mystery, Sci-Fi",
            "Alex Proyas",
            listOf(
                Actor(56788, "34671", "Will Smith"),
                Actor(46754, "23453", "Bridget Moynahan"),
                Actor(45332, "23425", "Bruce Greenwood")
            ),
            "In 2035, techno-phobic homicide detective Del Spooner of the Chicago PD heads the investigation of the apparent suicide of leading robotics scientist, Dr. Alfred Lanning. Unconvinced of the motive, Spooner's investigation into Lanning's death reveals a trail of secrets and agendas within the USR (United States Robotics) corporation and suspicions of murder. Little does he know that his investigation would lead to uncovering a larger threat to humanity.",
            "United States, Germany",
            "Nominated for 1 Oscar. 1 win & 15 nominations total",
            "PG-13",
            "115 min",
            "Jeff Vintar, Akiva Goldsman, Isaac Asimov",
            listOf(
                Rating("Internet Movie Database","7.1/10"),
                Rating("Metacritic", "59/100")
            ),
            "$144,801,023",
            null
        )
        document.set(data)
    }

    private fun loadMockDataComment() {

    }

    private fun loadMockDataScore() {

    }

    private fun loadMockDataUser() {

    }
}
//    private fun listenData(
//        allData: MutableList<DataClass>?,
//    ) {
//
//        val articles = FirebaseFirestore.getInstance()
//            .collection("articles")
//            .orderBy("createdTime", Query.Direction.DESCENDING)
//
//
//        articles.addSnapshotListener { snapshots, e ->
//            if (e != null) {
//                Log.w("Jim", "listen:error", e)
//                return@addSnapshotListener
//            }
//            for (dc in snapshots!!.documents) {
//                Log.d("Jim", " snapshots!!.documents ${dc.data}")
//                Log.d("Jim", " dc.data?.get(\"title\") ${dc.data?.get("title")}")
//                Log.d("Jim", "dc.data?.get(\"author\") = ${dc.data?.get("author")}")
//                dc.data?.get("author")
//                val tempAuthor = dc.data?.get("author") as HashMap<*, *>
//                Log.d("Jim", "author HashMap<*, *> = ${tempAuthor?.get("email")}")
//
//                val tempData = DataClass(
//                    AuthorData(
//                        tempAuthor?.get("email") as String,
//                        tempAuthor?.get("id") as String,
//                        tempAuthor?.get("name") as String,
//                    ),
//                    dc.data?.get("title") as String,
//                    dc.data?.get("content") as String,
//                    dc.data?.get("createdTime") as Long,
//                    dc.data?.get("id") as String,
//                    dc.data?.get("category") as String
//                )
//                Log.d("Jim", "tempData = $tempData")
//
//                allData?.add(tempData)
//                Log.d("Jim", "allData?.add = $allData")
//                mAdapter.submitList(allData)
//
//            }
//        }
//    }
//
//    private fun addData() {
//        val articles = FirebaseFirestore.getInstance()
//            .collection("articles")
//        val document = articles.document()
//        val data = hashMapOf(
//            "author" to hashMapOf(
//                "email" to "wayne@school.appworks.tw",
//                "id" to "waynechen323",
//                "name" to "AKA小安老師"
//            ),
//            "title" to "IU「亂穿」竟美出新境界！笑稱自己品味奇怪　網笑：靠顏值撐住女神氣場",
//            "content" to "南韓歌手IU（李知恩）無論在歌唱方面或是近期的戲劇作品都有亮眼的成績，但俗話說人無完美、美玉微瑕，曾再跟工作人員的互動影片中坦言自己品味很奇怪，近日在IG上分享了宛如「媽媽們青春時代的玉女歌手」超復古穿搭造型，卻意外美出新境界。",
//            "createdTime" to Calendar.getInstance().timeInMillis,
//            "id" to document.id,
//            "category" to "Beauty"
//        )
//        document.set(data)
//    }
