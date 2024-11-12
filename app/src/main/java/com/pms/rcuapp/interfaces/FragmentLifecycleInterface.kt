package com.pms.rcuapp.interfaces

interface FragmentLifecycleInterface {
    fun onPauseFragment()
    fun onResumeFragment(s: String?)
}