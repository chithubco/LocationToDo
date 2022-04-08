package com.echithub.locationtodo

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.echithub.locationtodo.data.model.Reminder
import com.echithub.locationtodo.databinding.AddReminderDialogBinding
import com.echithub.locationtodo.databinding.FragmentListBinding
import com.echithub.locationtodo.ui.adapters.ReminderListAdapter
import com.echithub.locationtodo.ui.viewmodel.ListViewModel
import com.google.android.material.snackbar.Snackbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ListFragment"
    private lateinit var mListViewModel: ListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListViewModel = ViewModelProvider(this).get(ListViewModel::class.java)

        val adapter = ReminderListAdapter(arrayListOf())
        binding.reminderRecycleView.adapter = adapter
        binding.reminderRecycleView.layoutManager = LinearLayoutManager(requireContext())

        // Observe Livedata
        mListViewModel.readAllData.observe(viewLifecycleOwner, Observer { reminders ->
            adapter.setData(reminders)
        })

        binding.fabAddReminder.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToMapsFragment()
            findNavController().navigate(action)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            mListViewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
            Snackbar.make(binding.swipeRefreshLayout,"Refresh",Snackbar.LENGTH_SHORT)
        }
    }

    private fun createAddReminderDialog(){
        val dialogBinding = DataBindingUtil.inflate<AddReminderDialogBinding>(
            LayoutInflater.from(requireContext()),
            R.layout.add_reminder_dialog,
            null,
            false
        )

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Add Reminder"){dialog, which ->
                var title = dialogBinding.etTitle.text.toString()
                var description = dialogBinding.etDescription.text.toString()
                val reminderToAdd = Reminder(1, title, description, "", "", "")
                mListViewModel.addReminder(reminderToAdd)
                Log.i(TAG,"Success adding record")
            }.setNegativeButton("Cancel"){dialog, which ->}
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater?.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}