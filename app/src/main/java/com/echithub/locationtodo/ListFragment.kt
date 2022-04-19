package com.echithub.locationtodo

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.echithub.locationtodo.data.model.Reminder
import com.echithub.locationtodo.databinding.AddReminderDialogBinding
import com.echithub.locationtodo.databinding.FragmentListBinding
import com.echithub.locationtodo.ui.adapters.ReminderListAdapter
import com.echithub.locationtodo.ui.viewmodel.ListViewModel
import com.echithub.locationtodo.utils.Permissions.hasLocationPermission
import com.echithub.locationtodo.utils.Permissions.requestLocationPermission
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class ListFragment : Fragment(R.layout.fragment_list), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ListFragment"
    private val mListViewModel by viewModels<ListViewModel>(){
        ListViewModel.ListViewModelFactory((requireContext().applicationContext as ToDoApplication).reminderRepo)
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentListBinding.inflate(inflater, container, false)
////        setHasOptionsMenu(true)
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentListBinding.bind(view)
        setHasOptionsMenu(true)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers(){
        val adapter = ReminderListAdapter(arrayListOf())
        binding.reminderRecycleView.adapter = adapter
        binding.reminderRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.tvNoReminderData.visibility = View.GONE
        // Observe Livedata
        mListViewModel.reminders.observe(viewLifecycleOwner, Observer { reminders ->

            if (reminders.isEmpty()){
                Log.i("Reminder Data",reminders.toString())
                binding.tvNoReminderData.visibility = View.VISIBLE
                binding.tvViewInMap.text = "Click to add reminders in Map"
            }else{
                binding.tvNoReminderData.visibility = View.GONE
                adapter.setData(reminders)
                binding.tvViewInMap.text = "Click to view/add reminders in Map"
            }
        })
        mListViewModel.refresh()
    }

    private fun setupListeners(){

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            mListViewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
            Snackbar.make(binding.swipeRefreshLayout, "Refresh", Snackbar.LENGTH_SHORT)
        }

        binding.fabAddReminder.setOnClickListener {

            if (hasLocationPermission(requireContext())) {
                val action = ListFragmentDirections.actionListFragmentToMapsFragment()
                findNavController().navigate(action)
            } else {
                requestLocationPermission(this)
            }

        }
    }

    private fun createAddReminderDialog() {
        val dialogBinding = DataBindingUtil.inflate<AddReminderDialogBinding>(
            LayoutInflater.from(requireContext()),
            R.layout.add_reminder_dialog,
            null,
            false
        )

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Add Reminder") { dialog, which ->
                var title = dialogBinding.etTitle.text.toString()
                var description = dialogBinding.etDescription.text.toString()
                val reminderToAdd = Reminder(1, title, description, "", "", "")
                mListViewModel.addReminder(reminderToAdd)
                Log.i(TAG, "Success adding record")
            }.setNegativeButton("Cancel") { dialog, which -> }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater?.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout ->{
                Toast.makeText(requireContext(), "Logout Operation", Toast.LENGTH_LONG).show()
                AuthUI.getInstance().signOut(requireContext())
                findNavController().navigate(ListFragmentDirections.actionListFragmentToMainFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            SettingsDialog.Builder(
                requireActivity(),
            ).build().show()
        }else{
            requestLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        val action = ListFragmentDirections.actionListFragmentToMapsFragment()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}