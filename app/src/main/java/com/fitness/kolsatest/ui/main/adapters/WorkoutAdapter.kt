package com.fitness.kolsatest.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fitness.kolsatest.R
import com.fitness.kolsatest.data.models.Workout

class WorkoutAdapter(
    private var workouts: List<Workout>
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val typeText: TextView = view.findViewById(R.id.typeText)
        val durationText: TextView = view.findViewById(R.id.durationText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        val context = holder.itemView.context

        holder.titleText.text = workout.title
        holder.typeText.text = context.getString(R.string.workout_type, workout.type.toString())
        holder.durationText.text = context.getString(R.string.workout_duration, workout.duration)
    }

    override fun getItemCount(): Int = workouts.size

    fun updateData(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged()
    }
}
