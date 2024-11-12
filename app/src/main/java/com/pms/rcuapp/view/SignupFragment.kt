package com.pms.rcuapp.view

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.SignupFragmentBinding
import com.pms.rcuapp.view.base.BaseFragment
import com.pms.rcuapp.viewmodel.SignupViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SignupFragment : BaseFragment(){

    private var _binding: SignupFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // ViewModel
    private val signupViewModel by lazy { SignupViewModel(activity as Context,this@SignupFragment,binding) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = SignupFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = signupViewModel
        binding.lifecycleOwner = this


        signupViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // For Set Login Text Span
        setSignUpSpanText()
    }

    private fun setSignUpSpanText() {
        val loginText : Spannable = SpannableString(resources.getString(R.string.alredayhaveId))

        loginText.setSpan(
            StyleSpan(Typeface.BOLD), 21, 32, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        binding.txtRedirectToLogin.text = loginText
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}