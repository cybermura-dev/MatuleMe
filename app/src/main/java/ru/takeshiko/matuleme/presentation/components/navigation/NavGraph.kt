package ru.takeshiko.matuleme.presentation.components.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.takeshiko.matuleme.presentation.screen.aboutapp.AboutAppScreen
import ru.takeshiko.matuleme.presentation.screen.addaddress.AddAddressScreen
import ru.takeshiko.matuleme.presentation.screen.addpaymentmethod.AddPaymentMethodScreen
import ru.takeshiko.matuleme.presentation.screen.cart.CartScreen
import ru.takeshiko.matuleme.presentation.screen.categories.CategoriesScreen
import ru.takeshiko.matuleme.presentation.screen.categoryresult.CategoryResultsScreen
import ru.takeshiko.matuleme.presentation.screen.checkout.CheckoutScreen
import ru.takeshiko.matuleme.presentation.screen.deliveryaddresses.DeliveryAddressesScreen
import ru.takeshiko.matuleme.presentation.screen.home.HomeScreen
import ru.takeshiko.matuleme.presentation.screen.notifications.NotificationsScreen
import ru.takeshiko.matuleme.presentation.screen.orderdetails.OrderDetailsScreen
import ru.takeshiko.matuleme.presentation.screen.orders.OrdersScreen
import ru.takeshiko.matuleme.presentation.screen.paymentmethods.PaymentMethodsScreen
import ru.takeshiko.matuleme.presentation.screen.productdetails.ProductDetailsScreen
import ru.takeshiko.matuleme.presentation.screen.profile.ProfileScreen
import ru.takeshiko.matuleme.presentation.screen.reviews.ReviewsScreen
import ru.takeshiko.matuleme.presentation.screen.search.SearchScreen
import ru.takeshiko.matuleme.presentation.screen.searchresults.SearchResultsScreen
import ru.takeshiko.matuleme.presentation.screen.updateaddress.UpdateAddressScreen
import ru.takeshiko.matuleme.presentation.screen.updatepaymentmethod.UpdatePaymentMethodScreen
import ru.takeshiko.matuleme.presentation.screen.userinfo.UserInfoScreen
import ru.takeshiko.matuleme.presentation.screen.wishlist.WishlistScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val enterSpec = tween<IntOffset>(
        durationMillis = 500,
        easing = FastOutSlowInEasing
    )

    val fadeSpec  = tween<Float>(durationMillis = 500)

    val scaleSpec = tween<Float>(
        durationMillis = 500,
        easing = FastOutSlowInEasing
    )

    NavHost(
        navController = navController,
        startDestination = "home",

        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = enterSpec
            ) + fadeIn(animationSpec = fadeSpec) + scaleIn(
                initialScale = 0.9f,
                animationSpec = scaleSpec
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 2 },
                animationSpec = enterSpec
            ) + fadeOut(animationSpec = fadeSpec) + scaleOut(
                targetScale = 0.9f,
                animationSpec = scaleSpec
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 2 },
                animationSpec = enterSpec
            ) + fadeIn(animationSpec = fadeSpec) + scaleIn(
                initialScale = 0.9f,
                animationSpec = scaleSpec
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = enterSpec
            ) + fadeOut(animationSpec = fadeSpec) + scaleOut(
                targetScale = 0.9f,
                animationSpec = scaleSpec
            )
        }
    ) {
        composable("home") { HomeScreen(navController) }
        composable("categories") { CategoriesScreen(navController) }
        composable("cart") { CartScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("notifications") { NotificationsScreen(navController) }
        composable("search") { SearchScreen(navController) }
        composable("wishlist") { WishlistScreen(navController) }
        composable("checkout") { CheckoutScreen(navController) }
        composable("orders") { OrdersScreen(navController) }
        composable("addresses") { DeliveryAddressesScreen(navController) }
        composable("payments") { PaymentMethodsScreen(navController) }
        composable("userinfo") { UserInfoScreen(navController) }
        composable("about_app") { AboutAppScreen(navController) }
        composable(
            route = "search_results/{query}",
            arguments = listOf(navArgument("query") { type = NavType.StringType })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchResultsScreen(
                navController = navController,
                query = query
            )
        }
        composable(
            route = "product/{product_id}",
            arguments = listOf(navArgument("product_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("product_id") ?: ""
            ProductDetailsScreen(
                navController = navController,
                productId = productId
            )
        }
        composable(
            route = "reviews/{product_id}",
            arguments = listOf(navArgument("product_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("product_id") ?: ""
            ReviewsScreen(
                navController = navController,
                productId = productId
            )
        }
        composable(
            route = "category/{category_id}",
            arguments = listOf(navArgument("category_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("category_id") ?: ""
            CategoryResultsScreen(
                navController = navController,
                categoryId = categoryId
            )
        }
        composable(
            route = "address_edit/{address_id}",
            arguments = listOf(navArgument("address_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("address_id") ?: ""
            if (addressId == "new") {
                AddAddressScreen(navController)
            } else {
                UpdateAddressScreen(navController, addressId =  addressId)
            }
        }
        composable(
            route = "payment_edit/{payment_id}",
            arguments = listOf(navArgument("payment_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val paymentId = backStackEntry.arguments?.getString("payment_id") ?: ""
            if (paymentId == "new") {
                AddPaymentMethodScreen(navController)
            } else {
                UpdatePaymentMethodScreen(navController, paymentId = paymentId)
            }
        }
        composable(
            route = "order/{order_id}",
            arguments = listOf(navArgument("order_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("order_id") ?: ""
            OrderDetailsScreen(
                navController = navController,
                orderId = orderId
            )
        }
    }
}