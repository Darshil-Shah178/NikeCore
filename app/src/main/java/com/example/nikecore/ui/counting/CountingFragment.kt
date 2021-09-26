package com.example.nikecore.ui.counting

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nikecore.R
import kotlinx.android.synthetic.main.counting_fragment.*
import kotlinx.android.synthetic.main.onboarding_fragment.*
import kotlinx.coroutines.*

class CountingFragment : Fragment() {

    companion object {
        fun newInstance() = CountingFragment()
    }

    private lateinit var viewModel: CountingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return inflater.inflate(R.layout.counting_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CountingViewModel::class.java)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // With blank your fragment BackPressed will be disabled.
        }

        viewModel.number.observe(viewLifecycleOwner,{
            countingTxt.text = it.toString()
            val `in`: Animation = AlphaAnimation(0.0f, 1.0f)
            `in`.duration = 1000
            countingTxt.startAnimation(`in`)
        })

        viewModel.navigateToAccountingFragment.observe(viewLifecycleOwner,  {
            if (it)
                 findNavController().navigate(R.id.action_countingFragment_to_runStartedFragment)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}