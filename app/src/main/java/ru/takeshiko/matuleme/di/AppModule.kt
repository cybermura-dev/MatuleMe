package ru.takeshiko.matuleme.di

import coil.ImageLoader
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import ru.takeshiko.matuleme.BuildConfig
import ru.takeshiko.matuleme.data.local.AppPreferencesManager
import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.CartRepository
import ru.takeshiko.matuleme.domain.repository.CartRepositoryImpl
import ru.takeshiko.matuleme.domain.repository.DeliveryAddressRepository
import ru.takeshiko.matuleme.domain.repository.DeliveryAddressRepositoryImpl
import ru.takeshiko.matuleme.domain.repository.FavoriteRepository
import ru.takeshiko.matuleme.domain.repository.FavoriteRepositoryImpl
import ru.takeshiko.matuleme.domain.repository.NotificationRepository
import ru.takeshiko.matuleme.domain.repository.NotificationRepositoryImpl
import ru.takeshiko.matuleme.domain.repository.OrderRepository
import ru.takeshiko.matuleme.domain.repository.OrderRepositoryImpl
import ru.takeshiko.matuleme.domain.repository.PaymentRepository
import ru.takeshiko.matuleme.domain.repository.PaymentRepositoryImpl
import ru.takeshiko.matuleme.domain.repository.ProductRepository
import ru.takeshiko.matuleme.domain.repository.ProductRepositoryImpl
import ru.takeshiko.matuleme.domain.repository.PromotionRepository
import ru.takeshiko.matuleme.domain.repository.PromotionRepositoryImpl
import ru.takeshiko.matuleme.domain.repository.SearchRepository
import ru.takeshiko.matuleme.domain.repository.SearchRepositoryImpl
import ru.takeshiko.matuleme.domain.repository.StorageRepository
import ru.takeshiko.matuleme.domain.repository.StorageRepositoryImpl
import ru.takeshiko.matuleme.domain.repository.UserRepository
import ru.takeshiko.matuleme.domain.repository.UserRepositoryImpl
import ru.takeshiko.matuleme.domain.usecase.AddAddressUseCase
import ru.takeshiko.matuleme.domain.usecase.AddOrderItemUseCase
import ru.takeshiko.matuleme.domain.usecase.AddPaymentUseCase
import ru.takeshiko.matuleme.domain.usecase.AddToCartItemUseCase
import ru.takeshiko.matuleme.domain.usecase.AddToFavoritesUseCase
import ru.takeshiko.matuleme.domain.usecase.CheckFirstLaunchUseCase
import ru.takeshiko.matuleme.domain.usecase.CheckLoginStatusUseCase
import ru.takeshiko.matuleme.domain.usecase.ClearCartUseCase
import ru.takeshiko.matuleme.domain.usecase.CompleteOnboardingUseCase
import ru.takeshiko.matuleme.domain.usecase.ConfirmResetPasswordUseCase
import ru.takeshiko.matuleme.domain.usecase.CreateOrderUseCase
import ru.takeshiko.matuleme.domain.usecase.DeleteAddressUseCase
import ru.takeshiko.matuleme.domain.usecase.DeleteOrderItemUseCase
import ru.takeshiko.matuleme.domain.usecase.DeletePaymentUseCase
import ru.takeshiko.matuleme.domain.usecase.DeleteSearchQueryUseCase
import ru.takeshiko.matuleme.domain.usecase.GetActivePromotionUseCase
import ru.takeshiko.matuleme.domain.usecase.GetActivePromotionsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetAddressByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetAddressesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetAverageRatingUseCase
import ru.takeshiko.matuleme.domain.usecase.GetCartItemUseCase
import ru.takeshiko.matuleme.domain.usecase.GetCartItemsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetFavoritesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetOrderByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetOrderItemsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetOrdersUseCase
import ru.takeshiko.matuleme.domain.usecase.GetPaymentByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetPaymentsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductCategoriesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductReviewsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductsByCategoryUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductsByQueryUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetPromotionsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetRecentSearchQueriesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetReviewCountUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import ru.takeshiko.matuleme.domain.usecase.IsFavoriteUseCase
import ru.takeshiko.matuleme.domain.usecase.IsInCartUseCase
import ru.takeshiko.matuleme.domain.usecase.LogSearchQueryUseCase
import ru.takeshiko.matuleme.domain.usecase.LoginWithEmailUseCase
import ru.takeshiko.matuleme.domain.usecase.LogoutUseCase
import ru.takeshiko.matuleme.domain.usecase.RegisterWithEmailUseCase
import ru.takeshiko.matuleme.domain.usecase.RemoveFromCartItemUseCase
import ru.takeshiko.matuleme.domain.usecase.RemoveFromFavoritesUseCase
import ru.takeshiko.matuleme.domain.usecase.ResendOtpUseCase
import ru.takeshiko.matuleme.domain.usecase.ResetPasswordUseCase
import ru.takeshiko.matuleme.domain.usecase.SetDefaultAddressUseCase
import ru.takeshiko.matuleme.domain.usecase.SetDefaultPaymentUseCase
import ru.takeshiko.matuleme.domain.usecase.SetNewPasswordUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateAddressUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateCartItemQuantityUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateOrderItemUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateOrderStatusUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdatePaymentUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateSearchQueryUseCase
import ru.takeshiko.matuleme.domain.usecase.UpdateUserDataUseCase
import ru.takeshiko.matuleme.domain.usecase.UploadFileUseCase
import ru.takeshiko.matuleme.domain.usecase.ValidateEmailUseCase
import ru.takeshiko.matuleme.domain.usecase.ValidatePasswordUseCase
import ru.takeshiko.matuleme.domain.usecase.VerifyOtpUseCase
import ru.takeshiko.matuleme.presentation.screen.addaddress.AddAddressViewModel
import ru.takeshiko.matuleme.presentation.screen.addpaymentmethod.AddPaymentMethodViewModel
import ru.takeshiko.matuleme.presentation.screen.cart.CartViewModel
import ru.takeshiko.matuleme.presentation.screen.categories.CategoriesViewModel
import ru.takeshiko.matuleme.presentation.screen.categoryresult.CategoryResultsViewModel
import ru.takeshiko.matuleme.presentation.screen.checkout.CheckoutViewModel
import ru.takeshiko.matuleme.presentation.screen.deliveryaddresses.DeliveryAddressesViewModel
import ru.takeshiko.matuleme.presentation.screen.login.LoginViewModel
import ru.takeshiko.matuleme.presentation.screen.onboarding.OnboardingViewModel
import ru.takeshiko.matuleme.presentation.screen.register.RegisterViewModel
import ru.takeshiko.matuleme.presentation.screen.forgotpassword.ForgotPasswordViewModel
import ru.takeshiko.matuleme.presentation.screen.home.HomeViewModel
import ru.takeshiko.matuleme.presentation.screen.notifications.NotificationsViewModel
import ru.takeshiko.matuleme.presentation.screen.orderdetails.OrderDetailsViewModel
import ru.takeshiko.matuleme.presentation.screen.orders.OrdersViewModel
import ru.takeshiko.matuleme.presentation.screen.paymentmethods.PaymentMethodsViewModel
import ru.takeshiko.matuleme.presentation.screen.productdetails.ProductDetailsViewModel
import ru.takeshiko.matuleme.presentation.screen.profile.ProfileViewModel
import ru.takeshiko.matuleme.presentation.screen.resetpassword.ResetPasswordViewModel
import ru.takeshiko.matuleme.presentation.screen.reviews.ReviewsViewModel
import ru.takeshiko.matuleme.presentation.screen.search.SearchViewModel
import ru.takeshiko.matuleme.presentation.screen.searchresults.SearchResultsViewModel
import ru.takeshiko.matuleme.presentation.screen.splash.SplashViewModel
import ru.takeshiko.matuleme.presentation.screen.updateaddress.UpdateAddressViewModel
import ru.takeshiko.matuleme.presentation.screen.updatepaymentmethod.UpdatePaymentMethodViewModel
import ru.takeshiko.matuleme.presentation.screen.userinfo.UserInfoViewModel
import ru.takeshiko.matuleme.presentation.screen.verification.OtpVerificationViewModel
import ru.takeshiko.matuleme.presentation.screen.wishlist.WishlistViewModel
import ru.takeshiko.matuleme.presentation.screen.writenewpassword.WriteNewPasswordViewModel
import ru.takeshiko.matuleme.presentation.utils.AndroidStringResourceProvider

val appModule = module {
    // Preferences
    single { AppPreferencesManager(androidContext()) }

    // Clients
    single {
        SupabaseClientManager(
            BuildConfig.SUPABASE_URL,
            BuildConfig.SUPABASE_KEY
        )
    }

    // Providers
    single {
        ImageLoader.Builder(get())
            .crossfade(true)
            .build()
    }
    single<StringResourceProvider> { AndroidStringResourceProvider(androidContext()) }

    // Repositories
    single<CartRepository> { CartRepositoryImpl(get()) }
    single<DeliveryAddressRepository> { DeliveryAddressRepositoryImpl(get()) }
    single<FavoriteRepository> { FavoriteRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get()) }
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    single<PromotionRepository> { PromotionRepositoryImpl(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get()) }
    single<StorageRepository> { StorageRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }

    // Use cases
    factoryOf(::AddAddressUseCase)
    factoryOf(::AddOrderItemUseCase)
    factoryOf(::AddPaymentUseCase)
    factoryOf(::AddToCartItemUseCase)
    factoryOf(::AddToFavoritesUseCase)
    factoryOf(::CheckFirstLaunchUseCase)
    factoryOf(::CheckLoginStatusUseCase)
    factoryOf(::ClearCartUseCase)
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::ConfirmResetPasswordUseCase)
    factoryOf(::CreateOrderUseCase)
    factoryOf(::DeleteAddressUseCase)
    factoryOf(::DeleteOrderItemUseCase)
    factoryOf(::DeletePaymentUseCase)
    factoryOf(::DeleteSearchQueryUseCase)
    factoryOf(::GetActivePromotionUseCase)
    factoryOf(::GetActivePromotionsUseCase)
    factoryOf(::GetAddressesUseCase)
    factoryOf(::GetAddressByIdUseCase)
    factoryOf(::GetAverageRatingUseCase)
    factoryOf(::GetCartItemUseCase)
    factoryOf(::GetCartItemsUseCase)
    factoryOf(::GetFavoritesUseCase)
    factoryOf(::GetOrdersUseCase)
    factoryOf(::GetOrderByIdUseCase)
    factoryOf(::GetOrderItemsUseCase)
    factoryOf(::GetPaymentsUseCase)
    factoryOf(::GetPaymentByIdUseCase)
    factoryOf(::GetProductByIdUseCase)
    factoryOf(::GetProductCategoriesUseCase)
    factoryOf(::GetProductReviewsUseCase)
    factoryOf(::GetProductsByCategoryUseCase)
    factoryOf(::GetProductsByQueryUseCase)
    factoryOf(::GetProductsUseCase)
    factoryOf(::GetPromotionsUseCase)
    factoryOf(::GetRecentSearchQueriesUseCase)
    factoryOf(::GetReviewCountUseCase)
    factoryOf(::GetUserUseCase)
    factoryOf(::IsFavoriteUseCase)
    factoryOf(::IsInCartUseCase)
    factoryOf(::LogSearchQueryUseCase)
    factoryOf(::LoginWithEmailUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::RegisterWithEmailUseCase)
    factoryOf(::RemoveFromCartItemUseCase)
    factoryOf(::RemoveFromFavoritesUseCase)
    factoryOf(::ResendOtpUseCase)
    factoryOf(::ResetPasswordUseCase)
    factoryOf(::SetDefaultAddressUseCase)
    factoryOf(::SetDefaultPaymentUseCase)
    factoryOf(::SetNewPasswordUseCase)
    factoryOf(::UpdateAddressUseCase)
    factoryOf(::UpdateCartItemQuantityUseCase)
    factoryOf(::UpdateOrderItemUseCase)
    factoryOf(::UpdateOrderStatusUseCase)
    factoryOf(::UpdatePaymentUseCase)
    factoryOf(::UpdateSearchQueryUseCase)
    factoryOf(::UpdateUserDataUseCase)
    factoryOf(::UploadFileUseCase)
    factoryOf(::ValidateEmailUseCase)
    factoryOf(::ValidatePasswordUseCase)
    factoryOf(::VerifyOtpUseCase)

    // View models
    viewModelOf(::AddAddressViewModel)
    viewModelOf(::AddPaymentMethodViewModel)
    viewModelOf(::CartViewModel)
    viewModelOf(::CategoriesViewModel)
    viewModelOf(::CategoryResultsViewModel)
    viewModelOf(::CheckoutViewModel)
    viewModelOf(::DeliveryAddressesViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::OrdersViewModel)
    viewModelOf(::OrderDetailsViewModel)
    viewModelOf(::OtpVerificationViewModel)
    viewModelOf(::PaymentMethodsViewModel)
    viewModelOf(::ProductDetailsViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ResetPasswordViewModel)
    viewModelOf(::ReviewsViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SearchResultsViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::UserInfoViewModel)
    viewModelOf(::UpdateAddressViewModel)
    viewModelOf(::UpdatePaymentMethodViewModel)
    viewModelOf(::WishlistViewModel)
    viewModelOf(::WriteNewPasswordViewModel)
}