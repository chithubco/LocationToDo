package com.echithub.locationtodo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.echithub.locationtodo.databinding.FragmentMainBinding
import com.echithub.locationtodo.ui.viewmodel.LoginViewModel
import com.echithub.locationtodo.utils.Constants.SIGN_IN_REQUEST_CODE
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private var _binding:FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var mViewModel: LoginViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater,container,false)

        binding.btnLogin.setOnClickListener {
            launchSingInFlow()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        navController = findNavController()
        mViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when(authenticationState){
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    navController.popBackStack()
                    binding.btnLogin.text = "Logout"
                }
                LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> Snackbar.make(
                    view, requireActivity().getString(R.string.login_unsuccessful_msg),
                    Snackbar.LENGTH_LONG
                ).show()
                else -> Log.e(
                    TAG,
                    "Authentication state that doesn't require any UI change $authenticationState"
                )
            }
        })
    }

    private fun launchSingInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build(),SIGN_IN_REQUEST_CODE)
    }

    fun logout() {
        AuthUI.getInstance().signOut(requireContext())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE){
            val response =IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                Log.i("MainFragment","Successfully singened in ${FirebaseAuth.getInstance().currentUser?.displayName}")
                val action = MainFragmentDirections.actionMainFragmentToListFragment()
                findNavController().navigate(action)
            }else{
                Log.i("MainFragment","Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }
}