package com.pms.rcuapp.view

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.request.RequestOptions
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.LoginscreenBinding
import com.pms.rcuapp.uttils.Session
import com.pms.rcuapp.view.base.BaseFragment
import com.pms.rcuapp.viewmodel.LoginViewModel


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : BaseFragment() {

    private var _binding: LoginscreenBinding? = null
    private val binding get() = _binding!!
    private val signInViewModel by lazy { LoginViewModel(activity as Context,this@LoginFragment,binding) }
    private var options: RequestOptions? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LoginscreenBinding.inflate(inflater, container, false)
        binding.viewModel = signInViewModel
        binding.lifecycleOwner = this

        val email: String = session.getDataByKey(Session.KEY_USER_EMAIL, "")
        val pwd: String = session.getDataByKey(Session.KEY_USER_PASSWORD, "")
        session.storeDataByKey(Session.KEY_USER_NAME,"")
        signInViewModel.email.set(email)
        signInViewModel.password.set(pwd)

        binding.chkRememberPassword.isChecked = session.getDataByKey(Session.KEY_USER_REMEMBER, false)

        signInViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        options = RequestOptions()
            .circleCrop()
            .placeholder(R.drawable.icon_main)
            .error(R.drawable.icon_main)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // For Set Login Text Span
        setLoginSpanText()
    }

    private fun setLoginSpanText() {
        val loginText : Spannable = SpannableString(resources.getString(R.string.donthaveId))
        loginText.setSpan( StyleSpan(Typeface.BOLD), 20, 32, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        binding.txtRedirectToSignup.text = loginText
    }

    fun addText(){
        findNavController().navigate(R.id.action_LoginFragment_to_SignUpFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}