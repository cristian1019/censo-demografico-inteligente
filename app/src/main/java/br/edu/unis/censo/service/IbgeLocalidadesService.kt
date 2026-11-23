package br.edu.unis.censo.service

import br.edu.unis.censo.model.Estado
import br.edu.unis.censo.model.Municipio
import org.json.JSONArray

/**
 * Integração com a API de Localidades do IBGE.
 * Documentação: https://servicodados.ibge.gov.br/api/docs/localidades
 */
class IbgeLocalidadesService {

    private val baseUrl = "https://servicodados.ibge.gov.br/api/v1/localidades"

    /** Lista todos os estados, em ordem alfabética. */
    suspend fun listarEstados(): List<Estado> {
        val json = JSONArray(HttpClient.get("$baseUrl/estados?orderBy=nome"))
        return (0 until json.length()).map { i ->
            val obj = json.getJSONObject(i)
            Estado(
                id = obj.getInt("id"),
                sigla = obj.getString("sigla"),
                nome = obj.getString("nome")
            )
        }
    }

    /** Lista os municípios de um estado, em ordem alfabética. */
    suspend fun listarMunicipios(estadoId: Int): List<Municipio> {
        val json = JSONArray(HttpClient.get("$baseUrl/estados/$estadoId/municipios?orderBy=nome"))
        return (0 until json.length()).map { i ->
            val obj = json.getJSONObject(i)
            Municipio(id = obj.getInt("id"), nome = obj.getString("nome"))
        }
    }
}
