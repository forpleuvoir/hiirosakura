package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

//import net.minecraft.world.item.SwingAnimationType
//import net.minecraft.world.item.component.SwingAnimation
//
//fun ContainerScope.SwingAnimationComponentWrapper(
//    key: ResourceLocation,
//    component: SwingAnimation,
//    removeAction: () -> Unit,
//    modifier: Modifier = Modifier,
//    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
//    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
//    onValueChange: (SwingAnimation, Boolean) -> Unit,
//) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
//    var component = component
//    Button(
//        Modifier.width(140f)
//            .hoverTip {
//                Row {
//                    Column(Modifier.margin(right = 10f), horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(2f)) {
//                        Text("type")
//                        Text("duration")
//                    }
//                    Column(horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(2f)) {
//                        Text(component.type.translateText)
//                        Text(component.duration.toString())
//                    }
//                }
//            }
//    ) {
//        Text(IGLang.edit)
//        click {
//            SwingAnimationEditor(key.asTranslateText(), component) { it, recompose ->
//                if (component != it) {
//                    component = it
//                    onValueChange(component, recompose)
//                    if (recompose) executeRecompose()
//                }
//            }.open()
//        }
//    }
//}
//
//fun SwingAnimationEditor(
//    title: Text,
//    swingAnimation: SwingAnimation,
//    modifier: Modifier = Modifier,
//    screenModifier: Modifier = Modifier,
//    onValueChange: (SwingAnimation, Boolean) -> Unit,
//): IGScreenImpl {
//    val type = swingAnimation.type.asMutableState
//    val duration = swingAnimation.duration.asMutableState
//
//    return DataComponentEditor(
//        title,
//        { SwingAnimation(type.getValue(), duration.getValue()) to true },
//        onValueChange,
//        modifier,
//        screenModifier
//    ) {
//        DialogContent {
//            Column(Modifier.width(130f), verticalArrangement = Arrangement.spacedBy(5f)) {
//                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
//                    Text("type")
//                    EnumSelector(type, SwingAnimationType.entries, modifier = Modifier.width(60f))
//                }
//                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
//                    Text("duration")
//                    IntEditor(duration, range = 0..Int.MAX_VALUE, modifier = Modifier.width(60f), editorModifier = { Modifier.weight(1) })
//                }
//            }
//        }
//    }
//}
