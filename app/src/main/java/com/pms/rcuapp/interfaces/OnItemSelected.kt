package com.squmish.rcuapp.interfaces

interface OnItemSelected<T> {
    fun onItemSelected(t: T?, position: Int)
}