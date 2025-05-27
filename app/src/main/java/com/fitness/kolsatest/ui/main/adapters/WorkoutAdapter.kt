package com.fitness.kolsatest.ui.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fitness.kolsatest.R
import com.fitness.kolsatest.data.models.Workout

class WorkoutAdapter(
    private var workouts: List<Workout>,
    private val onItemClick: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.titleText)
        private val typeText: TextView = view.findViewById(R.id.typeText)
        private val durationText: TextView = view.findViewById(R.id.durationText)
        private val descriptionText: TextView = view.findViewById(R.id.descriptionText)

        fun bind(workout: Workout, onItemClick: (Workout) -> Unit) {
            val context = itemView.context

            val typeName = when (workout.type) {
                1 -> context.getString(R.string.workout_type_training)
                2 -> context.getString(R.string.workout_type_stream)
                3 -> context.getString(R.string.workout_type_complex)
                else -> context.getString(R.string.workout_type_unknown)
            }

            titleText.text = workout.title
            typeText.text = context.getString(R.string.workout_type_format, typeName)
            durationText.text = context.getString(R.string.workout_duration_format, workout.duration)
            descriptionText.text = workout.description

            itemView.setOnClickListener {
                onItemClick(workout)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workouts[position], onItemClick)
    }


    override fun getItemCount(): Int = workouts.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged()
    }
}