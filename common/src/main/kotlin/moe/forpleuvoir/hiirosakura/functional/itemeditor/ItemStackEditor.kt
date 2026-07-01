package moe.forpleuvoir.hiirosakura.functional.itemeditor

//
//@Suppress("UNCHECKED_CAST")
//fun ItemStackEditor(
//    itemStack: ItemStack = ItemStackMatcher.handheldItemStack ?: ItemStack(Items.MELON),
//    consumer: (ItemStack) -> Unit
//) = ConfirmDialog(
//    stateOf(HSLang.itemEditor),
//) {
//    val result = itemStack.asMutableState
//    val itemState = itemStack.item.asMutableState
//    val countState = itemStack.count.asMutableState
//
//    val componentMap = itemStack.copy().components.let {
//        it as? PatchedDataComponentMap ?: PatchedDataComponentMap(it)
//    }
//
//    countState.subscribe {
//        result.setValue(ItemStack(itemState.getValue(), countState.getValue(), componentMap.copy()))
//    }
//
//    var recompose by lateInitValueOf {}
//
//    var previewRecompose by lateInitValueOf {
//        result.setValue(ItemStack(itemState.getValue(), countState.getValue(), componentMap.copy()))
//    }
//
//    itemState.subscribe {
//        previewRecompose()
//    }
//
//    confirm {
//        consumer(result.getValue())
//        closeScreen()
//    }
//
//    Row(Modifier.width(400f), horizontalArrangement = Arrangement.SpaceBetween) {
//        ItemType(itemState)
//        ItemPreview(itemSupplier = result).apply {
//            previewRecompose = {
//                result.setValue(ItemStack(itemState.getValue(), countState.getValue(), componentMap.copy()))
//                this.executeRecompose()
//            }
//        }
//    }
//
//    var countRecompose: (DataComponentType<*>, Any?) -> Unit by lateInitValueOf { _, _ -> }
//
//    Row(Modifier.matchSibling(), horizontalArrangement = Arrangement.SpaceBetween) {
//        countRecompose = ItemCount(countState, componentMap)
//        ComponentAdder(componentMap) {
//            recompose()
//            previewRecompose()
//        }
//    }
//    recompose = Components(componentMap) { type, component ->
//        countRecompose(type, component)
//        previewRecompose()
//    }
//
//}
//
//private fun RowScope.ItemPreview(itemSupplier: State<ItemStack>) = Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
//    Text(HSLang.itemEditorItemPreview)
//    Row(
//        Modifier.padding(horizontal = 5f, vertical = 4f)
//            .width(143f)
//            .render { guiGraphics, _, _, _ ->
//                guiGraphics.pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
//            }
//            .renderOverlay { ctx, mx, my, d ->
//                if (wasMouseOver) ctx.postEndRender {
//                    pushItemTooltip(itemSupplier.getValue(), mx, my)
//                }
//            },
//        horizontalArrangement = Arrangement.spacedBy(2f),
//    ) {
//        ItemIcon(itemSupplier, .6f)
//        Text(itemSupplier.getValue().styledHoverName)
//    }
//}
//
//private fun RowScope.ItemType(itemState: MutableState<Item>) = Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
//    Text(HSLang.itemEditorItemType)
//    ItemSelector(
//        itemState,
//        modifier = Modifier.width(100f),
//    )
//}
//
//private fun RowScope.ItemCount(countState: MutableState<Int>, componentMap: PatchedDataComponentMap): (DataComponentType<*>, Any?) -> Unit {
//    var result: (DataComponentType<*>, Any?) -> Unit by lateInitValueOf { _, _ -> }
//    Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
//        Text(HSLang.itemEditorItemCount)
//        var state = true
//        val maxCount = (componentMap.find { it.type.key == DataComponents.MAX_STACK_SIZE.key }?.value as? Int ?: 64).asMutableState
//        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
//            val switch = state.asMutableState
//            SwitchableProxy(
//                { IntSlider(countState, 1..maxCount.getValue(), modifier = Modifier.width(75f)) },
//                { IntEditor(countState, 1..maxCount.getValue(), modifier = Modifier.width(75f), editorModifier = { Modifier.weight(1) }) },
//                switch
//            )
//            Button {
//                click {
//                    switch.switch()
//                    state = switch.getValue()
//                }
//                Icon(IconTextures.SWITCH)
//            }
//        }.apply {
//            result = { type, component ->
//                if (type.key == DataComponents.MAX_STACK_SIZE.key) {
//                    maxCount.setValue(component as? Int ?: 64)
//                    countState.setValue(countState.getValue().coerceIn(0..maxCount.getValue()))
//                    this.executeRecompose()
//                }
//            }
//        }
//    }
//    return result
//}
//
//
//@Suppress("UNCHECKED_CAST")
//private fun RowScope.ComponentAdder(
//    componentMap: PatchedDataComponentMap,
//    onAdd: () -> Unit
//) = Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(5f)) {
//    val registryManager = registryAccess!!
//    val components = registryManager.lookupOrThrow(Registries.DATA_COMPONENT_TYPE).sortedBy { it.key(registryManager) }
//    val selected = components.first().asMutableState
//    Text(HSLang.itemEditorItemAddComponent)
//    var toggle by lateInitValueOf {}
//    SelectorWithSearcher(
//        options = components,
//        selected = selected,
//        predicate = { component, str ->
//            val id = component.keyOrUnknown(registryManager)
//            id.toString().contains(str) ||
//                    id.asTranslateText().string.contains(str)
//        },
//        optionWrapper = {
//            val isAdapted = DataComponentWrappers.isAdaptedComponent(it)
//            Text(
//                it.key(registryManager).toString(),
//                style = style(if (isAdapted) HSVColor(195f, 1f, 1f) else HSVColor(5f, .6f, 1f)),
//                modifier = Modifier.hoverText(if (isAdapted) HSLang.itemEditorAdaptedComponent else HSLang.itemEditorUnadaptedComponent)
//            )
//        },
//        selectedWrapper = {
//            Text(it.key(registryManager).toString(), modifier = Modifier.width(120f))
//        },
//        amountStep = 15f,
//        onSelected = { type ->
//            closeScreen()
//            toggle()
//            runCatching {
//                DataComponentWrappers.defaultValue(type)?.let {
//                    if (!componentMap.has(type)) {
//                        componentMap.set(type as DataComponentType<Any>, it)
//                        onAdd()
//                    } else {
//                        Toast.showToast(HSLang.itemEditorItemComponentExist(type.keyOrUnknown(registryManager)))
//                    }
//                } ?: run {
//                    DefaultComponentBuilder(type.key(registryManager)!!, type) { component, recompose ->
//                        if (!componentMap.has(type)) {
//                            componentMap.set(type as DataComponentType<Any>, component)
//                            if (recompose) onAdd()
//                        } else {
//                            Toast.showToast(HSLang.itemEditorItemComponentExist(type.keyOrUnknown(registryManager)))
//                        }
//                    }.open()
//                }
//            }.onFailure {
//                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
//                DataComponentWrappers.log.error(it)
//            }
//        },
//        searchBarModifier = {
//            Modifier.width((components.map { it.keyOrUnknown.toString() }.maxWidth + 12f).coerceAtLeast(210f)).maxHeight(180f)
//        },
//        listWrapperModifier = {
//            Modifier.width((components.map { it.keyOrUnknown.toString() }.maxWidth + 12f).coerceAtLeast(210f)).maxHeight(180f)
//        },
//    ) {
//        toggle = { this.toggle() }
//    }
//}
//
//@Suppress("UNCHECKED_CAST")
//private fun ColumnScope.Components(
//    components: PatchedDataComponentMap,
//    onComponentChange: (DataComponentType<*>, Any?) -> Unit
//): () -> Unit {
//    var recompose by lateInitValueOf<()-> Unit>()
//    ColumnListWrapped(
//        Modifier.matchSibling().minHeight(140f).maxHeight(180f),
//        spacing = 2f,
//        horizontalAlignment = Alignment.Left,
//        listModifier = {
//            Modifier.weight(1).fill()
//        },
//        onCreate = {
//            recompose = { this.executeRecompose() }
//        }
//    ) {
//        components.keySet().sortedBy {
//            it.key(registryAccess!!)
//        }.forEach { type ->
//            components[type]?.let { c ->
//                DataComponentWrapper(
//                    type, c,
//                    removeAction = {
//                        components.remove(type)
//                        onComponentChange(type, null)
//                        this.executeRecompose()
//                    }
//                ) { component, recompose ->
//                    components[type as DataComponentType<Any>] = component
//                    onComponentChange(type, component)
//                    if (recompose) this.executeRecompose()
//                }
//            }
//        }
//    }
//    return recompose
//}
//
