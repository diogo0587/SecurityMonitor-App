package com.devicesecurity.monitor.di

import android.content.Context
import com.devicesecurity.monitor.data.db.AppDatabase
import com.devicesecurity.monitor.data.db.dao.AlertDao
import com.devicesecurity.monitor.data.db.dao.AppPermissionDao
import com.devicesecurity.monitor.data.db.dao.SnapshotDao
import com.devicesecurity.monitor.data.repository.SecurityRepositoryImpl
import com.devicesecurity.monitor.data.scanner.SecurityScannerFacade
import com.devicesecurity.monitor.domain.repository.SecurityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideSnapshotDao(database: AppDatabase): SnapshotDao = database.snapshotDao()

    @Provides
    fun provideAppPermissionDao(database: AppDatabase): AppPermissionDao = database.appPermissionDao()

    @Provides
    fun provideAlertDao(database: AppDatabase): AlertDao = database.alertDao()

    @Provides
    @Singleton
    fun provideSecurityScannerFacade(@ApplicationContext context: Context): SecurityScannerFacade {
        return SecurityScannerFacade(context)
    }

    @Provides
    @Singleton
    fun provideSecurityRepository(
        scannerFacade: SecurityScannerFacade,
        snapshotDao: SnapshotDao,
        appPermissionDao: AppPermissionDao,
        alertDao: AlertDao
    ): SecurityRepository {
        return SecurityRepositoryImpl(scannerFacade, snapshotDao, appPermissionDao, alertDao)
    }
}
