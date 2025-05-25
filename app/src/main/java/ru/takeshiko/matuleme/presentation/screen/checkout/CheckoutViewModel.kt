package ru.takeshiko.matuleme.presentation.screen.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.OrderProductItemDto
import ru.takeshiko.matuleme.domain.models.OrderStatus
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto
import ru.takeshiko.matuleme.domain.models.UserDto
import ru.takeshiko.matuleme.domain.models.UserOrderDto
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.usecase.AddOrderItemUseCase
import ru.takeshiko.matuleme.domain.usecase.CreateOrderUseCase
import ru.takeshiko.matuleme.domain.usecase.GetActivePromotionUseCase
import ru.takeshiko.matuleme.domain.usecase.GetAddressesUseCase
import ru.takeshiko.matuleme.domain.usecase.GetCartItemsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetPaymentsUseCase
import ru.takeshiko.matuleme.domain.usecase.GetProductByIdUseCase
import ru.takeshiko.matuleme.domain.usecase.GetUserUseCase
import java.util.UUID

class CheckoutViewModel(
    private val getUser: GetUserUseCase,
    private val getCartItems: GetCartItemsUseCase,
    private val getProductById: GetProductByIdUseCase,
    private val getActivePromotion: GetActivePromotionUseCase,
    private val getAddresses: GetAddressesUseCase,
    private val getPayments: GetPaymentsUseCase,
    private val createOrder: CreateOrderUseCase,
    private val addOrderItem: AddOrderItemUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _orderCreated = MutableStateFlow(false)
    val orderCreated: StateFlow<Boolean> = _orderCreated.asStateFlow()

    private val _orderCreateInProgress = MutableStateFlow(false)
    val orderCreateInProgress: StateFlow<Boolean> = _orderCreateInProgress.asStateFlow()

    private val _user = MutableStateFlow<UserDto?>(null)
    val user: StateFlow<UserDto?> = _user.asStateFlow()

    fun loadCheckoutData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val userResult = getUser()
            _user.value = userResult.getOrNull()

            if (userResult.isFailure || user.value == null) {
                _errorMessage.value = userResult.exceptionOrNull()?.localizedMessage
                _isLoading.value = false
                return@launch
            }

            getCartItems(user.value!!.id).onSuccess { cartItemsList ->
                val cartItemsWithDetails = mutableListOf<CheckoutCartItem>()
                var subtotal = 0.0
                var discount = 0.0

                for (cartItem in cartItemsList) {
                    val productResult = getProductById(cartItem.productId)
                    val product = productResult.getOrNull()

                    val promotionResult = getActivePromotion(cartItem.productId)
                    val promotion = promotionResult.getOrNull()

                    var finalPrice = product?.basePrice ?: 0.0
                    if (promotion != null && product != null) {
                        val discountAmount = (product.basePrice * (promotion.discountPercent / 100.0))
                        finalPrice = product.basePrice - discountAmount
                        discount += discountAmount * cartItem.quantity
                    }

                    val itemSubtotal = finalPrice * cartItem.quantity
                    subtotal += itemSubtotal

                    cartItemsWithDetails.add(
                        CheckoutCartItem(
                            cartItem = cartItem,
                            product = product,
                            promotion = promotion,
                            finalPrice = finalPrice
                        )
                    )
                }

                val total = subtotal

                _state.value = _state.value.copy(
                    cartItems = cartItemsWithDetails,
                    subtotal = subtotal,
                    discount = discount,
                    total = total
                )
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            getAddresses(user.value!!.id).onSuccess { addressesList ->
                _state.value = _state.value.copy(
                    addresses = addressesList,
                    selectedAddress = addressesList.firstOrNull()
                )
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            getPayments(user.value!!.id).onSuccess { paymentsList ->
                _state.value = _state.value.copy(
                    payments = paymentsList,
                    selectedPayment = paymentsList.firstOrNull()
                )
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            _isLoading.value = false
        }
    }

    fun selectAddress(address: UserDeliveryAddressDto) {
        _state.value = _state.value.copy(selectedAddress = address)
    }

    fun selectPayment(payment: UserPaymentDto) {
        _state.value = _state.value.copy(selectedPayment = payment)
    }

    fun createUserOrder() {
        viewModelScope.launch {
            val currentState = _state.value
            val userResult = getUser()
            val user = userResult.getOrNull()

            if (userResult.isFailure || user == null) {
                _errorMessage.value = userResult.exceptionOrNull()?.localizedMessage
                return@launch
            }

            if (currentState.cartItems.isEmpty()) {
                _errorMessage.value = "Корзина пуста"
                return@launch
            }

            if (currentState.selectedAddress == null) {
                _errorMessage.value = "Выберите адрес доставки"
                return@launch
            }

            if (currentState.selectedPayment == null) {
                _errorMessage.value = "Выберите способ оплаты"
                return@launch
            }

            _orderCreateInProgress.value = true

            val newOrder = UserOrderDto(
                orderNumber = UUID.randomUUID().toString(),
                userId = user.id,
                email = user.email ?: "",
                phone = user.phoneNumber ?: "",
                address = currentState.selectedAddress.address,
                cardNumber = currentState.selectedPayment.cardNumber,
                cardHolder = currentState.selectedPayment.cardHolderName,
                status = OrderStatus.NEW,
                totalAmount = currentState.total,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )

            createOrder(newOrder).onSuccess { createdOrder ->
                var allItemsAdded = true

                for (item in currentState.cartItems) {
                    val orderItem = OrderProductItemDto(
                        id = UUID.randomUUID().toString(),
                        orderId = createdOrder.id!!,
                        productId = item.cartItem.productId,
                        quantity = item.cartItem.quantity,
                        price = item.finalPrice
                    )

                    addOrderItem(orderItem).onFailure {
                        allItemsAdded = false
                        _errorMessage.value = it.localizedMessage
                    }
                }

                if (allItemsAdded) {
                    _orderCreated.value = true
                }
            }.onFailure {
                _errorMessage.value = it.localizedMessage
            }

            _orderCreateInProgress.value = false
        }
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    fun resetOrderCreated() {
        _orderCreated.value = false
    }
}