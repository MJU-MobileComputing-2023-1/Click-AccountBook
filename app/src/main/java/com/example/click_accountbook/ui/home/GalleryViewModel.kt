package com.example.click_accountbook.ui.home

import com.example.click_accountbook.DBDao
import androidx.lifecycle.*
import com.example.click_accountbook.Receipt
import kotlinx.coroutines.launch

class GalleryViewModel(private val dbDao: DBDao) : ViewModel() {

    private val _receipts = MutableLiveData<List<Receipt>>()
    val receipts: LiveData<List<Receipt>> get() = _receipts

    init {
        getReceipts()
    }

    private fun getReceipts() = viewModelScope.launch {
        _receipts.value = dbDao.getAllReceipts()
    }
}

