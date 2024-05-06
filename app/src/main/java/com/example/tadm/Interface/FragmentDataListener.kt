package com.example.tadm.Interface

import com.example.tadm.model.NewEntryFormData

interface FragmentDataListener {
    fun onBasicDataReceived(data: NewEntryFormData)
    fun onFamilyDataReceived(data: String)
    fun onDeraDataReceived(data: String)

}
