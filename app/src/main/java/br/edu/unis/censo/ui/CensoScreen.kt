package br.edu.unis.censo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.unis.censo.model.NomeFrequencia
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CensoScreen(viewModel: CensoViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Censo Demográfico Inteligente") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Nomes mais comuns por localidade (IBGE - Censo 2022)",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))

            Seletor(
                rotulo = "Estado",
                textoSelecionado = state.estadoSelecionado?.let { "${it.nome} (${it.sigla})" } ?: "",
                opcoes = state.estados.map { "${it.nome} (${it.sigla})" },
                habilitado = state.estados.isNotEmpty(),
                aoSelecionar = { i -> viewModel.selecionarEstado(state.estados[i]) }
            )
            Spacer(Modifier.height(8.dp))

            Seletor(
                rotulo = "Município (opcional)",
                textoSelecionado = state.municipioSelecionado?.nome ?: "Todo o estado",
                opcoes = listOf("Todo o estado") + state.municipios.map { it.nome },
                habilitado = state.municipios.isNotEmpty(),
                aoSelecionar = { i ->
                    viewModel.selecionarMunicipio(if (i == 0) null else state.municipios[i - 1])
                }
            )
            Spacer(Modifier.height(12.dp))

            state.erro?.let { erro ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(erro, color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
                    if (state.estados.isEmpty()) {
                        TextButton(onClick = { viewModel.carregarEstados() }) { Text("Tentar de novo") }
                    }
                }
            }

            when {
                state.carregandoNomes -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.ranking != null -> {
                    val ranking = state.ranking!!
                    Text(
                        state.localidadeAtual ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    AbasDeNomes(
                        mulheres = ranking.mulheres,
                        homens = ranking.homens,
                        sobrenomes = ranking.sobrenomes
                    )
                }
                state.estadoSelecionado == null && state.carregandoLocalidades -> Box(
                    Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
                state.estadoSelecionado == null -> Text(
                    "Selecione um estado para ver os nomes mais comuns.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/** Dropdown (picker) reutilizável para estados e municípios. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Seletor(
    rotulo: String,
    textoSelecionado: String,
    opcoes: List<String>,
    habilitado: Boolean,
    aoSelecionar: (Int) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido && habilitado,
        onExpandedChange = { if (habilitado) expandido = it }
    ) {
        OutlinedTextField(
            value = textoSelecionado,
            onValueChange = {},
            readOnly = true,
            enabled = habilitado,
            label = { Text(rotulo) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido && habilitado,
            onDismissRequest = { expandido = false }
        ) {
            opcoes.forEachIndexed { i, opcao ->
                DropdownMenuItem(
                    text = { Text(opcao) },
                    onClick = {
                        expandido = false
                        aoSelecionar(i)
                    }
                )
            }
        }
    }
}

/** Abas que separam os nomes de mulheres, de homens e os sobrenomes. */
@Composable
private fun AbasDeNomes(
    mulheres: List<NomeFrequencia>,
    homens: List<NomeFrequencia>,
    sobrenomes: List<NomeFrequencia>
) {
    var abaSelecionada by rememberSaveable { mutableIntStateOf(0) }
    val abas = listOf("Mulheres", "Homens", "Sobrenomes")
    val listas = listOf(mulheres, homens, sobrenomes)

    TabRow(selectedTabIndex = abaSelecionada) {
        abas.forEachIndexed { i, titulo ->
            Tab(
                selected = abaSelecionada == i,
                onClick = { abaSelecionada = i },
                text = { Text(titulo) }
            )
        }
    }
    ListaDeNomes(listas[abaSelecionada])
}

@Composable
private fun ListaDeNomes(nomes: List<NomeFrequencia>) {
    if (nomes.isEmpty()) {
        Text("Sem dados para esta localidade.", modifier = Modifier.padding(16.dp))
        return
    }
    val formato = NumberFormat.getIntegerInstance(Locale("pt", "BR"))

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(nomes.size) { i ->
            val item = nomes[i]
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${i + 1}º", fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                    Text(
                        item.nome.replaceFirstChar { it.titlecase(Locale("pt", "BR")) },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${formato.format(item.frequencia)} pessoas", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
