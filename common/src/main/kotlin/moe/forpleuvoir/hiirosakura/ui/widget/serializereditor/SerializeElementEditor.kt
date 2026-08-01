package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeNull
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive

@Composable
fun SerializeElementEditor(
    data: SerializeElement,
    onDataChange: (SerializeElement) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SerializeElementRootEditor(data = data, onDataChange = onDataChange)
    }
}

@Composable
private fun SerializeElementRootEditor(
    data: SerializeElement,
    onDataChange: (SerializeElement) -> Unit,
) {
    when (data) {
        is SerializeObject -> SerializeObjectEditor(
            serializeObject = data,
            onValueChange = onDataChange,
        )
        is SerializeArray -> SerializeArrayEditor(
            serializeArray = data,
            onValueChange = onDataChange,
        )
        is SerializePrimitive -> SerializePrimitiveEditor(
            serializePrimitive = data,
            onValueChange = onDataChange,
        )
        is SerializeNull -> Unit
    }
}