package com.evya.myweatherapp.ui

import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.ActivityDayPlanBinding
import com.evya.myweatherapp.databinding.PlanStopItemBinding
import com.evya.myweatherapp.model.DayPlan
import com.evya.myweatherapp.model.PlanStop
import com.evya.myweatherapp.repository.DayPlanStore
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DayPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDayPlanBinding
    private lateinit var store: DayPlanStore
    private var plan = DayPlan()
    private val adapter = StopsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDayPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        store = DayPlanStore(this)
        plan = store.load()
        binding.back.setOnClickListener { finish() }
        binding.stops.layoutManager = LinearLayoutManager(this)
        binding.stops.adapter = adapter
        binding.notes.setText(plan.notes)
        binding.notes.doAfterTextChanged { plan = plan.copy(notes = it.toString()); store.save(plan) }
        binding.date.setOnClickListener {
            val date = runCatching { LocalDate.parse(plan.date) }.getOrDefault(LocalDate.now())
            DatePickerDialog(this, { _, year, month, day ->
                update(plan.copy(date = LocalDate.of(year, month + 1, day).toString()))
            }, date.year, date.monthValue - 1, date.dayOfMonth).show()
        }
        binding.share.setOnClickListener {
            val text = buildString {
                appendLine(getString(R.string.plan_title))
                if (plan.date.isNotBlank()) appendLine(plan.date)
                plan.stops.forEachIndexed { index, stop ->
                    appendLine("${index + 1}. ${stop.name}")
                    appendLine("https://www.google.com/maps/search/?api=1&query=${stop.latitude},${stop.longitude}")
                }
                if (plan.notes.isNotBlank()) appendLine(plan.notes)
            }
            try {
                startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text)
                }, getString(R.string.plan_share)))
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(this, R.string.plan_no_share, Toast.LENGTH_SHORT).show()
            }
        }
        render()
    }

    private fun update(next: DayPlan) { plan = next; store.save(plan); render() }
    private fun render() {
        adapter.notifyDataSetChanged()
        binding.empty.isVisible = plan.stops.isEmpty()
        binding.share.isEnabled = plan.stops.isNotEmpty()
        val date = runCatching {
            LocalDate.parse(plan.date).format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH))
        }.getOrNull()
        binding.date.text = date?.let { getString(R.string.plan_date_value, it) } ?: getString(R.string.plan_date)
    }

    private fun directions(stop: PlanStop) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=${stop.latitude},${stop.longitude}")))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.results_no_maps, Toast.LENGTH_SHORT).show()
        }
    }

    private inner class StopsAdapter : RecyclerView.Adapter<StopsAdapter.Holder>() {
        inner class Holder(val row: PlanStopItemBinding) : RecyclerView.ViewHolder(row.root)
        override fun getItemCount() = plan.stops.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
            PlanStopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun onBindViewHolder(holder: Holder, position: Int) {
            val stop = plan.stops[position]
            with(holder.row) {
                name.text = stop.name
                order.text = getString(R.string.plan_stop, position + 1, stop.category)
                up.isEnabled = position > 0
                down.isEnabled = position < plan.stops.lastIndex
                up.setOnClickListener { update(plan.move(stop.id, -1)) }
                down.setOnClickListener { update(plan.move(stop.id, 1)) }
                directions.setOnClickListener { directions(stop) }
                remove.setOnClickListener {
                    update(plan.remove(stop.id))
                    Snackbar.make(binding.root, R.string.plan_removed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.plan_undo) {
                            if (plan.stops.none { it.id == stop.id }) {
                                val restored = plan.stops.toMutableList().apply { add(position.coerceAtMost(size), stop) }
                                update(plan.copy(stops = restored))
                            }
                        }.show()
                }
            }
        }
    }
}
