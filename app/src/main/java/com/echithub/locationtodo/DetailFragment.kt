package com.echithub.locationtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.echithub.locationtodo.databinding.FragmentDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DetailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val TAG = "DetailFragement"

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_detail, container, false)
        _binding = FragmentDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = args.reminder.title
        binding.tvDescription.text = args.reminder.description

        binding.btnBack.setOnClickListener {
            val direction = DetailFragmentDirections.actionDetailFragmentToListFragment()
            findNavController().navigate(direction)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}