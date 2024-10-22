package com.app.routineturboa.data.repository

import android.content.Context
import androidx.room.Room
import com.app.routineturboa.data.dbutils.DbConstants
import com.app.routineturboa.data.room.AppDao
import com.app.routineturboa.data.room.AppData
import com.app.routineturboa.reminders.ReminderManager
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
    fun provideReminderManager(
        @ApplicationContext context: Context,
        appRepository: AppRepository
    ): ReminderManager {
        return ReminderManager(context, appRepository)
    }

    @Provides
    @Singleton
    fun provideAppData(@ApplicationContext context: Context): AppData {
        return Room.databaseBuilder(
            context.applicationContext,
            AppData::class.java,
            DbConstants.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // Wipes database between versions
        .build()
    }

    @Provides
    @Singleton
    fun provideAppRepository(appDao: AppDao): AppRepository {
        return AppRepository(appDao)
    }

    @Provides
    @Singleton
    fun provideAppDao(appDatabase: AppData): AppDao {
        return appDatabase.appDao()
    }
}
