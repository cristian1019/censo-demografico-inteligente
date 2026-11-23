package br.edu.unis.censo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.unis.censo.model.Estado
import br.edu.unis.censo.model.Municipio
import br.edu.unis.censo.model.RankingNomes
import br.edu.unis.censo.service.IbgeLocalidadesService
import br.edu.unis.censo.service.IbgeNomesService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado da tela: tudo o que a interface precisa para se desenhar. */
data class CensoUiState(
    val estados: List<Estado> = emptyList(),
    val municipios: List<Municipio> = emptyList(),
    val estadoSelecionado: Estado? = null,
    val municipioSelecionado: Municipio? = null,
    val ranking: RankingNomes? = null,
    val carregandoLocalidades: Boolean = false,
    val carregandoNomes: Boolean = false,
    val erro: String? = null
) {
    /** Nome da localidade cujo ranking está sendo exibido. */
    val localidadeAtual: String?
        get() = municipioSelecionado?.let { "${it.nome} - ${estadoSelecionado?.sigla}" }
            ?: estadoSelecionado?.nome
}

/**
 * Faz a ponte entre a interface e a camada de serviços.
 * Não acessa a internet diretamente: apenas chama os serviços do IBGE.
 */
class CensoViewModel(
    private val localidadesService: IbgeLocalidadesService = IbgeLocalidadesService(),
    private val nomesService: IbgeNomesService = IbgeNomesService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CensoUiState())
    val uiState: StateFlow<CensoUiState> = _uiState.asStateFlow()

    private var rankingJob: Job? = null

    init {
        carregarEstados()
    }

    fun carregarEstados() {
        viewModelScope.launch {
            _uiState.update { it.copy(carregandoLocalidades = true, erro = null) }
            try {
                val estados = localidadesService.listarEstados()
                _uiState.update { it.copy(estados = estados, carregandoLocalidades = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(carregandoLocalidades = false, erro = "Não foi possível carregar os estados. Verifique sua conexão.")
                }
            }
        }
    }

    /** Ao escolher um estado: busca os nomes do estado e a lista de municípios. */
    fun selecionarEstado(estado: Estado) {
        _uiState.update {
            it.copy(estadoSelecionado = estado, municipioSelecionado = null, municipios = emptyList(), ranking = null)
        }
        buscarRanking(estado.id)
        viewModelScope.launch {
            _uiState.update { it.copy(carregandoLocalidades = true) }
            try {
                val municipios = localidadesService.listarMunicipios(estado.id)
                _uiState.update { it.copy(municipios = municipios, carregandoLocalidades = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(carregandoLocalidades = false, erro = "Não foi possível carregar os municípios.")
                }
            }
        }
    }

    /** Ao escolher um município (ou nenhum, para voltar ao estado inteiro). */
    fun selecionarMunicipio(municipio: Municipio?) {
        val estado = _uiState.value.estadoSelecionado ?: return
        _uiState.update { it.copy(municipioSelecionado = municipio) }
        buscarRanking(municipio?.id ?: estado.id)
    }

    /** Consulta encadeada: usa o id da localidade escolhida na API de Nomes. */
    private fun buscarRanking(localidadeId: Int) {
        rankingJob?.cancel()
        rankingJob = viewModelScope.launch {
            _uiState.update { it.copy(carregandoNomes = true, ranking = null, erro = null) }
            try {
                val ranking = nomesService.buscarRanking(localidadeId)
                _uiState.update { it.copy(ranking = ranking, carregandoNomes = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(carregandoNomes = false, erro = "Não foi possível buscar os nomes desta localidade.")
                }
            }
        }
    }
}
