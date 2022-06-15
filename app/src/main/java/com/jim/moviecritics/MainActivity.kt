package com.jim.moviecritics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jim.moviecritics.data.*
import com.jim.moviecritics.databinding.ActivityMainBinding



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
            loadMockDataComment()
            loadMockDataScore()
            loadMockDataUser()
            listenData()
        }
    }

    private fun listenData() {
        val movies = FirebaseFirestore.getInstance()
            .collection("movie")
            .orderBy("id", Query.Direction.DESCENDING)

        movies.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("Jim", "movies listen: error", e)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                for (dc in snapshots.documents) {
                    Log.d("Jim", "movies snapshots.documents = ${dc.data}")
                    Log.d("Jim", "movies dc.data?.get(\"title\") = ${dc.data?.get("title")}")

                }

            }
        }

        val comments = FirebaseFirestore.getInstance()
            .collection("comment")
            .orderBy("createdTime", Query.Direction.DESCENDING)

        comments.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("Jim", "comments listen: error", e)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                for (dc in snapshots.documents) {
                    Log.d("Jim", "comments snapshots.documents = ${dc.data}")
                    Log.d("Jim", "comments dc.data?.get(\"content\") = ${dc.data?.get("content")}")

                }

            }
        }

        val scores = FirebaseFirestore.getInstance()
            .collection("score")
            .orderBy("createdTime", Query.Direction.DESCENDING)
        scores.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("Jim", "scores listen: error", e)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                for (dc in snapshots.documents) {
                    Log.d("Jim", "scores snapshots.documents = ${dc.data}")
                    Log.d("Jim", "scores dc.data?.get(\"average\") = ${dc.data?.get("average")}")

                }

            }
        }

        val users = FirebaseFirestore.getInstance()
            .collection("user")
            .orderBy("id", Query.Direction.DESCENDING)

        users.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("Jim", "users listen: error", e)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                for (dc in snapshots.documents) {
                    Log.d("Jim", "users snapshots.documents = ${dc.data}")
                    Log.d("Jim", "users dc.data?.get(\"name\") = ${dc.data?.get("name")}")

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
        val comments = FirebaseFirestore.getInstance().collection("comment")
        val document = comments.document()
        val data = Comment(
            "23456",
            12344,
            "tt0343818",
            Timestamp.now(),
            "Would Google LaMDA chat robot become a human?",
            listOf(12345, 45678, 89012),
            listOf(12345, 45678, 89012)
        )
        document.set(data)
    }

    private fun loadMockDataScore() {
        val scores = FirebaseFirestore.getInstance().collection("score")
        val document = scores.document()
        val tempLeisure = 5.6
        val tempHit = 6.5
        val tempCast = 7.2
        val tempMusic = 9.3
        val tempStory = 8.7
        val average = (tempLeisure + tempHit + tempMusic + tempStory)/5

        val data = Score(
            123345,
            67891,
            "tt0343818",
            Timestamp.now(),
            tempLeisure,
            tempHit,
            tempCast,
            tempMusic,
            tempStory,
            average
        )
        document.set(data)
    }

    private fun loadMockDataUser() {
        val users = FirebaseFirestore.getInstance().collection("user")
        val document = users.document()
        val data = User(
            12345,
            "MIN-JING",
            "https://1.bp.blogspot.com/-Tk6O2ne3XbI/Xtt6icgq3WI/AAAAAAABZRU/MAxy4N6fTmIWjBqDVRHg6V2bq8gDY2P9ACNcBGAsYHQ/s400/nebusoku_doctor_man.png",
            "Taipei City",
            "instagram.com",
            "twitter.com",
            listOf(123, 456, 789),
            listOf(123, 456, 789),
            listOf(123, 456, 789),
            listOf("tt0343818","tt0343818"),
            listOf("tt0343818","tt0343818")
        )
        document.set(data)
    }
}
