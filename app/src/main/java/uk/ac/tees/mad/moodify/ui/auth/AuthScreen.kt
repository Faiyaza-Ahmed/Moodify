package uk.ac.tees.mad.moodify.ui.auth

import android.util.Patterns
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uk.ac.tees.mad.moodify.ui.theme.*

// --- Public API: call this composable where you need Auth UI ---
// onLogin(email, password) -> called when user taps Login
// onSignup(firstName, lastName, email, password) -> called when user taps Signup
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onLogin: suspend (email: String, password: String) -> Result<Unit>,
    onSignup: suspend (firstName: String, lastName: String, email: String, password: String) -> Result<Unit>,
    onAuthSuccess: (() -> Unit)? = null
) {
    val pagerState = rememberPagerState(pageCount = {
        2
    }, initialPage = 0)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val bgBrush = Brush.verticalGradient(
        colors = listOf(GradientStart, GradientEnd)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = bgBrush)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Moodify",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 30.sp),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            AuthSegmentedControl(
                selectedIndex = pagerState.currentPage,
                onSelectIndex = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                tonalElevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (page) {
                            0 -> LoginPage(
                                onLogin = onLogin,
                                onSuccess = { onAuthSuccess?.invoke() },
                                snackbarHostState = snackbarHostState
                            )
                            1 -> SignupPage(
                                onSignup = onSignup,
                                onSuccess = { onAuthSuccess?.invoke() },
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "We securely store your moods. You can logout anytime.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}

@Composable
private fun AuthSegmentedControl(
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    val isLoginSelected = selectedIndex == 0
    val loginBg by animateColorAsState(
        targetValue = if (isLoginSelected) Color.White else Color.Transparent,
        animationSpec = TweenSpec(durationMillis = 300)
    )
    val signupBg by animateColorAsState(
        targetValue = if (!isLoginSelected) Color.White else Color.Transparent,
        animationSpec = TweenSpec(durationMillis = 300)
    )
    val loginColor = if (isLoginSelected) PurplePrimary else Color.White
    val signupColor = if (!isLoginSelected) PurplePrimary else Color.White

    Surface(
        shape = RoundedCornerShape(50),
        color = PurplePrimary.copy(alpha = 0.16f),
        modifier = Modifier
            .height(46.dp)
            .widthIn(min = 260.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .background(loginBg, RoundedCornerShape(40))
                    .clickable { onSelectIndex(0) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Log In",
                    style = MaterialTheme.typography.bodyLarge,
                    color = loginColor
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .background(signupBg, RoundedCornerShape(40))
                    .clickable { onSelectIndex(1) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.bodyLarge,
                    color = signupColor
                )
            }
        }
    }
}

/* ---------------------------
   LOGIN PAGE
   --------------------------- */
@Composable
private fun LoginPage(
    onLogin: suspend (email: String, password: String) -> Result<Unit>,
    onSuccess: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue tracking your mood",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        RoundedTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            singleLine = true,
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        RoundedTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            trailingIcon = {
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = description
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            singleLine = true,
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.clearFocus() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        FilledRoundedButton(
            text = if (!loading) "Log In" else "Signing in...",
            enabled = !loading,
            onClick = {
                // basic validation
                val validationError = when {
                    email.isBlank() -> "Please enter your email."
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email."
                    password.length < 6 -> "Password must be at least 6 characters."
                    else -> null
                }
                if (validationError != null) {
                    scope.launch {
                        snackbarHostState.showSnackbar(validationError, duration = SnackbarDuration.Short)
                    }
                    return@FilledRoundedButton
                }

                scope.launch {
                    loading = true
                    val result = onLogin(email.trim(), password)
                    loading = false
                    if (result.isSuccess) {
                        onSuccess()
                    } else {
                        val message = result.exceptionOrNull()?.localizedMessage ?: "Login failed."
                        snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(
            onClick = { /* TODO: navigate to Forgot password */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Forgot password?", color = PurplePrimary)
        }
    }
}

/* ---------------------------
   SIGNUP PAGE
   --------------------------- */
@Composable
private fun SignupPage(
    onSignup: suspend (firstName: String, lastName: String, email: String, password: String) -> Result<Unit>,
    onSuccess: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val focusManager = LocalFocusManager.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Create account",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Join Moodify to start tracking your emotional wellbeing.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RoundedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First name",
                modifier = Modifier.weight(1f),
                keyboardCapitalization = KeyboardCapitalization.Words,
                singleLine = true,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Right) }
            )

            RoundedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last name",
                modifier = Modifier.weight(1f),
                keyboardCapitalization = KeyboardCapitalization.Words,
                singleLine = true,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        RoundedTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            singleLine = true,
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        RoundedTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            trailingIcon = {
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = description
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            singleLine = true,
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        RoundedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm password",
            trailingIcon = {
                val description = if (confirmVisible) "Hide password" else "Show password"
                IconButton(onClick = { confirmVisible = !confirmVisible }) {
                    Icon(
                        imageVector = if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = description
                    )
                }
            },
            visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            singleLine = true,
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.clearFocus() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        FilledRoundedButton(
            text = if (!loading) "Create Account" else "Creating...",
            enabled = !loading,
            onClick = {
                val validationError = when {
                    firstName.isBlank() -> "Enter first name."
                    lastName.isBlank() -> "Enter last name."
                    email.isBlank() -> "Enter your email."
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email."
                    password.length < 6 -> "Password must be at least 6 characters."
                    password != confirmPassword -> "Passwords do not match."
                    else -> null
                }
                if (validationError != null) {
                    scope.launch {
                        snackbarHostState.showSnackbar(validationError, duration = SnackbarDuration.Short)
                    }
                    return@FilledRoundedButton
                }

                scope.launch {
                    loading = true
                    val result = onSignup(firstName.trim(), lastName.trim(), email.trim(), password)
                    loading = false
                    if (result.isSuccess) {
                        onSuccess()
                    } else {
                        val message = result.exceptionOrNull()?.localizedMessage ?: "Signup failed."
                        snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { /* maybe switch to login */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Already have an account? Log in", color = PurplePrimary)
        }
    }
}

/* ---------------------------
   Reusable Components
   --------------------------- */

@Composable
private fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = label) },
        label = { Text(text = label) },
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PurplePrimary,
            unfocusedBorderColor = PurplePrimary.copy(alpha = 0.18f),
            focusedLabelColor = PurplePrimary,
            cursorColor = PurplePrimary,
            focusedContainerColor = LavenderMist,
            unfocusedContainerColor = LavenderMist
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = keyboardCapitalization,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onAny = { onImeAction?.invoke() }
        ),
        modifier = modifier
            .heightIn(min = 56.dp)
            .semantics { testTag = label }
    )
}

@Composable
private fun FilledRoundedButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
    ) {
        Text(text = text, color = Color.White, style = MaterialTheme.typography.bodyLarge)
    }
}

