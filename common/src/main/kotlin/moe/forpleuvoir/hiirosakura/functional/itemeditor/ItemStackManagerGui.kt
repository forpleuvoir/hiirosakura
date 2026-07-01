package moe.forpleuvoir.hiirosakura.functional.itemeditor

import moe.forpleuvoir.hiirosakura.util.logger

private val logger = logger("ItemStackManagerGui")
//
//fun ContainerScope.ItemStackManagerGui(
//    registryAccess: RegistryAccess,
//    modifier: Modifier = Modifier,
//    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(5f),
//    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
//) = Column(modifier, verticalArrangement, horizontalAlignment) {
//    var recompose by lateInitValueOf<() -> Unit>()
//    val deferred = ItemStackManager.loadDataAsync(registryAccess)
//
//    val regex = Regex("").asMutableState
//
//    ToolBar(regexConsumer = {
//        regex.setValue(it)
//        recompose()
//    }) {
//        ItemStackManager.add(it)
//        recompose()
//    }
//
//    ItemStackList(
//        deferred,
//        regex,
//        mutableStateOf(false),
//        modifier = Modifier.fill(),
//        onCreate = { recompose = { this.executeRecompose() } }
//    )
//}
//
//
//private fun ContainerScope.ToolBar(
//    modifier: Modifier = Modifier,
//    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f),
//    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
//    regexConsumer: (Regex) -> Unit,
//    consumer: (ItemStack) -> Unit
//) = Row(modifier, horizontalArrangement, verticalAlignment) {
//    SearchBar(
//        textConsumer = {
//            regexConsumer(it.toRegex())
//        },
//        modifier = Modifier.weight(1),
//        textEditorModifier = { Modifier.weight(1) }
//    )
//    ItemStackMatcher.handheldItemStack?.let { handheldItem ->
//        Button {
//            Text(HSLang.itemEditorAddFromHandheldItem)
//            click {
//                ItemStackEditor(handheldItem, consumer).open()
//            }
//        }
//    }
//    Button {
//        Icon(IconTextures.PLUS, HSVColor(120f, 1f, .65f), modifier = Modifier.size(9f, 9f))
//        click {
//            ItemStackEditor(ItemStack(Items.MELON), consumer).open()
//        }
//    }
//}
//
//
//private fun ContainerScope.ItemStackList(
//    loadDataAsync: Deferred<Result<Unit>>,
//    regex: MutableState<Regex>,
//    loaded: MutableState<Boolean>,
//    modifier: Modifier = Modifier,
//    scrollState: ScrollState = ScrollState(),
//    spacing: Float = 2f,
//    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
//    barThickness: Float = 9f,
//    onCreate: ColumnListWidget.() -> Unit = {},
//    listModifier: RowScope.() -> Modifier = { Modifier.fill().weight(1) },
//    scrollerModifier: BoxScope.() -> Modifier = { Modifier },
//) = ColumnListWrapped(
//    modifier,
//    scrollState,
//    spacing,
//    horizontalAlignment,
//    barThickness,
//    onCreate,
//    listModifier,
//    scrollerModifier
//) {
//    if (!loaded.getValue()) {
//        //TODO i18n
//        Text("Loading...")
//    } else {
//        val list = ItemStackManager.asSequence().filter {
//            regex.getValue().containsMatchIn(it.hoverName.string)
//                    || regex.getValue().containsMatchIn(it.count.toString())
//        }
//        val filtered = list.count() != ItemStackManager.asSequence().count()
//
//        if (list.count() == 0) {
//            Text(IGLang.hasNothing)
//            return@ColumnListWrapped
//        }
//        val recompose = { this@ColumnListWrapped.executeRecompose() }
//        list.forEachIndexed { index, itemStack ->
//            EntryRow(index, itemStack, filtered, recompose)
//        }
//    }
//    if (!loaded.getValue()) {
//        ioLaunch {
//            loadDataAsync.await()
//            loaded.setValue(true)
////            delay(50.milliseconds)
//            this@ColumnListWrapped.executeRecompose()
//        }
//    }
//}
//
//private fun ColumnListScope.EntryRow(
//    index: Int,
//    itemStack: ItemStack,
//    filtered: Boolean,
//    recompose: () -> Unit
//) = Row(Modifier.fill().bgHoverHighlightBox(), horizontalArrangement = Arrangement.SpaceBetween) {
//    Row(
//        modifier = Modifier.renderOverlay { guiGraphics, mx, my, d ->
//            if (wasMouseOver) guiGraphics.postEndRender {
//                pushItemTooltip(itemStack, mx, my)
//            }
//        },
//        horizontalArrangement = Arrangement.spacedBy(4f)
//    ) {
//        if (!filtered) {
//            MoveButton(recompose, index)
//        }
//        ItemIcon(itemStack)
//        Text(itemStack.styledHoverName, Modifier)
//    }
//    Row(horizontalArrangement = Arrangement.spacedBy(4f)) {
//
//        if (mc.player?.isCreative == true) {
//            Button {
//                Text(HSLang.itemEditorGetToBackpack)
//                click {
//                    mc.player?.inventory?.add(itemStack.copy())
//                    Toast.showToast(HSLang.itemEditorGetToBackpackSuccess(itemStack.hoverName))
//                }
//            }
//        }
//
//        CopyButton(Modifier.hoverText(HSLang.itemEditorCopyToCommand)) {
//            runCatching {
//                val command = itemStack.asCommand()
//                mc.keyboardHandler.clipboard = command
//                Toast.showToast {
//                    Text(command, modifier = Modifier.maxWidth(360f).maxHeight(400f), setting = TextSetting(autoNewLine = true))
//                }
//            }.onFailure {
//                Toast.showToast(Text.literal("Error: ${it.message}").withColor(Colors.RED))
//                logger.error(it)
//            }
//        }
//
//        EditButton {
//            ItemStackEditor(itemStack) { newItem ->
//                ItemStackManager[index] = newItem
//                recompose()
//            }.open()
//        }
//
//        RemoveButton {
//            ConfirmDialog(HSLang.itemEditorRemoveConfirm.asState) {
//                Row(
//                    Modifier
//                        .width(120f)
//                        .padding(horizontal = 5f, vertical = 4f)
//                        .render { ctx, _, _, _ ->
//                            ctx.pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
//                        }
//                        .renderOverlay { guiGraphics, mx, my, d ->
//                            if (wasMouseOver) guiGraphics.postEndRender {
//                                pushItemTooltip(itemStack, mx, my)
//                            }
//                        },
//                    Arrangement.spacedBy(5f)
//                ) {
//                    ItemIcon(itemStack, 0.6f)
//                    Text(itemStack.hoverName)
//                }
//                confirm {
//                    ItemStackManager.removeAt(index)
//                    recompose()
//                    closeScreen()
//                }
//            }.open()
//        }
//    }
//}
//
//fun IGGuiGraphics.pushItemTooltip(
//    itemStack: ItemStack,
//    mx: Float,
//    my: Float
//) {
//    val lines = Screen.getTooltipFromItem(mc, itemStack)
//    if (lines.isEmpty()) return
//    val list = lines.stream()
//        .map { it.visualOrderText }
//        .map { ClientTooltipComponent.create(it) }
//        .collect(Util.toMutableList())
//    renderTooltip(
//        textRenderer,
//        list,
//        mx.toInt(),
//        my.toInt(),
//        DefaultTooltipPositioner.INSTANCE,
//        itemStack.get(DataComponents.TOOLTIP_STYLE)
//    )
//}
//
//fun ItemStack.asCommand(): String {
//    val tag = DataComponentPatch.CODEC
//        .encodeStart(registryAccess!!.createSerializationContext(NbtOps.INSTANCE), componentsPatch)
//        .orThrow
//        .getAsString()
//    return "/give @p ${item.key}$tag $count"
//}
//
//private fun <T : Tag> T.getAsString(): String {
//    return if (this is CompoundTag) {
////        TextComponentTagVisitor("").visit(this).string
//        CommandNbtWriter().apply {
//            visitCompound(this@getAsString as CompoundTag)
//        }.build()
//    } else {
//        this.toString()
//    }
//}
//
//class CommandNbtWriter() : StringTagVisitor() {
//    override fun visitCompound(tag: CompoundTag) {
//        this.builder.append('[')
//        val list = ArrayList<MutableMap.MutableEntry<String, Tag>>(tag.entrySet())
//        list.sortWith(Map.Entry.comparingByKey<String, Tag>())
//
//        for (i in list.indices) {
//            val entry = list[i]
//            if (i != 0) {
//                this.builder.append(',')
//            }
//
//            val key = entry.key
//            if (!key.equals("true", ignoreCase = true) && !key.equals("false", ignoreCase = true) && Pattern.compile("[A-Za-z._]+[A-Za-z0-9._+-]*")
//                    .matcher(key)
//                    .matches()
//            ) {
//                this.builder.append(key)
//            } else {
//                buildString {
//                    StringTag.quoteAndEscape(key, this)
//                }.apply {
//                    builder.append(this.trim('"'))
//                }
//            }
//            this.builder.append('=')
//            val sub = StringTagVisitor()
//            entry.value.accept(sub)
//            this.builder.append(sub.builder)
//        }
//
//        this.builder.append(']')
//    }
//}
//
//private fun ContainerScope.MoveButton(
//    recompose: () -> Unit,
//    index: Int,
//) = Column {
//    FlatButton(
//        hoveredColor = Colors.BLACK.alpha(.15f),
//        round = 0,
//        modifier = Modifier.hoverText(IGLang.moveUp).padding(1).active(index > 0)
//    ) {
//        Icon(IconTextures.UP, modifier = Modifier, color = Colors.GRAY.alpha((index > 0).either(1f, .25f)))
//
//        click {
//            ItemStackManager.moveElement(index, (index - 1).coerceAtLeast(0))
//            recompose()
//        }
//    }
//    FlatButton(
//        hoveredColor = Colors.BLACK.alpha(.15f),
//        round = 0,
//        modifier = Modifier.hoverText(IGLang.moveDown, Tip.DefaultSetting.copy(optionalDirection = Direction.clockwiseFromBottom)).padding(1)
//            .active(index != ItemStackManager.lastIndex)
//    ) {
//        Icon(
//            IconTextures.DOWN,
//            modifier = Modifier,
//            color = Colors.GRAY.alpha((index != ItemStackManager.lastIndex).either(1f, .25f))
//        )
//
//        click {
//            ItemStackManager.moveElement(index, (index + 1).coerceAtMost(ItemStackManager.lastIndex))
//            recompose()
//        }
//    }
//}