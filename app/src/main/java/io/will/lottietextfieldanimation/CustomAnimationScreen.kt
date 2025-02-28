package io.will.lottietextfieldanimation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCancellationBehavior
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

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

        Spacer(modifier = Modifier.height(6.dp))

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
        // TODO: This is a placeholder to avoid the flashing when switching animations; It's still flashing when switching from/to password field tho.
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

    BasicTextField(
        value = textState,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        onValueChange = onTextChanged, onTextLayout = { result -> textLayoutResult = result },
        textStyle = TextStyle(fontSize = 36.sp, color = Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(1.dp, Color.Black)
            .padding(8.dp)
            .onFocusChanged {
                if (it.hasFocus) {
                    onFocused()
                }
            }
            .onGloballyPositioned { layoutCoordinates ->
                // Get the position of the TextField in the root coordinate system
                val position = layoutCoordinates.positionInRoot()

                textLayoutResult?.let { layoutResult ->
                    // Get the current cursor offset from the text state selection
                    val cursorOffset = textState.selection.start

                    // Retrieve the rectangle that represents the cursor's position
                    val cursorRect = layoutResult.getCursorRect(cursorOffset)

                    // Calculate the cursor's coordinates in the root coordinate system
                    cursorCoordinates = Offset(cursorRect.left + position.x, cursorRect.top + position.y)

                    onCursorChanged?.invoke(cursorCoordinates.x, layoutResult.size.width)
                }
            }
    )
}
