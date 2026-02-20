package com.gusbarros.blockcalls.di

import androidx.room.Room
import com.gusbarros.blockcalls.data.local.BlockCallsDatabase
import com.gusbarros.blockcalls.data.repository.CallHistoryRepositoryImpl
import com.gusbarros.blockcalls.data.repository.ContactRepositoryImpl
import com.gusbarros.blockcalls.domain.repository.CallHistoryRepository
import com.gusbarros.blockcalls.domain.repository.ContactRepository
import com.gusbarros.blockcalls.domain.usecase.RecordBlockedCallUseCase
import com.gusbarros.blockcalls.domain.usecase.ValidateContactUseCase
import com.gusbarros.blockcalls.presentation.home.HomeViewModel
import com.gusbarros.blockcalls.presentation.onboarding.OnboardingViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Data Layer - Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            BlockCallsDatabase::class.java,
            "block_calls_db"
        ).build()
    }

    single { get<BlockCallsDatabase>().blockedCallDao() }

    // Data Layer - Repository Implementations
    single<ContactRepository> {
        ContactRepositoryImpl(contentResolver = androidContext().contentResolver)
    }

    single<CallHistoryRepository> {
        CallHistoryRepositoryImpl(dao = get())
    }

    // Domain Layer - Use Cases
    factory { ValidateContactUseCase(contactRepository = get()) }
    factory { RecordBlockedCallUseCase(callHistoryRepository = get()) }

    // Presentation Layer - ViewModels
    viewModel { OnboardingViewModel(application = androidApplication()) }
    viewModel { HomeViewModel(application = androidApplication(), callHistoryRepository = get()) }
}
