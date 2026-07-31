# Minecraft 26.1.2 注册表分类

## 静态注册表 (BuiltInRegistries)

这些注册表在游戏 bootstrap 时注册并冻结，通过 `BuiltInRegistries` 静态访问。

### 核心游戏对象

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `BLOCK` | `Block` | 方块 | `val block = BuiltInRegistries.BLOCK.get(Identifier.parse("minecraft:stone"))` |
| `ITEM` | `Item` | 物品 | `val item = BuiltInRegistries.ITEM.get(Identifier.parse("minecraft:diamond"))` |
| `ENTITY_TYPE` | `EntityType<?>` | 实体类型 | `val type = BuiltInRegistries.ENTITY_TYPE.get(Identifier.parse("minecraft:zombie"))` |
| `BLOCK_ENTITY_TYPE` | `BlockEntityType<?>` | 方块实体类型 | `val type = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(Identifier.parse("minecraft:chest"))` |
| `FLUID` | `Fluid` | 流体 | `val fluid = BuiltInRegistries.FLUID.get(Identifier.parse("minecraft:water"))` |
| `PARTICLE_TYPE` | `ParticleType<?>` | 粒子类型 | `val type = BuiltInRegistries.PARTICLE_TYPE.get(Identifier.parse("minecraft:flame"))` |
| `SOUND_EVENT` | `SoundEvent` | 声音事件 | `val sound = BuiltInRegistries.SOUND_EVENT.get(Identifier.parse("minecraft:entity.player.hurt"))` |
| `MOB_EFFECT` | `MobEffect` | 药水效果 | `val effect = BuiltInRegistries.MOB_EFFECT.get(Identifier.parse("minecraft:poison"))` |
| `POTION` | `Potion` | 药水 | `val potion = BuiltInRegistries.POTION.get(Identifier.parse("minecraft:strong_healing"))` |

### 组件系统

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `DATA_COMPONENT_TYPE` | `DataComponentType<?>` | 数据组件类型 | `val type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(Identifier.parse("minecraft:damage"))` |
| `DATA_COMPONENT_PREDICATE_TYPE` | `DataComponentPredicate.Type<?>` | 数据组件谓词类型 | `val type = BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.get(Identifier.parse("minecraft:any_of"))` |
| `CONSUME_EFFECT_TYPE` | `ConsumeEffect.Type<?>` | 消耗效果类型 | `val type = BuiltInRegistries.CONSUME_EFFECT_TYPE.get(Identifier.parse("minecraft:apply_effects"))` |
| `ENCHANTMENT_EFFECT_COMPONENT_TYPE` | `DataComponentType<?>` | 附魔效果组件类型 | `val type = BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.get(Identifier.parse("minecraft:enchantment"))` |

### GUI / 显示

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `CREATIVE_MODE_TAB` | `CreativeModeTab` | 创造模式标签页 | `val tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(Identifier.parse("minecraft:building_blocks"))` |
| `MENU` | `MenuType<?>` | 菜单类型（GUI） | `val menu = BuiltInRegistries.MENU.get(Identifier.parse("minecraft:chest"))` |
| `DECORATED_POT_PATTERN` | `DecoratedPotPattern` | 饰纹陶罐图案 | `val pattern = BuiltInRegistries.DECORATED_POT_PATTERN.get(Identifier.parse("minecraft:angler"))` |
| `MAP_DECORATION_TYPE` | `MapDecorationType` | 地图装饰类型 | `val type = BuiltInRegistries.MAP_DECORATION_TYPE.get(Identifier.parse("minecraft:player"))` |
| `NUMBER_FORMAT_TYPE` | `NumberFormatType<?>` | 数字格式类型 | `val type = BuiltInRegistries.NUMBER_FORMAT_TYPE.get(Identifier.parse("minecraft:blank"))` |

### 配方系统

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `RECIPE_TYPE` | `RecipeType<?>` | 配方类型 | `val type = BuiltInRegistries.RECIPE_TYPE.get(Identifier.parse("minecraft:crafting"))` |
| `RECIPE_SERIALIZER` | `RecipeSerializer<?>` | 配方序列化器 | `val serializer = BuiltInRegistries.RECIPE_SERIALIZER.get(Identifier.parse("minecraft:crafting_shaped"))` |
| `RECIPE_DISPLAY` | `RecipeDisplay.Type<?>` | 配方显示类型 | `val type = BuiltInRegistries.RECIPE_DISPLAY.get(Identifier.parse("minecraft:crafting_shapeless"))` |
| `SLOT_DISPLAY` | `SlotDisplay.Type<?>` | 槽位显示类型 | `val type = BuiltInRegistries.SLOT_DISPLAY.get(Identifier.parse("minecraft:item"))` |
| `RECIPE_BOOK_CATEGORY` | `RecipeBookCategory` | 配方书分类 | `val category = BuiltInRegistries.RECIPE_BOOK_CATEGORY.get(Identifier.parse("minecraft:building_blocks"))` |

### AI / 村民

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `ACTIVITY` | `Activity` | 活动 | `val activity = BuiltInRegistries.ACTIVITY.get(Identifier.parse("minecraft:core"))` |
| `SCHEDULE` | `Schedule` | 日程 | `val schedule = BuiltInRegistries.SCHEDULE.get(Identifier.parse("minecraft:villager_baby"))` |
| `MEMORY_MODULE_TYPE` | `MemoryModuleType<?>` | 记忆模块类型 | `val type = BuiltInRegistries.MEMORY_MODULE_TYPE.get(Identifier.parse("minecraft:home"))` |
| `SENSOR_TYPE` | `SensorType<?>` | 传感器类型 | `val type = BuiltInRegistries.SENSOR_TYPE.get(Identifier.parse("minecraft:villager_sensor"))` |
| `VILLAGER_TYPE` | `VillagerType` | 村民类型 | `val type = BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse("minecraft:plains"))` |
| `VILLAGER_PROFESSION` | `VillagerProfession` | 村民职业 | `val prof = BuiltInRegistries.VILLAGER_PROFESSION.get(Identifier.parse("minecraft:farmer"))` |
| `POINT_OF_INTEREST_TYPE` | `PoiType` | 兴趣点类型 | `val type = BuiltInRegistries.POINT_OF_INTEREST_TYPE.get(Identifier.parse("minecraft:home"))` |

### 战利品表

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `LOOT_POOL_ENTRY_TYPE` | `LootPoolEntryType` | 战利品池条目类型 | `val type = BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.get(Identifier.parse("minecraft:item"))` |
| `LOOT_FUNCTION_TYPE` | `LootItemFunctionType<?>` | 战利品函数类型 | `val type = BuiltInRegistries.LOOT_FUNCTION_TYPE.get(Identifier.parse("minecraft:set_count"))` |
| `LOOT_CONDITION_TYPE` | `LootItemConditionType` | 战利品条件类型 | `val type = BuiltInRegistries.LOOT_CONDITION_TYPE.get(Identifier.parse("minecraft:killed_by_player"))` |
| `LOOT_NUMBER_PROVIDER_TYPE` | `LootNumberProviderType` | 战利品数字提供器类型 | `val type = BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE.get(Identifier.parse("minecraft:uniform"))` |
| `LOOT_NBT_PROVIDER_TYPE` | `LootNbtProviderType` | 战利品 NBT 提供器类型 | `val type = BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE.get(Identifier.parse("minecraft:context"))` |
| `LOOT_SCORE_PROVIDER_TYPE` | `LootScoreProviderType` | 战利品分数提供器类型 | `val type = BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE.get(Identifier.parse("minecraft:fixed"))` |

### 世界生成 — 提供器 / 测试类型

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `BLOCK_PREDICATE_TYPE` | `BlockPredicateType<?>` | 方块谓词类型 | `val type = BuiltInRegistries.BLOCK_PREDICATE_TYPE.get(Identifier.parse("minecraft:matching_blocks"))` |
| `RULE_TEST` | `RuleTestType<?>` | 规则测试类型 | `val type = BuiltInRegistries.RULE_TEST.get(Identifier.parse("minecraft:tag_match"))` |
| `RULE_BLOCK_ENTITY_MODIFIER` | `RuleBlockEntityModifierType<?>` | 规则方块实体修改器类型 | `val type = BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER.get(Identifier.parse("minecraft:append_loot"))` |
| `POS_RULE_TEST` | `PosRuleTestType<?>` | 位置规则测试类型 | `val type = BuiltInRegistries.POS_RULE_TEST.get(Identifier.parse("minecraft:always_true"))` |
| `FLOAT_PROVIDER_TYPE` | `FloatProviderType<?>` | 浮点提供器类型 | `val type = BuiltInRegistries.FLOAT_PROVIDER_TYPE.get(Identifier.parse("minecraft:constant"))` |
| `INT_PROVIDER_TYPE` | `IntProviderType<?>` | 整数提供器类型 | `val type = BuiltInRegistries.INT_PROVIDER_TYPE.get(Identifier.parse("minecraft:constant"))` |
| `HEIGHT_PROVIDER_TYPE` | `HeightProviderType<?>` | 高度提供器类型 | `val type = BuiltInRegistries.HEIGHT_PROVIDER_TYPE.get(Identifier.parse("minecraft:constant"))` |
| `BLOCKSTATE_PROVIDER_TYPE` | `BlockStateProviderType<?>` | 方块状态提供器类型 | `val type = BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE.get(Identifier.parse("minecraft:simple_state_provider"))` |

### 世界生成 — 特性 / 结构

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `FEATURE` | `Feature<?>` | 地形特性 | `val feature = BuiltInRegistries.FEATURE.get(Identifier.parse("minecraft:lake"))` |
| `CARVER` | `WorldCarver<?>` | 世界雕刻器 | `val carver = BuiltInRegistries.CARVER.get(Identifier.parse("minecraft:cave"))` |
| `STRUCTURE_TYPE` | `StructureType<?>` | 结构类型 | `val type = BuiltInRegistries.STRUCTURE_TYPE.get(Identifier.parse("minecraft:mineshaft"))` |
| `STRUCTURE_PIECE` | `StructurePieceType` | 结构片段类型 | `val type = BuiltInRegistries.STRUCTURE_PIECE.get(Identifier.parse("minecraft:mineshaft"))` |
| `STRUCTURE_PLACEMENT` | `StructurePlacementType<?>` | 结构放置类型 | `val type = BuiltInRegistries.STRUCTURE_PLACEMENT.get(Identifier.parse("minecraft:random_spread"))` |
| `STRUCTURE_PROCESSOR` | `StructureProcessorType<?>` | 结构处理器类型 | `val type = BuiltInRegistries.STRUCTURE_PROCESSOR.get(Identifier.parse("minecraft:block_ignore"))` |
| `STRUCTURE_POOL_ELEMENT` | `StructurePoolElementType<?>` | 结构池元素类型 | `val type = BuiltInRegistries.STRUCTURE_POOL_ELEMENT.get(Identifier.parse("minecraft:empty_pool_element"))` |
| `PLACEMENT_MODIFIER_TYPE` | `PlacementModifierType<?>` | 放置修饰符类型 | `val type = BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.get(Identifier.parse("minecraft:block_filter"))` |

### 世界生成 — 树木

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `FOLIAGE_PLACER_TYPE` | `FoliagePlacerType<?>` | 树叶放置器类型 | `val type = BuiltInRegistries.FOLIAGE_PLACER_TYPE.get(Identifier.parse("minecraft:blob_foliage_placer"))` |
| `TRUNK_PLACER_TYPE` | `TrunkPlacerType<?>` | 树干放置器类型 | `val type = BuiltInRegistries.TRUNK_PLACER_TYPE.get(Identifier.parse("minecraft:straight_trunk_placer"))` |
| `ROOT_PLACER_TYPE` | `RootPlacerType<?>` | 根放置器类型 | `val type = BuiltInRegistries.ROOT_PLACER_TYPE.get(Identifier.parse("minecraft:none_root_placer"))` |
| `TREE_DECORATOR_TYPE` | `TreeDecoratorType<?>` | 树装饰器类型 | `val type = BuiltInRegistries.TREE_DECORATOR_TYPE.get(Identifier.parse("minecraft:leave_vine"))` |
| `FEATURE_SIZE_TYPE` | `FeatureSizeType<?>` | 特性大小类型 | `val type = BuiltInRegistries.FEATURE_SIZE_TYPE.get(Identifier.parse("minecraft:two_layers_feature_size"))` |

### 世界生成 — 基于 Codec 的（MapCodec 注册表）

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `BIOME_SOURCE` | `MapCodec<? extends BiomeSource>` | 生物群系源 | `val codec = BuiltInRegistries.BIOME_SOURCE.get(Identifier.parse("minecraft:multi_noise"))` |
| `CHUNK_GENERATOR` | `MapCodec<? extends ChunkGenerator>` | 区块生成器 | `val codec = BuiltInRegistries.CHUNK_GENERATOR.get(Identifier.parse("minecraft:noise"))` |
| `MATERIAL_CONDITION` | `MapCodec<? extends ConditionSource>` | 材质条件 | `val codec = BuiltInRegistries.MATERIAL_CONDITION.get(Identifier.parse("minecraft:steep"))` |
| `MATERIAL_RULE` | `MapCodec<? extends RuleSource>` | 材质规则 | `val codec = BuiltInRegistries.MATERIAL_RULE.get(Identifier.parse("minecraft:block"))` |
| `DENSITY_FUNCTION_TYPE` | `MapCodec<? extends DensityFunction>` | 密度函数类型 | `val codec = BuiltInRegistries.DENSITY_FUNCTION_TYPE.get(Identifier.parse("minecraft:constant"))` |
| `BLOCK_TYPE` | `MapCodec<? extends Block>` | 方块类型 | `val codec = BuiltInRegistries.BLOCK_TYPE.get(Identifier.parse("minecraft:simple_block"))` |
| `POOL_ALIAS_BINDING_TYPE` | `MapCodec<? extends PoolAliasBinding>` | 池别名绑定类型 | `val codec = BuiltInRegistries.POOL_ALIAS_BINDING_TYPE.get(Identifier.parse("minecraft:direct"))` |
| `ENTITY_SUB_PREDICATE_TYPE` | `MapCodec<? extends EntitySubPredicate>` | 实体子谓词类型 | `val codec = BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE.get(Identifier.parse("minecraft:entity_flags"))` |

### 附魔系统

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `ENCHANTMENT_LEVEL_BASED_VALUE_TYPE` | `MapCodec<? extends LevelBasedValue>` | 附魔等级基于值类型 | `val codec = BuiltInRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE.get(Identifier.parse("minecraft:level"))` |
| `ENCHANTMENT_ENTITY_EFFECT_TYPE` | `MapCodec<? extends EnchantmentEntityEffect>` | 附魔实体效果类型 | `val codec = BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE.get(Identifier.parse("minecraft:damage_entity"))` |
| `ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE` | `MapCodec<? extends EnchantmentLocationBasedEffect>` | 附魔位置基于效果类型 | `val codec = BuiltInRegistries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE.get(Identifier.parse("minecraft:apply_mob_effect"))` |
| `ENCHANTMENT_VALUE_EFFECT_TYPE` | `MapCodec<? extends EnchantmentValueEffect>` | 附魔值效果类型 | `val codec = BuiltInRegistries.ENCHANTMENT_VALUE_EFFECT_TYPE.get(Identifier.parse("minecraft:add_value"))` |
| `ENCHANTMENT_PROVIDER_TYPE` | `MapCodec<? extends EnchantmentProvider>` | 附魔提供器类型 | `val codec = BuiltInRegistries.ENCHANTMENT_PROVIDER_TYPE.get(Identifier.parse("minecraft:binomial"))` |

### 杂项

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `GAME_EVENT` | `GameEvent` | 游戏事件 | `val event = BuiltInRegistries.GAME_EVENT.get(Identifier.parse("minecraft:block_place"))` |
| `ATTRIBUTE` | `Attribute` | 属性 | `val attr = BuiltInRegistries.ATTRIBUTE.get(Identifier.parse("minecraft:generic.max_health"))` |
| `STAT_TYPE` | `StatType<?>` | 统计类型 | `val type = BuiltInRegistries.STAT_TYPE.get(Identifier.parse("minecraft:custom"))` |
| `CUSTOM_STAT` | `Identifier` | 自定义统计 | `val stat = BuiltInRegistries.CUSTOM_STAT.get(Identifier.parse("minecraft:play_one_minute"))` |
| `COMMAND_ARGUMENT_TYPE` | `ArgumentTypeInfo<?, ?>` | 命令参数类型 | `val type = BuiltInRegistries.COMMAND_ARGUMENT_TYPE.get(Identifier.parse("minecraft:entity"))` |
| `POSITION_SOURCE_TYPE` | `PositionSourceType<?>` | 位置源类型 | `val type = BuiltInRegistries.POSITION_SOURCE_TYPE.get(Identifier.parse("minecraft:block"))` |
| `CHUNK_STATUS` | `ChunkStatus` | 区块状态 | `val status = BuiltInRegistries.CHUNK_STATUS.get(Identifier.parse("minecraft:empty"))` |
| `GAME_RULE` | `GameRule<?>` | 游戏规则 | `val rule = BuiltInRegistries.GAME_RULE.get(Identifier.parse("minecraft:keep_inventory"))` |
| `DEBUG_SUBSCRIPTION` | `DebugSubscription<?>` | 调试订阅 | `val sub = BuiltInRegistries.DEBUG_SUBSCRIPTION.get(Identifier.parse("minecraft:game_test"))` |
| `TRIGGER_TYPES` | `CriterionTrigger<?>` | 进度触发器类型 | `val trigger = BuiltInRegistries.TRIGGER_TYPES.get(Identifier.parse("minecraft:impossible"))` |
| `TICKET_TYPE` | `TicketType` | 票据类型 | `val type = BuiltInRegistries.TICKET_TYPE.get(Identifier.parse("minecraft:portal"))` |
| `SLOT_SOURCE_TYPE` | `MapCodec<? extends SlotSource>` | 槽位源类型 | `val codec = BuiltInRegistries.SLOT_SOURCE_TYPE.get(Identifier.parse("minecraft:bottom"))` |
| `TEST_FUNCTION` | `Consumer<GameTestHelper>` | 测试函数 | `val func = BuiltInRegistries.TEST_FUNCTION.get(Identifier.parse("minecraft:test_example"))` |
| `SPAWN_CONDITION_TYPE` | `MapCodec<? extends SpawnCondition>` | 生成条件类型 | `val codec = BuiltInRegistries.SPAWN_CONDITION_TYPE.get(Identifier.parse("minecraft:in_block"))` |
| `DIALOG_TYPE` | `MapCodec<? extends Dialog>` | 对话类型 | `val codec = BuiltInRegistries.DIALOG_TYPE.get(Identifier.parse("minecraft:dialog"))` |
| `DIALOG_ACTION_TYPE` | `MapCodec<? extends Action>` | 对话动作类型 | `val codec = BuiltInRegistries.DIALOG_ACTION_TYPE.get(Identifier.parse("minecraft:say"))` |
| `INPUT_CONTROL_TYPE` | `MapCodec<? extends InputControl>` | 输入控制类型 | `val codec = BuiltInRegistries.INPUT_CONTROL_TYPE.get(Identifier.parse("minecraft:keyboard"))` |
| `DIALOG_BODY_TYPE` | `MapCodec<? extends DialogBody>` | 对话体类型 | `val codec = BuiltInRegistries.DIALOG_BODY_TYPE.get(Identifier.parse("minecraft:text"))` |
| `PERMISSION_TYPE` | `MapCodec<? extends Permission>` | 权限类型 | `val codec = BuiltInRegistries.PERMISSION_TYPE.get(Identifier.parse("minecraft:default"))` |
| `PERMISSION_CHECK_TYPE` | `MapCodec<? extends PermissionCheck>` | 权限检查类型 | `val codec = BuiltInRegistries.PERMISSION_CHECK_TYPE.get(Identifier.parse("minecraft:has_permission"))` |
| `ENVIRONMENT_ATTRIBUTE` | `EnvironmentAttribute<?>` | 环境属性 | `val attr = BuiltInRegistries.ENVIRONMENT_ATTRIBUTE.get(Identifier.parse("minecraft:daytime"))` |
| `ATTRIBUTE_TYPE` | `AttributeType<?>` | 属性类型 | `val type = BuiltInRegistries.ATTRIBUTE_TYPE.get(Identifier.parse("minecraft:generic"))` |

### 元注册表

| 字段名 | 元素类型 | 中文描述 | 使用示例 |
|--------|----------|----------|----------|
| `REGISTRY` | `Registry<? extends Registry<?>>` | 注册表的注册表 | `BuiltInRegistries.REGISTRY` |

## 数据包驱动注册表 (Registries)

这些注册表从数据包 JSON 加载，需要通过 `RegistryAccess` 访问。

### 核心注册表

| 常量 | 元素类型 | 中文描述 | 使用示例 |
|------|----------|----------|----------|
| `BIOME` | `Biome` | 生物群系 | `val biomes = registryAccess.lookupOrThrow(Registries.BIOME)` |
| `DIMENSION_TYPE` | `DimensionType` | 维度类型 | `val types = registryAccess.lookupOrThrow(Registries.DIMENSION_TYPE)` |
| `DIMENSION` | `Level` | 维度（世界） | `val dimensions = registryAccess.lookupOrThrow(Registries.DIMENSION)` |
| `LEVEL_STEM` | `LevelStem` | 世界茎（维度模板） | `val stems = registryAccess.lookupOrThrow(Registries.LEVEL_STEM)` |
| `ENCHANTMENT` | `Enchantment` | 附魔 | `val enchantments = registryAccess.lookupOrThrow(Registries.ENCHANTMENT)` |
| `DAMAGE_TYPE` | `DamageType` | 伤害类型 | `val damageTypes = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE)` |

### 装饰与音乐

| 常量 | 元素类型 | 中文描述 | 使用示例 |
|------|----------|----------|----------|
| `BANNER_PATTERN` | `BannerPattern` | 旗帜图案 | `val patterns = registryAccess.lookupOrThrow(Registries.BANNER_PATTERN)` |
| `CHAT_TYPE` | `ChatType` | 聊天类型 | `val chatTypes = registryAccess.lookupOrThrow(Registries.CHAT_TYPE)` |
| `TRIM_MATERIAL` | `TrimMaterial` | 盔甲锻造材料 | `val materials = registryAccess.lookupOrThrow(Registries.TRIM_MATERIAL)` |
| `TRIM_PATTERN` | `TrimPattern` | 盔甲锻造图案 | `val patterns = registryAccess.lookupOrThrow(Registries.TRIM_PATTERN)` |
| `JUKEBOX_SONG` | `JukeboxSong` | 唱片机音乐 | `val songs = registryAccess.lookupOrThrow(Registries.JUKEBOX_SONG)` |
| `INSTRUMENT` | `Instrument` | 乐器 | `val instruments = registryAccess.lookupOrThrow(Registries.INSTRUMENT)` |

### 世界生成

| 常量 | 元素类型 | 中文描述 | 使用示例 |
|------|----------|----------|----------|
| `CONFIGURED_FEATURE` | `ConfiguredFeature` | 配置的特性 | `val features = registryAccess.lookupOrThrow(Registries.CONFIGURED_FEATURE)` |
| `PLACED_FEATURE` | `PlacedFeature` | 放置的特性 | `val features = registryAccess.lookupOrThrow(Registries.PLACED_FEATURE)` |
| `CONFIGURED_CARVER` | `ConfiguredWorldCarver` | 配置的雕刻器 | `val carvers = registryAccess.lookupOrThrow(Registries.CONFIGURED_CARVER)` |
| `DENSITY_FUNCTION` | `DensityFunction` | 密度函数 | `val functions = registryAccess.lookupOrThrow(Registries.DENSITY_FUNCTION)` |
| `NOISE` | `NoiseParameters` | 噪声参数 | `val noises = registryAccess.lookupOrThrow(Registries.NOISE)` |
| `NOISE_SETTINGS` | `NoiseGeneratorSettings` | 噪声生成器设置 | `val settings = registryAccess.lookupOrThrow(Registries.NOISE_SETTINGS)` |
| `STRUCTURE` | `Structure` | 结构 | `val structures = registryAccess.lookupOrThrow(Registries.STRUCTURE)` |
| `STRUCTURE_SET` | `StructureSet` | 结构集 | `val sets = registryAccess.lookupOrThrow(Registries.STRUCTURE_SET)` |
| `TEMPLATE_POOL` | `StructureTemplatePool` | 结构模板池 | `val pools = registryAccess.lookupOrThrow(Registries.TEMPLATE_POOL)` |
| `PROCESSOR_LIST` | `StructureProcessorList` | 结构处理器列表 | `val lists = registryAccess.lookupOrThrow(Registries.PROCESSOR_LIST)` |
| `WORLD_PRESET` | `WorldPreset` | 世界预设 | `val presets = registryAccess.lookupOrThrow(Registries.WORLD_PRESET)` |
| `FLAT_LEVEL_GENERATOR_PRESET` | `FlatLevelGeneratorPreset` | 超平坦世界生成器预设 | `val presets = registryAccess.lookupOrThrow(Registries.FLAT_LEVEL_GENERATOR_PRESET)` |
| `MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST` | `MultiNoiseBiomeSourceParameterList` | 多噪声生物群系源参数列表 | `val lists = registryAccess.lookupOrThrow(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)` |

### 生物变体

| 常量 | 元素类型 | 中文描述 | 使用示例 |
|------|----------|----------|----------|
| `CAT_VARIANT` | `CatVariant` | 猫变体 | `val variants = registryAccess.lookupOrThrow(Registries.CAT_VARIANT)` |
| `CHICKEN_VARIANT` | `ChickenVariant` | 鸡变体 | `val variants = registryAccess.lookupOrThrow(Registries.CHICKEN_VARIANT)` |
| `COW_VARIANT` | `CowVariant` | 牛变体 | `val variants = registryAccess.lookupOrThrow(Registries.COW_VARIANT)` |
| `FROG_VARIANT` | `FrogVariant` | 青蛙变体 | `val variants = registryAccess.lookupOrThrow(Registries.FROG_VARIANT)` |
| `PAINTING_VARIANT` | `PaintingVariant` | 画变体 | `val variants = registryAccess.lookupOrThrow(Registries.PAINTING_VARIANT)` |
| `PIG_VARIANT` | `PigVariant` | 猪变体 | `val variants = registryAccess.lookupOrThrow(Registries.PIG_VARIANT)` |
| `WOLF_VARIANT` | `WolfVariant` | 狼变体 | `val variants = registryAccess.lookupOrThrow(Registries.WOLF_VARIANT)` |
| `WOLF_SOUND_VARIANT` | `WolfSoundVariant` | 狼声音变体 | `val variants = registryAccess.lookupOrThrow(Registries.WOLF_SOUND_VARIANT)` |
| `ZOMBIE_NAUTILUS_VARIANT` | `ZombieNautilusVariant` | 僵尸鹦鹉螺变体 | `val variants = registryAccess.lookupOrThrow(Registries.ZOMBIE_NAUTILUS_VARIANT)` |

### 数据 / 战利品 / 进度

| 常量 | 元素类型 | 中文描述 | 使用示例 |
|------|----------|----------|----------|
| `LOOT_TABLE` | `LootTable` | 战利品表 | `val tables = registryAccess.lookupOrThrow(Registries.LOOT_TABLE)` |
| `ITEM_MODIFIER` | `LootItemFunction` | 物品修饰器 | `val modifiers = registryAccess.lookupOrThrow(Registries.ITEM_MODIFIER)` |
| `PREDICATE` | `LootItemCondition` | 谓词 | `val predicates = registryAccess.lookupOrThrow(Registries.PREDICATE)` |
| `ADVANCEMENT` | `Advancement` | 进度 | `val advancements = registryAccess.lookupOrThrow(Registries.ADVANCEMENT)` |
| `RECIPE` | `Recipe` | 配方 | `val recipes = registryAccess.lookupOrThrow(Registries.RECIPE)` |

### 对话 / 测试

| 常量 | 元素类型 | 中文描述 | 使用示例 |
|------|----------|----------|----------|
| `DIALOG` | `Dialog` | 对话 | `val dialogs = registryAccess.lookupOrThrow(Registries.DIALOG)` |
| `TEST_ENVIRONMENT` | `TestEnvironmentDefinition` | 测试环境定义 | `val envs = registryAccess.lookupOrThrow(Registries.TEST_ENVIRONMENT)` |
| `TEST_INSTANCE` | `GameTestInstance` | 游戏测试实例 | `val instances = registryAccess.lookupOrThrow(Registries.TEST_INSTANCE)` |
| `TIMELINE` | `Timeline` | 时间线 | `val timelines = registryAccess.lookupOrThrow(Registries.TIMELINE)` |
| `TRIAL_SPAWNER_CONFIG` | `TrialSpawnerConfig` | 试刷刷怪笼配置 | `val configs = registryAccess.lookupOrThrow(Registries.TRIAL_SPAWNER_CONFIG)` |