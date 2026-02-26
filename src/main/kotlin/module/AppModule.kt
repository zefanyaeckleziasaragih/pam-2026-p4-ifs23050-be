package org.delcom.module

import org.delcom.repositories.IZodiacRepository
import org.delcom.repositories.ZodiacRepository
import org.delcom.services.ProfileService
import org.delcom.services.ZodiacService
import org.koin.dsl.module

val appModule = module {
    single<IZodiacRepository> { ZodiacRepository() }
    single { ZodiacService(get()) }
    single { ProfileService() }
}