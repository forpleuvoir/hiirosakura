package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base

//
//@Suppress("UNCHECKED_CAST")
//fun <C : Any> ContainerScope.DefaultComponentWrapper(
//    key: Identifier,
//    componentType: DataComponentType<C>,
//    component: Any?,
//    removeAction: () -> Unit,
//    modifier: Modifier = Modifier,
//    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
//    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
//    onValueChange: (C, Boolean) -> Unit
//) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
//    Button(Modifier.hoverText(IGLang.edit)) {
//        Icon(IconTextures.EDIT)
//        click {
//            runCatching {
//                componentType.codecOrThrow()
//                    .encodeStart(registryAccess!!.createSerializationContext(NebulaOps), component as C)
//                    .orThrow
//            }.onFailure {
//                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
//                DataComponentWrappers.log.error(it)
//            }.onSuccess {
//                var data = it
//                var result = component
//                //Editor
//                DataComponentEditor(
//                    key.asTranslateText(),
//                    { result!! as C to true },
//                    onValueChange,
//                    modifier = Modifier.width(340f),
//                    onConfirm = {
//                        var r = true
//                        runCatching {
//                            result = componentType.codecOrThrow()
//                                .parse(registryAccess!!.createSerializationContext(NebulaOps), data)
//                                .orThrow
//                        }.onFailure {
//                            Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
//                            DataComponentWrappers.log.error(it)
//                            r = false
//                        }
//                        r
//                    }
//                ) {
//                    SerializeElementEditor(data, modifier = Modifier.width(330f).height(190f)) {
//                        data = it
//                    }
//                }.open()
//            }
//
//        }
//    }
//}
//
//
//fun <C : Any> DefaultComponentBuilder(
//    key: Identifier,
//    componentType: DataComponentType<C>,
//    onValueChange: (C, Boolean) -> Unit,
//): IGScreenImpl {
//    var data: SerializeElement = SerializeObject()
//    var editorRecompose by lateInitValueOf {}
//    val type = mutableStateOf(SerializeElementType.Object)
//    type.subscribe {
//        data = it.defaultValue
//        editorRecompose()
//    }
//    var result: C by lateInitValueOf()
//    return DataComponentEditor(
//        key.asTranslateText(),
//        { result to true },
//        onValueChange,
//        modifier = Modifier.maxWidth(340f),
//        onConfirm = {
//            var r = true
//            runCatching {
//                result = componentType.codecOrThrow()
//                    .parse(registryAccess!!.createSerializationContext(NebulaOps), data)
//                    .orThrow
//            }.onFailure {
//                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
//                DataComponentWrappers.log.error(it)
//                r = false
//            }
//            r
//        }
//    ) {
//        EnumSelector(type, SerializeElementType.entries, modifier = Modifier.width(70f))
//        Box {
////            val modifier = when (data) {
////                is Map<*, *>, is Iterable<*> -> Modifier.width(330f).height(190f)
////                is Boolean                   -> Modifier.width(40f)
////                else                         -> Modifier.width(160f)
////            }
//            val modifier = when {
//                data.isObject || data.isArray -> Modifier.width(330f).height(190f)
//                data.isPrimitive && (data as SerializePrimitive).isBoolean -> Modifier.width(40f)
//                else -> Modifier.width(160f)
//            }
//            SerializeElementEditor(data.toSerializeElement(), modifier = modifier) {
//                data = it
//            }
//        }.apply {
//            editorRecompose = { this.executeRecompose() }
//        }
//    }
//}