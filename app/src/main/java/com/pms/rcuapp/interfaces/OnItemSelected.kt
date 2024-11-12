package com.pms.rcuapp.interfaces

interface OnItemSelected<T> {
    fun onItemSelected(t: T?, position: Int)
}