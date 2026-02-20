package com.gusbarros.blockcalls.di

import com.gusbarros.blockcalls.data.repository.ContactRepositoryImpl
import com.gusbarros.blockcalls.domain.repository.ContactRepository
import com.gusbarros.blockcalls.domain.usecase.ValidateContactUseCase
import com.gusbarros.blockcalls.presentation.home.HomeViewModel
import com.gusbarros.blockcalls.presentation.onboarding.OnboardingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin dependency injection module.
 *
 * Define todas as dependências do app seguindo Clean Architecture:
 * - Domain: Use Cases e Repository Interfaces
 * - Data: Repository Implementations
 * - Presentation: ViewModels
 */
val appModule = module {

    // Data Layer - Repository Implementation
    single<ContactRepository> {
        ContactRepositoryImpl(
            contentResolver = androidContext().contentResolver
        )
    }

    // Domain Layer - Use Cases
    factory {
        ValidateContactUseCase(
            contactRepository = get()
        )
    }

    // Presentation Layer - ViewModels
    viewModel {
        OnboardingViewModel(
            application = get()
        )
    }

    viewModel {
        HomeViewModel(
            application = get()
        )
    }
}
