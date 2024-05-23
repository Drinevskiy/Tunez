package com.example.tunez.viewmodels

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            SearchViewModel(
                inventoryApplication().container.itemsRepository
            )
        }

    }
}