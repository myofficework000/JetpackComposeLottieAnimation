package io.will.lottietextfieldanimation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*

@Composable
fun CustomAnimationScreen(modifier: Modifier = Modifier) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var passwordTextState by remember { mutableStateOf(TextFieldValue("")) }
    var isPasswordField by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(8.dp)) {
        CustomAnimation(
            isTextFieldEmpty = textState.text.isEmpty(),
            isPassword = isPasswordField,
            animationProgress = animationProgress
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Text")
        CustomTextField(
            textState = textState,
            onTextChanged = { textState = it },
            onCursorChanged = { cursorXCoordinate, textFieldWidth ->
                animationProgress = cursorXCoordinate / textFieldWidth
            },
            onFocused = { isPasswordField = false }
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text("Password")
        CustomTextField(
            textState = passwordTextState,
            onTextChanged = { passwordTextState = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onFocused = { isPasswordField = true }
        )
    }
}

@Composable
fun CustomAnimation(
    isTextFieldEmpty: Boolean,
    isPassword: Boolean,
    animationProgress: Float
) {
    Box {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coveringeyes))
        val progress by animateLottieCompositionAsState(
            composition,
            isPlaying = isPassword,
        )
        LottieAnimation(
            composition = composition,
            progress = { progress }
        )

        when {
            isPassword -> CoveringEyesAnimation()
            isTextFieldEmpty -> BlinkingAnimation()
            else -> WatchingAnimation(progress = animationProgress)
        }
    }
}

@Composable
fun BlinkingAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.blinking))
    val progress by animateLottieCompositionAsState(
        composition,
        speed = 1.5f,
        iterations = LottieConstants.IterateForever,
        cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress }
    )
}

@Composable
fun WatchingAnimation(progress: Float) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.watching))
    LottieAnimation(
        composition = composition,
        progress = { progress },
    )
}

@Composable
fun CoveringEyesAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coveringeyes))
    val progress by animateLottieCompositionAsState(
        composition,
        cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress }
    )
}

@Composable
fun CustomTextField(
    textState: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onCursorChanged: ((Float, Int) -> Unit)? = null,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    var cursorCoordinates by remember { mutableStateOf(Offset(0f, 0f)) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Color(0xFF32FFAA) else Color.Gray,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, shape = RectangleShape)
            .padding(8.dp)
    ) {
        BasicTextField(
            value = textState,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            onValueChange = onTextChanged,
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .onFocusChanged {
                    isFocused = it.hasFocus
                    if (it.hasFocus) {
                        onFocused()
                    }
                }
                .onGloballyPositioned { layoutCoordinates ->
                    val position = layoutCoordinates.positionInRoot()
                    textLayoutResult?.let { layoutResult ->
                        val cursorOffset = textState.selection.start
                        val cursorRect = layoutResult.getCursorRect(cursorOffset)
                        cursorCoordinates = Offset(cursorRect.left + position.x, cursorRect.top + position.y)
                        onCursorChanged?.invoke(cursorCoordinates.x, layoutResult.size.width)
                    }
                }
        )
    }
}
