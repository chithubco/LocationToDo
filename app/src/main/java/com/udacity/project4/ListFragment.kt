package com.udacity.project4

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.databinding.AddReminderDialogBinding
import com.udacity.project4.databinding.FragmentListBinding
import com.udacity.project4.ui.adapters.ReminderListAdapter
import com.udacity.project4.ui.viewmodel.ListViewModel
import com.udacity.project4.utils.Permissions.hasLocationPermission
import com.udacity.project4.utils.Permissions.requestLocationPermission
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
    private val adapter = ReminderListAdapter(arrayListOf())
    private val args: ListFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentListBinding.bind(view)
        setHasOptionsMenu(true)

        setupObservers()
        setupListeners()
        if (args.reminder != null){
            val reminder = args.reminder!!
            val action = ListFragmentDirections.actionListFragmentToDetailFragment(reminder)
            findNavController().navigate(action)
        }
    }

    private val swipeCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val layoutPosition = viewHolder.layoutPosition
            val selectReminder = adapter.reminderList[layoutPosition]
            mListViewModel.deleteReminder(selectReminder)
        }
    }

    private fun setupObservers(){
        binding.reminderRecycleView.adapter = adapter
        binding.reminderRecycleView.layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.reminderRecycleView)

        binding.tvNoReminderData.visibility = View.GONE
        // Observe Livedata
        mListViewModel.reminderList.observe(viewLifecycleOwner, Observer {

            if (it.isNullOrEmpty()){
                binding.tvNoReminderData.visibility = View.VISIBLE
                binding.tvViewInMap.text = "Click to add reminders in Map"
            }else{
                adapter.setData(it)
                binding.tvNoReminderData.visibility = View.GONE
                binding.tvViewInMap.text = "Click to view/add reminders in Map"
            }
        })

    }

    private fun setupListeners(){

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            mListViewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
            Snackbar.make(binding.swipeRefreshLayout, "Refresh", Snackbar.LENGTH_SHORT)
        }

        binding.fabAddReminder.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToMapsFragment()
            findNavController().navigate(action)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.main_menu, menu)
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