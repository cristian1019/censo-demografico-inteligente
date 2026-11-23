package br.edu.unis.censo.service

import br.edu.unis.censo.model.NomeFrequencia
import br.edu.unis.censo.model.RankingNomes
import org.json.JSONArray
import org.json.JSONObject

/**
 * Integração com a API de Nomes do IBGE (Censo 2022).
 * Endpoint: https://servicodados.ibge.gov.br/api/v3/nomes/2022/localidade/{id_localidade}
 */
class IbgeNomesService {

    private val baseUrl = "https://servicodados.ibge.gov.br/api/v3/nomes/2022/localidade"

    /** Busca os nomes e sobrenomes mais comuns de um estado ou município. */
    suspend fun buscarRanking(localidadeId: Int): RankingNomes {
        val json = JSONObject(HttpClient.get("$baseUrl/$localidadeId"))
        return RankingNomes(
            localidade = json.optInt("localidade", localidadeId),
            mulheres = lerLista(json.optJSONArray("mulheres")),
            homens = lerLista(json.optJSONArray("homens")),
            sobrenomes = lerLista(json.optJSONArray("sobrenomes"))
        )
    }

    private fun lerLista(array: JSONArray?): List<NomeFrequencia> {
        if (array == null) return emptyList()
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            NomeFrequencia(
                nome = obj.getString("nome"),
                frequencia = obj.getLong("frequencia")
            )
        }
    }
}
