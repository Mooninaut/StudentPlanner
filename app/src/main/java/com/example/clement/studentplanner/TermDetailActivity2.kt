package com.example.clement.studentplanner

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.course_list_item_v2.view.*
import kotlinx.android.synthetic.main.term_detail_activity_v2.*
import java.time.LocalDate


/**
 * Created by Clement on 12/5/2017.
 */

class TermDetailActivity2 : AppCompatActivity() {
    enum class CourseStatus {
        PASSED, FAILED, IN_PROGRESS
    }

    data class CourseData(
            val term: Int,
            val start: LocalDate,
            val end: LocalDate,
            val name: String,
            val status: CourseStatus,
            val CUs: Int
    )
    private class Course2Adapter(
            private val courses: List<CourseData>,
            private val context: Context
    ) : RecyclerView.Adapter<Course2Adapter.Course2ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Course2ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.course_list_item_v2, parent, false)
            return Course2ViewHolder(v)
        }

        override fun onBindViewHolder(holder: Course2ViewHolder, position: Int) {
            val course = courses[position]
            with(course) {
                holder.status.text = status.toString()
                if (status == CourseStatus.PASSED) {
                    holder.date.text = end.toString()
                } else {
                    holder.date.text = start.toString() + "—" + end.toString()
                }
                holder.name.text = name
                holder.units.text = context.resources.getStringArray(R.array.dice_faces)[CUs - 1]
            }
        }

        override fun getItemCount(): Int {
            return courses.size
        }

        internal class Course2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardView: CardView = itemView.card
            val units: TextView = itemView.units
            val name: TextView = itemView.name
            val date: TextView = itemView.date
            val status: TextView = itemView.status
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.term_detail_activity_v2)
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        termDetailRecyclerView.adapter = Course2Adapter(courses, this)
    }

    companion object {
        internal val courses: List<CourseData> = listOf (
            CourseData(5,
                    LocalDate.of(2017, 4, 1),
                    LocalDate.of(2017, 4, 15),
                    "C188 Software Engineering",
                    CourseStatus.PASSED,
                    4),
            CourseData(5,
                    LocalDate.of(2017, 4, 16),
                    LocalDate.of(2017, 4, 30),
                    "C195 Software II - Advanced Java Concepts",
                    CourseStatus.PASSED,
                    6),
            CourseData(5,
                    LocalDate.of(2017, 5, 1),
                    LocalDate.of(2017, 6, 14),
                    "C193 Client-Server Application Development",
                    CourseStatus.PASSED,
                    3),
            CourseData(5,
                    LocalDate.of(2017, 6, 15),
                    LocalDate.of(2017, 7, 23),
                    "C179 Business of IT - Applications",
                    CourseStatus.PASSED,
                    4)
        )
    }
}
